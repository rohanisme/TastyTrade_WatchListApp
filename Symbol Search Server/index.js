"use strict";

// require express and bodyParser
const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
// include websocket
const http = require("http");
// create express app
const app = express();

// define port to run express app
const port = 8080;

app.use(cors());
const server = http.createServer(app);

// use bodyParser middleware on express app
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


app.get("/", (req, res) => {
  res.send("Hello World");
});


// Import API route
var routes = require("./routes/appRoutes"); 
routes(app);



// Listen to server
server.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});