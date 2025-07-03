const mongoose = require('mongoose');

const mailSchema = new mongoose.Schema({
  from: {
    type: String,
    required: true
  },
  to: [
    {
      type: String
    }
  ],
  subject: {
    type: String
  },
  body: {
    type: String
  },
  sentAt: {
    type: Date
  }
}, {
  toJSON: { virtuals: true }
});

mailSchema.virtual('id').get(function () {
  return this._id.toHexString();
});

module.exports = mongoose.model('Mail', mailSchema);
