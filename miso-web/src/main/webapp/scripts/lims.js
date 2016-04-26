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
    for (var i = 0; i < self._N(field).length; i++) {
      self._N(field)[i].checked = true;
    }
  },

  checkAllConfirm: function (field, message) {
    if (confirm(message)) {
      var self = this;
      for (var i = 0; i < self._N(field).length; i++) {
        self._N(field)[i].checked = true;
      }
    }
  },

  uncheckAll: function (field) {
    var self = this;
    for (var i = 0; i < self._N(field).length; i++) {
      self._N(field)[i].checked = false;
    }
  },

  uncheckOthers: function (field, item) {
    var self = this;
    for (var i = 0; i < self._N(field).length; i++) {
      if (self._N(field)[i] != item) {
        self._N(field)[i].checked = false;
      }
    }
  },

  _N: function (element) {
    if (typeof element == 'string') {
      element = document.getElementsByName(element);
    }
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
  
  addDateTimePicker: function (id) {
    jQuery("#" + id).datetimepicker({
      controlType: 'select',
      oneLine: true,
      dateFormat: 'dd/mm/yy',
      timeFormat: 'HH:mm'
    });
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
  dateRegex: '^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[012])\/(19|20)[0-9]{2}$',
  dateTimeRegex: '^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[012])\/(19|20)[0-9]{2} ([01][0-9]|2[0-3]):[0-5][0-9]$',
  sanitizeRegex: '[^<>\'\"\/&]+',
  unicodeWordRegex: '^[\\p{L}0-9_\\^\\-\\.\\s]+$',
  _unicodeWord: XRegExp('^[\\p{L}0-9_\\^\\-\\.\\s]+$'),

  isNullCheck: function (value) {
    return (value === "" || value === " " || value === "undefined" || value === "&nbsp;" || value === undefined);
  },

  validate_input_field: function (field, name, okstatus) {
    var self = this;
    var errormsg = '';
    if (!self._unicodeWord.test(jQuery(field).val())) {
      okstatus = false;
      errormsg = "In the " + name + " " + jQuery(field).attr("id") +
                 " field you CAN use alpha numeric values with the following symbols:\n" +
                 "^ - _ .\n" +
                 "but you CANNOT use slashes, comma, brackets, single or double quotes, and it CANNOT end with a space or be empty\n\n";
    }
    return {"okstatus": okstatus, "errormsg": errormsg};
  },

  // Clean input fields by removing leading and trailing whitespace
  clean_input_field: function (field) {
    var oldval = field.val();
    field.val(oldval.trim(oldval));
  }
};

Utils.page = {
  pageReload: function () {
    window.location.reload(true);
  },

  newWindow: function (url) {
    newwindow = window.open(url, 'name', 'height=500,width=500,menubar=yes,status=yes,scrollbars=yes');
    if (window.focus) {
      newwindow.focus();
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
        {'doOnSuccess': function () {
          jQuery('#alertList').html("<i style='color: gray'>No unread alerts</i>");
          Utils.alert.checkAlerts();
        }
        }
      );
    }
  }
};



