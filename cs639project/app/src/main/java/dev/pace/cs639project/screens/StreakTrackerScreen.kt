@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakTrackerScreen(
    onNavigateBack: () -> Unit
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                actions = {
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
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF5F7FB)), 
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