/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

//stop browser caching
jQuery.ajaxSetup({cache: false});

var ajaxurl = '/miso/fluxion.ajax';

var Utils = Utils || {
  /** Maps a form element's child input elements to a JSON object. */
  mappifyForm: function (formName) {
    var values = {};
    jQuery.each(jQuery('#' + formName).serializeArray(), function (i, field) {
      values[field.name] = field.value;
    });
    return values;
  },

  /** Maps a standard DOM container's (div, span, etc) child input elements to a JSON object. */
  mappifyInputs: function (parentContainerName) {
    var values = {};
    jQuery.each(jQuery('#' + parentContainerName).find(":input").serializeArray(), function (i, field) {
      values[field.name] = field.value;
    });
    return values;
  },

  mappifyTable: function (table) {
    var values = [];
    jQuery.each(jQuery('#' + table).find("tr:gt(0)"), function () {
      var rowval = {};
      jQuery.each(jQuery(this).find("td"), function () {
        var td = jQuery(this);
        if (!Utils.validation.isNullCheck(td.attr("name"))) {
          rowval[td.attr("name")] = td.html();
        }
      });
      values.push(rowval);
    });
    return values;
  }
};

Utils.timer = {
  timedFunc: function () {
    var timer;
    return function (func, time) {
      clearTimeout(timer);
      timer = setTimeout(func, time);
    };
  },

  typewatchFunc: function (obj, func, wait, capturelength) {
    var options = {
      callback: func,
      wait: wait,
      highlight: true,
      captureLength: capturelength
    };
    jQuery(obj).typeWatch(options);
  },

  queueFunctions: function (funcs) {
    if (Object.prototype.toString.apply(funcs) === '[object Array]') {
      for (var i = 0; i < funcs.length; i++) {
        var f = funcs[i];
        jQuery('body').queue("queue", function () {
          f();
          if (i < (funcs.length - 1)) {
            setTimeout(function () {
              jQuery('body').dequeue("queue");
            }, 1000);
          }
        });
      }
    }
    return jQuery('body');
  }
};

Utils.ui = {
  checkUser: function (username) {
    Fluxion.doAjax(
      'dashboard',
      'checkUser',
      {'username': username, 'url': ajaxurl},
      {'': ''}
    );
  },

  checkAll: function (field) {
    var self = this;
    for (i = 0; i < self._N(field).length; i++) self._N(field)[i].checked = true;
  },

  checkAllConfirm: function (field, message) {
    if (confirm(message)) {
      var self = this;
      for (i = 0; i < self._N(field).length; i++) self._N(field)[i].checked = true;
    }
  },

  uncheckAll: function (field) {
    var self = this;
    for (i = 0; i < self._N(field).length; i++) self._N(field)[i].checked = false;
  },

  uncheckOthers: function (field, item) {
    var self = this;
    for (i = 0; i < self._N(field).length; i++) {
      if (self._N(field)[i] != item) {
        self._N(field)[i].checked = false;
      }
    }
  },

  _N: function (element) {
    if (typeof element == 'string') element = document.getElementsByName(element);
    return Element.extend(element);
  },

  toggleRightInfo: function (div, id) {
    if (jQuery(div).hasClass("toggleRight")) {
      jQuery(div).removeClass("toggleRight").addClass("toggleRightDown");
    }
    else {
      jQuery(div).removeClass("toggleRightDown").addClass("toggleRight");
    }
    jQuery("#" + id).toggle("blind", {}, 500);
  },

  toggleLeftInfo: function (div, id) {
    if (jQuery(div).hasClass("toggleLeft")) {
      jQuery(div).removeClass("toggleLeft").addClass("toggleLeftDown");
    }
    else {
      jQuery(div).removeClass("toggleLeftDown").addClass("toggleLeft");
    }
    jQuery("#" + id).toggle("blind", {}, 500);
  },

  addDatePicker: function (id) {
    jQuery("#" + id).datepicker({dateFormat: 'dd/mm/yy', showButtonPanel: true});
  },

  addMaxDatePicker: function (id, maxDateOffset) {
    jQuery("#" + id).datepicker({dateFormat: 'dd/mm/yy', showButtonPanel: true, maxDate: maxDateOffset});
  },

  disableButton: function (buttonDiv) {
    jQuery('#' + buttonDiv).attr('disabled', 'disabled');
    jQuery('#' + buttonDiv).html("Processing...");
  },

  reenableButton: function (buttonDiv, text) {
    jQuery('#' + buttonDiv).removeAttr('disabled');
    jQuery('#' + buttonDiv).html(text);
  },

  confirmRemove: function (obj) {
    if (confirm("Are you sure you wish to remove this item?")) {
      obj.remove();
    }
  },

  escape: function (obj, callback) {
    return obj.each(function () {
      jQuery(document).on("keydown", obj, function (e) {
        var keycode = ((typeof e.keyCode != 'undefined' && e.keyCode) ? e.keyCode : e.which);
        if (keycode === 27) {
          callback.call(obj, e);
        }
      });
    });
  }
};

