package edu.nd.pmcburne.hwapp.one
import androidx.room.*
import kotlinx.coroutines.flow.Flow
//entity
@Entity(tableName = "games")
data class GameEntity( // room entity represents a single basketball game
    @PrimaryKey val id: String,
    val gender: String,
    val date: String,
    val homeTeamName: String,
    val homeTeamAbbr: String,
    val awayTeamName: String,
    val awayNameAbbr: String,
    val homeScore: String?,
    val awayScore: String?,
    val statusName: String,
    val statusShortDetail: String?,
    val period: Int?,
    val displayClock: String?,
    val homeIsWinner: Boolean?,
    val awayIsWinner: Boolean?
)

// DAO
@Dao
interface GameDAO {
    @Query("SELECT * FROM games WHERE gender = :gender AND date = :date ORDER BY id ASC") // returns a flow of games for specific gender and date
    fun getGames(gender: String,date: String): Flow<List<GameEntity>>

    @Upsert
    suspend fun upsertGames(games: List<GameEntity>) // inserts or updates game in db

    @Query("DELETE FROM games WHERE gender = :gender AND date = :date") // deletes game
    suspend fun deleteGamesForDate(gender: String, date: String)
}

// Database
@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
abstract class BasketballDatabase : RoomDatabase() { // main room database for the app, access to the gameDAO used to store and get scores
    abstract fun gameDao(): GameDAO
}