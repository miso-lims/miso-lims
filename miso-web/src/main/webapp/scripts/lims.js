/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
jQuery.ajaxSetup({
  cache: false
});

var ajaxurl = '/miso/fluxion.ajax';

var Utils = Utils
    || {
      /** Maps a form element's child input elements to a JSON object. */
      mappifyForm: function(formName) {
        var values = {};
        jQuery.each(jQuery('#' + formName).serializeArray(), function(i, field) {
          values[field.name] = field.value;
        });
        return values;
      },

      /** Maps a standard DOM container's (div, span, etc) child input elements to a JSON object. */
      mappifyInputs: function(parentContainerName) {
        var values = {};
        jQuery.each(jQuery('#' + parentContainerName).find(":input").serializeArray(), function(i, field) {
          values[field.name] = field.value;
        });
        return values;
      },

      mappifyTable: function(table) {
        var values = [];
        jQuery.each(jQuery('#' + table).find("tr:gt(0)"), function() {
          var rowval = {};
          jQuery.each(jQuery(this).find("td"), function() {
            var td = jQuery(this);
            if (!Utils.validation.isNullCheck(td.attr("name"))) {
              rowval[td.attr("name")] = td.html();
            }
          });
          values.push(rowval);
        });
        return values;
      },

      setSortFromPriority: function(table) {
        var info = table.aoColumns.reduce(function(acc, curr, index) {
          return acc.iSortPriority > curr.iSortPriority ? acc : {
            iSortPriority: curr.iSortPriority,
            bSortDirection: !!curr.bSortDirection,
            iPos: index
          };
        }, {
          iSortPriority: -1,
          bSortDirection: false,
          iPos: 0
        })
        table.aaSorting = [[info.iPos, info.bSortDirection ? "asc" : "desc"]];
        return table;
      },

      checkCommonSampleClasses: function(jqueryDataTableData, getSampleClassFromObject, selectedIdsArray, errorText) {
        var sampleClasses = jqueryDataTableData.aaData.filter(function(x) {
          return selectedIdsArray.indexOf(x.id) != -1;
        }).map(getSampleClassFromObject);
        for (var i = 1; i < sampleClasses.length; i++) {
          if (sampleClasses[i] != sampleClasses[0]) {
            alert(errorText);
            return false;
          }
        }
        return true;
      },
      createToggleColumn: function(nameOfList) {
        var list = eval(nameOfList);
        var className = nameOfList.replace(/\./g, '');
        return {
          "sTitle": "",
          "mData": "id",
          "include": true,
          "mRender": function(data, type, full) {
            var checked = list.indexOf(data) > -1;

            return "<input id=\"" + data + "_" + className + "\" class=\"" + className + " bulkCheckbox\" elementId=\"" + data
                + "\" type=\"checkbox\" onclick=\"Utils.toggleListRange(this.checked, event, " + data + ", " + nameOfList + ", '"
                + className + "')\"" + (checked ? " checked=\"checked\"" : "") + ">";
          }
        };
      },

      toggleListRange: function(state, ev, id, list, className) {
        if (!ev.shiftKey) {
          if (state) {
            list.lastSingleSelect = id + "_" + className; // Record last click for range selection
          }
          Utils.toggleListItem(state, id, list);
        } else {
          var currentShiftSelect = id + "_" + className;
          var checkboxes = [];
          jQuery("input." + className).each(function() {
            checkboxes.push(jQuery(this));
          });

          function selectRange(selectIndex, shiftIndex) {
            var minSelectIndex = Math.min(selectIndex, shiftIndex);
            var maxSelectIndex = Math.max(selectIndex, shiftIndex);
            checkboxes.forEach(function(element, index) {
              var elementId = Number(element.attr('elementid'));
              if (index >= minSelectIndex && index <= maxSelectIndex) {
                // mark
                element.prop('checked', true);
                Utils.toggleListItem(true, elementId, list);
              }
            });
          }

          function getArrayIndex(elementId) {
            return checkboxes.findIndex(function(item) {
              return item.attr('id') === elementId;
            });
          }

          var selectIndex = getArrayIndex(list.lastSingleSelect);
          if (selectIndex === -1) {
            list.lastSingleSelect = 0;
          }
          var shiftIndex = getArrayIndex(currentShiftSelect);

          selectRange(selectIndex, shiftIndex);
        }
      },

      toggleListItem: function(state, id, list) {
        if (state) {
          var index = list.indexOf(id);
          if (index === -1) {
            list.push(id);
          }
        } else {
          var index = list.indexOf(id);
          if (index > -1) {
            list.splice(index, 1);
          }
        }
      },

      showOkDialog: function(title, fields, callback) {
        var dialogArea = document.getElementById('dialog');
        while (dialogArea.hasChildNodes()) {
          dialogArea.removeChild(dialogArea.lastChild);
        }

        fields.forEach(function(field) {
          var p = document.createElement('P');
          p.textContent = field;
          dialogArea.appendChild(p);
        });

        var dialog = jQuery('#dialog').dialog({
          autoOpen: true,
          width: 500,
          title: title,
          modal: true,
          buttons: {
            'OK': {
              id: 'ok',
              text: 'OK',
              click: function() {
                dialog.dialog("close");
                if (typeof callback == 'function') {
                  callback();
                }
              }
            }
          }
        });
      },

      showConfirmDialog: function(title, okButton, fields, callback, cancelCallback) {
        var dialogArea = document.getElementById('dialog');
        while (dialogArea.hasChildNodes()) {
          dialogArea.removeChild(dialogArea.lastChild);
        }

        fields.forEach(function(field) {
          var p = document.createElement('P');
          p.textContent = field;
          dialogArea.appendChild(p);
        });

        var buttons = {};
        buttons[okButton] = {
          id: 'ok',
          text: okButton,
          click: function() {
            dialog.dialog("close");
            callback();
          }
        };
        buttons["Cancel"] = {
          id: 'cancel',
          text: 'Cancel',
          click: function() {
            dialog.dialog("close");
            if (typeof cancelCallback == 'function') {
              cancelCallback();
            }
          }
        };
        var dialog = jQuery('#dialog').dialog({
          autoOpen: true,
          width: 500,
          title: title,
          modal: true,
          buttons: buttons
        });
      },
      showDialog: function(title, okButton, fields, callback, backHandler) {
        var dialogArea = document.getElementById('dialog');
        while (dialogArea.hasChildNodes()) {
          dialogArea.removeChild(dialogArea.lastChild);
        }

        var output = {};
        fields.forEach(function(field) {
          var p = document.createElement('P');
          var input;
          p.appendChild(document.createTextNode(field.label + (field.required ? "*" : "") + ": "));
          switch (field.type) {
          case 'select':
            if (field.values.length == 0) {
              return;
            }
            input = document.createElement('SELECT');
            field.values.forEach(function(value, index) {
              var option = document.createElement('OPTION');
              option.text = field.getLabel ? field.getLabel(value) : value;
              option.value = index;
              input.appendChild(option);
              if (field.value == option.text) {
                input.value = index;
                output[field.property] = value;
              }
            });
            input.onchange = function() {
              output[field.property] = field.values[parseInt(input.value)];
            };
            if (!field.value) {
              output[field.property] = field.values[0];
            }
            break;
          case 'text':
            input = document.createElement('INPUT');
            input.setAttribute('type', 'text');
            input.value = field.value || "";
            output[field.property] = field.value || "";
            input.onchange = function() {
              output[field.property] = input.value;
            };
            break;
          case 'textarea':
            input = document.createElement('TEXTAREA');
            input.setAttribute('rows', field.rows || 10);
            input.setAttribute('cols', field.cols || 40);
            input.value = field.value || "";
            output[field.property] = field.value || "";
            input.onchange = function() {
              output[field.property] = input.value;
            };
            break;
          case 'int':
            input = document.createElement('INPUT');
            input.setAttribute('type', 'text');
            input.value = field.value || 0;
            output[field.property] = field.value || 0;
            input.onchange = function() {
              output[field.property] = parseInt(input.value);
            };
            break;
          case 'float':
            input = document.createElement('INPUT');
            input.setAttribute('type', 'text');
            input.value = field.value || 0;
            output[field.property] = field.value || 0;
            input.onchange = function() {
              output[field.property] = parseFloat(input.value);
            };
            break;
          case 'date':
            input = document.createElement('INPUT');
            input.setAttribute('type', 'text');
            input.onchange = function() {
              output[field.property] = input.value;
            };
            jQuery(input).datepicker({
              dateFormat: 'yy-mm-dd',
              showButtonPanel: true,
            });
            break;
          case 'compare':
            input = document.createElement('DIV');
            var compareTypeControl = document.createElement('SELECT');
            var values = [{
              label: 'ignore',
              comparator: function(x, y) {
                return true;
              }
            }, {
              label: '>',
              comparator: function(x, y) {
                return x > y;
              }
            }, {
              label: '>=',
              comparator: function(x, y) {
                return x >= y;
              }
            }, {
              label: '=',
              comparator: function(x, y) {
                return x == y;
              }
            }, {
              label: '<=',
              comparator: function(x, y) {
                return x <= y;
              }
            }, {
              label: '<',
              comparator: function(x, y) {
                return x < y;
              }
            }];
            values.forEach(function(value, index) {
              var option = document.createElement('OPTION');
              option.text = field.getLabel ? field.getLabel(value.label) : value.label;
              option.value = index;
              compareTypeControl.appendChild(option);
            });
            compareTypeControl.value = 0;

            var valueControl = document.createElement('INPUT');
            valueControl.setAttribute('type', 'text');
            valueControl.value = 0;

            var update = function() {
              var y = parseFloat(valueControl.value);
              output[field.property] = isNaN(y) ? function(x) {
                return true;
              } : function(x) {
                return values[parseInt(compareTypeControl.value)].comparator(x, y);
              }
            }
            compareTypeControl.onchange = update;
            valueControl.onchange = update;
            update();

            input.appendChild(compareTypeControl);
            input.appendChild(valueControl);
            break; 
          default:
            throw "Unknown field type: " + field.type;
          }
          p.appendChild(input);
          dialogArea.appendChild(p);
        });

        var buttons = {};
        buttons[okButton] = {
          id: "ok",
          text: okButton,
          click: function() {
            var badFields = fields.filter(function(field) {
              return field.required && !output[field.property];
            }).map(function(field) {
              return field.label;
            });
            if (badFields.length) {
              alert("You must fill out the following fields: " + badFields.join(", "));
              return;
            }
            dialog.dialog("close");
            callback(output);
          }
        };
        if (backHandler) {
          buttons["Back"] = {
            id: "back",
            text: "Back",
            click: function() {
              dialog.dialog("close");
              backHandler();
            }
          };
        }
        buttons["Cancel"] = {
          id: "cancel",
          text: "Cancel",
          click: function() {
            dialog.dialog("close");
          }
        };
        var dialog = jQuery('#dialog').dialog({
          autoOpen: true,
          height: fields.reduce(function(length, field) {
            return length + (field.type == 'textarea' ? field.rows * 20 : 40);
          }, 200),
          width: 500,
          title: title,
          modal: true,
          buttons: buttons
        });
      },
      showWizardDialog: function(title, actions) {
        var dialogArea = document.getElementById('dialog');
        while (dialogArea.hasChildNodes()) {
          dialogArea.removeChild(dialogArea.lastChild);
        }

        actions.forEach(function(action) {
          var p = document.createElement('P');
          var link = document.createElement('A');
          var img = document.createElement('IMG');
          img.src = '/styles/images/arrow.svg';
          link.appendChild(img);
          link.appendChild(document.createTextNode(action.name));
          link.href = '#';
          link.onclick = function() {
            dialog.dialog("close");
            action.handler();
            return false;
          };
          p.appendChild(link);
          dialogArea.appendChild(p);
        });

        var dialog = jQuery('#dialog').dialog({
          autoOpen: true,
          height: actions.length * 40 + 200,
          width: 450,
          title: title,
          modal: true,
          buttons: {
            "Cancel": {
              id: 'cancel',
              text: 'Cancel',
              click: function() {
                dialog.dialog("close");
              }
            }
          }
        });
      },

      showAjaxErrorDialog: function(xhr, textStatus, errorThrown, errorCallback) {
        var lines = [errorThrown];
        try {
          var responseObj = JSON.parse(xhr.responseText);
          if (responseObj.detail) {
            lines = lines.concat(responseObj.detail.split('\n'));
            if (responseObj.dataFormat === 'validation') {
              jQuery.each(responseObj.data, function(key, val) {
                var errors = val.split('\n');
                jQuery.each(errors, function(index, error) {
                  lines.push('* ' + (key === 'GENERAL' ? error : (key + ": " + error)));
                });
              });
            }
          }
        } catch (e) {
          // If we got detail, great; if we didn't meh.
        }
        Utils.showOkDialog('Error', lines, errorCallback);
      },

      ajaxWithDialog: function(title, method, url, data, callback, errorCallback) {
        var dialogArea = document.getElementById('dialog');
        while (dialogArea.hasChildNodes()) {
          dialogArea.removeChild(dialogArea.lastChild);
        }
        var p = document.createElement('P');
        p.appendChild(document.createTextNode('Working...'));
        dialogArea.appendChild(p);

        var dialog = jQuery('#dialog').dialog({
          autoOpen: true,
          height: 400,
          width: 350,
          title: title,
          modal: true,
          buttons: {},
          closeOnEscape: false,
          open: function(event, ui) {
            jQuery(this).parent().children().children('.ui-dialog-titlebar-close').hide();
          }
        });
        jQuery.ajax({
          'dataType': 'json',
          'type': method,
          'url': url,
          'data': JSON.stringify(data),
          'contentType': 'application/json; charset=utf8',
          'success': function(data, textStatus, xhr) {
            dialog.dialog("close");
            callback(data, textStatus, xhr);
          },
          'error': function(xhr, textStatus, errorThrown) {
            dialog.dialog("close");
            Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown, errorCallback);
          }
        });
      },
      printDialog: function(type, ids) {
        Utils.ajaxWithDialog('Getting Printers', 'GET', window.location.origin + '/miso/rest/printer', null, function(printers) {
          Utils.showDialog('Select Printer', 'Print', [{
            "property": "printer",
            "label": "Printer",
            "required": true,
            "type": "select",
            "value": window.localStorage.getItem("miso-printer"),
            "values": printers.filter(function(printer) {
              return printer.available;
            }),
            "getLabel": Utils.array.getName
          }, {
            "property": "copies",
            "label": "Copies",
            "required": true,
            "type": "int",
            "value": 1
          }], function(result) {
            window.localStorage.setItem("miso-printer", result.printer.name);
            Utils.ajaxWithDialog('Printing', 'POST', window.location.origin + '/miso/rest/printer/' + result.printer.id + '?'
                + jQuery.param({
                  type: type,
                  ids: ids.join(','),
                  copies: Math.max(1, result.copies)
                }), null,
                function(result) {
                  Utils.showOkDialog('Printing', [result == ids.length ? 'Printing successful.'
                      : (result + ' of ' + ids.length + ' printed.')]);
                });
          }, null);
        });
      },

      /**
       * Helper method for extracting the components of a BoxPosition into integers e.g. "BOX3 A01" gets converted to [65, 1]
       */
      getBoxPositionAsIntArray: function(boxPosnString) {
        var posnRegex = /.*([A-Z])(\d{2})$/;
        var posn = posnRegex.exec(boxPosnString);
        var rowVal = posn[1].charCodeAt(0);
        var colVal = parseInt(posn[2]);
        return {
          row: rowVal,
          col: colVal
        };
      },

      valOrNull: function(val) {
        if (val === 0) {
          return 0;
        }
        return val || null;
      },

      // Return current date in format YYYY-MM-DD
      getCurrentDate: function() {
        var today = new Date();
        var dd = today.getDate();
        var mm = today.getMonth() + 1; // Month is zero-indexed
        var yyyy = today.getFullYear();

        // Zero-pad month and day if necessary
        return yyyy + "-" + (mm < 10 ? "0" + mm : mm) + "-" + (dd < 10 ? "0" + dd : dd);
      },
      createBoxDialog: function(result, getItemCount, callback){
        var boxFields = [{
          property: 'alias',
          type: 'text',
          label: 'Alias',
          value: '',
          required: true
        }, {
          property: 'use',
          type: 'select',
          label: 'Box Use',
          values: Constants.boxUses,
          getLabel: Utils.array.getAlias,
          required: true
        }, {
          property: 'size',
          type: 'select',
          label: 'Box Size',
          values: Constants.boxSizes,
          getLabel: function(boxSize){
            return boxSize.scannable ? boxSize.rowsByColumns + " (Scannable)" : boxSize.rowsByColumns;
          },
          required: true
        }, {
          property: 'matrixBarcode',
          type: 'text',
          label: 'Matrix Barcode',
          value: ''
        }];
        var createBoxDto = function(boxFields){
          var box = {};
          box['alias'] = boxFields['alias'];
          box['description'] = boxFields['description'] || '';
          box['sizeId'] = boxFields['size'].id;
          box['useId'] = boxFields['use'].id;
          box['identificationBarcode'] = boxFields['matrixBarcode'];
          box['locationBarcode'] = boxFields['locationBarcode'];
          box['tubeCount'] = 0;
          box['storageLocationBarcode'] = boxFields['storageLocationBarcode'];
          return box;
        }
        Utils.showDialog('Create Box', 'Create', boxFields, function(boxResult) {             
          var box = createBoxDto(boxResult);
          var boxSize = Utils.array.findFirstOrNull(function(size) {
            return size.id == box.sizeId;
          }, Constants.boxSizes);
          if(getItemCount(result) > boxSize.rows * boxSize.columns){
            Utils.showOkDialog('Error', ['The box is too small for the number of items.',
              'If this is intended, please create the box manually before entering the data.']);
            return;
          }
          Utils.ajaxWithDialog('Creating Box', 'POST', '/miso/rest/box', box, callback);
        });
      },
      getEmptyBoxPositions: function(box){
        var occupied = box.items.map(function(item) {
          return item.coordinates;
        });
        var free = [];
        for (var row = 0; row < box.rows; row++) {
          for (var col = 0; col < box.cols; col++) {
            var pos = String.fromCharCode(65 + row) + (col < 9 ? '0' : '') + (col + 1);
            if (occupied.indexOf(pos) === -1) {
              free.push(pos);
            }
          }
        }
        return free;
      }
    };

