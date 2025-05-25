const express = require('express');
const app = express();
const usersRoutes = require('./routes/userRoute');

app.use(express.json());
app.use('/api/users', usersRoutes);

app.listen(3000);