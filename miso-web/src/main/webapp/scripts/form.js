FormUtils = (function($) {

  /*
   * FormTarget Structure: {
   *   getSaveUrl: required function(object) returning string; URL to save object
   *   getSaveMethod: required function(object) returning string (POST|PUT); HTTP method to save object
   *   getEditUrl: required function(object) returning string; URL for the object's edit page
   *   getSections: required function(config) returning array of FormSections; see below
   *   onLoad: optional function(); called after the form is initialized
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
   *   type: required string (read-only|text|dropdown|special); type of field. Note: read-only means not directly editable. The value
   *       may still be changed via javascript, and that updated value will be validated and saved
   *   include: optional boolean; determines whether the field is displayed. Field is displayed by default
   *   initial: optional string; value to initialize field value to for new items
   *   required: optional boolean; whether the field is required
   *   maxLength: optional integer; maximum number of characters for text input
   *   getSource: function() returning array of objects; required for dropdown fields; Provides dropdown options
   *   getItemLabel: function(item) returning string; required for dropdown fields; get the label for a dropdown option
   *   getItemValue: function(item) returning string; required for dropdown fields; get the value for a dropdown option
   *   makeControls: function() returning single or array of jQuery controls; required for special fields; set up special fields
   * }
   */

  return {
    createForm: function(containerId, saveId, object, targetName, config) {
      var container = $('#' + containerId);
      var target = FormTarget[targetName];
      if (!object) {
        object = {};
      }

      writeGeneralValidationBox(container);
      var sections = getFilteredSections(target, config);
      sections.forEach(function(section) {
        writeSection(container, section, object);
      });

      $('#' + saveId).click(function() {
        validateAndSave(containerId, object, target, sections);
      });

      if (target.onLoad) {
        target.onLoad();
      }
    }
  };

  function validateAndSave(containerId, object, target, sections) {
    var selector = '#' + containerId;
    Validate.cleanFields(selector);
    Validate.clearErrors(selector);

    sections.forEach(function(section) {
      section.fields.forEach(function(field) {
        switch (field.type) {
        case 'special': // FormTarget is responsible for managing updates, likely via an additional hidden field
          break;
        case 'read-only':
          object[field.data] = field.getDisplayValue ? $('#' + field.data).val() : $('#' + field.data).text();
          break;
        case 'text':
        case 'dropdown':
        case 'date':
          object[field.data] = $('#' + field.data).val();
          addValidation(field);
          break;
        default:
          throw new Error('Unknown field type: ' + field.type);
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
        $('<p>').text('This form seems to be invalid')));
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

  function writeSection(container, section, object) {
    container.append($('<h2>').text(section.title));
    var tbody = $('<tbody>');

    section.fields.forEach(function(field) {
      var tr = $('<tr>');
      tr.append(makeFieldLabel(field));
      tr.append(makeFieldInput(field, object));
      tbody.append(tr);
    });

    container.append($('<div>').attr('id', section.id).append($('<table>').addClass('in').append(tbody)));

    section.fields.forEach(function(field) {
      if (field.type === 'date') {
        Utils.ui.addDatePicker(field.data);
      }
    });
  }

  function makeFieldLabel(field, object) {
    return $('<td>').addClass('h').text(field.title + ':' + (field.required ? '*' : ''));
  }

  function makeFieldInput(field, object) {
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
      td.append(makeReadOnlyInput(field, value));
      break;
    case 'text':
    case 'date': // treat as text for now. date picker gets added later
      td.append(makeTextInput(field, value));
      break;
    case 'dropdown':
      td.append(makeDropdownInput(field, value));
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
    } else if (object[field.data]) {
      value = object[field.data];
    }
    if (value === null && field.hasOwnProperty('initial')) {
      value = field.initial;
    }
    return value;
  }

  function makeReadOnlyInput(field, value) {
    var input = $('<span>').attr('id', field.data + (field.getDisplayValue ? 'Label' : ''));
    if (value !== null) {
      input.text(value);
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

  function makeDropdownInput(field, value) {
    var select = $('<select>').attr('id', field.data);
    if (!field.required) {
      select.append($('<option>').val(null).text(field.nullLabel || 'None'));
    }
    field.getSource().forEach(function(item) {
      select.append($('<option>').val(field.getItemValue(item)).text(field.getItemLabel(item)));
    });
    select.val(value);
    return select;
  }

  function makeSpecialInput(field, value) {
    return field.makeControls();
  }

  function makeFieldValidationBox(field) {
    return $('<div>').attr('id', field.data + 'Error').addClass('errorContainer');
  }

})(jQuery);