Utils.fileUpload = {
  fileUploadProgress: function (formname, divname, successfunc) {
    var self = this;
    //self.processingOverlay();

    Fluxion.doAjaxUpload(
      formname,
      'fileUploadProgressBean',
      'checkUploadStatus',
      {'url': ajaxurl},
      {'statusElement': divname, 'progressElement': 'trash', 'doOnSuccess': successfunc},
      {'': ''}
    );
  },

  processingOverlay: function () {
    jQuery.colorbox({width: "30%", html: "Processing..."});
  }
};

Utils.validation = {
  //_base64 : XRegExp('^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})$'),
  _unicodeWord: XRegExp('^[\\p{L}0-9_\\^\\-\\.\\s]+$'),

  isNullCheck: function (value) {
    return (value === "" || value === " " || value === "undefined" || value === "&nbsp;" || value === undefined);
  },

  validate_input_field: function (field, name, okstatus) {
    var self = this;
    var errormsg = '';
    //if (!jQuery(field).val().match(/^[a-zA-Z0-9_\^\-\.\s]+$/)) {
    if (!self._unicodeWord.test(jQuery(field).val())) {
      okstatus = false;
      errormsg = "In the " + name + " " + jQuery(field).attr("id") +
                 " field you CAN use alpha numeric values with the following symbols:\n" +
                 "^ - _ .\n" +
                 "but you CANNOT use slashes, comma, brackets, single or double quotes, and it CANNOT end with a space or be empty\n\n";
    }
    return {"okstatus": okstatus, "errormsg": errormsg};
  },

  base64Check: function (str) {
    var base64 = new RegExp('^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})$');
    if (base64.test(str)) {
      return Utils.codec.base64_decode(str);
    }
    else {
      return str;
    }
  }
};

Utils.codec = {
  // private property
  _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

  // public method for encoding
  base64_encode: function (input) {
    var self = this;
    var output = "";
    var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
    var i = 0;

    input = self._utf8_encode(input);

    while (i < input.length) {
      chr1 = input.charCodeAt(i++);
      chr2 = input.charCodeAt(i++);
      chr3 = input.charCodeAt(i++);

      enc1 = chr1 >> 2;
      enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
      enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
      enc4 = chr3 & 63;

      if (isNaN(chr2)) {
        enc3 = enc4 = 64;
      }
      else if (isNaN(chr3)) {
        enc4 = 64;
      }

      output = output +
               self._keyStr.charAt(enc1) + self._keyStr.charAt(enc2) +
               self._keyStr.charAt(enc3) + self._keyStr.charAt(enc4);
    }
    return output;
  },

  // public method for decoding
  base64_decode: function (input) {
    var self = this;
    var output = "";
    var chr1, chr2, chr3;
    var enc1, enc2, enc3, enc4;
    var i = 0;

    input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

    while (i < input.length) {

      enc1 = self._keyStr.indexOf(input.charAt(i++));
      enc2 = self._keyStr.indexOf(input.charAt(i++));
      enc3 = self._keyStr.indexOf(input.charAt(i++));
      enc4 = self._keyStr.indexOf(input.charAt(i++));

      chr1 = (enc1 << 2) | (enc2 >> 4);
      chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
      chr3 = ((enc3 & 3) << 6) | enc4;

      output = output + String.fromCharCode(chr1);

      if (enc3 != 64) {
        output = output + String.fromCharCode(chr2);
      }
      if (enc4 != 64) {
        output = output + String.fromCharCode(chr3);
      }
    }
    output = self._utf8_decode(output);
    return output;
  },

  // private method for UTF-8 encoding
  _utf8_encode: function (string) {
    string = string.replace(/\r\n/g, "\n");
    var utftext = "";

    for (var n = 0; n < string.length; n++) {
      var c = string.charCodeAt(n);
      if (c < 128) {
        utftext += String.fromCharCode(c);
      }
      else if ((c > 127) && (c < 2048)) {
        utftext += String.fromCharCode((c >> 6) | 192);
        utftext += String.fromCharCode((c & 63) | 128);
      }
      else {
        utftext += String.fromCharCode((c >> 12) | 224);
        utftext += String.fromCharCode(((c >> 6) & 63) | 128);
        utftext += String.fromCharCode((c & 63) | 128);
      }
    }

    return utftext;
  },

  // private method for UTF-8 decoding
  _utf8_decode: function (utftext) {
    var string = "";
    var i = 0;
    var c = c1 = c2 = 0;

    while (i < utftext.length) {
      c = utftext.charCodeAt(i);
      if (c < 128) {
        string += String.fromCharCode(c);
        i++;
      }
      else if ((c > 191) && (c < 224)) {
        c2 = utftext.charCodeAt(i + 1);
        string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
        i += 2;
      }
      else {
        c2 = utftext.charCodeAt(i + 1);
        c3 = utftext.charCodeAt(i + 2);
        string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
        i += 3;
      }
    }
    return string;
  }
};

