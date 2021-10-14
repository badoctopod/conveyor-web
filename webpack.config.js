const MomentLocalesPlugin = require('moment-locales-webpack-plugin');

module.exports = {
  mode: 'production',
  entry: './src/js/index.js',
  output: {
    filename: 'index.bundle.js'
  },
  plugins: [
    new MomentLocalesPlugin({
      localesToKeep: ['ru'],
    }),
  ],
};
