const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/car-api': {
        target: "http://localhost:8085/",
        pathRewrite: {
          "^/car-api": ''
        }
      }
    }
  }
})