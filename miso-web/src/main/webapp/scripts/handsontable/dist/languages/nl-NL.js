(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory(require("../../handsontable"));
	else if(typeof define === 'function' && define.amd)
		define(["../../handsontable"], factory);
	else {
		var a = typeof exports === 'object' ? factory(require("../../handsontable")) : factory(root["Handsontable"]);
		for(var i in a) (typeof exports === 'object' ? exports : root)[i] = a[i];
	}
})(typeof self !== 'undefined' ? self : this, function(__WEBPACK_EXTERNAL_MODULE_0__) {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 11);
/******/ })
/************************************************************************/
/******/ ({

/***/ 0:
/***/ (function(module, exports) {

module.exports = __WEBPACK_EXTERNAL_MODULE_0__;

/***/ }),

/***/ 11:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


exports.__esModule = true;
exports.default = void 0;

var _handsontable = _interopRequireDefault(__webpack_require__(0));

var _dictionary;

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

var C = _handsontable.default.languages.dictionaryKeys;
var dictionary = (_dictionary = {
  languageCode: 'nl-NL'
}, _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ROW_ABOVE, 'Rij boven invoegen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ROW_BELOW, 'Rij onder invoegen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_INSERT_LEFT, 'Kolom links invoegen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_INSERT_RIGHT, 'Kolom rechts invoegen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_ROW, ['Rij verwijderen', 'Rijen verwijderen']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_COLUMN, ['Kolom verwijderen', 'Kolommen verwijderen']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNDO, 'Ongedaan maken'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REDO, 'Opnieuw uitvoeren'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_READ_ONLY, 'Alleen lezen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_CLEAR_COLUMN, 'Kolom leegmaken'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT, 'Uitlijning'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_LEFT, 'Links'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_CENTER, 'Gecentreerd'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_RIGHT, 'Rechts'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_JUSTIFY, 'Uitvullen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_TOP, 'Boven'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_MIDDLE, 'Midden'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_BOTTOM, 'Onder'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_FREEZE_COLUMN, 'Kolom blokkeren'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNFREEZE_COLUMN, 'Kolom blokkering opheffen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS, 'Randen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_TOP, 'Boven'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_RIGHT, 'Rechts'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_BOTTOM, 'Onder'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_LEFT, 'Links'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_BORDERS, 'Rand(en) verwijderen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ADD_COMMENT, 'Opmerking toevoegen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_EDIT_COMMENT, 'Opmerking bewerken'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_COMMENT, 'Opmerking verwijderen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_READ_ONLY_COMMENT, 'Opmerking Alleen-lezen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_MERGE_CELLS, 'Cellen samenvoegen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNMERGE_CELLS, 'Samenvoeging van cellen opheffen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_COPY, 'Kopiëren'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_CUT, 'Knippen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_NESTED_ROWS_INSERT_CHILD, 'Geneste rij invoegen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_NESTED_ROWS_DETACH_CHILD, 'Geneste rij ontkoppelen'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_HIDE_COLUMN, ['Kolom verbergen', 'Kolommen verbergen']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_SHOW_COLUMN, ['Kolom zichbaar maken', 'Kolommen zichtbaar maken']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_HIDE_ROW, ['Rij verbergen', 'Rijen verbergen']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_SHOW_ROW, ['Rij zichtbaar maken', 'Rijen zichtbaar maken']), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NONE, 'Geen'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_EMPTY, 'Is leeg'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_EMPTY, 'Is niet leeg'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_EQUAL, 'Is gelijk aan'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_EQUAL, 'Is niet gelijk aan'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BEGINS_WITH, 'Begint met'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_ENDS_WITH, 'Eindigt op'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_CONTAINS, 'Bevat'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_CONTAIN, 'Bevat niet'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_GREATER_THAN, 'Is groter'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_GREATER_THAN_OR_EQUAL, 'Is groter of gelijk aan'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_LESS_THAN, 'Is kleiner dan'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_LESS_THAN_OR_EQUAL, 'Is kleiner dan of gelijk aan'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BETWEEN, 'Is tussen'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_BETWEEN, 'Ligt niet tussen'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_AFTER, 'Na'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BEFORE, 'Voor'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_TODAY, 'Vandaag'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_TOMORROW, 'Morgen'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_YESTERDAY, 'Gisteren'), _defineProperty(_dictionary, C.FILTERS_VALUES_BLANK_CELLS, 'Cellen leegmaken'), _defineProperty(_dictionary, C.FILTERS_DIVS_FILTER_BY_CONDITION, 'Filteren op conditie'), _defineProperty(_dictionary, C.FILTERS_DIVS_FILTER_BY_VALUE, 'Filteren op waarde'), _defineProperty(_dictionary, C.FILTERS_LABELS_CONJUNCTION, 'En'), _defineProperty(_dictionary, C.FILTERS_LABELS_DISJUNCTION, 'Of'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_SELECT_ALL, 'Alles selecteren'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_CLEAR, 'Leeg maken'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_OK, 'OK'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_CANCEL, 'Annuleren'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_SEARCH, 'Zoeken'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_VALUE, 'Waarde'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_SECOND_VALUE, 'Tweede waarde'), _dictionary);

_handsontable.default.languages.registerLanguageDictionary(dictionary);

var _default = dictionary;
exports.default = _default;

/***/ })

/******/ })["___"];
});