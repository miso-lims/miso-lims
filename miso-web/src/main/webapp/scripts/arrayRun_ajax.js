(function(ArrayRun, $, undefined) { // NOSONAR (paranoid assurance that undefined is undefined)
  var formSelector = '#arrayRun-form';
  var runJson = null;
  var changelogInitialised = false;
  var searchResults = null;

  ArrayRun.userIsAdmin = false;

  ArrayRun.setRunJson = function(json) {
    runJson = json;
    updatePage();
  };

  ArrayRun.validateAndSave = function() {
    Validate.cleanFields(formSelector);
    Validate.clearErrors(formSelector);

    $('#instrument').attr('class', 'form-control');
    $('#instrument').attr('data-parsley-required', 'true');
    $('#instrument').attr('data-parsley-errors-container', '#instrumentError');

    $('#alias').attr('class', 'form-control');
    $('#alias').attr('data-parsley-required', 'true');
    $('#alias').attr('data-parsley-maxlength', '255');
    $('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    $('#alias').attr('data-parsley-errors-container', '#aliasError');

    $('#description').attr('class', 'form-control');
    $('#description').attr('data-parsley-max-length', '255');
    $('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    $('#description').attr('data-parsley-errors-container', '#descriptionError');

    $('#filePath').attr('class', 'form-control');
    $('#filePath').attr('data-parsley-maxlength', '255');
    $('#filePath').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    $('#filePath').attr('data-parsley-errors-container', '#filePathError');

    $('#startDate').attr('class', 'form-control');
    $('#startDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
    $('#startDate').attr('data-date-format', 'YYYY-MM-DD');
    $('#startDate').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');
    $('#startDate').attr('required', 'true');
    $('#startDate').attr('data-parsley-errors-container', '#startDate');

    if (!document.getElementById('completionDate').disabled) {
      $('#completionDate').attr('class', 'form-control');
      $('#completionDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
      $('#completionDate').attr('data-date-format', 'YYYY-MM-DD');
      $('#completionDate').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');
      $('#completionDate').attr('required', 'true');
      $('#completionDate').attr('data-parsley-errors-container', '#completionDate');
    } else {
      $('#completionDate').removeAttr('required');
    }

    $(formSelector).parsley();
    $(formSelector).parsley().validate();

    Validate.updateWarningOrSubmit(formSelector, null, function() {
      save();
    });
  };

  ArrayRun.checkForCompletionDate = function() {
    var statusVal = $('input[name=health]:checked').val();
    if (Utils.validation.isNullCheck(statusVal)) {
      return;
    }
    var completionDate = document.getElementById("completionDate");
    if (!completionDate) {
      return;
    }
    var allowModification = ((statusVal === "Failed" || statusVal === "Completed") && (!completionDate.value || ArrayRun.userIsAdmin));
    completionDate.disabled = !allowModification;
  };

  ArrayRun.searchArrays = function() {
    $('#arraySearchContainer').prop('disabled', true).addClass('disabled');
    $('.arraySearchLoader').css('visibility', 'visible');

    var searchString = $('#arraySearchField').val();
    if (!searchString) {
      showArraySearchResults();
      return;
    }

    $.ajax({
      url: '/miso/rest/arrayruns/array-search?' + jQuery.param({
        q: searchString
      }),
      type: 'GET',
      dataType: 'json'
    }).success(function(data) {
      showArraySearchResults(data);
    }).fail(function(response, textStatus, serverStatus) {
      showArraySearchResults();
      var error = JSON.parse(response.responseText);
      var message = error.detail ? error.detail : error.message;
      Utils.showOkDialog('Search error', [message]);
    });
  };

  ArrayRun.setArray = function() {
    var arrayId = $('#arraySearchResults option:selected').val();
    $('#arraySearchField').val('');
    $('#array').val(arrayId);
    updateJson();
    updatePage();
  };

  function updatePage() {
    if (runJson && runJson.id) {
      $('#id').text(runJson.id);
      $('#instrument').empty();
      $('#instrument').append($('<a href="/miso/instrument/' + runJson.instrumentId + '">' + runJson.instrumentName + '</a>'));
      $('#alias').val(runJson.alias);
      $('#description').val(runJson.description);
      $('#filePath').val(runJson.filePath);
      $('#arraySearchField').empty();
      $('input[name=health][value=' + runJson.status + ']').prop("checked", true);
      $('#startDate').val(runJson.startDate);
      $('#completionDate').val(runJson.completionDate);
      $('#lastModified').text(runJson.lastModified);
    }
    updateDisplayedArray();
    var startDate = document.getElementById("startDate");
    startDate.disabled = startDate.value && !ArrayRun.userIsAdmin;
    ArrayRun.checkForCompletionDate();
    updateChangelogs();
  }

  function updateDisplayedArray() {
    $('#arrayLabel').empty();
    if (runJson && runJson.array) {
      $('#array').val(runJson.array.id);
      $('#arrayLabel').append($('<a href="/miso/array/' + runJson.array.id + '">' + runJson.array.alias + '</a>'));
    } else {
      $('#array').val('');
      $('#arrayLabel').text('Not set');
    }
    showArraySearchResults();
    updateSamplesTable();
  }

  function updateJson() {
    var run = runJson || {};
    if (!run.id) {
      run.instrumentId = $('#instrument option:selected').val();
      run.instrumentName = $('#instrument option:selected').text();
    }
    run.alias = $('#alias').val();
    run.description = $('#description').val();
    run.filePath = $('#filePath').val();
    updateRunArrayJson(run);
    run.status = $('input[name=health]:checked').val();
    run.startDate = $('#startDate').val();
    run.completionDate = $('#completionDate').val();
    run.lastModified = $('#lastModified').text();
    runJson = run;
  }

  function updateRunArrayJson(run) {
    var arrayId = $('#array').val();
    if (!run.array || run.array.id != arrayId) {
      if (!arrayId || arrayId <= 0) {
        run.array = null;
      } else {
        // array must be in search results if it has been changed
        var byId = searchResults.filter(function(item) {
          return item.id == arrayId;
        });
        if (byId.length === 1) {
          run.array = byId[0];
        } else {
          throw new Error('couldn\'t find array in search results');
        }
      }
    }
  }

  function updateSamplesTable() {
    $('#listingSamplesTable').empty();
    var data = [];
    var lengthOptions = [50, 25, 10];
    if (runJson && runJson.array) {
      data = runJson.array.samples.map(function(sample) {
        return [sample.coordinates, Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.name),
            Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.alias)];
      });
      lengthOptions.unshift(runJson.array.columns * runJson.array.rows);
    }
    $('#listingSamplesTable')
        .dataTable(
            {
              "aaData": data,
              "aoColumns": [{
                "sTitle": "Position"
              }, {
                "sTitle": "Sample Name"
              }, {
                "sTitle": "Sample Alias"
              }],
              "bJQueryUI": true,
              "bDestroy": true,
              "aLengthMenu": [lengthOptions, lengthOptions],
              "iDisplayLength": lengthOptions[0],
              "sPaginationType": "full_numbers",
              "sDom": '<"#toolbar.fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"lf>r<t><"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
              "aaSorting": [[0, "asc"]]
            }).css("width", "100%");
  }

  function updateChangelogs() {
    if (!runJson || !runJson.id) {
      // unsaved; no changelogs
      return;
    }
    $.ajax({
      url: '/miso/rest/arrayruns/' + runJson.id + '/changelog',
      type: 'GET',
      dataType: 'json'
    }).success(function(data) {
      if (changelogInitialised) {
        $('#changelog').dataTable().fnDestroy();
        $('#changelog').empty();
      }
      changelogInitialised = true;
      ListUtils.createStaticTable('changelog', ListTarget.changelog, {}, data);
    }).fail(function(response, textStatus, serverStatus) {
      Validate.displayErrors(JSON.parse(response.responseText));
    });
  }

  function showArraySearchResults(results) {
    var select = $('#arraySearchResults');
    select.empty();

    if (results && results.length) {
      searchResults = results;
      if (results.length > 1) {
        select.append(makeOption(-1, 'SELECT'));
      }
      $.each(results, function(index, result) {
        select.append(makeOption(result.id, result.alias));
      });
    }
    select.append(makeOption(-1, "None"));
    select[0].selectedIndex = 0;

    $('#arraySearchContainer').prop('disabled', false).removeClass('disabled');
    $('.arraySearchLoader').css('visibility', 'hidden');
  }

  function makeOption(value, label) {
    return '<option value="' + value + '">' + label + '</option>';
  }

  function save() {
    var isNew = !runJson || !runJson.id;
    updateJson();

    $.ajax({
      url: '/miso/rest/arrayruns' + (isNew ? '' : ('/' + runJson.id)),
      type: isNew ? 'POST' : 'PUT',
      dataType: 'json',
      contentType: 'application/json; charset=utf8',
      data: JSON.stringify(runJson)
    }).success(function(data) {
      window.location.href = '/miso/arrayrun/' + data.id;
    }).fail(function(response, textStatus, serverStatus) {
      Validate.displayErrors(JSON.parse(response.responseText), formSelector);
    });
  }

}(window.ArrayRun = window.ArrayRun || {}, jQuery));
