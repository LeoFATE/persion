module.exports = {
  publicPath: '/',
  outputDir: '../src/main/resources/static',
  assetsDir: 'static',
  devServer: {
    host: 'localhost',
    port: 8081,
    hot: true,
    open: true,
    // Proxy API requests to Spring Boot backend to avoid CORS during development
    proxy: {
      '^/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    },
    historyApiFallback: true
  }
};
