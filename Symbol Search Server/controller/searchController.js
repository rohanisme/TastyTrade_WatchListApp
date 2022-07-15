const CSVToJSON = require('csvtojson');

exports.searchSymbol = (req, res) => {

CSVToJSON().fromFile('./data/stocks_data.csv')
    .then(users => {
        var filtered = users.filter(a => a.Symbol.startsWith(req.params.id));
        console.log("Filtered", filtered)

        if(filtered.length == 0) return res.status(404).send("Result Not Found")

        return res.send(filtered)
   
    }).catch(err => {
        console.log(err);
    });
};