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
    return SampleArray.getRowLabel(col);
  };

  SampleArray.getPositionString = function (row, col) {
    return "R" + positionStringifier.getRowLabel(row) + "C" + positionStringifier.getColLabel(col);
  };

  SampleArray.Visual = function () {
    var self = new Box.Visual();

    self.isMultiSelectEnabled = function () {
      return true;
    };

    self.onSelectionChanged = function (items) {
      var positions = items.map(function (item) {
        return item.position;
      });
      var selectedSample = positions.length === 1 ? getItemAtPosition(positions[0]) : null;

      clearSampleSearchResults();
      hideBulkPositionControls();
      $("#singlePositionControls").show();

      if (positions.length) {
        if (positions.length > 1) {
          $("#singlePositionControls").hide();
          showBulkPositionControls(positions);
        } else {
          $("#selectedPosition").text(positions[0]);
          setSelectedSampleDetails(selectedSample);
          $("#search, #searchField, #resultSelect").prop("disabled", false).removeClass("disabled");
          $("#removeSelected")
            .prop("disabled", !selectedSample)
            .toggleClass("disabled", !selectedSample);
        }
      } else {
        $("#selectedPosition").empty();
        setSelectedSampleDetails(null);
        $("#search, #searchField, #resultSelect, #updateSelected, #removeSelected")
          .prop("disabled", true)
          .addClass("disabled");
      }
      $("#updateSelected").prop("disabled", true).addClass("disabled");
      $("#warningMessages").html("");
      $("#searchField").val("");
      $("#searchField").select().focus();
    };

    self.getRowLabel = function (row) {
      return SampleArray.getRowLabel(row);
    };

    self.getColLabel = function (col) {
      return SampleArray.getColLabel(col);
    };

    self.getPositionString = function (row, col) {
      return SampleArray.getPositionString(row, col);
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
    var selectedPositions = getSelectedPositions();
    if (!selectedPositions.length) {
      return;
    }
    showSamplesLoading(true);

    var data = selectedPositions.map(function (position) {
      return {
        position: position,
        searchString: null,
      };
    });
    Utils.ajaxWithDialog(
      "Remove Samples",
      "PUT",
      Urls.rest.arrays.updatePositions(arrayJson.id),
      data,
      function (responseData) {
        clearSampleSearchResults();
        SampleArray.setArrayJson(responseData);
        showSamplesLoading(false);
      },
      function (xhr, textStatus, errorThrown) {
        showSamplesLoading(false);
        Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown);
      },
      true
    );
  };

  SampleArray.searchSamples = function () {
    var searchString = $("#searchField").val();
    if (!searchString) {
      clearSampleSearchResults();
      return;
    }
    showSamplesLoading(true);
    var url =
      Urls.rest.arrays.sampleSearch +
      "?" +
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
      .fail(function (xhr, textStatus, errorThrown) {
        clearSampleSearchResults();
        Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown);
      });
  };

  SampleArray.updatePosition = function () {
    var selectedPositions = getSelectedPositions();
    if (selectedPositions.length !== 1) {
      $("#warningMessages").html("Please select a single position from the grid, then retry.");
      return;
    } else if ($("#resultSelect").val() == -1) {
      $("#warningMessages").html("Please select an item to add.");
      return;
    }

    var sampleId = jQuery("#resultSelect").val();
    var selectedItems = selectedPositions.map(getItemAtPosition).filter(function (item) {
      return !!item;
    });

    var addTheItem = function () {
      showSamplesLoading(true);

      var url =
        Urls.rest.arrays.position(arrayJson.id, selectedPositions[0]) +
        "?" +
        Utils.page.param({
          sampleId: sampleId,
        });
      Utils.ajaxWithDialog(
        "Update Position",
        "PUT",
        url,
        null,
        function (data) {
          clearSampleSearchResults();
          SampleArray.setArrayJson(data);
          showSamplesLoading(false);
        },
        function (xhr, textStatus, errorThrown) {
          showSamplesLoading(false);
          Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown);
        },
        true
      );
    };

    var checkConsentAndAdd = function () {
      var revoked = Utils.array.findFirstOrNull(function (id) {
        return id == sampleId;
      }, consentRevokedSampleIds);
      if (sampleId && sampleId > 0 && revoked) {
        var lines = ["Donor has revoked consent for the following item."];
        lines.push("* " + jQuery("#resultSelect option:selected").text());
        Utils.showConfirmDialog("Warning", "Proceed anyway", lines, addTheItem);
      } else {
        addTheItem();
      }
    };

    if (
      selectedItems.length === selectedPositions.length &&
      selectedItems.every(function (item) {
        return item.id == sampleId;
      })
    ) {
      clearSampleSearchResults();
      $("#searchField").val("");
      return;
    }

    var replacedItems = selectedItems.filter(function (item) {
      return item.id != sampleId;
    });
    if (replacedItems.length) {
      var lines = ["The following positions already contain samples:"];
      replacedItems.forEach(function (item) {
        lines.push("* " + item.coordinates + ": " + item.alias);
      });
      lines.push("Replace them?");
      Utils.showConfirmDialog("Replace Sample", "Replace", lines, checkConsentAndAdd);
    } else {
      checkConsentAndAdd();
    }
  };

  SampleArray.bulkUpdatePositions = function () {
    var inputs = $("#bulkUpdateTable tbody input");
    if (!inputs.length) {
      $("#warningMessages").html("Please select one or more positions from the grid, then retry.");
      return;
    }
    var data = [];
    inputs.each(function (index, input) {
      data.push({
        position: $(input).data("position"),
        searchString: $(input).val() ? $(input).val() : null,
      });
    });

    showSamplesLoading(true);
    Utils.ajaxWithDialog(
      "Update Positions",
      "PUT",
      Urls.rest.arrays.updatePositions(arrayJson.id),
      data,
      function (responseData) {
        clearSampleSearchResults();
        SampleArray.setArrayJson(responseData);
        showSamplesLoading(false);
      },
      function (xhr, textStatus, errorThrown) {
        showSamplesLoading(false);
        Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown);
      },
      true
    );
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

  function hideBulkPositionControls() {
    $("#bulkUpdateTable tbody").empty();
    $("#bulkPositionControls").hide();
  }

  function showBulkPositionControls(positions) {
    var body = $("#bulkUpdateTable tbody");
    body.empty();

    for (var i = 0; i < positions.length; i++) {
      var position = positions[i];
      var row = $("<tr>");
      row.append($("<td>", { text: position }));
      var inputCell = $("<td>");
      var input = $('<input type="text" class="bulkUpdateInput"/>');
      input.data("position", position);
      if (i < positions.length - 1) {
        input.keyup(
          (function () {
            var nextIndex = i + 1;
            return function (event) {
              if (event.which == "13") {
                $("#bulkUpdateTable tbody input:eq(" + nextIndex + ")").focus();
              }
            };
          })()
        );
        input.on(
          "paste",
          (function () {
            var index = i;
            return function (e) {
              for (
                var clipEvent = e;
                clipEvent.originalEvent && clipEvent.type == "paste";
                clipEvent = clipEvent.originalEvent
              );
              var lines = clipEvent.clipboardData
                ? clipEvent.clipboardData.getData("Text").split(/\r?\n/)
                : [];
              if (lines.length > 1) {
                for (var next = 0; next < lines.length; next++) {
                  var inputBox = $("#bulkUpdateTable tbody input:eq(" + next + ")");
                  inputBox.val(lines[next].replace(/^\s*|\s*$/g, ""));
                }
                $("#bulkUpdateTable tbody input:eq(" + next + ")").focus();
                e.preventDefault();
                return;
              }
              window.setTimeout(function () {
                $("#bulkUpdateTable tbody input:eq(" + (index + 1) + ")").focus();
              }, 100);
            };
          })()
        );
      } else {
        input.keyup(function (event) {
          if (event.which == "13") {
            $("#bulkUpdate").click();
          }
        });
      }
      inputCell.append(input);
      row.append(inputCell);
      body.append(row);
      if (i === 0) {
        input.focus();
      }
    }
    $("#bulkPositionControls").show();
  }

  function getSelectedPositions() {
    return visual.selectedItems.map(function (item) {
      return item.position;
    });
  }

  function setSelectedSampleDetails(sample) {
    if (!sample) {
      $("#selectedName").empty();
      $("#selectedAlias").empty();
      $("#selectedBarcode").empty();
      return;
    }
    $("#selectedName").html(Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.name));
    $("#selectedAlias").html(Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.alias));
    if (sample.identificationBarcode) {
      $("#selectedBarcode").text(sample.identificationBarcode);
    } else {
      $("#selectedBarcode").empty();
    }
  }

  function showSampleSearchResults(results) {
    $("#resultSelect").empty();
    $("#warningMessages").html("");
    $("#updateSelected").prop("disabled", true).addClass("disabled");

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
      url: Urls.rest.arrays.changelog(arrayJson.id),
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
