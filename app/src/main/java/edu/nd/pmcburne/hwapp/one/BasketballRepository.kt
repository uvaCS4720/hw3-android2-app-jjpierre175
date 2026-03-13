package edu.nd.pmcburne.hwapp.one
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BasketballRepository @Inject constructor(
    private val apiService: BasketballApiService,
    private val gameDAO: GameDAO,
    @param:ApplicationContext private val context: Context // check network connectivity
) {
    fun getGames(gender: String, date: LocalDate): Flow<List<GameEntity>> {
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return gameDAO.getGames(gender, dateStr) // return a flow of games for the specific gender and date
    }

    suspend fun refreshGames(gender: String, date: LocalDate): Result<Unit> {
        if (!isNetworkAvailable()) { // prevent api calls when the device is offline
            return Result.failure(Exception("No internet connection"))
        }

        return try {
            // format date for api endpoint
            val year = date.year.toString()
            val month = date.monthValue.toString().padStart(2, '0')
            val day = date.dayOfMonth.toString().padStart(2, '0')

            val response = apiService.getScoreboard(gender, year, month, day) // call the api
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

            val entities = response.games?.mapNotNull { wrapper -> // convert api models into room entities
                val game = wrapper.game ?: return@mapNotNull null
                val home = game.home ?: return@mapNotNull null
                val away = game.away ?: return@mapNotNull null

                GameEntity(
                    id = "${gender}_${game.gameID ?: return@mapNotNull null}",
                    gender = gender,
                    date = dateStr,
                    homeTeamName = home.names?.short ?: home.names?.full ?: "Unknown",
                    homeTeamAbbr = home.names?.char6 ?: "???",
                    awayTeamName = away.names?.short ?: away.names?.full ?: "Unknown",
                    awayNameAbbr = away.names?.char6 ?: "???",
                    homeScore = home.score,
                    awayScore = away.score,
                    statusName = game.gameState ?: "pre",
                    statusShortDetail = when (game.gameState) {
                        "pre" -> game.startTime
                        "final" -> "Final"
                        else -> game.currentPeriod
                    },
                    period = null,
                    displayClock = when (game.gameState) {
                        "live" -> game.contestClock
                        "final" -> "Final"
                        else -> null
                    },
                    homeIsWinner = home.winner,
                    awayIsWinner = away.winner
                )
            } ?: emptyList()
            gameDAO.upsertGames(entities) // save games to room database
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isNetworkAvailable(): Boolean { // checks for internet connection
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}