const express = require('express');
const cookieParser = require('cookie-parser');
const path = require('path');

require('dotenv').config();
const mongoose = require('mongoose');

const app = express();

mongoose.connect(process.env.MONGO_URI ).then(() => {
  console.log("Connected to MongoDB");
}).catch((err) => {
  console.error("MongoDB connection error:", err);
});

app.use(cookieParser());

const cors = require('cors');

app.use(cors({
  origin: 'http://localhost:3000',
  credentials: true
}));


app.set('json spaces', 2);

const usersRoutes = require('./routes/userRoute');
const tokensRoutes = require('./routes/tokenRoute');
const labelsRoutes = require('./routes/labelRoute');
const mailsRoutes = require('./routes/mailRoute');

app.use(express.json({ limit: '10mb' }));
app.use('/api/users', usersRoutes);
app.use('/api/tokens', tokensRoutes);
app.use('/api/labels', labelsRoutes);
// app.use('/api/mails', mailsRoutes);
app.use('/profilePics', express.static(path.join(__dirname, 'profilePics')));

app.listen(4000, '0.0.0.0');
