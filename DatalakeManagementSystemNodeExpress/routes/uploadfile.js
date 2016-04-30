var util = require("util");
var fs = require("fs");
var java = require("java");
var Models = require("../models/models");
var User = Models.User;
var Doc = Models.Doc;
var _dir = "/home/cis550/bobby_tables/uploads/";
var doc_id = null;
java.classpath.push("google-collections-1.0-rc2.jar");
java.classpath.push("extractor2.jar");


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
	console.log("Uploaded file has been linked");
}


function uploadFile(req, res, next) {
	if (req.files && req.files.dataitem) {
		console.log(util.inspect(req.files));
		if (req.files.dataitem.size === 0) {
		            return next(new Error("Please select a file!"));
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
							var extractor = java.newInstanceSync(
									"extractor.Extractor",
									localFilePath);
							java.callMethod(extractor, "extract", linkerResponse);
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

exports.do_work = 
	  function(req,res, next) {
		
		uploadFile(req, res);
	};
