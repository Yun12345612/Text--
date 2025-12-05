const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  runtimeCompiler: true,
  devServer: {
    port: 8083,
    proxy: {
      '/api': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        logLevel: 'debug'
      }
    },
    // 额外配置：确保热更新 WebSocket 连接正常
    client: {
      webSocketURL: 'ws://localhost:8084/ws',
    },
  }
})