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
/******/ 	return __webpack_require__(__webpack_require__.s = 9);
/******/ })
/************************************************************************/
/******/ ({

/***/ 0:
/***/ (function(module, exports) {

module.exports = __WEBPACK_EXTERNAL_MODULE_0__;

/***/ }),

/***/ 9:
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
  languageCode: 'lv-LV'
}, _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ROW_ABOVE, 'Ievietot rindu augšā'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ROW_BELOW, 'Ievietot rindu apakšā'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_INSERT_LEFT, 'Ievietot kolonnu pa kreisi'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_INSERT_RIGHT, 'Ievietot kolonnu pa labi'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_ROW, ['Dzēst rindu', 'Dzēst rindas']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_COLUMN, ['Dzēst kolonnu', 'Dzēst kolonnas']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNDO, 'Atsaukt'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REDO, 'Pārtaisīt'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_READ_ONLY, 'Lasīšanas režīms'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_CLEAR_COLUMN, 'Notīrīt kolonnu'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT, 'Izvietojums'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_LEFT, 'Pa kreisi'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_CENTER, 'Centrēts'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_RIGHT, 'Pa labi'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_JUSTIFY, 'Izlīdzināts'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_TOP, 'Augšā'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_MIDDLE, 'Pa vidu'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_BOTTOM, 'Apakšā'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_FREEZE_COLUMN, 'Iesaldēt kolonnu'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNFREEZE_COLUMN, 'Atsaldēt kolonnu'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS, 'Robežas'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_TOP, 'Augšā'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_RIGHT, 'Pa labi'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_BOTTOM, 'Apakšā'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_LEFT, 'Pa kreisi'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_BORDERS, 'Noņemt robežu(-as)'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ADD_COMMENT, 'Pievienot komentāru'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_EDIT_COMMENT, 'Labot komentāru'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_COMMENT, 'Dzēst komentāru'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_READ_ONLY_COMMENT, 'Lasīšanas režīma komentārs'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_MERGE_CELLS, 'Sapludināt šūnas'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNMERGE_CELLS, 'Nesapludināt šunas'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_COPY, 'Kopēt'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_CUT, 'Izgriezt'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_NESTED_ROWS_INSERT_CHILD, 'Ievietot pakārtoto rindu'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_NESTED_ROWS_DETACH_CHILD, 'Atdalīt no vecāka'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_HIDE_COLUMN, ['Palēpt kolonnu', 'Palēpt kolonnas']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_SHOW_COLUMN, ['Rādīt kolonnu', 'Rādīt kolonnas']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_HIDE_ROW, ['Palēpt rindu', 'Paslēpt rindas']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_SHOW_ROW, ['Rādīt rindu', 'Rādīt rindas']), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NONE, 'Nekas'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_EMPTY, 'Ir tukšs'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_EMPTY, 'Nav tukšs'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_EQUAL, 'Vienāds ar'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_EQUAL, 'Nav vienāds ar'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BEGINS_WITH, 'Sākas ar'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_ENDS_WITH, 'Beidzas ar'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_CONTAINS, 'Satur'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_CONTAIN, 'Nesatur'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_GREATER_THAN, 'Lielāks par'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_GREATER_THAN_OR_EQUAL, 'Lielāks vai vienāds ar'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_LESS_THAN, 'Mazāks par'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_LESS_THAN_OR_EQUAL, 'Mazāks vai vienāds ar'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BETWEEN, 'Ir starp'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_BETWEEN, 'Nav starp'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_AFTER, 'Pēc'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BEFORE, 'Pirms'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_TODAY, 'Šodien'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_TOMORROW, 'Rītdien'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_YESTERDAY, 'Vakar'), _defineProperty(_dictionary, C.FILTERS_VALUES_BLANK_CELLS, 'Tukšas šūnas'), _defineProperty(_dictionary, C.FILTERS_DIVS_FILTER_BY_CONDITION, 'Filtrēt pēc nosacījuma'), _defineProperty(_dictionary, C.FILTERS_DIVS_FILTER_BY_VALUE, 'Filtrēt pēc vērtības'), _defineProperty(_dictionary, C.FILTERS_LABELS_CONJUNCTION, 'Un'), _defineProperty(_dictionary, C.FILTERS_LABELS_DISJUNCTION, 'Vai'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_SELECT_ALL, 'Izvēlēties visu'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_CLEAR, 'Notīrīt'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_OK, 'Labi'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_CANCEL, 'Atcelt'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_SEARCH, 'Meklēt'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_VALUE, 'Vērtība'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_SECOND_VALUE, 'Otra vērtība'), _dictionary);

_handsontable.default.languages.registerLanguageDictionary(dictionary);

var _default = dictionary;
exports.default = _default;

/***/ })

/******/ })["___"];
});