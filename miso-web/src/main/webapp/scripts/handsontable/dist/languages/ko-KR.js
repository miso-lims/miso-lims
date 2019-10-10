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
/******/ 	return __webpack_require__(__webpack_require__.s = 8);
/******/ })
/************************************************************************/
/******/ ({

/***/ 0:
/***/ (function(module, exports) {

module.exports = __WEBPACK_EXTERNAL_MODULE_0__;

/***/ }),

/***/ 8:
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
  languageCode: 'ko-KR'
}, _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ROW_ABOVE, '위쪽에 행 삽입'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ROW_BELOW, '아래쪽에 행 삽입'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_INSERT_LEFT, '왼쪽에 열 삽입'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_INSERT_RIGHT, '오른쪽에 열 삽입'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_ROW, ['행 삭제', '여러 행 삭제']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_COLUMN, ['열 삭제', '여러 열 삭제']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNDO, '되돌리기'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REDO, '다시하기'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_READ_ONLY, '읽기 전용'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_CLEAR_COLUMN, '열 지우기'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT, '정렬'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_LEFT, '왼쪽'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_CENTER, '중앙'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_RIGHT, '오른쪽'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_JUSTIFY, '자동'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_TOP, '위쪽'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_MIDDLE, '가운데'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ALIGNMENT_BOTTOM, '아래쪽'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_FREEZE_COLUMN, '열 고정'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNFREEZE_COLUMN, '열 고정 해제'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS, '테두리'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_TOP, '위쪽'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_RIGHT, '오른쪽'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_BOTTOM, '아래쪽'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_BORDERS_LEFT, '왼쪽'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_BORDERS, '테두리 지우기'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_ADD_COMMENT, '댓글 달기'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_EDIT_COMMENT, '댓글 편집'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_REMOVE_COMMENT, '댓글 삭제'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_READ_ONLY_COMMENT, '읽기 전용 댓글'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_MERGE_CELLS, '셀 병합'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_UNMERGE_CELLS, '셀 병합 해제'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_COPY, '복사'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_CUT, '잘라내기'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_NESTED_ROWS_INSERT_CHILD, '자녀 행 추가'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_NESTED_ROWS_DETACH_CHILD, '부모행에서 제거'), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_HIDE_COLUMN, ['열 숨기기', '여러 열 숨기기']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_SHOW_COLUMN, ['열 숨기기 해제', '여러 열 숨기기 해제']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_HIDE_ROW, ['행 숨기기', '여러 행 숨기기']), _defineProperty(_dictionary, C.CONTEXTMENU_ITEMS_SHOW_ROW, ['행 숨기기 해제', '여러 행 숨기기 해제']), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NONE, '조건없음'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_EMPTY, '비어있음'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_EMPTY, '비어있지 않음'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_EQUAL, '같'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_EQUAL, '같지 않음'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BEGINS_WITH, '시작 문자'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_ENDS_WITH, '끝 문자'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_CONTAINS, '포함'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_CONTAIN, '포함하지 않음'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_GREATER_THAN, '보다 큼'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_GREATER_THAN_OR_EQUAL, '크거나 같음'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_LESS_THAN, '보다 작'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_LESS_THAN_OR_EQUAL, '작거나 같음'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BETWEEN, '사이'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_NOT_BETWEEN, '사이 제외'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_AFTER, '다음'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_BEFORE, '전'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_TODAY, '오늘'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_TOMORROW, '내일'), _defineProperty(_dictionary, C.FILTERS_CONDITIONS_YESTERDAY, '어제'), _defineProperty(_dictionary, C.FILTERS_VALUES_BLANK_CELLS, '공란'), _defineProperty(_dictionary, C.FILTERS_DIVS_FILTER_BY_CONDITION, '조건부 필터'), _defineProperty(_dictionary, C.FILTERS_DIVS_FILTER_BY_VALUE, '값 필터'), _defineProperty(_dictionary, C.FILTERS_LABELS_CONJUNCTION, '그리고'), _defineProperty(_dictionary, C.FILTERS_LABELS_DISJUNCTION, '또는'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_SELECT_ALL, '전체선택'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_CLEAR, '지우기'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_OK, '확인'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_CANCEL, '취소'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_SEARCH, '찾기'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_VALUE, '값'), _defineProperty(_dictionary, C.FILTERS_BUTTONS_PLACEHOLDER_SECOND_VALUE, '두번째 값'), _dictionary);

_handsontable.default.languages.registerLanguageDictionary(dictionary);

var _default = dictionary;
exports.default = _default;

/***/ })

/******/ })["___"];
});