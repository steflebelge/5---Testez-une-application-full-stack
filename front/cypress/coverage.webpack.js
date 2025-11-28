module.exports = {
  mode: "development",
  resolve: {
    extensions: [".ts", ".js"]
  },
  module: {
    rules: [
      {
        test: /\.[jt]s$/,
        enforce: "post",
        exclude: [
          /node_modules/,
          /cypress/,
          /\.spec\.ts$/,
          /main\.ts$/,
          /polyfills\.ts$/,
          /environment\./
        ],
        use: {
          loader: "@jsdevtools/coverage-istanbul-loader",
          options: {
            esModules: true
          }
        }
      }
    ]
  }
};
