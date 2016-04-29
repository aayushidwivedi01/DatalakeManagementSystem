
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

router.post('/upload', function(req, res, next) {
	res.render('upload', {
		title : 'DLMS'
	});
});

router.post('/searchresults', function(req, res, next) {
	res.render('searchresults', {
		title : 'DLMS'
	});
});


//function savePermissions(req, res, next) {
//	User.findOne({username: "ankit"}, function(err, usr){
//		if(err){
//			console.error(err);
//		}
//		console.log(usr);
//		var doc = new Doc({id: "1234", path: "/x/s/w/1234", permission: "public"});
//		doc.save(function(err){
//			if(err){
//				console.error(err);
//			}
//			else {
//				console.log("Document has been saved!");
//				var owner = new Owner({username: usr._id, document_id: doc._id});
//				owner.save(function(err){
//					if(err){
//						console.error(err);
//					}
//					console.log("Ownership has been saved!");
//				});
//			}
//		});
//	});
//};
//
//
//router.post('/uploadfile', function(req, res, next) {
//	if (req.files) {
//		console.log(util.inspect(req.files));
//		if (req.files.dataitem.size === 0) {
//		            return next(new Error("Please select a file!"));
//		}
//		fs.exists(req.files.dataitem.path, function(exists) {
//			if(exists) {
//				console.log("New file uploaded at - %s", req.files.dataitem.path);
//				console.log("Saving file");
//				var newPath = "/home/cis550/bobby_tables/uploads/" +
//						"" + req.session.user + "_"+req.files.dataitem.name;
//				
//				fs.writeFile(newPath, req.files.dataitem.path, function (err) {
//					if (err){
//						console.log("Error saving the file");
//						console.log(err);
//					}
//					res.send("Got your file!"  );
//				  });
//				
//			} else {
//				res.send("Invalid Request!");
//			}
//		});
//	}
//});

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
//function uploadFile(remoteFilename, fileName) {
//var fileBuffer = fs.readFileSync(fileName);
//var metaData = getContentTypeByFile(fileName);
//s3.putObject({
//	ACL : 'public-read',
//	Bucket : bucket,
//	Key : remoteFilename,
//	Body : fileBuffer,
//	ContentType : metaData
//}, function(error, response) {
//	console.log('uploaded file[' + fileName + '] to [' + remoteFilename + '] as [' + metaData + ']');
//	console.log(arguments);
//});
//}

module.exports = router;
