const express = require('express');
const app = express();
// const articleRoutes = require('./routes/articles');

app.use(express.json());
// app.use('/articles', articleRoutes);

app.listen(3000);