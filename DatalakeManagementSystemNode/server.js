var http = require('http');
var fs = require('fs');
var url = require('url');

http.createServer(function handler(request, response) {
	//parse the request
	var pathname = url.parse(request.url).pathname;
	
	//print the requested pathname to console
	console.log("File requested:" + pathname);
	
	//read the file from the filesystem
	fs.readFile(pathname.substr(1), function (err, data){
		if (err){
			console.log(err);
			response.writeHead(404,{'Content-Type':'text/html'});
		} else{
			response.writeHead(200,{'Content-Type':'text/html'});
			response.write(data.toString());
		}
		
		//end of response
		response.end();
		
	});
}).listen(8081);
   
console.log('Server running at http://127.0.0.1:8081/');
