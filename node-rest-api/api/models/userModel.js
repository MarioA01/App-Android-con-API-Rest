const mongoose = require('mongoose');

const userSchema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    name: { type: String, required: true },
    email: {type: String, unique: true},
    salt: { type: String, required: true },
    date_time: { type: Date, required: true, default: Date.now },
    password: { type: String, required: true }
});

module.exports = mongoose.model('Users', userSchema);