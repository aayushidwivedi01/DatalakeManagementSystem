var express = require('express');
var url = require('url');
var router = express.Router();

function get_graph(req, res, next) {
	var node = url.parse(req.url, true).query.node;
	res.send(node);
}

exports.do_work = function(req, res, next){
	get_graph(req,res);
};
