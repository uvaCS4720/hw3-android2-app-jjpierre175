package edu.nd.pmcburne.hwapp.one
import retrofit2.http.GET
import retrofit2.http.Path

interface BasketballApiService {
    @GET("scoreboard/basketball-{gender}/d1/{year}/{month}/{day}") // ncaa d1 basketball game scoreboard
    suspend fun getScoreboard(
        @Path("gender") gender: String, // men or women
        @Path("year") year: String, // 4 number year
        @Path("month") month: String, // 2 number month
        @Path("day") day: String // 2 number day
    ): ScoreboardResponse // return ScoreboardResponse object with list of games
}