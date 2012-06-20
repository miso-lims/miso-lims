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

function queueFunctions(funcs) {
  if (Object.prototype.toString.apply(funcs) === '[object Array]') {
    for (var i = 0; i < funcs.length; i++) {
      var f = funcs[i];
      jQuery('body').queue("queue", function() {
        f();
        if (i < (funcs.length - 1)) {
          setTimeout(function() {
            jQuery('body').dequeue("queue");
          }, 1000);
        }
      });
    }
  }
  return jQuery('body');
}

function timedFunc() {
  var timer;
  return function(func, time) {
    clearTimeout(timer);
    timer = setTimeout(func, time);
  };
}

function typewatchFunc(obj, func, wait, capturelength) {
  var options = {
    callback: func,
    wait: wait,
    highlight: true,
    captureLength: capturelength
  };
  jQuery(obj).typeWatch(options);
}

function checkUser(username) {
  Fluxion.doAjax(
          'dashboard',
          'checkUser',
          {'username':username, 'url':ajaxurl},
          {'':''}
  );
}

function clearInputField(field) {
  field.value = "";
}

function checkAll(field) {
  for (i = 0; i < $N(field).length; i++) $N(field)[i].checked = true;
}

function uncheckAll(field) {
  for (i = 0; i < $N(field).length; i++) $N(field)[i].checked = false;
}

function uncheckOthers(field, item) {
  for (i = 0; i < $N(field).length; i++) {
    if ($N(field)[i] != item) {
      $N(field)[i].checked = false;
    }
  }
}

function $N(element) {
  if (typeof element == 'string') element = document.getElementsByName(element);
  return Element.extend(element);
}

function toggleFade(id) {
  var obj = $(id);
  if (obj.style.display != undefined && obj.style.display == "none") {
    obj.appear();
  }
  else {
    obj.fade();
  }
}

function toggleRightInfo(div, id) {
  if (jQuery(div).hasClass("toggleRight")) {
    jQuery(div).removeClass("toggleRight").addClass("toggleRightDown");
  }
  else {
    jQuery(div).removeClass("toggleRightDown").addClass("toggleRight");
  }
  jQuery("#" + id).toggle("blind", {}, 500);
}

function toggleLeftInfo(div, id) {
  if (jQuery(div).hasClass("toggleLeft")) {
    jQuery(div).removeClass("toggleLeft").addClass("toggleLeftDown");
  }
  else {
    jQuery(div).removeClass("toggleLeftDown").addClass("toggleLeft");
  }
  jQuery("#" + id).toggle("blind", {}, 500);
}

function addDatePicker(id) {
  jQuery("#" + id).datepicker({dateFormat:'dd/mm/yy',showButtonPanel: true});
}

function addMaxDatePicker(id, maxDateOffset) {
  jQuery("#" + id).datepicker({dateFormat:'dd/mm/yy',showButtonPanel: true, maxDate:maxDateOffset});
}

function previewExperiment(expId) {
  Fluxion.doAjax(
          'experimentPreview',
          'previewExperiment',
          {'experimentId':expId, 'url':ajaxurl},
          {'updateElement':'expinfo' + expId}
  );
}

function previewProject(projectId) {
  Fluxion.doAjax(
          'projectPreview',
          'previewProject',
          {'projectId':projectId, 'url':ajaxurl},
          {'updateElement':'projectinfo' + projectId}
  );
}

function fileUploadProgress(formname, divname, successfunc) {
  processingOverlay();

  Fluxion.doAjaxUpload(
          formname,
          'fileUploadProgressBean',
          'checkUploadStatus',
          {'url':ajaxurl},
          {'statusElement':divname, 'progressElement':'trash', 'doOnSuccess':successfunc},
          {'':''}
  );
}

var processingOverlay = function() {
  jQuery.colorbox({width:"30%",html:"Processing..."});
};

var isNullCheck = function(value) {
  return (value === "" || value === " " || value === "undefined" || value === "&nbsp;" || value === undefined);
};

var pageReload = function() {
  window.location.reload(true);
};

function newWindow(url) {
  newwindow = window.open(url, 'name', 'height=500,width=500,menubar=yes,status=yes,scrollbars=yes');
  if (window.focus) {
    newwindow.focus()
  }
  return false;
}

function pageRedirect(url) {
  window.location = url;
}

function confirmRemove(obj) {
  if (confirm("Are you sure you wish to remove this item?")) {
    obj.remove();
  }
}

function checkAlerts() {
  Fluxion.doAjax(
          'dashboard',
          'checkAlerts',
          {'url':ajaxurl},
          {'ajaxType':'periodical', 'updateFrequency':30, 'doOnSuccess':processMyAccountAlerts}
  );
}

var processMyAccountAlerts = function(json) {
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
};

function loadAlerts() {
  Fluxion.doAjax(
          'dashboard',
          'getAlerts',
          {'url':ajaxurl},
          {'ajaxType':'periodical', 'updateFrequency':30, 'doOnSuccess':processAlerts}
  );
}

var processAlerts = function(json) {
  if (isNullCheck(json.html)) {
    jQuery('#alertList').html("<i style='color: gray'>No unread alerts</i>");
  }
  else {
    jQuery('#alertList').html(json.html);
  }
};

var processSystemAlerts = function(json) {
  if (isNullCheck(json.html)) {
    jQuery('#systemAlertList').html("<i style='color: gray'>No unread alerts</i>");
  }
  else {
    jQuery('#systemAlertList').html(json.html);
  }
};

function confirmAlertRead(alert) {
  if (confirm("Mark this alert as read?")) {
    var a = jQuery(alert).parent();
    Fluxion.doAjax(
            'dashboard',
            'setAlertAsRead',
            {'alertId':a.attr('alertId'),'url':ajaxurl},
            {'doOnSuccess':a.remove()}
    );
  }

}

function confirmAllAlertsRead() {
  if (confirm("Mark all alerts as read?")) {
    Fluxion.doAjax(
            'dashboard',
            'setAllAlertsAsRead',
            {'url':ajaxurl},
            {'doOnSuccess': function(json) {
              jQuery('#alertList').html("<i style='color: gray'>No unread alerts</i>");
              checkAlerts();
            }
            }
    );
  }
}

function disableButton(buttonDiv) {
  jQuery('#' + buttonDiv).attr('disabled', 'disabled');
  jQuery('#' + buttonDiv).html("Processing...");
}
function reenableButton(buttonDiv, text) {
  jQuery('#' + buttonDiv).removeAttr('disabled');
  jQuery('#' + buttonDiv).html(text);
}

function validate_input_field(field, name, okstatus) {
  var errormsg = '';
  if (!jQuery(field).val().match(/^[a-zA-Z0-9_\^\-\.\/\s]+$/)) {
    okstatus = false;
    errormsg = "In the " + name + " " + jQuery(field).attr("id") + " field you CAN use alpha numeric values with the following symbols:\n"
                       + "^ - _ .\n"
            + "but you CANNOT use comma, brackets, single or double quotes, it CANNOT end with a space and it CANNOT be empty\n";
  }
  return {"okstatus":okstatus, "errormsg":errormsg};
}