Utils.timer = {
  timedFunc: function() {
    var timer;
    return function(func, time) {
      clearTimeout(timer);
      timer = setTimeout(func, time);
    };
  },

  typewatchFunc: function(obj, func, wait, capturelength) {
    var options = {
      callback: func,
      wait: wait,
      highlight: true,
      captureLength: capturelength
    };
    jQuery(obj).typeWatch(options);
  },

  queueFunctions: function(funcs) {
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
};

Utils.ui = {

  checkAll: function(field) {
    var self = this;
    for (var i = 0; i < self._N(field).length; i++) {
      self._N(field)[i].checked = true;
    }
  },

  checkAllConfirm: function(field, message) {
    if (confirm(message)) {
      var self = this;
      for (var i = 0; i < self._N(field).length; i++) {
        self._N(field)[i].checked = true;
      }
    }
  },

  uncheckAll: function(field) {
    var self = this;
    for (var i = 0; i < self._N(field).length; i++) {
      self._N(field)[i].checked = false;
    }
  },

  uncheckOthers: function(field, item) {
    var self = this;
    for (var i = 0; i < self._N(field).length; i++) {
      if (self._N(field)[i] != item) {
        self._N(field)[i].checked = false;
      }
    }
  },

  _N: function(element) {
    if (typeof element == 'string') {
      element = document.getElementsByName(element);
    }
    return Element.extend(element);
  },

  toggleElement: function(id) {
    jQuery("#" + id).slideToggle({
      'duration': 500
    });
  },

  toggleRightInfo: function(div, id) {
    if (jQuery(div).hasClass("toggleRight")) {
      jQuery(div).removeClass("toggleRight").addClass("toggleRightDown");
    } else {
      jQuery(div).removeClass("toggleRightDown").addClass("toggleRight");
    }
    Utils.ui.toggleElement(id);
  },

  toggleLeftInfo: function(div, id) {
    if (jQuery(div).hasClass("toggleLeft")) {
      jQuery(div).removeClass("toggleLeft").addClass("toggleLeftDown");
    } else {
      jQuery(div).removeClass("toggleLeftDown").addClass("toggleLeft");
    }
    Utils.ui.toggleElement(id);
  },

  collapseClass: function(class_) {
    jQuery('.' + class_).slideUp({
      'duration': 500
    });
  },

  goodDateFormat: 'yy-mm-dd',

  addDatePicker: function(id) {
    jQuery("#" + id).datepicker({
      dateFormat: Utils.ui.goodDateFormat,
      showButtonPanel: true
    });
  },

  addMaxDatePicker: function(id, maxDateOffset) {
    jQuery("#" + id).datepicker({
      dateFormat: Utils.ui.goodDateFormat,
      showButtonPanel: true,
      maxDate: maxDateOffset
    });
  },

  addDateTimePicker: function(id) {
    jQuery("#" + id).datetimepicker({
      controlType: 'select',
      oneLine: true,
      dateFormat: Utils.ui.goodDateFormat,
      timeFormat: 'HH:mm:ss'
    });
  },

  disableButton: function(buttonDiv) {
    jQuery('#' + buttonDiv).attr('disabled', 'disabled');
    jQuery('#' + buttonDiv).html("Processing...");
  },

  reenableButton: function(buttonDiv, text) {
    jQuery('#' + buttonDiv).removeAttr('disabled');
    jQuery('#' + buttonDiv).html(text);
  },

  confirmRemove: function(obj) {
    if (confirm("Are you sure you wish to remove this item?")) {
      obj.remove();
    }
  },

  escape: function(obj, callback) {
    return obj.each(function() {
      jQuery(document).on("keydown", obj, function(e) {
        var keycode = ((typeof e.keyCode != 'undefined' && e.keyCode) ? e.keyCode : e.which);
        if (keycode === 27) {
          callback.call(obj, e);
        }
      });
    });
  },

  setDisabled: function(selector, disabled) {
    var element = jQuery(selector);
    element.prop('disabled', disabled);
    if (disabled) {
      element.addClass('disabled');
    } else {
      element.removeClass('disabled');
    }
  },

};

Utils.fileUpload = {
  fileUploadProgress: function(formname, divname, successfunc) {
    Fluxion.doAjaxUpload(formname, 'fileUploadProgressBean', 'checkUploadStatus', {
      'url': ajaxurl
    }, {
      'statusElement': divname,
      'progressElement': 'trash',
      'doOnSuccess': successfunc
    }, {
      '': ''
    });
  },

  processingOverlay: function() {
    jQuery.colorbox({
      width: "30%",
      html: "Processing..."
    });
  }
};

Utils.validation = {
  dateRegex: '^(19|20)[0-9]{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$',
  dateTimeRegex: '^(19|20)[0-9]{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$',
  sanitizeRegex: '^[^<>&]*$',
  alphanumRegex: '^[-_\\w]*$',
  unicodeWordRegex: '^[\\p{L}0-9_\\^\\-\\.\\s]+$',
  _unicodeWord: XRegExp('^[\\p{L}0-9_\\^\\-\\.\\s]+$'),

  isNullCheck: function(value) {
    return (value === "" || value === " " || value === "undefined" || value === "&nbsp;" || value === undefined);
  },

  validate_input_field: function(field, name, okstatus) {
    var self = this;
    var errormsg = '';
    if (!self._unicodeWord.test(jQuery(field).val())) {
      okstatus = false;
      errormsg = "In the " + name + " " + jQuery(field).attr("id")
          + " field you CAN use alpha numeric values with the following symbols:\n" + "^ - _ .\n"
          + "but you CANNOT use slashes, comma, brackets, single or double quotes, and it CANNOT end with a space or be empty\n\n";
    }
    return {
      "okstatus": okstatus,
      "errormsg": errormsg
    };
  },

  // Clean input fields by removing leading and trailing whitespace
  clean_input_field: function(field) {
    var oldval = field.val();
    field.val(oldval.trim(oldval));
  },
  isEmpty: function(value) {
    return value === undefined || value === null || value === '';
  },

  hasNoSpecialChars: function(value) {
    var regex = new RegExp(Utils.validation.sanitizeRegex);
    return regex.test(value);
  },
};

Utils.page = {
  pageReload: function() {
    window.location.reload(true);
  },

  newWindow: function(url) {
    newwindow = window.open(url, 'name', 'height=500,width=500,menubar=yes,status=yes,scrollbars=yes');
    if (window.focus) {
      newwindow.focus();
    }
    return false;
  },

  pageRedirect: function(url) {
    window.location = url;
  }
};

Utils.array = {
  /**
   * Gets item's alias
   */
  getAlias: function(obj) {
    if (obj.alias)
      return obj.alias;
  },

  getId: function(obj) {
    return obj.id;
  },

  getName: function(obj) {
    if (obj.name)
      return obj.name;
  },

  /**
   * Gets values for a given key
   */
  getValues: function(key, objArr) {
    return objArr.map(function(obj) {
      return obj[key];
    });
  },

  findFirstOrNull: function(predicate, referenceCollection) {
    var results = referenceCollection.filter(predicate);
    return results.length > 0 ? results[0] : null;
  },
  findUniqueOrThrow: function(predicate, referenceCollection) {
    var results = referenceCollection.filter(predicate);
    if (results.length != 1) {
      throw 'element not found';
    } else {
      return results[0];
    }
  },
  maybeGetProperty: function(obj, propertyName) {
    return obj ? obj[propertyName] : null;
  },
  aliasPredicate: function(alias) {
    return function(item) {
      return item.alias == alias;
    };
  },
  idPredicate: function(id) {
    return function(item) {
      return item.id == id;
    };
  },
  namePredicate: function(name) {
    return function(item) {
      return item.name == name;
    };
  },
  descriptionPredicate: function(description) {
    return function(item) {
      return item.description == description;
    }
  },
  /**
   * Gets the object from a given id and collection
   */
  getObjById: function(id, referenceCollection) {
    return Utils.array.findFirstOrNull(Utils.array.idPredicate(id), referenceCollection);
  },
  /**
   * Gets the id of an object from a given alias and collection
   */
  getIdFromAlias: function(alias, referenceCollection) {
    return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.aliasPredicate(alias), referenceCollection), 'id');
  },

  /**
   * Gets the alias of an object from a given id and collection
   */
  getAliasFromId: function(id, referenceCollection) {
    return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(id), referenceCollection), 'alias');
  },
  deduplicateNumeric: function(input) {
    return input.sort(function(a, b) {
      return a - b;
    }).filter(function(obj, index, arr) {
      return index == 0 || obj !== arr[index - 1];
    });
  },
  deduplicateString: function(input) {
    return input.sort(function(a, b) {
      return a.localeCompare(b);
    }).filter(function(obj, index, arr) {
      return index == 0 || obj !== arr[index - 1];
    });
  },
  deduplicateById: function(input) {
    return input.sort(function(a, b) {
      return a.id - b.id;
    }).filter(function(obj, index, arr) {
      return index == 0 || obj.id != arr[index - 1].id;
    });
  },
  removeArchived: function(input) {
    return input.filter(function(a) {
      return !a.archived;
    });
  }
};

