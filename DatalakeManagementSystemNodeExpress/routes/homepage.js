var express = require('express');
var router = express.Router();

function get_homepage(req, res, next) {
	if(req.session.user){
		res.render('homepage',{
			'title':"Welcome, "+ req.session.user + "!"});
	}
	else{
		res.send('You are not logged in to any account!');
	}
}

exports.do_work = function(req, res, next){
	get_homepage(req,res);
};
