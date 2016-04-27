var express = require('express');
var router = express.Router();
var Models = require("../models/models");
var User = Models.User;
var Doc = Models.Doc;
//var Owner = Models.Owner;
var docsPerPage = 10;
var pageNumber = 1;

function render_permissions(req,res, results){
	res.render('permissions',
			{classes: results});
}

function fetch_permissions(req, res){
//	Doc.findPaginated({}, function (err, result) {
//	    if (err) {throw err;}
//	    console.log(JSON.parse(result.documents));
//	}, docsPerPage, pageNumber);
	Doc.find({username: req.session.user}, function(err, results){
		if (err){
			throw err;
		}else{
//			for(var i = 0; i <results.length; i++){
//				console.log("Username:" + results[i]['username'] + "" +
//						"\tFilename:" + results[i]['id'] + "" +
//						"\tPermission:" + results[i]['permission']);
//			}
			render_permissions(req,res,results);
		}
	});
}


exports.do_work = function(req, res, next){
	fetch_permissions(req,res);
	};
	
