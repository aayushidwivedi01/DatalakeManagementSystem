var express = require('express');
var router = express.Router();

function verify_account(req, res){
	var username = req.body.username;
	var password = req.body.password;
	var passwordconfirm = req.body.passwordconfirm;
	
	if (!username || !password || password != passwordconfirm){
		res.send("Incorrect username or password");
	}else{
		//TO-DO: verify account details
		login_sucess(req, res, username);
	}
};

exports.do_work = 
	  function(req,res, next) {
		verify_account(req, res);
	};

