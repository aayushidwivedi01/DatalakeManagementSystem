var express = require('express');
var router = express.Router();
var util = require("util");
var fs = require("fs");
var aws = require('aws-sdk');
var credentials = new aws.SharedIniFileCredentials({profile: 'default'});
aws.config.credentials = credentials;
var bucket = 'dlms-documents';
var s3 = new aws.S3();
var http = require('http');
var Models = require("../models/models");
var User = Models.User;
var Doc = Models.Doc;
var Owner = Models.Owner;
var uploadfile = require('./uploadfile');


/* GET home page. */
router.get('/', function(req, res, next) {
	res.render('index', {
		title : 'DLMS'
	});
});

router.get('/index', function(req, res, next) {
	res.render('index', {
		title : 'DLMS'
	});
});

router.post('/homepage', function(req, res, next) {
	res.render('homepage', {
		title : 'DLMS'
	});
});

router.get('/createaccount', function(req, res, next) {
	res.render('createaccount', {
		title : 'DLMS'
	});
});

router.get('/upload', function(req, res, next) {
	res.render('upload', {
		title : 'DLMS',
		upload_error: 0
	});
});

router.get('/status', function(req, res, next) {
	res.render('status', {
		title : 'DLMS',
		status: uploadfile.status
			});
});


function getContentTypeByFile(fileName) {
	var rc = 'application/octet-stream';
	var fn = fileName.toLowerCase();

	if (fn.indexOf('.html') >= 0) {
		rc = 'text/html';
	} else if (fn.indexOf('.css') >= 0) {
		rc = 'text/css';
	} else if (fn.indexOf('.json') >= 0) {
		rc = 'application/json';
	} else if (fn.indexOf('.js') >= 0) {
		rc = 'application/x-javascript';
	} else if (fn.indexOf('.png') >= 0) {
		rc = 'image/png';
	} else if (fn.indexOf('.jpg') >= 0) {
		rc = 'image/jpg';
	}
	return rc;
}
module.exports = router;