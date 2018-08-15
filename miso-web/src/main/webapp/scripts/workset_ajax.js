(function(Workset, $, undefined) { // NOSONAR (paranoid assurance that undefined is undefined)
  var worksetJson = null;

  Workset.setWorksetJson = function(json) {
    worksetJson = json;
    updatePage();
  };

  Workset.validateAndSave = function() {
    Validate.cleanFields('#workset-form');
    Validate.clearErrors('#workset-form');

    // Alias input field validation
    $('#alias').attr('class', 'form-control');
    $('#alias').attr('data-parsley-required', 'true');
    $('#alias').attr('data-parsley-maxlength', '100');
    $('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    $('#alias').attr('data-parsley-errors-container', '#aliasError');

    // Description input field validation
    $('#description').attr('class', 'form-control');
    $('#description').attr('data-parsley-max-length', '255');
    $('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    $('#description').attr('data-parsley-errors-container', '#descriptionError');

    $('#workset-form').parsley();
    $('#workset-form').parsley().validate();

    Validate.updateWarningOrSubmit('#workset-form', null, function() {
      save(!worksetJson || !worksetJson.id);
    });
  };

  function updatePage() {
    if (worksetJson.id) {
      // workset is being edited
      $('#id').text(worksetJson.id);
      $('#alias').val(worksetJson.alias);
      $('#description').val(worksetJson.description);
      $('#creator').text(worksetJson.creator);
    }
  }

  function updateJson() {
    var workset = worksetJson || {};
    workset.alias = $('#alias').val();
    workset.description = $('#description').val();
    worksetJson = workset;
  }

  function save(isNew) {
    updateJson();
    var url = '/miso/rest/worksets';
    if (!isNew) {
      url += '/' + worksetJson.id;
    }
    var type = isNew ? 'POST' : 'PUT';

    $.ajax({
      url: url,
      type: type,
      dataType: 'json',
      contentType: 'application/json; charset=utf8',
      data: JSON.stringify(worksetJson)
    }).success(function(data) {
      window.location.href = '/miso/workset/' + data.id;
    }).fail(function(response, textStatus, serverStatus) {
      Validate.displayErrors(JSON.parse(response.responseText), '#workset-form');
    });
  }

}(window.Workset = window.Workset || {}, jQuery));