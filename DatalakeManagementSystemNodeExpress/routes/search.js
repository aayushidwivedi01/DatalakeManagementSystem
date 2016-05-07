var util = require("util");
var java = require("java");
java.classpath.push("commons-lang3-3.1.jar");
java.classpath.push("commons-io.jar");
java.classpath.push("google-collections-1.0-rc2.jar");
java.classpath.push("searchengine.jar");
var url = require('url');

function search(req,res, err){
	var url_parts = url.parse(req.url, true);
	var request = url_parts.query;
	console.log(request);
	var searchEngine = java.newInstanceSync(
			"searchengine.SearchEngine",
			request.query, 
			req.session.user);
	var results = java.callMethodSync(searchEngine, "search");
	//results.addSync("item1");
	console.log(results.toStringSync()); 
	res.render('results', {
		result: request.query
	});
}

module.exports = {
		do_work:function(req,res, next) {
			search(req, res);
		}};
	  
