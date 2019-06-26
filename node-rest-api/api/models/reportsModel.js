const mongoose = require('mongoose');

const reportsSchema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    title: { type: String, required: true },
    descripcion: { type: String, required: true },
    date_time: { type: Date, required: true, default: Date.now },
    productImage: { type: String, required: true }
});

module.exports = mongoose.model('Reports', reportsSchema);