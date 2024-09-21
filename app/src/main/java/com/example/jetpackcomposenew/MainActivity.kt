@file:Suppress("DEPRECATION")

package com.example.jetpackcomposenew

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import android.location.Geocoder
import android.util.Log
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false) // Enable edge-to-edge layout

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            AppNavigation()
        }
    }

    // Function to fetch the user's last known location
    private fun getLastKnownLocation(onLocationResult: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Pass location to callback
                    onLocationResult(location)
                }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(false) }

    // Listen for route changes to toggle bottom bar visibility
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            showBottomBar = backStackEntry.destination.route != "splash"
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("splash") {
                SplashScreen(navController)
            }
            composable("home") {
                HomeScreen(navController)
            }
            composable("details/{restaurantName}") { backStackEntry ->
                val restaurantName = backStackEntry.arguments?.getString("restaurantName")
                RestaurantDetailsScreen(restaurantName ?: "Unknown Restaurant")
            }
            composable("order") {
                OrderScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("category/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")
                if (category != null) {
                    CategoryScreen(category, navController)
                }
            }
        }

    }
}



@Composable
fun BottomNavBar(navController: NavController) {
    val currentDestination = navController.currentDestination
    val currentRoute = currentDestination?.route  // Extract the current route safely

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",  // Check the route safely
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Order") },
            label = { Text("Order") },
            selected = currentRoute == "order",  // Check the route safely
            onClick = {
                navController.navigate("order") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",  // Check the route safely
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        )
    }
}



@Composable
fun OrderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No Orders", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile Image or Avatar
            Image(
                painter = painterResource(id = R.drawable.profile_avatar), // Replace with actual avatar resource
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp)) // Circular avatar
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name and Email
            Text(text = "jonny kumar", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "jonnykumar@example.com", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Profile Button
            Button(
                onClick = { /* Handle coupon claim */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AA4F)),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Edit Profile")
            }
        }
    }
}


@Composable
fun RestaurantDetailsScreen(restaurantName: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Display the restaurant name passed as a parameter
        Text(text = restaurantName, style = MaterialTheme.typography.headlineSmall)

        // Burger Details
        BurgerDetails()

        // Coupon Button
        CouponButton()

        // Food Items
        FoodItemSection()
    }
}


@Composable
fun BurgerDetails() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Burger Anzay", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Indian, Fast food, Burger", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Text(text = "4.3 ⭐️")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "1k+ Reviews")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "15min Delivery")
        }
    }
}

@Composable
fun CouponButton() {
    Button(
        onClick = { /* Handle coupon claim */ },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AA4F)),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Claim Free Cheese!")
    }
}

@Composable
fun FoodItemSection() {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(3) {
            FoodItem()
        }
    }
}

@Composable
fun FoodItem() {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.burger), // Example image
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Cheese Burger", style = MaterialTheme.typography.titleMedium)
                Text(text = "$15", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00AA4F))
    ) {
        Text(
            text = "SnapBites",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White
        )
    }
    // Navigate to home after a delay
    LaunchedEffect(Unit) {
        delay(2000) // 2-second splash
        navController.navigate("home")
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var address by remember { mutableStateOf("Fetching location...") }
    var location by remember { mutableStateOf<Location?>(null) } // State to hold the location

    // Request location permission and fetch address
    RequestLocationPermission(
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context),
        onLocationReceived = { loc ->
            if (loc != null) {
                Log.d("HomeScreen", "Location received: ${loc.latitude}, ${loc.longitude}")
                location = loc
            } else {
                Log.d("HomeScreen", "Location is null")
            }
        }
    )


    // Use LaunchedEffect to fetch address when location is received
    LaunchedEffect(location) {
        location?.let {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addressList = withContext(Dispatchers.IO) {
                    geocoder.getFromLocation(it.latitude, it.longitude, 1)
                }
                if (addressList != null && addressList.isNotEmpty()) {
                    address = addressList[0].getAddressLine(0)
                } else {
                    address = "Address not found"
                }
            } catch (e: Exception) {
                address = "Error fetching address"
            }
        } ?: run {
            address = "Unable to fetch location"
        }
    }

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                DeliveryHeader(address = address)
                CouponBanner()

                var searchText by remember { mutableStateOf("") }

                // Search Bar
                SearchBar(
                    searchText = searchText,
                    onSearchTextChanged = { newText -> searchText = newText }
                )

                FoodCategories(navController)
                HighestRatingSection(searchText, navController)
            }
        }
    )
}


@Composable
fun SearchBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        label = { Text(text = "Search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        singleLine = true,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Person, contentDescription = "Search Icon")
        },
    )
}

