module.exports = {
  mode: 'development',
  module: {
    rules: [
      {
        test: /\.[jt]s$/,
        enforce: 'post',
        exclude: /node_modules|cypress/,
        use: {
          loader: '@jsdevtools/coverage-istanbul-loader',
          options: { esModules: true }
        }
      }
    ]
  }
};
