var util = require("util");
var java = require("java");
java.classpath.push("commons-lang3-3.1.jar");
java.classpath.push("commons-io.jar");
java.classpath.push("google-collections-1.0-rc2.jar");
java.classpath.push("dlms.jar");
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
	var graphResults = "";
	var len = results.sizeSync();
	var weblinks = "";
	for(var i = 0; i < len; i++){
		var nodes = results.getSync(i);
		console.log("nodes:" + nodes.toStringSync());
		var num_nodes = nodes.sizeSync();
		for (var j = 0; j < num_nodes; j++){
			var node = nodes.getSync(j).substring(nodes.getSync(j).lastIndexOf("/") + 1);
			weblinks = weblinks.concat(node);
			if (j < num_nodes - 1){
				weblinks = weblinks.concat("->");
			}
			if (j < num_nodes - 1){
				graphResults = graphResults.concat(nodes.getSync(j), ",");
			}else {
				graphResults = graphResults.concat(nodes.getSync(j));
			}
		}
		weblinks = weblinks.concat(",");
		if (i < len - 1){
			graphResults = graphResults.concat(" ");
		}
		
		
	}
	console.log("web links: " + weblinks);
	console.log("graph strings: " + graphResults);
	res.render('results', {
		result: weblinks,
		nodes: graphResults
	});
}

module.exports = {
		do_work:function(req,res, next) {
			search(req, res);
		}};
	  