@Composable
fun HighestRatingSection(searchText: String, navController: NavController) {
    val items = listOf(
        "Snap Pizza" to R.drawable.snap_pizza,
        "Taco Supreme" to R.drawable.taco_supreme,
        "Deluxe Burger" to R.drawable.burger,
        "Cheese Burger" to R.drawable.cheese_burger
    )

    val filteredItems = items.filter { it.first.contains(searchText, ignoreCase = true) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Highest rating in town", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(filteredItems) { item ->
                HighestRatedItem(item.first, item.second) {
                    // On click navigate to RestaurantDetailsScreen
                    navController.navigate("details/${item.first}")
                }
            }
        }
    }
}

@Composable
fun HighestRatedItem(itemName: String, imageRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(end = 16.dp)
            .clickable { onClick() }
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = itemName, style = MaterialTheme.typography.titleMedium)
                Text(text = "4.3 ⭐️ 156+ reviews", style = MaterialTheme.typography.labelSmall)
                Text(text = "1.5km • 15min", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}


@Composable
fun FoodCategories(navController: NavController) {
    // Map of categories and their respective icons (replace with actual drawable resources)
    val categoryIcons = mapOf(
        "Promo" to R.drawable.promocode1,     // Ensure these resources exist in your drawable folder
        "Taco" to R.drawable.taco_icon,
        "Drinks" to R.drawable.drinks_icon,
        "Meat" to R.drawable.meat_icon,
        "Sushi" to R.drawable.sushi_icon,
        "Pizza" to R.drawable.pizza
    )

    // Check if any of the icons are missing, or use a default fallback
    val defaultIcon = R.drawable.default_icon  // Ensure this default icon exists in your drawable folder

    LazyRow(modifier = Modifier.padding(40.dp)) {
        items(categoryIcons.keys.toList()) { category ->
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navController.navigate("category/$category")
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Safely load the category's icon, or use the default one if not available
                val icon = categoryIcons[category] ?: defaultIcon
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = category,  // `category` is a String and will be passed here correctly
                    modifier = Modifier.size(40.dp)  // Adjust the size as needed
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Display category name as text
                Text(text = category, style = MaterialTheme.typography.bodySmall)  // `category` is a String
            }
        }
    }
}


@Composable
fun CategoryScreen(category: String, navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "$category Restaurants", style = MaterialTheme.typography.headlineSmall)

        // Placeholder content for restaurants
        LazyColumn {
            items(5) { index ->
                RestaurantItem(name = "$category Restaurant $index") {
                    // On click navigate to RestaurantDetailsScreen
                    navController.navigate("details/$category Restaurant $index")
                }
            }
        }
    }
}



@Composable
fun CouponBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.freedeliverycoupon),
            contentDescription = "Coupon Background",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)), // Add corner shape if needed
            contentScale = ContentScale.Crop // Scale the image to fill the box
        )

        // Content overlaid on the background image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "You have 2x free delivery coupon!",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = { /* Handle click */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AA4F)),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(text = "Order Now")
            }
        }
    }
}

@Composable
fun RestaurantItem(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() } // Trigger the onClick lambda when clicked
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "4.1 ⭐️", style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
fun DeliveryHeader(address: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column {
            Text(text = "Deliver To", style = MaterialTheme.typography.labelSmall)
            Text(text = address, style = MaterialTheme.typography.titleMedium)  // Display dynamic address here
        }
    }
}




@Composable
fun RequestLocationPermission(
    fusedLocationClient: FusedLocationProviderClient,
    onPermissionGranted: () -> Unit = {},  // Callback if permission is granted
    onPermissionDenied: () -> Unit = {},   // Callback if permission is denied
    onLocationReceived: (Location?) -> Unit = {} // Callback when location is received
) {
    val context = LocalContext.current // Current context of the app
    var permissionGranted by remember { mutableStateOf(false) }  // State to track if permission is granted

    // Create a launcher to request location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle the result of the permission request
        permissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show()
            onPermissionGranted()  // Execute callback when granted

            // Fetch last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onLocationReceived(location)
                } else {
                    Log.d("LocationPermission", "Last known location is null")
                    onLocationReceived(null)
                }
            }


        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            onPermissionDenied()  // Execute callback when denied
        }
    }

    // LaunchedEffect block to run the permission request once
    LaunchedEffect(Unit) {
        when (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION  // Fine location permission
        )) {
            PackageManager.PERMISSION_GRANTED -> {
                // If permission is already granted, trigger granted callback
                permissionGranted = true
                onPermissionGranted()

                // Fetch last known location
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    onLocationReceived(location)
                }
            }
            else -> {
                // Otherwise, launch the permission request dialog
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}
