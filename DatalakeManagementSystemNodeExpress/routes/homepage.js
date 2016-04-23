var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {
	if(req.session.user){
		res.send('Session Exists');
	}
	else{
		res.send('Invalid Session');
	}
});

module.exports = router;
