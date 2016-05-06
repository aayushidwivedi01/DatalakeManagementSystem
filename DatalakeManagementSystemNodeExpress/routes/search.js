var util = require("util");
var java = require("java");
java.classpath.push("google-collections-1.0-rc2.jar");
java.classpath.push("search2.jar");
var url = require('url');

function searchResponse(err, data){
	console.log(data);
	console.log("Uploaded file has been linked");
}
function search(req,res, err){
	var url_parts = url.parse(req.url, true);
	var request = url_parts.query;
	console.log(request);
	var searchEngine = java.newInstanceSync(
			"searchengine.SearchEngine",
			request.query, 
			req.session.user);
	java.callMethodSync(searchEngine, "search", 
			searchResponse);

	res.render('results', {
		result: request.query
	});
}

module.exports = {
		do_work:function(req,res, next) {
			search(req, res);
		}};
	  
