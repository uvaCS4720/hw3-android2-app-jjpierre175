package edu.nd.pmcburne.hwapp.one

import com.google.gson.annotations.SerializedName

data class ScoreboardResponse( // top level response object returned by the API
    val games: List<GameWrapper>? = null
)

data class GameWrapper( // each item in the "games" list wraps a single game object
    val game: ApiGame? = null
)

data class ApiGame(
    val gameID: String,
    val away: ApiTeamSide,
    val home: ApiTeamSide,
    val gameState: String,       // pre, live, final
    val startTime: String?,      // upcoming
    val currentPeriod: String?,  // halves, quarters, etc
    val contestClock: String?,   // time remaining
    val finalMessage: String?    // often "FINAL"
)

data class ApiTeamSide(
    val score: String?, // current score
    val winner: Boolean?, // true if this team won this game
    val names: ApiNames // team name variants
)

data class ApiNames(
    val short: String?,
    val char6: String?,
    val full: String?
)
