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
java.classpath.push("jackson-annotations-2.7.3.jar");
java.classpath.push("jackson-core-2.6.6.jar");
java.classpath.push("jackson-databind-2.6.6.jar");
java.classpath.push("jackson-mapper-asl-1.9.13.jar");
java.classpath.push("jackson-mapper-lgpl-1.9.13.jar");
java.classpath.push("jaxp-api.jar");
java.classpath.push("je-4.0.92.jar");
java.classpath.push("tika-app-1.12-SNAPSHOT.jar");
java.classpath.push("extractor.jar");
java.classpath.push("dlms.jar");


function saveDocument(req, res, saveStatus) {
	var code = 0;
	var file_path = _dir + req.session.user + "_"+req.files.dataitem.name;
	doc_id = req.session.user + "_" + req.files.dataitem.name;
	var doc = new Doc({id: doc_id, username: req.session.user, path: file_path, permission: req.body.scope});
	
	doc.save(function(err){
		if(err){
			saveStatus(-1);
			console.log("File exists in DLMS");
			}
		else {
			try{
				java.callStaticMethodSync(
						"storage.DBWrapper",
						"setup", 
						"/home/cis550/db");
				var document = java.newInstanceSync(
						"bean.Document", 
						doc_id,
						req.session.user,
						file_path,
						req.body.scope);
				var documentDA = java.newInstanceSync("storage.DocumentDA");
				java.callMethodSync(
						documentDA,
						"store",
						document);
				saveStatus(1);
				console.log("Document has been saved!");
			} catch(exception){
				console.log("Error opening DB");
				saveStatus(-1);
			}finally{
				java.callStaticMethodSync(
						"storage.DBWrapper",
						"close");
			}			
			
		}
	});
	
	
}

function linkerResponse(err, data){
	console.log(data);
	console.log("Uploaded file has been linked");
	if(data === 1){
		status = 'idle';
	}else{
		status = 'error';
	}
}

function copyFile(target, source, writeStatus) {
	var rd = fs.createReadStream(source);
	rd.on("error", function(err) {
		writeStatus(1);
	 });
	var wr = fs.createWriteStream(target);
	wr.on("error", function(err) {
		writeStatus(1);
	});
	wr.on("close", function(ex) {
		writeStatus();
	});
	rd.pipe(wr);
	console.log("Done copying");
}

function uploadFile(req, res, next) {
	if(!req.session.user){
		res.redirect("/");
	}
	if (req.files && req.files.dataitem) {
		console.log(util.inspect(req.files));
		if (req.files.dataitem.size === 0) {
			res.send("Please select a file!");
		}
		fs.exists(req.files.dataitem.path, function(exists) {
			if(exists) {
				console.log("New file uploaded at - %s", req.files.dataitem.path);
				console.log("Saving file");
				
				var localFilePath = _dir + req.session.user + "_"+req.files.dataitem.name;
				copyFile(localFilePath, req.files.dataitem.path, function (err) {
					if (err){
						console.log("Error saving the file");
						console.log(err);
					}
					saveDocument(req, res, function(saveStatus){
						if(saveStatus===1){
							saveStatus = 'extracting';
							console.log("Path:" + localFilePath);
							var e = java.newInstanceSync(
									"extractor.Extractor", 
									localFilePath
									);
							java.callMethod(e, "extract", linkerResponse);
							console.log("Excecuting extractor");
							if(req.session.user){
								res.redirect('/viewfiles');
							}
							else{
								res.redirect('/');
							}
						}else{
							res.render('upload', {
									title : 'DLMS',
									upload_error: -1
								});
						}
						
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
	  
