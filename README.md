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

You can run the application by importing the project in anroid studio and running it either to simulator or emulator.

#Symbol Search Server
You can the run application by running the following commands
- Open command prompt from the folder location
- run node index.js (Considering node js is installed in the system)
- The local server start running at http://localhost:8080/
- U can perform the search query using a browser or postman tool
- Eg . http://localhost:8080/search/AA
