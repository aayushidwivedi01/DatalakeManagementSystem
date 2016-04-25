var express = require('express');
var router = express.Router();

router.get('/', function(req, res, next) {
	if(req.session.user){
		res.render('homepage',
				{'title':"Welcome, "+ req.session.user + "!"})
	}
	else{
		res.send('You are not logged in to any account!');
	}
});

module.exports = router;
