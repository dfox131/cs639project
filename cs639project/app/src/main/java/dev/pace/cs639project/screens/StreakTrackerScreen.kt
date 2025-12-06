@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakTrackerScreen(
    onNavigateBack: () -> Unit // Add a callback for the back button
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Streak Tracker", // Title for the new screen
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    // Back button to return to the HomeScreen
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Use standard back arrow
                            contentDescription = "Go Back"
                        )
                    }
                },
                actions = {
                    // Retain the notification icon for consistency
                    IconButton(onClick = { /* Handle notification click */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding from the Scaffold
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF5F7FB)), // Use the same background color as HomeScreen
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Remove the redundant MomentumAppBar() call

            Spacer(Modifier.height(32.dp))

            // 2. Custom Badge
            StreakBadge(days = 2)

            Spacer(Modifier.height(32.dp))

            // 3. Weekly Streak Tracker (M, T, W, T, F, S, S)
            WeeklyStreakTracker(currentDay = 3)

            Spacer(Modifier.height(32.dp))

            // 4. Custom Calendar View
            StreakCalendar()
        }
    }
}