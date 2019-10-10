/**
 * Config responsible for building Mobile End-to-End test files (bundled into `test/dist/`):
 *  - mobile.entry.js
 *  - helpers.entry.js
 */
const path = require('path');
const webpack = require('webpack');
const configFactory = require('./test-e2e');
const JasmineHtml = require('./plugin/jasmine-html');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports.create = function create(envArgs) {
  const config = configFactory.create(envArgs);

  config.forEach(function(c) {

    // Remove all 'JasmineHtml' instances
    c.plugins = c.plugins.filter(function(plugin) {
      return !(plugin instanceof HtmlWebpackPlugin);
    });

    c.plugins.push(
      new JasmineHtml({
        filename: path.resolve(__dirname, '../test/MobileRunner.html'),
        baseJasminePath: '../',
        externalCssFiles: [
          'lib/normalize.css',
          '../dist/handsontable.css',
          'helpers/common.css',
        ],
        externalJsFiles: [
          'lib/jquery.min.js',
          'lib/jquery.simulate.js',
          'lib/lodash.underscore.js',
          'lib/backbone.js',
          '../node_modules/numbro/dist/numbro.js',
          '../node_modules/numbro/dist/languages.min.js',
          '../dist/moment/moment.js',
          '../dist/pikaday/pikaday.js',
          '../dist/handsontable.js',
          '../dist/languages/all.js',
        ],
      })
    );
  });

  return [].concat(config);
}
