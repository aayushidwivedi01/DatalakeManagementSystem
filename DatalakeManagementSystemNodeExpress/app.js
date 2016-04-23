var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var session = require('express-session');
var bodyParser = require('body-parser');
var multiparty = require('connect-multiparty');
var routes = require('./routes/index');
var users = require('./routes/users');
var login = require('./routes/login');
var homepage = require('./routes/homepage');
var app = express();

app.use(cookieParser());
app.use(session({secret: 'shh1243',
				resave:false,
				saveUninitialized:true}));

app.use(multiparty({}));

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');


app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
	extended : true
}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', users);
app.use('/homepage', homepage);
app.post('/login', login.do_work);


app.get('/hello', function(req, res) {
	res.send('Hello World');
});

var server = app.listen(8081, function() {

	var host = server.address().address;
	var port = server.address().port;

	console.log("Example app listening at http://%s:%s", host, port);

});
