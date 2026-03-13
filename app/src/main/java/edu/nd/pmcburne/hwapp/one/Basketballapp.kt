package edu.nd.pmcburne.hwapp.one
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // tells hilt to generate the dependency
class BasketballApp : Application() // custom application class for the app
