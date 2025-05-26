const express = require('express');
const app = express();
const usersRoutes = require('./routes/userRoute');
const tokensRoutes = require('./routes/tokenRoute');
const blackListRoutes = require('./routes/blackListRoute');

app.use(express.json());
app.use('/api/users', usersRoutes);
app.use('/api/tokens', tokensRoutes);
app.use('/api/blacklist', blackListRoutes);

app.listen(3000, '0.0.0.0');
