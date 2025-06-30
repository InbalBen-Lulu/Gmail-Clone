const mongoose = require('mongoose');

const labelSchema = new mongoose.Schema({
  userId: { type: String, required: true, index: true },
  name: { type: String, required: true },
  color: { type: String, default: '#808080' }
}, {
  toJSON: { virtuals: true }
});

labelSchema.virtual('id').get(function () {
  return this._id.toHexString();
});

labelSchema.index({ userId: 1, name: 1 }, { unique: true });

module.exports = mongoose.model('Label', labelSchema);
