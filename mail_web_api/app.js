const express = require('express');
const app = express();

app.set('json spaces', 2);

const usersRoutes = require('./routes/userRoute');
const tokensRoutes = require('./routes/tokenRoute');
const blackListRoutes = require('./routes/blackListRoute');
const labelsRoutes = require('./routes/labelRoute');
const mailsRoutes = require('./routes/mailRoute');

app.use(express.json());
app.use('/api/users', usersRoutes);
app.use('/api/tokens', tokensRoutes);
app.use('/api/blacklist', blackListRoutes);
app.use('/api/labels', labelsRoutes);
app.use('/api/mails', mailsRoutes);

app.listen(3000, '0.0.0.0');
