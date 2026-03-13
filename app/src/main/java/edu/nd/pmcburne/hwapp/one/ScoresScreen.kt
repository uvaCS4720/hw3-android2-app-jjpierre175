package edu.nd.pmcburne.hwapp.one
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresScreen(viewModel: ScoresViewModel = hiltViewModel()) { // main UI screen for the app
    val uiState by viewModel.uiState.collectAsState() // collect UI state from the viewmodel
    var showDatePicker by remember { mutableStateOf(false) } // controls whether the date picker dialog is visible
    val datePickerState = rememberDatePickerState( // stores the currently selected date in the material date picker
        initialSelectedDateMillis = uiState.selectedDate
            .atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Basketball Scores") },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) { // button  to open the date picker dialog
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                    IconButton(onClick = { viewModel.refresh() }) { // button to manually refresh scores
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text( // displays the currently selected date
                text = uiState.selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row( // toggle between men's and women's games
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SegmentedButtonRow(
                    isMens = uiState.isMens,
                    onToggle = { viewModel.setGender(it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) { // show loading bar while scores are refreshed
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.error?.let { err ->
                Text( // error message if loading fails
                    text = "⚠ $err",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Games list with pull-to-refresh
            PullToRefreshBox( // wraps the main content so the user can pull downward to manually refresh
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.games.isEmpty() && !uiState.isLoading) { // empty state when no state
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.error != null) "No cached data available.\nConnect to the internet to load scores."
                            else "No games scheduled for this date.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                } else {
                    LazyColumn( // scrollable list of game cards
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.games, key = { it.id }) { game ->
                            GameCard(game = game, isMens = uiState.isMens)
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog( // date picker dialog for selecting different scoreboard date
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / 86_400_000)
                        viewModel.setDate(date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun SegmentedButtonRow(isMens: Boolean, onToggle: (Boolean) -> Unit) { // two buttons to switch between men's and women's games
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = isMens,
            onClick = { onToggle(true) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text("Men's")
        }
        SegmentedButton(
            selected = !isMens,
            onClick = { onToggle(false) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("Women's")
        }
    }
}

@Composable
fun GameCard(game: GameEntity, isMens: Boolean) { // displays a single game in a material card
    val status = game.statusName.lowercase() // ncaa api status values
    val isScheduled = status == "pre"
    val isInProgress = status == "live"
    val isFinal = status == "final"

    // determine period label
    val periodLabel: String = if (isInProgress && game.period != null) {
        if (isMens) {
            // men's: 2 halves
            when (game.period) {
                1 -> "1st Half"
                2 -> "2nd Half"
                else -> "OT ${game.period - 2}"
            }
        } else {
            // women's: 4 quarters
            when (game.period) {
                1 -> "1st Qtr"
                2 -> "2nd Qtr"
                3 -> "3rd Qtr"
                4 -> "4th Qtr"
                else -> "OT ${game.period - 4}"
            }
        }
    } else ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row( // top row shoes current game status
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    isFinal -> {
                        Text(
                            text = "FINAL",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    isInProgress -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface( // red dot shows the game is live
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Text(
                                text = if (periodLabel.isNotEmpty() && game.displayClock != null)
                                    "$periodLabel · ${game.displayClock}"
                                else game.statusShortDetail ?: "In Progress",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    isScheduled -> {
                        Text(
                            text = game.statusShortDetail ?: "Scheduled",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    else -> {
                        Text(
                            text = game.statusShortDetail ?: game.statusName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TeamScoreRow( // away team row
                label = "Away",
                teamName = game.awayTeamName,
                score = game.awayScore,
                isWinner = game.awayIsWinner == true && isFinal,
                showScore = isInProgress || isFinal
            )

            Spacer(modifier = Modifier.height(6.dp))

            TeamScoreRow( // home team row
                label = "Home",
                teamName = game.homeTeamName,
                score = game.homeScore,
                isWinner = game.homeIsWinner == true && isFinal,
                showScore = isInProgress || isFinal
            )
        }
    }
}

@Composable
fun TeamScoreRow(
    label: String,
    teamName: String,
    score: String?,
    isWinner: Boolean,
    showScore: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface( // home/away badge
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isWinner) { // trophy shows the team that won
                Text("🏆", style = MaterialTheme.typography.bodySmall)
            }

            Text( // team name
                text = teamName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.widthIn(max = 220.dp)
            )
        }

        if (showScore && score != null) {
            Text( // score
                text = score,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}