Utils.page = {
  pageReload: function () {
    window.location.reload(true);
  },

  newWindow: function (url) {
    newwindow = window.open(url, 'name', 'height=500,width=500,menubar=yes,status=yes,scrollbars=yes');
    if (window.focus) {
      newwindow.focus()
    }
    return false;
  },

  pageRedirect: function (url) {
    window.location = url;
  }
};

Utils.alert = {
  checkAlerts: function () {
    var self = this;
    Fluxion.doAjax(
      'dashboard',
      'checkAlerts',
      {'url': ajaxurl},
      {'ajaxType': 'periodical', 'updateFrequency': 30, 'doOnSuccess': self.processMyAccountAlerts}
    );
  },

  processMyAccountAlerts: function (json) {
    if (json.newAlerts) {
      if (!jQuery("#myAccountSpan").hasClass("unreadAlertSpan")) {
        jQuery("#myAccountSpan").addClass("unreadAlertSpan");
      }

      if (!jQuery("#myAccountLink").hasClass("unreadAlertLink")) {
        jQuery("#myAccountLink").addClass("unreadAlertLink");
      }
    }
    else {
      if (jQuery("#myAccountSpan").hasClass("unreadAlertSpan")) {
        jQuery("#myAccountSpan").removeClass("unreadAlertSpan");
      }

      if (jQuery("#myAccountLink").hasClass("unreadAlertLink")) {
        jQuery("#myAccountLink").removeClass("unreadAlertLink");
      }
    }
  },

  loadAlerts: function () {
    var self = this;
    Fluxion.doAjax(
      'dashboard',
      'getAlerts',
      {'url': ajaxurl},
      {'ajaxType': 'periodical', 'updateFrequency': 30, 'doOnSuccess': self.processAlerts}
    );
  },

  processAlerts: function (json) {
    if (Utils.validation.isNullCheck(json.html)) {
      jQuery('#alertList').html("<i style='color: gray'>No unread alerts</i>");
    }
    else {
      jQuery('#alertList').html(json.html);
    }
  },

  processSystemAlerts: function (json) {
    if (Utils.validation.isNullCheck(json.html)) {
      jQuery('#systemAlertList').html("<i style='color: gray'>No unread alerts</i>");
    }
    else {
      jQuery('#systemAlertList').html(json.html);
    }
  },

  confirmAlertRead: function (alert) {
    if (confirm("Mark this alert as read?")) {
      var a = jQuery(alert).parent();
      Fluxion.doAjax(
        'dashboard',
        'setAlertAsRead',
        {'alertId': a.attr('alertId'), 'url': ajaxurl},
        {'doOnSuccess': a.remove()}
      );
    }
  },

  confirmAllAlertsRead: function () {
    if (confirm("Mark all alerts as read?")) {
      Fluxion.doAjax(
        'dashboard',
        'setAllAlertsAsRead',
        {'url': ajaxurl},
        {'doOnSuccess': function (json) {
          jQuery('#alertList').html("<i style='color: gray'>No unread alerts</i>");
          Utils.alert.checkAlerts();
        }
        }
      );
    }
  }
};



