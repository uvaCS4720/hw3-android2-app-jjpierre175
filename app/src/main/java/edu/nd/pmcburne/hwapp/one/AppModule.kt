package edu.nd.pmcburne.hwapp.one
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient { // logs all HTTP requests and responses, helpful for api calls
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit { // retrofit instance used to make api calls
        return Retrofit.Builder()
            .baseUrl("https://ncaa-api.henrygd.me/") // ncaa basketball scoreboard api
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): BasketballApiService { // room database used for offline storage of game data
        return retrofit.create(BasketballApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BasketballDatabase { // gameDAO used to read and write game data
        return Room.databaseBuilder(
            context,
            BasketballDatabase::class.java,
            "basketball_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGameDao(db: BasketballDatabase): GameDAO = db.gameDao()
}