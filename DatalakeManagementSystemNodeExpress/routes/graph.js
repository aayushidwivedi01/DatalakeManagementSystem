var express = require('express');
var url = require('url');
var java = require('java');
var router = express.Router();
java.classpath.push("generateJSON.jar");

function get_graph(req, res, next) {
	var node = url.parse(req.url, true).query.node;
	var generateJSON = java.newInstanceSync(
			"searchengine.GenerateOutputJson");
	
	var results = java.callMethodSync(generateJSON, "getData", node);
	//var results = JSON.stringify(dataset);
	var test = "{this is a sample}";
	console.log("JSON: "+ results);
	console.log("JSONString: "+ JSON.stringify(results));
	res.render('graph',{
		dataset:JSON.stringify(results)
	});
}

exports.do_work = function(req, res, next){
	get_graph(req,res);
};
