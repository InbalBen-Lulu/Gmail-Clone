const mongoose = require('mongoose');

const mailStatusSchema = new mongoose.Schema({
  userId: {
    type: String,
    required: true
  },
  mailId: {
    type: mongoose.Schema.Types.ObjectId,
    required: true,
    ref: 'Mail'
  },
  type: {
    type: String,
    enum: ['sent', 'received'],
    required: true
  },
  isDraft: {
    type: Boolean,
    default: false
  },
  isSpam: {
    type: Boolean,
    default: false
  },
  isStar: {
    type: Boolean,
    default: false
  },
  isRead: {
    type: Boolean,
    default: false
  },
  labels: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Label'
    }
  ]
});

mailStatusSchema.index({ userId: 1, mailId: 1 }, { unique: true });

module.exports = mongoose.model('MailStatus', mailStatusSchema);
