var aws = require('aws-sdk');
var fs = require('fs');
var bucket = 'dlms_documents';
var s3 = new aws.S3();
var http = require('http');

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

var url = require('url');

http.createServer(function handler(request, response) {
	// parse the request
	var pathname = url.parse(request.url).pathname;

	// print the requested pathname to console
	console.log("File requested:" + pathname);

	if (url === '/uploadfile') {
		console.log(req);
	}
	else {
		// read the file from the filesystem
		fs.readFile(pathname.substr(1), function(err, data) {
			if (err) {
				console.log(err);
				response.writeHead(404, {
					'Content-Type' : 'text/html'
				});
			} else {
				response.writeHead(200, {
					'Content-Type' : 'text/html'
				});
				response.write(data.toString());
			}
	}

		// end of response
		response.end();

	});
}).listen(8080);

console.log('Server running at http://127.0.0.1:8080/');
