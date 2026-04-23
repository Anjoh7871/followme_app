package com.example.followme02.screen.home

import android.util.Log
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.followme02.R
import com.example.followme02.model.Destinations
import com.example.followme02.viewmodel.DestinationViewModel
import com.example.followme02.viewmodel.ProfileViewModel
import com.example.followme02.screen.home.MapViewModel
import com.example.followme02.screen.home.MapPointUI
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
    destinationViewModel: DestinationViewModel = viewModel(),
    mapViewModel: MapViewModel = viewModel()
) {
    val profile = viewModel.uiState.value
    val selectedDestination = destinationViewModel.selectedDestination.value
    val currentJourneyKm = destinationViewModel.currentJourneyKm.value
    val recentlyCompletedDestination = destinationViewModel.recentlyCompletedDestination.value
    val colorScheme = MaterialTheme.colorScheme
    val mapPoints = mapViewModel.mapPoints
    Log.d("MAP_DEBUG", "mapPoints size = ${mapPoints.size}")

    var showDestinationDialog by remember { mutableStateOf(false) }
    var showChangeDestinationWarning by remember { mutableStateOf(false) }
    var pendingDestination by remember { mutableStateOf<Destinations?>(null) }

    val destinations = destinationViewModel.allDestinations.value
    val visited = destinationViewModel.visitedDestinationIds.value

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.loadUser()
        destinationViewModel.loadDestinations()
    }

    LaunchedEffect(destinations, visited) {
        if (destinations.isNotEmpty()) {
            mapViewModel.loadMapData(destinations, visited)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadUser()
                destinationViewModel.loadDestinations()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(profile.totalAccumulatedKm, selectedDestination?.destinationId) {
        destinationViewModel.syncCurrentJourney(profile.totalAccumulatedKm)
    }

    Scaffold(
        containerColor = colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("workout")
                },
                modifier = Modifier.size(76.dp),
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Workout",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            HeaderSection(viewModel)

            Spacer(modifier = Modifier.height(20.dp))

            JourneyCard(
                currentKm = currentJourneyKm,
                targetKm = selectedDestination?.kmThreshold ?: 0.0,
                destinationName = selectedDestination?.name ?: "Choose destination",
                onClick = {
                    showDestinationDialog = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            MapJourneyCard(700.00, mapPoints = mapPoints)

            Spacer(modifier = Modifier.height(120.dp))
        }
    }

    if (showDestinationDialog) {
        DestinationPickerDialog(
            viewModel = destinationViewModel,
            onDismiss = { showDestinationDialog = false },
            onDestinationSelected = { destination ->
                val hasStartedJourney =
                    selectedDestination != null && currentJourneyKm > 0.0

                val isSameDestination =
                    selectedDestination?.destinationId == destination.destinationId

                when {
                    isSameDestination -> {
                        showDestinationDialog = false
                    }

                    hasStartedJourney -> {
                        pendingDestination = destination
                        showDestinationDialog = false
                        showChangeDestinationWarning = true
                    }

                    else -> {
                        destinationViewModel.selectDestination(
                            destination = destination,
                            currentTotalKm = profile.totalAccumulatedKm
                        )
                        showDestinationDialog = false
                    }
                }
            }
        )
    }

    recentlyCompletedDestination?.let { destination ->
        val context = LocalContext.current
        val drawableName = imageUrlToDrawableName(destination.imageUrl)

        val imageResId = remember(drawableName) {
            drawableName?.let {
                context.resources.getIdentifier(it, "drawable", context.packageName)
            } ?: 0
        }

        AlertDialog(
            onDismissRequest = {
                destinationViewModel.clearRecentlyCompletedDestination()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        destinationViewModel.clearRecentlyCompletedDestination()
                    }
                ) {
                    Text("Close")
                }
            },
            title = {
                Column {
                    Text("Congratulations! 🎉")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(destination.name)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = destination.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = "You completed your journey to ${destination.name}!"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = destination.factText ?: "No fun fact available."
                    )
                }
            }
        )
    }

    if (showChangeDestinationWarning && pendingDestination != null) {
        AlertDialog(
            onDismissRequest = {
                showChangeDestinationWarning = false
                pendingDestination = null
            },
            title = {
                Text("Change destination?")
            },
            text = {
                Text(
                    "You have already started this journey. If you choose a new destination now, you will lose your current progress."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDestination?.let { destination ->
                            destinationViewModel.selectDestination(
                                destination = destination,
                                currentTotalKm = profile.totalAccumulatedKm
                            )
                        }
                        showChangeDestinationWarning = false
                        pendingDestination = null
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showChangeDestinationWarning = false
                        pendingDestination = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HeaderSection(viewModel: ProfileViewModel) {
    val profile = viewModel.uiState.value
    val username = profile.username.ifBlank { "User" }
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Welcome back, $username! 👋",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Keep moving toward your destination!",
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                title = "Level",
                value = profile.currentLevel.toString(),
                containerColor = colorScheme.secondaryContainer,
                contentColor = colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Points",
                value = profile.totalPoints.toString(),
                containerColor = colorScheme.tertiaryContainer,
                contentColor = colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
fun JourneyCard(
    currentKm: Double,
    targetKm: Double = 235.0,
    destinationName: String = "Tromsø",
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val progress = if (targetKm > 0) {
        (currentKm / targetKm).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    val currentKmText = if (currentKm % 1.0 == 0.0) {
        currentKm.toInt().toString()
    } else {
        String.format("%.1f", currentKm)
    }

    val targetKmText = if (targetKm % 1.0 == 0.0) {
        targetKm.toInt().toString()
    } else {
        String.format("%.1f", targetKm)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Your Virtual Journey",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Destination: $destinationName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "$currentKmText / $targetKmText km",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
                color = colorScheme.primary,
                trackColor = colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
            )
        }
    }
}

/*@Composable
fun MapJourneyCard() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Map Route Placeholder",
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant
            )

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

 */
@Composable
fun MapJourneyCard(
    currentKm: Double,
    mapPoints: List<MapPointUI>
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Point A: Narvik
        val startPoint = GeoPoint(68.4385, 17.4272)

        // Example Point B: Tromsø (Approx 250km from Narvik by road, less as crow flies)
        // Adjust the 170.0 threshold to whatever your "crow flies" distance logic requires
        /*val destinations = listOf(
            MapPoint("Tromsø", GeoPoint(69.6492, 18.9553), 170.0),
            MapPoint("Harstad", GeoPoint(68.7986, 16.5415), 170.0),
            MapPoint("Berlin", GeoPoint(52.5200, 13.4050), 170.0)
        )*/

        OSMMapView(
            startPoint = startPoint,
            destinations = mapPoints,
            currentKm = currentKm
        )

    }
}

data class MapPoint(val name: String, val location: GeoPoint, val threshold: Double)

@Composable
fun OSMMapView(
    startPoint: GeoPoint,
    destinations: List<MapPointUI>,
    currentKm: Double
) {
    val context = LocalContext.current

    // 1. Initialize the MapView
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            //zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        }
    }

    // 2. Lifecycle management (Required for OSMdroid)
    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }

    // 3. Render the Map
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            view.overlays.clear()

            // Add Point A (Narvik)
            val startMarker = Marker(view)
            startMarker.position = startPoint
            startMarker.title = "Narvik (Start)"
            view.overlays.add(startMarker)

            val pointsToFit = mutableListOf<GeoPoint>()
            pointsToFit.add(startPoint)

            // Add Point B if reached
            /*
            destinations.forEach { dest ->
                if (currentKm >= dest.threshold) {
                    val endMarker = Marker(view)
                    endMarker.position = dest.location
                    endMarker.title = "${dest.name} reached!"
                    view.overlays.add(endMarker)
                    pointsToFit.add(dest.location)
                }
            }

             */
            destinations.forEach { dest ->

                val marker = Marker(view)
                marker.position = dest.location
                marker.title = dest.name

                val iconRes = if (dest.isVisited) {
                    R.drawable.ic_marker_green
                } else {
                    R.drawable.ic_marker_red
                }

                marker.icon = ContextCompat.getDrawable(context, iconRes)

                view.overlays.add(marker)
                pointsToFit.add(dest.location)
            }

            // FIX: Use a Post-Layout listener to ensure the map has a width/height
            view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?, left: Int, top: Int, right: Int, bottom: Int,
                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
                ) {
                    if (pointsToFit.size > 1) {
                        val boundingBox = BoundingBox.fromGeoPoints(pointsToFit)
                        // Increase padding to 250 to ensure the pins aren't cut off by the card edges
                        view.zoomToBoundingBox(boundingBox, true, 300)
                    } else {
                        view.controller.setCenter(startPoint)
                        //view.controller.setZoom(10.0) // Zoomed in on Narvik
                        view.controller.setZoom(8.5)
                    }
                    // Remove listener so it doesn't keep snapping back when the user moves the map
                    view.removeOnLayoutChangeListener(this)
                }
            })

            view.invalidate()
        }
    )
}

private fun imageUrlToDrawableName(imageUrl: String?): String? {
    if (imageUrl.isNullOrBlank()) return null

    return imageUrl
        .substringAfterLast("/")
        .substringBeforeLast(".")
        .lowercase()
}