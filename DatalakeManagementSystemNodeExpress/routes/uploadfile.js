var util = require("util");
var fs = require("fs");
var Models = require("../models/models");
var User = Models.User;
var Doc = Models.Doc;
//var Owner = Models.Owner;

function savePermissions(req, res, status) {
	var code = 0;
	var file_path = "/home/cis550/bobby_tables/uploads/" +
	"" + req.session.user + "_"+req.files.dataitem.name;
	var doc_id = req.session.user + "_" + req.files.dataitem.name;
	var doc = new Doc({id: doc_id, username: req.session.user, path: file_path, permission: req.body.scope});
	doc.save(function(err){
		if(err){
			status(-1);
			console.log("File exists in DLMS");
			}
		else {
			console.log("Document has been saved!");
				status(0);
		}
	});
	
	
}


function uploadFile(req, res, next) {
	if (req.files) {
		console.log(util.inspect(req.files));
		if (req.files.dataitem.size === 0) {
		            return next(new Error("Please select a file!"));
		}
		fs.exists(req.files.dataitem.path, function(exists) {
			if(exists) {
				console.log("New file uploaded at - %s", req.files.dataitem.path);
				console.log("Saving file");
				var newPath = "/home/cis550/bobby_tables/uploads/" +
						"" + req.session.user + "_"+req.files.dataitem.name;
				
				fs.writeFile(newPath, req.files.dataitem.path, function (err) {
					if (err){
						console.log("Error saving the file");
						console.log(err);
					}
					savePermissions(req, res, function(status){
						if(status === -1){
							res.send("File already exists in DLMS");
						}else{
						res.send("File saved successfully!");
						}
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
