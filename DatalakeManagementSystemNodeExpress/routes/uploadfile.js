var util = require("util");
var fs = require("fs");
var java = require("java");
var Models = require("../models/models");
var User = Models.User;
var Doc = Models.Doc;
var _dir = "/home/cis550/bobby_tables/uploads/";
var doc_id = null;
var status = 'idle';
java.classpath.push("google-collections-1.0-rc2.jar");
java.classpath.push("tika-app-1.12-SNAPSHOT.jar");
java.classpath.push("extractor.jar");


function savePermissions(req, res, status) {
	var code = 0;
	var file_path = _dir + req.session.user + "_"+req.files.dataitem.name;
	doc_id = req.session.user + "_" + req.files.dataitem.name;
	var doc = new Doc({id: doc_id, username: req.session.user, path: file_path, permission: req.body.scope});
	doc.save(function(err){
		if(err){
			status(-1);
			console.log("File exists in DLMS");
			}
		else {
			console.log("Document has been saved!");
				status(1);
		}
	});
	
	
}

function linkerResponse(err, data){
	console.log(data);
	console.log("Uploaded file has been linked");
	status = 'idle';

}


function uploadFile(req, res, next) {
	if (req.files && req.files.dataitem) {
		console.log(util.inspect(req.files));
		if (req.files.dataitem.size === 0) {
		           res.send("Empty file uploaded!");
		}
		fs.exists(req.files.dataitem.path, function(exists) {
			if(exists) {
				console.log("New file uploaded at - %s", req.files.dataitem.path);
				console.log("Saving file");
				
				var localFilePath = _dir + req.session.user + "_"+req.files.dataitem.name;
				
				fs.writeFile(localFilePath, req.files.dataitem.path, function (err) {
					if (err){
						console.log("Error saving the file");
						console.log(err);
					}
					savePermissions(req, res, function(status){
						if(status===1){
							status = 'extracting';
							console.log("Path:" + localFilePath);
							var e = java.newInstanceSync(
									"extractor.Extractor", 
									localFilePath
									);
							java.callMethod(e, "extract", linkerResponse);
						}
						res.render('upload', {
							title : 'DLMS',
							upload_error : status
						});
					});
				  });
				
			} else {
				res.send("Invalid Request!");
			}
		});
	}
}

module.exports = {
		do_work:function(req,res, next) {
			
			uploadFile(req, res);
		},
		status:status};
	  
