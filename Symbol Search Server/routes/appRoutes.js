'use strict';

module.exports = function(app) {
    var searchController = require('../controller/searchController');
    
    app.route("/search/:id")
    .get(searchController.searchSymbol)
}