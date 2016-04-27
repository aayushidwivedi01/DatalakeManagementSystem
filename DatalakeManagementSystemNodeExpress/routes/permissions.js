var express = require('express');
var router = express.Router();
var Models = require("../models/models");
var User = Models.User;
var Doc = Models.Doc;
var Owner = Models.Owner;
var docsPerPage = 10;
var pageNumber = 1;

function fetch_permissions(req, res){
	Doc.findPaginated({}, function (err, result) {
	    if (err) {throw err;}
	    console.log(JSON.parse(result.documents));
	}, docsPerPage, pageNumber);
}
exports.do_work = function(req, res, next){
	fetch_permissions(req,res);
	};
	
