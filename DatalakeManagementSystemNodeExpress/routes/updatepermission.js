var util = require("util");
var Models = require("../models/models");
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
			console.log("Document has been saved!");
			//res.send("Permission on file " + doc_id + " changed to " + req.body.scope);
			Doc.find({username: req.session.user}, function(err, results){
				if (err){
					throw err;
				}else{
					res.render('permissions',{
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