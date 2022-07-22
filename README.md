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
Just Build the android app in the latest android studio and you can run it on simulator or emulator device
- You can a add a watchlist by clicking on the add/plus floating button on screen
- You can add stocks to watchlist by search the stock symbol and taping on the search result
- You can edit a watchlist by long press on the watchlist name on watchlist screen. This takes you to an edit or delete watchlist screen.
- You can delete a watchlist by swipping left on the edit watchlist screen. And by updating the watchlist.

You can run the application by importing the project in anroid studio and running it either to simulator or emulator.

#Symbol Search Server
You can the run application by running the following commands
- Open command prompt from the folder location
- run node index.js (Considering node js is installed in the system)
- The local server start running at http://localhost:8080/
- U can perform the search query using a browser or postman tool
- Eg . http://localhost:8080/search/AA
