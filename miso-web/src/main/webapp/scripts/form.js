FormUtils = (function($) {

  /*
   * FormTarget Structure: {
   *   getSaveUrl: required function(object) returning string; URL to save object
   *   getSaveMethod: required function(object) returning string (POST|PUT); HTTP method to save object
   *   getEditUrl: required function(object) returning string; URL for the object's edit page
   *   getSections: required function(config) returning array of FormSections; see below
   *   onLoad: optional function(updateField); called after the form is initialized
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
   *   data: required string; JSON property to use for value (unless getValue is supplied). Also used as the input ID
   *   getDisplayValue: optional function(object) returning string; generate a value to display in a read-only field instead of the
   *       data value
   *   getLink: optional function(object) returning URL string; generate a link URL for read-only field
   *   type: required string (read-only|text|dropdown|checkbox|int|decimal|special); type of field. Note: read-only means not directly
   *       editable. The value may still be changed via javascript, and that updated value will be validated and saved
   *   include: optional boolean; determines whether the field is displayed. Field is displayed by default
   *   initial: optional string; value to initialize field value to for new items
   *   required: optional boolean; whether the field is required
   *   maxLength: optional integer; maximum number of characters for text input
   *   min: minimum value for int or decimal input
   *   precision: maximum precision (length, excluding the decimal) for decimal input
   *   scale: maximum scale (decimal places) for decimal input
   *   nullLabel: optional string; label for null value in dropdown. If not provided, and the field is not required, there will be no
   *       null value in the dropdown - it will default to the first option unless an initial value is specified
   *   getSource: function() returning array of objects; required for dropdown fields; Provides dropdown options
   *   getItemLabel: function(item) returning string; get the label for a dropdown option. If omitted and the item is a string, it is
   *       used as the label; otherwise, an error is thrown
   *   getItemValue: function(item) returning string; get the value for a dropdown option. If omitted, the item is used as the value
   *   onChange: function(newValue, updateField); allows modifying other fields when a dropdown type field value is changed. updateField
   *       is a function(dataProperty, options). options may include 'disabled' (boolean) and 'value' (string/number/boolean depending on
   *       field type)
   *   makeControls: function() returning single or array of jQuery controls; required for special fields; set up special fields
   * }
   */

  var defaultDecimalPrecision = 21;
  var defaultDecimalScale = 17;

  return {
    createForm: function(containerId, saveId, object, targetName, config) {
      var container = $('#' + containerId);
      var target = FormTarget[targetName];
      if (!object) {
        object = {};
      }

      writeGeneralValidationBox(container);
      var sections = getFilteredSections(target, config);

      var updateField = function(dataProperty, options) {
        var field = findField(sections, dataProperty);
        for ( var option in options) {
          switch (option) {
          case 'disabled':
            Utils.ui.setDisabled('#' + field.data, options.disabled);
            break;
          case 'value':
            setFormValue(field, options.value);
            break;
          case 'required':
            field.required = options.required;
            $('#' + field.data).closest('tr').children().first().text(getFieldLabelText(field));
            break;
          default:
            throw new Error('Invalid field update option: ' + option);
          }
        }
      }

      sections.forEach(function(section) {
        writeSection(container, section, object, updateField);
      });

      $('#' + saveId).click(function() {
        validateAndSave(containerId, object, target, sections);
      });

      sections.forEach(function(section) {
        section.fields.forEach(function(field) {
          if (field.onChange) {
            field.onChange(getFormValue(field), updateField);
          }
        });
      });
      if (target.onLoad) {
        target.onLoad(updateField);
      }
    }
  };

  function getFormValue(field) {
    var control = $('#' + field.data);
    switch (field.type) {
    case 'read-only':
      return field.getDisplayValue ? control.val() : control.text();
    case 'text':
    case 'dropdown':
    case 'date':
    case 'decimal':
      return control.val().length ? control.val() : null;
    case 'int':
      return control.val().length ? Number(control.val()) : null;
    case 'checkbox':
      return control.is(':checked');
    default:
      throw new Error('Can\'t get value of field with type ' + field.type);
    }
  }

  function setFormValue(field, value) {
    switch (field.type) {
    case 'text':
    case 'dropdown':
    case 'date':
    case 'decimal':
    case 'int':
      $('#' + field.data).val(value);
      break;
    case 'checkbox':
      $('#' + field.data).prop('checked', value);
      break;
    default:
      throw new Error('Can\'t set value of field with type ' + field.type);
    }
  }

  function findField(sections, dataProperty) {
    var fields = sections.flatMap(function(section) {
      return section.fields;
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

  function validateAndSave(containerId, object, target, sections) {
    var selector = '#' + containerId;
    Validate.cleanFields(selector);
    Validate.clearErrors(selector);

    sections.forEach(function(section) {
      section.fields.forEach(function(field) {
        if (field.type !== 'special') { // FormTarget is responsible for managing updates, likely via an additional hidden field
          object[field.data] = getFormValue(field);
        }
        if (['text', 'dropdown', 'date', 'decimal', 'int'].indexOf(field.type) !== -1) {
          addValidation(field);
        }
      });
    });

    $(selector).parsley();
    $(selector).parsley().validate();

    Validate.updateWarningOrSubmit(selector, null, function() {
      save(containerId, object, target);
    });
  }

  function addValidation(field) {
    var control = $('#' + field.data).addClass('form-control').attr('data-parsley-errors-container', '#' + field.data + 'Error');
    if (field.required) {
      control.attr('data-parsley-required', true);
    }
    if (field.maxLength) {
      control.attr('data-parsley-max-length', field.maxLength);
    }
    if (field.type === 'text') {
      control.attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    } else if (field.type === 'date') {
      control.attr('data-date-format', 'YYYY-MM-DD');
      control.attr('data-parsley-pattern', Utils.validation.dateRegex);
      control.attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');
    } else if (field.type === 'int') {
      control.attr('data-parsley-type', 'integer');
      if (field.hasOwnProperty('min')) {
        control.attr('data-parsley-min', field.min);
      }
    } else if (field.type === 'decimal') {
      var precision = field.precision || defaultDecimalPrecision;
      var scale = field.scale || defaultDecimalScale;
      control.attr('data-parsley-type', 'number');
      control.attr('data-parsley-maxlength', precision + 1);
      var max = Math.pow(10, precision - scale) - Math.pow(0.1, scale);
      var min = field.hasOwnProperty('min') ? field.min : max * -1;
      control.attr('data-parsley-range', '[' + min + ', ' + max + ']')
      var pattern = '\\d{0,' + (precision - scale) + '}(?:\\.\\d{1,' + scale + '})?';
      control.attr('data-parsley-pattern', pattern);
      control.attr('data-parsley-error-message', 'Must be a number between ' + min + ' and ' + max);
    }
  }

  function save(containerId, object, target) {
    $.ajax({
      url: target.getSaveUrl(object),
      type: target.getSaveMethod(object),
      dataType: 'json',
      contentType: 'application/json; charset=utf8',
      data: JSON.stringify(object)
    }).success(function(data) {
      window.location.href = target.getEditUrl(data);
    }).fail(function(response, textStatus, serverStatus) {
      Validate.displayErrors(JSON.parse(response.responseText), '#' + containerId);
    });
  }

  function writeGeneralValidationBox(container) {
    container.append($('<div>').addClass('bs-callout bs-callout-warning hidden').append($('<h2>').text('Oh snap!')).append(
        $('<p>').text('This form seems to be invalid')).append($('<div>').addClass('generalErrors')));
  }

  function getFilteredSections(target, config) {
    var filtered = [];
    target.getSections(config).filter(function(section) {
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

  function writeSection(container, section, object, updateField) {
    container.append($('<h2>').text(section.title));
    var tbody = $('<tbody>');

    section.fields.forEach(function(field) {
      var tr = $('<tr>');
      tr.append(makeFieldLabel(field));
      tr.append(makeFieldInput(field, object, updateField));
      tbody.append(tr);
    });

    container.append($('<div>').attr('id', section.id).append($('<table>').addClass('in').append(tbody)));

    section.fields.forEach(function(field) {
      if (field.type === 'date') {
        $('#' + field.data).attr('placeholder', 'YYYY-MM-DD');
        Utils.ui.addDatePicker(field.data);
      }
    });
  }

  function makeFieldLabel(field) {
    return $('<td>').addClass('h').text(getFieldLabelText(field));
  }

  function getFieldLabelText(field) {
    return field.title + ':' + (field.required ? '*' : '');
  }

  function makeFieldInput(field, object, updateField) {
    var td = $('<td>');
    var value = getValue(field, object);

    if (field.getDisplayValue) {
      // we're displaying something different, so put the actual data in a hidden field
      var hidden = $('<input>').attr('id', field.data).attr('type', 'hidden');
      if (object[field.data]) {
        hidden.val(object[field.data]);
      }
      td.append(hidden);
    }

    switch (field.type) {
    case 'read-only':
      td.append(makeReadOnlyInput(field, value, object));
      break;
    case 'text':
    case 'int':
    case 'decimal':
    case 'date': // treat as text for now. date picker gets added later
      td.append(makeTextInput(field, value));
      break;
    case 'dropdown':
      td.append(makeDropdownInput(field, value, updateField));
      break;
    case 'checkbox':
      td.append(makeCheckboxInput(field, value));
      break;
    case 'special':
      td.append(makeSpecialInput(field, value));
      break;
    default:
      throw new Error('Unknown field type: ' + field.type);
    }
    if (field.type !== 'special') {
      td.append(makeFieldValidationBox(field));
    }
    return td;
  }

  function getValue(field, object) {
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

  function makeReadOnlyInput(field, value, item) {
    var isLink = field.getLink && field.getLink(item);
    var input = $(isLink ? '<a>' : '<span>').attr('id', field.data + (field.getDisplayValue ? 'Label' : ''));
    if (value !== null) {
      input.text(value);
    }
    if (isLink) {
      input.attr('href', field.getLink(item));
    }
    return input;
  }

  function makeTextInput(field, value) {
    var input = $('<input>').attr('id', field.data).attr('type', 'text');
    if (value !== null) {
      input.val(value);
    }
    if (field.maxLength) {
      input.attr('maxlength', field.maxlength);
    }
    return input;
  }

  function makeDropdownInput(field, value, updateField) {
    var select = $('<select>').attr('id', field.data);
    if (!field.required || field.nullLabel) {
      select.append($('<option>').val(null).text(field.nullLabel || 'None'));
    }
    field.getSource().forEach(function(item) {
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
    select.val(value);
    if (field.onChange) {
      select.change(function() {
        field.onChange(this.value, updateField);
      });
    }
    return select;
  }

  function makeCheckboxInput(field, value) {
    return $('<input>').attr('id', field.data).attr('type', 'checkbox').prop('checked', value);
  }

  function makeSpecialInput(field, value) {
    return field.makeControls();
  }

  function makeFieldValidationBox(field) {
    return $('<div>').attr('id', field.data + 'Error').addClass('errorContainer');
  }

})(jQuery);
