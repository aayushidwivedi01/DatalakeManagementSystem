var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var flash = require('connect-flash');
var session = require('express-session');
var bodyParser = require('body-parser');
var multiparty = require('connect-multiparty');
var routes = require('./routes/index');
var users = require('./routes/users');
var login = require('./routes/login');
var homepage = require('./routes/homepage');
var account = require('./routes/account');
var uploadfile = require('./routes/uploadfile');
var permissions = require('./routes/permissions');
var updatePermission = require('./routes/updatepermission');
//var java = require("java");
//java.classpath.push("test.jar");
var app = express();
app.use(cookieParser());
app.use(session({secret: 'shh1243',
				resave:false,
				saveUninitialized:true, 
				cookie: { maxAge: 60000 }}));
app.use(flash());
app.use(function(req, res, next){
    res.locals.success = req.flash('success');
    res.locals.errors = req.flash('error');
    next();
});
app.use(multiparty({}));
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
	extended : true
}));
app.use(express.static(path.join(__dirname, 'public')));


app.use('/', routes);
app.use('/users', users);
app.use('/verifynewaccount', account.do_work);
app.post('/login', login.do_work);
app.get('/logout', function(req,res){
	req.session.destroy(function(){
		res.redirect('/');
	});
});
app.get('/test', function(req, res, next) {
	res.render('test', {
		num: 4
	});
});
app.use('/homepage', homepage);
app.use('/uploadfile',uploadfile.do_work);
app.use('/permissions', permissions.do_work);
app.post('/changepermission', function(req,res){
	res.render('updatepermission',{
		title: 'DLMS', 
		file:req.body.filename,
	});
});
app.use('/updatepermission', updatePermission.do_work);

var server = app.listen(8081, function() {

	var host = server.address().address;
	var port = server.address().port;
	console.log("App listening at http://%s:%s", host, port);
//	 var user = java.newInstanceSync("bean.User", "ankit", "pass");
//	 user.toString(function (error,data)
//	     { 
//	       console.log("Returned data"+data);
//
//	                                        });
//	 java.callMethod(user, "toString", callback);

	 console.log("Afetr");
});


