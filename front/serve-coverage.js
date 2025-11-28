const express = require("express");
const path = require("path");

const app = express();
const distPath = path.join(__dirname, "dist/yoga");

app.use(express.static(distPath));

app.get("*", (req, res) => {
  res.sendFile(path.join(distPath, "index.html"));
});

const port = 4200;
app.listen(port, () => {
  console.log(`Coverage build served on http://localhost:${port}`);
});
