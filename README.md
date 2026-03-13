[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/NYuLn2p4)

Jayden Pierre (ntk4nk)

This app shows NCAA Division I basketball scores using a public scoreboard API. The app allows users to view scores for both men's and women's college basketball on a selected date.

Game data is fetched from a remote API and stored locally in a room database. This allows the app to continue to show scores that had been downloaded even when device is not connected to internet.

Features 
- view NCAA Division I basketball scores
- toggle between men's and women's games
- select a date using date picker
- display
  - home and away team names
  - game status (scheduled, live, or final)
  - period and time remaining for live games
  - winner indicator for completed games
- pull-to-refresh or refresh button to update scores
- loading indicator during API requests
- offline mode using a room database
- automatic ui updates using kotlin flow

Architecture
- UI Layer
  - ScoresScreen
  - Displays games and user controls
- ViewModel
  - ScoresViewModel
  - Manages UI state and handles refresh logic
- Repository
  - BasketballRepository
  - Gets scores from the API
  - Saves and gets games from the local database
- Database
  - Room database (GameDatabase)
  - Stores game data for offline access
- Networking
  - Retrofit for APi calls
  - OkHttp for HTTP client
  - Gson for Json parsing

Offline mode: all scores downloaded from the API are stored in the local room database. if the device loses internet connection, the app will still display previously downloaded scores for the date