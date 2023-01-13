(function (SampleArray, $, undefined) {
  // NOSONAR (paranoid assurance that undefined is undefined)
  var arrayJson = null;
  var visual = null;
  var changelogInitialised = false;
  var consentRevokedSampleIds = [];

  var positionStringifier = {
    getRowLabel: function (row) {
      return SampleArray.getRowLabel(row);
    },

    getColLabel: function (col) {
      return SampleArray.getColLabel(col);
    },

    getPositionString: function (row, col) {
      return SampleArray.getPositionString(row, col);
    },
  };

  SampleArray.getRowLabel = function (row) {
    return row >= 10 ? row : "0" + row;
  };

  SampleArray.getColLabel = function (col) {
    return positionStringifier.getRowLabel(col);
  };

  SampleArray.getPositionString = function (row, col) {
    return "R" + positionStringifier.getRowLabel(row) + "C" + positionStringifier.getColLabel(col);
  };

  SampleArray.Visual = function () {
    var self = new Box.Visual();

    self.isMultiSelectEnabled = function () {
      return false;
    };

    self.onSelectionChanged = function (items) {
      var position = null;
      if (items.length > 0) {
        position = items[0].position;
      }
      var sample = Utils.array.findFirstOrNull(function (item) {
        return item.coordinates === position;
      }, self.data);

      clearSampleSearchResults();

      if (sample) {
        // filled position selected
        $("#selectedPosition").text(position);
        $("#selectedName").text(sample.name);
        if (sample.identificationBarcode) {
          $("#selectedBarcode").text(sample.identificationBarcode);
        } else {
          $("#selectedBarcode").empty();
        }
        $("#selectedAlias").html(
          Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.alias)
        );
        $("#selectedName").html(Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.name));
        $("#removeSelected, #searchField, #search").prop("disabled", false).removeClass("disabled");
      } else {
        // empty position or no position selected
        if (position) {
          $("#selectedPosition").text(position);
        } else {
          $("#selectedPosition").empty();
          $("#search, #searchField, #resultSelect, #updateSelected")
            .prop("disabled", true)
            .addClass("disabled");
        }
        $("#selectedName").empty();
        $("#selectedAlias").empty();
        $("#selectedBarcode").empty();
        $("#updateSelected, #removeSelected").prop("disabled", true).addClass("disabled");
      }
      $("#warningMessages").html("");
      $("#searchField").val("");
      $("#searchField").select().focus();
    };

    self.getRowLabel = function (row) {
      return row >= 10 ? row : "0" + row;
    };

    self.getColLabel = function (col) {
      return positionStringifier.getRowLabel(col);
    };

    self.getPositionString = function (row, col) {
      return (
        "R" + positionStringifier.getRowLabel(row) + "C" + positionStringifier.getColLabel(col)
      );
    };

    return self;
  };

  SampleArray.setArrayJson = function (json) {
    arrayJson = json;
    if (!visual) {
      visual = new SampleArray.Visual();
    }
    updatePage();
  };

  SampleArray.removeSelected = function () {
    showSamplesLoading(true);
    var selectedPosition = visual.selectedItems[0].position;

    $.ajax({
      url: "/miso/rest/arrays/" + arrayJson.id + "/positions/" + selectedPosition,
      type: "DELETE",
      dataType: "json",
    })
      .done(function (data) {
        clearSampleSearchResults();
        SampleArray.setArrayJson(data);
      })
      .fail(function (response, textStatus, serverStatus) {
        Utils.showOkDialog("Error removing sample", [JSON.parse(response.responseText).detail]);
        showSamplesLoading(false);
      });
  };

  SampleArray.searchSamples = function () {
    var searchString = $("#searchField").val();
    if (!searchString) {
      clearSampleSearchResults();
      return;
    }
    showSamplesLoading(true);
    var url =
      "/miso/rest/arrays/sample-search?" +
      Utils.page.param({
        q: searchString,
      });
    $.ajax({
      url: url,
      dataType: "json",
      type: "GET",
    })
      .done(function (data) {
        showSampleSearchResults(data);
      })
      .fail(function (response, textStatus, serverStatus) {
        clearSampleSearchResults();
        var error = JSON.parse(response.responseText);
        var message = error.detail ? error.detail : error.message;
        Utils.showOkDialog("Search error", [message]);
      });
  };

  SampleArray.updatePosition = function () {
    if (visual.selectedItems.length !== 1) {
      $("#warningMessages").html("Please select a single position from the grid, then retry.");
      return;
    } else if ($("#resultSelect").val() == -1) {
      $("#warningMessages").html("Please select an item to add.");
      return;
    }

    var selectedPosition = visual.selectedItems[0].position;
    var selectedItem = getItemAtPosition(selectedPosition);

    var addTheItem = function () {
      showSamplesLoading(true);

      $.ajax({
        url:
          "/miso/rest/arrays/" +
          arrayJson.id +
          "/positions/" +
          selectedPosition +
          "?" +
          Utils.page.param({
            sampleId: jQuery("#resultSelect").val(),
          }),
        type: "PUT",
        dataType: "json",
      })
        .done(function (data) {
          clearSampleSearchResults();
          SampleArray.setArrayJson(data);
        })
        .fail(function (response, textStatus, serverStatus) {
          var error = JSON.parse(response.responseText);
          var message = error.detail ? error.detail : error.message;
          $("#warningMessages").html("Error adding item: " + message);
          $("#ajaxLoader").addClass("hidden");
          showSamplesLoading(false);
        });
    };

    var checkConsentAndAdd = function () {
      var sampleId = $("#resultSelect").val();
      var revoked = Utils.array.findFirstOrNull(function (id) {
        return id == sampleId;
      }, consentRevokedSampleIds);
      if (sampleId && sampleId > 0 && revoked) {
        var lines = ["Donor has revoked consent for the following item."];
        lines.push("* " + jQuery("#resultSelect").text());
        Utils.showConfirmDialog("Warning", "Proceed anyway", lines, addTheItem);
      } else {
        addTheItem();
      }
    };

    if (selectedItem) {
      if (selectedItem.id == $("#resultSelect").val()) {
        // setting same item where it already is. No change necessary
        clearSampleSearchResults();
        $("#searchField").val("");
      } else {
        // if selectedPosition is already filled, confirm before deleting that position
        Utils.showConfirmDialog(
          "Replace Sample",
          "Replace",
          [
            selectedItem.alias +
              " is already located at position " +
              selectedPosition +
              ". Replace it?",
          ],
          checkConsentAndAdd
        );
      }
    } else {
      checkConsentAndAdd();
    }
  };

  SampleArray.updateSamplesTable = function (array) {
    $("#listingSamplesTable").empty();
    var data = !array
      ? []
      : array.samples.map(function (sample) {
          return [
            sample.coordinates,
            Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.name),
            Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.alias),
          ];
        });
    $("#listingSamplesTable")
      .dataTable({
        aaData: data,
        aoColumns: [
          {
            sTitle: "Position",
          },
          {
            sTitle: "Sample Name",
          },
          {
            sTitle: "Sample Alias",
          },
        ],
        bJQueryUI: true,
        bDestroy: true,
        sPaginationType: "full_numbers",
        sDom: '<"#toolbar.fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"lf>r<t><"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
        aaSorting: [[0, "asc"]],
      })
      .css("width", "100%");
  };

  function clearSampleSearchResults() {
    showSampleSearchResults();
  }

  function showSampleSearchResults(results) {
    $("#resultSelect").empty();
    $("#warningMessages").html("");

    if (!results || !results.length) {
      consentRevokedSampleIds = [];
      $("#resultSelect").append('<option value="-1" selected="selected">No results</option>');
    } else {
      consentRevokedSampleIds = results
        .filter(function (sample) {
          return sample.identityConsentLevel === "Revoked";
        })
        .map(function (sample) {
          return sample.id;
        });
      if (results.length > 1) {
        $("#resultSelect").append('<option value="-1" selected="selected">SELECT</option>');
      }
      $.each(results, function (index, result) {
        var opt = $("<option>");
        opt.val(result.id);
        opt.text(result.name + ": " + result.alias);
        $("#resultSelect").append(opt);
      });
      $("#updateSelected").prop("disabled", false).removeClass("disabled");
    }

    $("#ajaxLoader").addClass("hidden");
    $("#searchField, #search, #resultSelect").prop("disabled", false).removeClass("disabled");
    visual.setDisabled(false);
  }

  function updatePage() {
    if (arrayJson.id) {
      // array is being edited
      $("#id").text(arrayJson.id);
      $("#alias").val(arrayJson.alias);
      $("#arrayModel").text(arrayJson.arrayModelAlias);
      $("#serialNumber").val(arrayJson.serialNumber);
      $("#description").val(arrayJson.description);
      createVisual();
      SampleArray.updateSamplesTable(arrayJson);
      updateChangelogs();
    }
  }

  function createVisual() {
    visual.create({
      div: "#arraySamplesVisual",
      size: {
        rows: arrayJson.rows,
        cols: arrayJson.columns,
      },
      data: arrayJson.samples,
    });
    visual.setDisabled(false);
  }

  function getItemAtPosition(coordinates) {
    var selected = arrayJson.samples.filter(function (item) {
      return item.coordinates === coordinates;
    });
    return selected.length === 1 ? selected[0] : null;
  }

  function showSamplesLoading(showLoading) {
    disableSampleControls(showLoading);
    if (showLoading) {
      $("#warningMessages").html(
        '<img id="ajaxLoader" src="/styles/images/ajax-loader.gif" alt="Loading" />'
      );
    } else {
      $("#warningMessages").html("");
    }
  }

  function disableSampleControls(disable) {
    var controls = $("#updateSelected, #removeSelected, #resultSelect, #search, #searchField");
    visual.setDisabled(disable);
    if (disable) {
      controls.prop("disabled", true).addClass("disabled");
    } else {
      controls.prop("disabled", false).removeClass("disabled");
    }
  }

  function updateChangelogs() {
    $.ajax({
      url: "/miso/rest/arrays/" + arrayJson.id + "/changelog",
      type: "GET",
      dataType: "json",
    })
      .done(function (data) {
        if (changelogInitialised) {
          $("#changelog").dataTable().fnDestroy();
          $("#changelog").empty();
        }
        changelogInitialised = true;
        ListUtils.createStaticTable("changelog", ListTarget.changelog, {}, data);
      })
      .fail(function (response, textStatus, serverStatus) {
        showAjaxErrorDialog(response, textStatus, serverStatus);
      });
  }
})((window.SampleArray = window.SampleArray || {}), jQuery);
