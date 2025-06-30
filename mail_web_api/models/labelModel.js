const mongoose = require('mongoose');

const labelSchema = new mongoose.Schema({
  userId: { type: String, required: true, index: true },
  name: { type: String, required: true },
  color: { type: String, default: '#808080' }
}, {
  toJSON: {
    virtuals: true,
    versionKey: false, 
    transform: function (doc, ret) {
      ret.id = ret._id.toString();
      delete ret._id;           
    }
  }
});

labelSchema.index({ userId: 1, name: 1 }, { unique: true });

module.exports = mongoose.model('Label', labelSchema);
