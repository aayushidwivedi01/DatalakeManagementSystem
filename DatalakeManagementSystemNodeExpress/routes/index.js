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
var User = require("../models/models");

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

router.get('/homepage', function(req, res, next) {
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
		title : 'DLMS'
	});
});


router.post("/dbtest", function(req, res, next) {
	var usr = new User({username: "ankit", password: "mishra2014"});
	usr.save(function(err){
		if(err){
			console.error(err);
		}
		console.log("User has been saved!");
	});
	User.findOne({username: "ankit"}, function(err, usr){
		if(err){
			console.error(err);
		}
		res.send("Got User %s", usr);
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

function uploadFile(remoteFilename, fileName) {
	var fileBuffer = fs.readFileSync(fileName);
	var metaData = getContentTypeByFile(fileName);

	s3.putObject({
		ACL : 'public-read',
		Bucket : bucket,
		Key : remoteFilename,
		Body : fileBuffer,
		ContentType : metaData
	}, function(error, response) {
		console.log('uploaded file[' + fileName + '] to [' + remoteFilename + '] as [' + metaData + ']');
		console.log(arguments);
	});
}

router.post('/uploadfile', function(req, res, next) {
	if (req.files) {
		console.log(util.inspect(req.files));
		if (req.files.dataitem.size === 0) {
		            return next(new Error("Hey, first would you select a file?"));
		}
		fs.exists(req.files.dataitem.path, function(exists) {
			if(exists) {
				console.log("New file uploaded at - %s", req.files.dataitem.path);
				console.log("Uploading fileto S3");
				uploadFile("temp", req.files.dataitem.path);
				res.send("Got your file!");
			} else {
				res.send("Well, there is no magic for those who don’t believe in it!");
			}
		});
	}
});

module.exports = router;
