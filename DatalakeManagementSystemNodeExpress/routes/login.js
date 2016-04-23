var express = require('express');
var router = express.Router();
var User = require("../models/models");

function verify_account(req, res){
	var name = req.body.username;
	var passwd = req.body.password;
	
	if (!name || !passwd){
		res.send("Incorrect username or password");
	}else{
		//verify account details
		User.findOne({username: name}, function(err, checkUsr){
			if(err){
				console.log("User not found");
				res.send("Username incorrect.");
			}
			else{
				if (checkUsr.password === passwd){
					login_sucess(req, res, name);
				}
				else{
					console.log("Password incorrect");
					res.send("Incorrect password");
				}
			}
		});
		
	}
};
	
function login_sucess(req,res, username){
	session = req.session;
	session.user = username;
	res.render('homepage',
			{'title':"Welcome, "+ username + "!"})
};
	
exports.do_work = 
  function(req,res, next) {
	verify_account(req, res);
};

//module.exports = router;