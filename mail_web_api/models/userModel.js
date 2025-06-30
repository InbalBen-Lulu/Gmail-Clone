const mongoose = require('mongoose');


const userSchema = new mongoose.Schema({
  userId: {
    type: String,
    required: true,
    unique: true,
    lowercase: true,
    trim: true
  },
  name: {
    type: String,
    required: true,
    trim: true
  },
  password: {
    type: String,
    required: true,
    minlength: [8, 'Password must be at least 8 characters']
  },
  gender: {
    type: String,
    required: true
  },
  birthDate: {
    type: Date,
    required: true
  },
  profileImage: {
    type: String
  },
  hasCustomImage: {
    type: Boolean,
    default: false
  }
});

userSchema.virtual('id').get(function () {
  return this._id.toHexString();
});

module.exports = mongoose.model('User', userSchema);