Utils.sorting = {
  /**
   * Sorts based on a given property.
   */
  standardSort: function(property) {
    return function(a, b) {
      var first = a[property];
      var second = b[property];
      if (typeof first == 'number' && typeof second == 'number') {
        return first > second ? 1 : (second > first ? -1 : 0);
      } else if (typeof first == 'string' && typeof second == 'string') {
        return first.localeCompare(second);
      } else if (typeof first == 'function' || typeof second == 'function') {
        throw 'Cannot compare function definitions; both are special in their own way';
      } else {
        // mixed case; make a best guess
        return ('' + first).localeCompare('' + second);
      }
    };
  },

  /**
   * Sorts by comparing the first two arguments, then the second two if the first two are identical. All arguments must be integers.
   */
  twoByTwoComparator: function(a, b, y, z) {
    return (a - b) || (y - z);
  },

  /**
   * Takes two strings, extracts the BoxPosition string from each, and compares the BoxPosition using a given comparator.
   */
  sortBoxPositions: function(a, b, sortOnRows) {
    var aValues = Utils.getBoxPositionAsIntArray(a);
    var aRow = aValues.row, aCol = aValues.col;
    var bValues = Utils.getBoxPositionAsIntArray(b);
    var bRow = bValues.row, bCol = bValues.col;
    if (!aRow || !bRow || !aCol || !bCol) {
      // don't know what to do with locations not like 'A01'-'Q33'
      return a.localeCompare(b);
    }
    // assumption: users always want to sort BoxPosition ascending
    if (sortOnRows) {
      // compare by rows first, then by columns if rows are the same
      return Utils.sorting.twoByTwoComparator(aRow, bRow, aCol, bCol);
    } else {
      // compare by columns first, then by rows if columns are the same
      return Utils.sorting.twoByTwoComparator(aCol, bCol, aRow, bRow);
    }
  },

  sampleClassComparator: function(a, b) {
    return (Constants.sampleCategories.indexOf(a.sampleCategory) - Constants.sampleCategories.indexOf(b.sampleCategory))
        || a.alias.localeCompare(b.alias);
  }
};
