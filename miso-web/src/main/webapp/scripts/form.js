FormUtils = (function($) {

  /*
   * FormTarget Structure: {
   *   getSaveUrl: required function(object, config) returning string; URL to save object
   *   getSaveMethod: required function(object) returning string (POST|PUT); HTTP method to save object
   *   getEditUrl: required function(object, config) returning string; URL for the object's edit page
   *   getSections: required function(config, object) returning array of FormSections; see below
   *   onLoad: optional function(updateField); called after the form is initialized
   *   confirmSave: optional function(object, saveCallback); called before saving. saveCallback must be invoked to confirm and save
   * }
   * 
   * FormSection Structure: {
   *   include: optional boolean; determines whether the section should be displayed. Section is displayed by default
   *   title: required string; Section heading
   *   getFields: required function(config) returning array of Fields. See below
   * }
   * 
   * Field Structure: {
   *   title: string; required except for hidden fields; Field name to display
   *   data: required string; JSON property to use for value. Also used as the input ID
   *   getDisplayValue: optional function(object) returning string; generate a value to display in a read-only field instead of the
   *       data value
   *   getLink: optional function(object) returning URL string; generate a link URL for read-only field
   *   openNewTab: optional boolean (default: false); whether to open links in new tab
   *   type: required string (read-only|text|textarea|password|dropdown|checkbox|int|decimal|date|datetime|special); type of field. Note:
   *       read-only means not directly editable. The value may still be changed via javascript, and that updated value will be validated
   *       and saved
   *   include: optional boolean (default: true); determines whether the field is displayed
   *   omit: optional boolean (default: false); determines whether field is saved. Field is saved by default. If true, the data property
   *       doesn't need to exist in the JSON, and won't be updated even if it does
   *   initial: optional string; value to initialize field value to for new items
   *   disabled: optional boolean (default: false); whether the field is disabled
   *   required: optional boolean (default: false); whether the field is required
   *   maxLength: optional integer; maximum number of characters for text input
   *   regex: optional regex; validation regex for text input. Can be 'url', 'email', or an actual regex
   *   min: minimum value for int or decimal input
   *   max: maximum value for int or decimal input
   *   precision: maximum precision (length, excluding the decimal) for decimal input
   *   scale: maximum scale (decimal places) for decimal input
   *   nullLabel: optional string; label for null value in dropdown. If not provided, and the field is not required, there will be no
   *       null value in the dropdown - it will default to the first option unless an initial value is specified
   *   getSource: function() returning array of objects; required for dropdown fields; Provides dropdown options
   *   convertToBoolean: optional boolean (default: false); if true, dropdown values 'true' and 'false' will be converted to booleans
   *   sortSource: optional function(a, b); sort function for dropdown options
   *   getItemLabel: function(item) returning string; get the label for a dropdown option. If omitted and the item is a string, it is
   *       used as the label; otherwise, an error is thrown
   *   getItemValue: function(item) returning string; get the value for a dropdown option. If omitted, the item is used as the value
   *   onChange: function(newValue, formObject); allows modifying other fields when a dropdown type field value is changed. See Form
   *       object below
   *   makeControls: function(form) returning single or array of jQuery controls; required for special fields; set up special fields
   *   trackChanges: optional boolean (default: true); whether changes to the field should be tracked. This only affects the warning that
   *       appears when leaving a page with unsaved changes
   * }
   * 
   * Form object: {
   *   isChanged: function() returns true if the form has been modified by the user; false otherwise
   *   markOtherChanges: function() marks the form as having been modified. This is automatic for the normal form fields, but is
   *       necessary for other page elements
   *   get: function(dataProperty) returns the field value
   *   updateField: function(dataProperty, options). options may include
   *       * 'disabled' (boolean)
   *       * 'required' (boolean)
   *       * 'value' (string/number/boolean depending on field type)
   *       * 'source' (array of objects or strings)
   *       * 'label' (string)
   *       * 'link' (URL string)
   *   save: function(postSaveCallback) save the form data. postSaveCallback is a function(data) where data is the object returned from
   *       the save operation
   * }
   */

  var defaultDecimalPrecision = 21;
  var defaultDecimalScale = 17;

  var dialogId = 'dialog';
  var dialogFormId = 'dialogForm';

  var changeOrder = {
    disabled: 1,
    required: 1,
    source: 2,
    value: 3,
    label: 3,
    link: 3
  };

  var units = {
    volume: {
      title: 'Volume Units',
      data: 'volumeUnits',
      source: Constants.volumeUnits
    },
    concentration: {
      title: 'Concentration Units',
      data: 'concentrationUnits',
      source: Constants.concentrationUnits
    }
  };

  return {
    createForm: function(containerId, saveId, object, targetName, config) {
      var container = $('#' + containerId);
      var target = FormTarget[targetName];
      if (!object) {
        object = {};
      }

      writeGeneralValidationBox(container);
      var sections = getFilteredSections(target, config, object);
      var form = makeFormObject(containerId, sections, object, target, config);

      sections.forEach(function(section) {
        writeSection(container, section, object, form);
      });

      if (saveId) {
        $('#' + saveId).click(function() {
          form.save();
        });
      }

      sections.forEach(function(section) {
        section.fields.forEach(function(field) {
          if (field.onChange) {
            field.onChange(getFormValue(containerId, field), form);
          }
        });
      });
      if (target.onLoad) {
        target.onLoad(form.updateField);
      }

      if (containerId !== dialogFormId) {
        window.onbeforeunload = function() {
          if (form.isChanged()) {
            return ''; // will cause a dialog asking whether to leave with unsaved changes
          } else {
            return undefined; // will prevent the confirm dialog
          }
        };
      }

      form.setUnchanged();

      return form;
    },

    createFormDialog: function(title, object, targetName, config, postSaveCallback) {
      var dialog = $('#' + dialogId);
      dialog.empty();
      dialog.append($('<form>').attr('id', dialogFormId).attr('data-parsley-validate', '').attr('autocomplete', 'off').attr(
          'acceptCharset', 'utf-8'));
      var form = FormUtils.createForm(dialogFormId, null, object, targetName, config);

      var target = FormTarget[targetName];
      var sections = getFilteredSections(target, config, object);

      dialog.dialog({
        autoOpen: true,
        width: 500,
        title: title,
        modal: true,
        buttons: {
          'Save': {
            id: 'ok',
            text: 'Save',
            click: function() {
              form.save(postSaveCallback);
            }
          }
        }
      });
    },

    makeQcPassedField: function(include) {
      return {
        title: 'QC Passed',
        data: 'qcPassed',
        type: 'dropdown',
        getSource: function() {
          return [{
            label: 'True',
            value: true
          }, {
            label: 'False',
            value: false
          }];
        },
        convertToBoolean: true,
        getItemLabel: function(item) {
          return item.label;
        },
        getItemValue: function(item) {
          return item.value;
        },
        include: include,
        nullLabel: 'Unknown'
      };
    },

    makeUnitsField: function(object, unitType) {
      var unit = units[unitType];
      if (!unit) {
        throw new Error('Unknown unit type: ' + unitType);
      }
      return {
        title: unit.title,
        data: unit.data,
        type: 'dropdown',
        getSource: function() {
          return unit.source;
        },
        getItemLabel: function(item) {
          return Utils.decodeHtmlString(item.units);
        },
        getItemValue: function(item) {
          return item.name;
        },
        required: true,
        trackChanges: !!object[unitType]
      }
    },

    makeBoxLocationField: function() {
      return {
        title: 'Box Location',
        data: 'boxPosition',
        type: 'read-only',
        getDisplayValue: function(object) {
          if (!object.box) {
            return 'n/a';
          }
          var location = '';
          if (object.box.locationBarcode) {
            location += object.box.locationBarcode + ', ';
          }
          location += object.box.alias + ' ' + object.boxPosition;
          return location;
        },
        getLink: function(object) {
          return object.box ? ('/miso/box/' + object.box.id) : null;
        }
      }
    },

    makeDistributionFields: function() {
      return [{
        title: 'Distributed',
        data: 'distributed',
        type: 'checkbox',
        onChange: function(newValue, form) {
          var options = {
            disabled: !newValue,
            required: newValue
          }
          if (!newValue) {
            options.value = '';
          }
          form.updateField('distributionDate', options);
          form.updateField('distributionRecipient', options);
        }
      }, {
        title: 'Distribution Date',
        data: 'distributionDate',
        type: 'date'
      }, {
        title: 'Distribution Recipient',
        data: 'distributionRecipient',
        type: 'text',
        maxLength: 250
      }];
    }
  };

  function makeFormObject(containerId, sections, object, target, config) {
    var form;
    var original = {};
    var otherChanges = false;

    function updateOriginal() {
      otherChanges = false;
      sections.forEach(function(section) {
        section.fields.forEach(function(field) {
          if (shouldTrackField(field)) {
            original[field.data] = getFormValue(containerId, field);
          }
        });
      });
    }

    function isSetInForm(containerId, field) {
      var val = getFormValue(containerId, field);
      return val != null && val != '';
    }

    function isSetInObject(object, property) {
      return object.hasOwnProperty(property) && object[property] != null;
    }

    function triggerUpdate(field) {
      field.onChange(getFormValue(containerId, field), form);
    }

    function shouldTrackField(field) {
      return !field.omit && field.trackChanges !== false && field.type !== 'special';
    }

    form = {
      setUnchanged: updateOriginal,
      isChanged: function() {
        if (otherChanges) {
          return true;
        }
        var changed = false;
        sections.forEach(function(section) {
          section.fields.forEach(function(field) {
            if (shouldTrackField(field) && (isSetInForm(containerId, field) || isSetInObject(original, field.data))
                && getFormValue(containerId, field) != original[field.data]) {
              changed = true;
            }
          });
        });
        return changed;
      },
      markOtherChanges: function() {
        otherChanges = true;
      },
      get: function(dataProperty) {
        return getFormValue(containerId, findField(sections, dataProperty));
      },
      updateField: function(dataProperty, options) {
        var field = findField(sections, dataProperty);
        var inputSelector = '#' + makeInputId(containerId, field.data);
        Object.keys(options).sort(sortChanges).forEach(
            function(option) {
              switch (option) {
              case 'disabled':
                Utils.ui.setDisabled(inputSelector, options.disabled);
                break;
              case 'value':
                setFormValue(containerId, field, options.value, object);
                break;
              case 'required':
                field.required = options.required;
                $(inputSelector).closest('tr').children().first().text(getFieldLabelText(field));
                break;
              case 'source': {
                if (field.type !== 'dropdown') {
                  throw new Error('Cannot set source for non-dropdown field \'' + field.title + '\'');
                }
                field.getSource = function() {
                  return options.source;
                }
                var originalValue = getFormValue(containerId, field);
                var select = $(inputSelector);
                select.empty();
                addDropdownOptions(field, select);
                var newOption = $(inputSelector + ' option[value="' + originalValue + '"]');
                if (newOption.length) {
                  select.val(originalValue);
                }
                break;
              }
              case 'label': {
                if (field.type !== 'read-only' || !field.getDisplayValue) {
                  throw new Error('Cannot set label for field \'' + field.title + '\'');
                }
                $('#' + makeInputId(containerId, field.data) + 'Label').text(options.label);
                break;
              }
              case 'link': {
                var label = $('#' + makeInputId(containerId, field.data) + 'Label');
                if (field.type !== 'read-only' || !label.length) {
                  throw new Error('Cannot set link for field \'' + field.title + '\'');
                }
                if (options.link) {
                  var newLink = $('<a>').attr('id', makeInputId(containerId, field.data) + 'Label').attr('href', options.link).text(
                      label.text());
                  if (field.openNewTab) {
                    newLink.attr('target', '_blank');
                  }
                  label.replaceWith(newLink);
                } else {
                  label.replaceWith($('<span>').attr('id', makeInputId(containerId, field.data) + 'Label').text(label.text()));
                }
                break;
              }
              default:
                throw new Error('Invalid field update option: ' + option);
              }
            });
        if (field.onChange) {
          triggerUpdate(field);
        }
      },
      save: function(postSaveCallback) {
        validateAndSave(containerId, object, target, sections, config, function(data) {
          updateOriginal();
          if (postSaveCallback) {
            postSaveCallback(data);
          } else {
            window.location.href = target.getEditUrl(data, config);
          }
        });
      }
    };
    return form;
  }

  function getFormValue(containerId, field) {
    var control = $('#' + makeInputId(containerId, field.data));
    switch (field.type) {
    case 'read-only':
      return field.getDisplayValue ? control.val() : control.text();
    case 'text':
    case 'textarea':
    case 'password':
    case 'date':
    case 'datetime':
    case 'decimal':
      return control.val() !== null && control.val().length ? control.val() : null;
    case 'dropdown': {
      var val = control.val() !== null && control.val().length ? control.val() : null;
      if (field.convertToBoolean) {
        if (val === 'true') {
          return true;
        } else if (val === 'false') {
          return false;
        }
      }
      return val;
    }
    case 'int':
      return control.val().length ? Number(control.val()) : null;
    case 'checkbox':
      return control.is(':checked');
    default:
      throw new Error('Can\'t get value of field with type ' + field.type);
    }
  }

  function setFormValue(containerId, field, value, object) {
    var inputSelector = '#' + makeInputId(containerId, field.data);
    switch (field.type) {
    case 'text':
    case 'textarea':
    case 'password':
    case 'dropdown':
    case 'date':
    case 'datetime':
    case 'decimal':
    case 'int':
      $(inputSelector).val(value);
      break;
    case 'checkbox':
      $(inputSelector).prop('checked', value);
      break;
    case 'read-only':
      // Note: only the value is updated; display value and/or link url must be updated separately if applicable
      if (field.getDisplayValue) {
        $(inputSelector).val(value);
      } else {
        $(inputSelector).text(value);
      }
      break;
    default:
      throw new Error('Can\'t set value of field with type ' + field.type);
    }
  }

  function findField(sections, dataProperty) {
    var fields = sections.map(function(section) {
      return section.fields;
    }).reduce(function(all, current) {
      return all.concat(current);
    }).filter(function(field) {
      return field.data === dataProperty;
    });

    if (!fields.length) {
      throw new Error('No field found for data property: ' + dataProperty);
    } else if (fields.length > 1) {
      throw new Error('Multiple fields found for data property: ' + dataProperty);
    }
    return fields[0];
  }

  function sortChanges(a, b) {
    return (changeOrder[a] || 0) - (changeOrder[b] || 0);
  }

  function validateAndSave(containerId, object, target, sections, config, postSaveCallback) {
    var selector = '#' + containerId;
    Validate.removeValidation(selector + ' .form-control');
    Validate.cleanFields(selector);
    Validate.clearErrors(selector);

    sections.forEach(function(section) {
      section.fields.forEach(function(field) {
        if (!field.omit && field.type !== 'special') { // FormTarget is responsible for managing updates, likely via an additional hidden field
          object[field.data] = getFormValue(containerId, field);
        }
        if (['text', 'textarea', 'password', 'dropdown', 'date', 'datetime', 'decimal', 'int'].indexOf(field.type) !== -1) {
          addValidation(containerId, field);
        }
      });
    });

    $(selector).parsley();
    $(selector).parsley().validate();

    Validate.updateWarningOrSubmit(selector, null, function() {
      var saveCallback = function() {
        save(containerId, object, target, config, postSaveCallback);
      };

      if (target.confirmSave) {
        target.confirmSave(object, saveCallback);
      } else {
        saveCallback();
      }
    });
  }

  function addValidation(containerId, field) {
    var inputId = makeInputId(containerId, field.data);
    var control = $('#' + inputId).addClass('form-control').attr('data-parsley-errors-container', '#' + inputId + 'Error');
    if (field.required) {
      control.attr('data-parsley-required', true);
    }
    if (field.maxLength) {
      control.attr('data-parsley-maxlength', field.maxLength);
    }
    if (field.type === 'text' || field.type === 'textarea' || field.type === 'password') {
      if (field.regex) {
        switch (field.regex) {
        case 'url':
        case 'email':
          control.attr('data-parsley-type', field.regex);
          break;
        default:
          control.attr('data-parsley-pattern', field.regex);
          break;
        }
      } else {
        control.attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
      }
    } else if (field.type === 'date') {
      control.attr('data-date-format', 'YYYY-MM-DD');
      control.attr('data-parsley-pattern', Utils.validation.dateRegex);
      control.attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');
    } else if (field.type === 'datetime') {
      control.attr('data-date-format', 'YYYY-MM-DD hh:mm:ss');
      control.attr('data-parsley-pattern', Utils.validation.dateTimeRegex);
      control.attr('data-parsley-error-message', 'Time must be of form YYYY-MM-DD hh:mm:ss');
    } else if (field.type === 'int') {
      control.attr('data-parsley-type', 'integer');
      if (field.hasOwnProperty('min')) {
        if (field.hasOwnProperty('max')) {
          control.attr('data-parsley-range', '[' + field.min + ',' + field.max + ']');
        } else {
          control.attr('data-parsley-min', field.min);
        }
      } else if (field.hasOwnProperty('max')) {
        control.attr('data-parsley-max', field.min);
      }
    } else if (field.type === 'decimal') {
      var precision = field.precision || defaultDecimalPrecision;
      var scale = field.scale || defaultDecimalScale;
      control.attr('data-parsley-type', 'number');
      control.attr('data-parsley-maxlength', precision + 1);
      var maxPossible = Math.pow(10, precision - scale) - Math.pow(0.1, scale);
      var max = field.hasOwnProperty('max') ? field.max : maxPossible;
      var min = field.hasOwnProperty('min') ? field.min : maxPossible * -1;
      control.attr('data-parsley-range', '[' + min + ', ' + max + ']')
      var pattern = '\\d{0,' + (precision - scale) + '}(?:\\.\\d{1,' + scale + '})?';
      control.attr('data-parsley-pattern', pattern);
      control.attr('data-parsley-error-message', 'Must be a number between ' + min + ' and ' + max);
    }
    if (field.match) {
      control.attr('data-parsley-equalto', '#' + makeInputId(containerId, field.match));
    }
  }

  function save(containerId, object, target, config, postSaveCallback) {
    $.ajax({
      url: target.getSaveUrl(object, config),
      type: target.getSaveMethod(object),
      dataType: 'json',
      contentType: 'application/json; charset=utf8',
      data: JSON.stringify(object)
    }).success(function(data) {
      postSaveCallback(data);
    }).fail(function(response, textStatus, serverStatus) {
      if (containerId === dialogFormId) {
        Utils.showAjaxErrorDialog(response, textStatus, serverStatus);
      } else {
        Validate.displayErrors(JSON.parse(response.responseText), containerId);
      }
    });
  }

  function writeGeneralValidationBox(container) {
    container.append($('<div>').addClass('bs-callout bs-callout-warning hidden').append($('<h2>').text('Oh snap!')).append(
        $('<p>').text('This form seems to be invalid')).append($('<div>').addClass('generalErrors')));
  }

  function getFilteredSections(target, config, object) {
    var filtered = [];
    target.getSections(config, object).filter(function(section) {
      return !section.hasOwnProperty('include') || section.include;
    }).forEach(function(section) {
      var fields = section.fields.filter(function(field) {
        return !field.hasOwnProperty('include') || field.include;
      });
      if (fields.length) {
        filtered.push({
          title: section.title,
          fields: fields
        });
      }
    });
    return filtered;
  }

  function writeSection(container, section, object, form) {
    container.append($('<h2>').text(section.title));
    var tbody = $('<tbody>');

    section.fields.forEach(function(field) {
      var tr = $('<tr>');
      tr.append(makeFieldLabel(field));
      tr.append(makeFieldInput(container.attr('id'), field, object, form));
      tbody.append(tr);
    });

    container.append($('<div>').attr('id', section.id).append($('<table>').addClass('in').append(tbody)));

    var containerId = container.attr('id');
    section.fields.forEach(function(field) {
      var inputId = makeInputId(containerId, field.data);
      if (field.type === 'date') {
        $('#' + inputId).attr('placeholder', 'YYYY-MM-DD');
        Utils.ui.addDatePicker(inputId);
      } else if (field.type === 'datetime') {
        $('#' + inputId).attr('placeholder', 'YYYY-MM-DD hh:mm:ss');
        Utils.ui.addDateTimePicker(inputId);
      }
      if (field.disabled) {
        Utils.ui.setDisabled('#' + inputId, true);
      }
    });
  }

  function makeFieldLabel(field) {
    return $('<td>').addClass('h').text(getFieldLabelText(field));
  }

  function getFieldLabelText(field) {
    return field.title + ':' + (field.required ? '*' : '');
  }

  function makeFieldInput(containerId, field, object, form) {
    var td = $('<td>');
    var value = getDisplayValue(field, object);

    if (field.getDisplayValue) {
      // we're displaying something different, so put the actual data in a hidden field
      var hidden = $('<input>').attr('id', makeInputId(containerId, field.data)).attr('type', 'hidden');
      if (object[field.data]) {
        hidden.val(object[field.data]);
      }
      td.append(hidden);
    }

    var inputId = makeInputId(containerId, field.data);
    switch (field.type) {
    case 'read-only':
      td.append(makeReadOnlyInput(inputId, field, value, object));
      break;
    case 'text':
    case 'int':
    case 'decimal':
    case 'date': // treat as text for now. date picker gets added later
    case 'datetime':
      td.append(makeTextInput(inputId, field, value, 'text', form));
      break;
    case 'textarea':
      td.append(makeTextareaInput(inputId, field, value));
      break;
    case 'password':
      td.append(makeTextInput(inputId, field, value, 'password', form));
      break;
    case 'dropdown':
      td.append(makeDropdownInput(inputId, field, value, form));
      break;
    case 'checkbox':
      td.append(makeCheckboxInput(inputId, field, value, form));
      break;
    case 'special':
      td.append(makeSpecialInput(field, value, form));
      break;
    default:
      throw new Error('Unknown field type: ' + field.type);
    }
    if (field.note) {
      td.append(makeFieldNote(field));
    }
    if (field.type !== 'special') {
      td.append(makeFieldValidationBox(inputId, field));
    }
    return td;
  }

  function makeInputId(containerId, dataField) {
    return containerId + '_' + dataField;
  }

  function getDisplayValue(field, object) {
    var value = null;
    if (field.getDisplayValue) {
      value = field.getDisplayValue(object);
    } else if (object.hasOwnProperty(field.data)) {
      value = object[field.data];
    }
    if (value === null && field.hasOwnProperty('initial')) {
      value = field.initial;
    }
    return value;
  }

  function makeReadOnlyInput(inputId, field, value, item) {
    var isLink = field.getLink && field.getLink(item);
    var input = $(isLink ? '<a>' : '<span>').attr('id', inputId + (field.getDisplayValue ? 'Label' : ''));
    if (value !== null) {
      input.text(value);
    }
    if (isLink && item[field.data]) {
      input.attr('href', field.getLink(item));
      if (field.openNewTab) {
        input.attr('target', '_blank');
      }
    }
    return input;
  }

  function makeTextInput(inputId, field, value, type, form) {
    var input = $('<input>').attr('id', inputId).attr('type', type || 'text');
    if (value !== null) {
      input.val(value);
    }
    if (field.maxLength) {
      input.attr('maxlength', field.maxlength);
    }
    if (field.onChange) {
      input.change(function() {
        field.onChange(this.value, form);
      });
    }
    return input;
  }

  function makeTextareaInput(inputId, field, value) {
    var input = $('<textarea>').attr('id', inputId);
    if (value !== null) {
      input.val(value);
    }
    if (field.maxLength) {
      input.attr('maxlength', field.maxlength);
    }
    return input;
  }

  function makeDropdownInput(inputId, field, value, form) {
    var select = $('<select>').attr('id', inputId);
    addDropdownOptions(field, select);
    if (typeof value === 'boolean') {
      value = value.toString();
    }
    select.val(value);
    if (field.onChange) {
      select.change(function() {
        field.onChange(this.value, form);
      });
    }
    return select;
  }

  function addDropdownOptions(field, select) {
    if (!field.required || field.nullLabel) {
      select.append($('<option>').val(null).text(field.nullLabel || 'None'));
    }
    var items = field.sortSource ? field.getSource().sort(field.sortSource) : field.getSource();
    items.forEach(function(item) {
      var itemValue = field.getItemValue ? field.getItemValue(item) : item;
      var itemLabel = field.getItemLabel ? field.getItemLabel(item) : null;
      if (field.getItemLabel) {
        itemLabel = field.getItemLabel(item);
      } else if (typeof item === 'string') {
        itemLabel = item;
      } else {
        throw new Error('Unable to determine label for dropdown item in field: ' + field.title);
      }
      select.append($('<option>').val(itemValue).text(itemLabel));
    });
  }

  function makeCheckboxInput(inputId, field, value, form) {
    var checkbox = $('<input>').attr('id', inputId).attr('type', 'checkbox').prop('checked', value);
    if (field.onChange) {
      checkbox.change(function() {
        field.onChange(this.checked, form);
      });
    }
    return checkbox;
  }

  function makeSpecialInput(field, value, form) {
    return field.makeControls(form);
  }

  function makeFieldNote(field) {
    return $('<span>').addClass('message-info').text(field.note);
  }

  function makeFieldValidationBox(inputId, field) {
    return $('<div>').attr('id', inputId + 'Error').addClass('errorContainer');
  }

})(jQuery);
