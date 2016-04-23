var express = require('express');
var router = express.Router();


function verify_account(req, res){
	var username = req.body.username;
	var password = req.body.password;
	
	if (!username || !password){
		res.send("Incorrect username or password");
	}else{
		//TO-DO: verify account details
		login_sucess(req, res, username);
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