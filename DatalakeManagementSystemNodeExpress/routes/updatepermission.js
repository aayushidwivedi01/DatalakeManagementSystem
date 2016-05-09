var util = require("util");
var Models = require("../models/models");
var java = require("java");
var Doc = Models.Doc;
var status = 0;

function updatePermission(req, res) {
	
	if (req.session.user){
		var doc_id = req.body.filename;
		console.log("PERMISSION:" + req.body.scope);
		Doc.findOneAndUpdate({id:doc_id},{permission: req.body.scope}, function(err,doc){
			if(err){
				throw err;
			}
			try{
				java.callStaticMethodSync(
						"storage.DBWrapper",
						"setup", 
						"/home/cis550/db");

				var documentDA = java.newInstanceSync("storage.DocumentDA");
				var file_path = java.callMethodSync(
						documentDA,
						"fetch",
						doc_id).getPathSync();
				var document = java.newInstanceSync(
						"bean.Document", 
						doc_id,
						req.session.user,
						file_path,
						req.body.scope);
				java.callMethodSync(
						documentDA,
						"store",
						document);
				console.log("DOCUMENT PATH: " + file_path);
				console.log("Document has been saved!");
			} catch(exception){
				console.log("Error opening DB");
			}finally{
				java.callStaticMethodSync(
						"storage.DBWrapper",
						"close");
			}			
			console.log("Document has been saved!");
			//res.send("Permission on file " + doc_id + " changed to " + req.body.scope);
			Doc.find({username: req.session.user}, function(err, results){
				if (err){
					throw err;
				}else{
					res.render('viewfiles',{
						classes: results,
						});
				}
			});
			
		});
	}else{
		res.redirect('/');
	}
	
}
exports.do_work = 
	  function(req,res, next) {
		updatePermission(req, res);
	};