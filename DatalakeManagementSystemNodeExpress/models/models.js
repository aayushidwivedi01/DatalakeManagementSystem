var mongoose = require("mongoose");
var mongoosePages = require('mongoose-pages');
mongoose.connect('mongodb://dlms_webapp:webapp@ds013971.mlab.com:13971/webappdb');
var db = mongoose.connection;
db.on("error", console.error.bind(console, "connection error"));
db.once("open", function(callback) {
	console.log("Mongo Connection succeeded.");
});
var Schema = mongoose.Schema;

var userSchema = new Schema({
	username: { type: String, required: true, index: { unique: true } },
    password: { type: String, required: true }
});

var User = mongoose.model("User", userSchema);

var docSchema = new Schema({
	id: { type: String, required: true, index: { unique: true }},
    path: { type: String, required: true },
    permission: { type: String, required: true }
});

mongoosePages.skip(docSchema);

var Doc = mongoose.model("Doc", docSchema);


var ownerSchema = new Schema({
	username: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    document_id: { type: mongoose.Schema.Types.ObjectId, ref: "Doc", required: true }
});
ownerSchema.index({"username": 1, "document_id": 1}, { unique: true });

var Owner = mongoose.model("Owner", ownerSchema);

module.exports = {
	    User: User,
	    Doc: Doc,
	    Owner: Owner
	};