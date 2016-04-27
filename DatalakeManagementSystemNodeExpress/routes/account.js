var express = require('express');
var router = express.Router();
var User = require("../models/models");

function verify_account(req, res){
	var name = req.body.username;
	var passwd = req.body.password;
	var passwdconfirm = req.body.passwordconfirm;
	var usr = new User({username: name, password: passwd});
	if (!name || !passwd || !passwdconfirm){
		//res.redirect()
		console.log("Incorrect Username/password");
		res.send("Username or password incorrect. %s", usr);
	} else if (passwd !== passwdconfirm){
		console.log("Passwords don't match");
		res.send("Passwords don't match %s", usr);
	} else {
		User.findOne({username: name}, function(err, checkUsr){
			if(err){
				console.log("Username already exists. Select a new username.");
				res.send("Username already exists. Select a new username. %s", checkUsr);
			}
			
			usr.save(function(err){
			if(err){
				console.log("Error adding new user");
				res.send("Username already exists. Select a new username. %s", checkUsr);
			}
			console.log("User has been saved!");
			session = req.session;
			session.user = name;
			res.render('homepage',
					{'title':"Welcome, "+ name + "!"})
			});
			
			
		});
		
	}
	
};


exports.do_work = 
	  function(req,res, next) {
		verify_account(req, res);
	};

