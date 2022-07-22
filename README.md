# TastyTrade_WatchListApp

Folder Structure
- Android App contains the watchlist mobile application built using android studio and java
- Symbol Search Server contains the backend code using nodejs 

#Android Application
The watch list application is built using java based on the requirements given.
Complete code base is in java
For saving the data locally i have used shared preferences
There is one activity called Main Activity and three fragments called Watchlist, StockDetails and AddWatchlist
The rest api services are implemented using retrofit services
The graph is plotted using a open source dependency called Hello Charts - 'com.github.lecho:hellocharts-library:1.5.8@aar'

Running the application 
You can run the Android app on a simulator or emulator device by simply building it in the most recent version of Android Studio.
- A floating add/plus button on the screen will allow you to add a watchlist.
- By typing in the stock symbol and tapping the search result, you can add stocks to your watchlist.
- You can edit a watchlist by long press on the watchlist name on watchlist screen.You then have the option to change or delete your watchlist.
- You can delete a watchlist by swipping left on the edit watchlist screen. And by updating the watchlist.

You can run the application by importing the project in anroid studio and running it either to simulator or emulator.

#Symbol Search Server
You can the run application by running the following commands
- Open command prompt from the folder location
- run node index.js (Considering node js is installed in the system)
- The local server start running at http://localhost:8080/
- U can perform the search query using a browser or postman tool
- Eg . http://localhost:8080/search/AA
