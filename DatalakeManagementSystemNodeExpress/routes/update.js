var util = require("util");
var Models = require("../models/models");
var Doc = Models.Doc;

function updatePermission(req, res) {
	if (req.session.user){
		var doc_id = req.body.filename;
		console.log("PERMISSION:" + req.body.scope);
		Doc.findOneAndUpdate({id:doc_id},{permission: req.body.scope}, function(err,doc){
			if(err){
				throw err;
			}
			console.log("Document has been saved!");
			res.send("Permission on file " + doc_id + " changed to " + req.body.scope);
		});
//		Doc.find({id:doc_id},function(err, doc){
//			if(err){
//				res.send("Error updating the permission");
//				throw err;
//				}
//			else {
//				doc.permission = req.body.scope;
//				doc.save(function(err){
//				console.log("Document has been saved!");
//				res.redirect("/changepermission");
//				});
//			}
//		});
	}else{
		res.redirect('/');
	}
	
}
exports.do_work = 
	  function(req,res, next) {
		updatePermission(req, res);
	};