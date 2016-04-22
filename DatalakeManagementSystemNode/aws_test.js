var express = require('express');
var app = express();
var aws = require('aws-sdk');
var fs = require('fs');
var bucket = 'dlms_documents';
var s3 = new aws.S3();
var http = require('http');

app.get('/', function (req, res) {
	   res.send('Hello World');
	});

var server = app.listen(8081, function () {

  var host = server.address().address;
  var port = server.address().port;

  console.log("Example app listening at http://%s:%s", host, port);

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
		console.log('uploaded file[' + fileName + '] to [' + remoteFilename
				+ '] as [' + metaData + ']');
		console.log(arguments);
	});
}
