var express = require('express');
var router = express.Router();
var Models = require("../models/models");
var User = Models.User;

function login_sucess(req,res, user){
	session = req.session;
	session.user = user.username;
	session.user_id = user._id;
	res.render('homepage',
			{'title':"Welcome, "+ session.user + "!"})
}

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
					login_sucess(req, res, checkUsr);
				}
				else{
					console.log("Password incorrect");
					res.send("Incorrect password");
				}
			}
		});
		
	}
}
	

	
exports.do_work = 
  function(req,res, next) {
	verify_account(req, res);
};

//module.exports = router;