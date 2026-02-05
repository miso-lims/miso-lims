var RunLibraryMetrics = (function ($) {
  var tableSelector = "#metricsTable";

  var tableData = null;
  var runReviewer = null;

  var dataReviewOptions = [
    {
      label: "Pending",
      value: "",
    },
    {
      label: "Pass",
      value: "true",
    },
    {
      label: "Fail",
      value: "false",
    },
  ];

  return {
    buildTable: function (data, isRunReviewer) {
      tableData = data;
      runReviewer = isRunReviewer;

      var table = $(tableSelector);
      table.empty();

      var headerRow = $("<tr>");
      headerRow.append($("<th>").text("Library Aliquot"));

      var metricsHeadings = new Set();
      data.forEach(function (element) {
        element.metrics.forEach(function (metric) {
          if (!metricsHeadings.has(metric.title)) {
            headerRow.append($("<th>").text(metric.title));
            metricsHeadings.add(metric.title);
          }
        });
      });
      headerRow.append($("<th>").text("Effective QC Status"));
      headerRow.append($("<th>").text("QC Status"));
      table.append($("<thead>").append($(headerRow)));

      var tbody = $("<tbody>");
      data.forEach(function (rowData) {
        var row = $("<tr>");
        row.append($("<td>").text(rowData.libraryAliquot.alias));
        metricsHeadings.forEach(function (heading) {
          var cellAdded = false;
          var metricCell = $("<td>");
          rowData.metrics.forEach(function (metric) {
            if (metric.title === heading) {
              metricCell.append($("<div>").text(formatValue(metric)), displayThreshold(metric));
              if (!metricPassed(metric)) {
                metricCell.addClass("failed");
              }
              cellAdded = true;
            }
          });
          if (cellAdded === false) {
            metricCell.append("N/A");
            metricCell.addClass("na");
          }
          row.append(metricCell);
        });
        var effectiveQcCell = $("<td>");
        updateEffectiveQcCell(rowData, effectiveQcCell);
        row.append(effectiveQcCell);
        row.append(makeQcCell(rowData, effectiveQcCell));
        tbody.append(row);
        rowData.update = function (update) {
          Object.assign(rowData.selectedNode, update);
          updateEffectiveQcCell(rowData, effectiveQcCell);
        };
      });
      table.append(tbody);
    },
    showQcAllDialog: function () {
      var fields = [
        {
          label: "Status",
          property: "option",
          type: "select",
          values: [
            {
              id: 0,
              description: "Pending",
            },
          ].concat(Constants.RunItemQcStatuses),
          getLabel: Utils.array.get("description"),
        },
        {
          label: "Note",
          property: "note",
          type: "text",
        },
      ];
      Utils.showDialog("QC All Run-Libraries", "Apply", fields, function (results) {
        setAllRunItemQcs(results.option.id, results.note);
      });
    },
    showReviewAllDialog: function () {
      var fields = [
        {
          label: "Data Review",
          property: "option",
          type: "select",
          values: dataReviewOptions,
          getLabel: Utils.array.get("label"),
        },
      ];
      Utils.showDialog("Review All Run-Libraries", "Apply", fields, function (results) {
        setAllRunLibraryReviews(results.option.value);
      });
    },
    saveAll: function () {
      var changes = getAllChanges();
      if (!changes.length) {
        Utils.showOkDialog("Error", ["No changes to save"]);
        return;
      }
      function save() {
        Utils.ajaxWithDialog(
          "Saving...",
          "PUT",
          Urls.rest.qcStatuses.bulkUpdate,
          changes.map(Utils.array.get("update")),
          function () {
            changes.forEach(function (change) {
              change.rowData.dataReview = null;
              change.rowData.update(change.update);
            });
          }
        );
      }
      if (
        anyQcChanges() &&
        changes.some(function (change) {
          return change.rowData.dataReview !== null;
        })
      ) {
        warnDataReviewReset(save);
      } else {
        save();
      }
    },
    hasUnsavedChanges: function () {
      return getAllChanges().length > 0;
    },
  };

  function formatValue(metric) {
    var decimalPlaces = 0;
    if (metric.threshold != null) {
      decimalPlaces = Utils.decimalStrings.getDecimalPlaces(metric.threshold);
    }
    if (metric.threshold2 != null) {
      decimalPlaces = Math.max(
        decimalPlaces,
        Utils.decimalStrings.getDecimalPlaces(metric.threshold2)
      );
    }
    decimalPlaces += 1;
    return Utils.decimalStrings.truncateDecimal(metric.value, decimalPlaces);
  }

  function displayThreshold(metric) {
    return $("<div>").text(" (threshold: " + makeThresholdTypeLabel(metric) + ")");
  }

  function makeThresholdTypeLabel(metric) {
    switch (metric.thresholdType) {
      case "gt":
        return "> " + metric.threshold;
      case "ge":
        return ">= " + metric.threshold;
      case "lt":
        return "< " + metric.threshold;
      case "le":
        return "<= " + metric.threshold;
      case "between":
        return "between " + metric.threshold + " and " + metric.threshold2;
      default:
        throw new Error("Unknown thresholdType: metric.thresholdType");
    }
  }

  function metricPassed(metric) {
    switch (metric.thresholdType) {
      case "gt":
        return metric.value > metric.threshold;
      case "ge":
        return metric.value >= metric.threshold;
      case "lt":
        return metric.value < metric.threshold;
      case "le":
        return metric.value <= metric.threshold;
      case "between":
        return metric.value >= metric.threshold && metric.value <= metric.threshold2;
      default:
        throw new Error("Unknown thresholdType: metric.thresholdType");
    }
  }

  function updateEffectiveQcCell(rowData, cell) {
    cell.removeClass("failed");
    var status = "Pending";
    for (var i = 0; i < rowData.qcNodes.length; i++) {
      var qcNode = rowData.qcNodes[i];
      if (qcNode.qcPassed === false) {
        status = "Failed (" + qcNode.typeLabel + ")";
        cell.addClass("failed");
        break;
      } else if (i === rowData.qcNodes.length - 1 && qcNode.qcPassed === true) {
        status = "Passed (" + qcNode.typeLabel + ")";
      }
    }
    return cell.text(status);
  }

  function makeQcCell(rowData, effectiveQcCell) {
    var cell = $("<td>");
    var table = $("<table>");

    var controls = {
      effectiveQc: effectiveQcCell,
      node: $("<select>")
        .addClass("nodeSelect")
        .append(
          rowData.qcNodes.map(function (qcNode, i) {
            return makeSelectOption(i, qcNode.typeLabel + " " + qcNode.label);
          })
        )
        .val(rowData.qcNodes.length - 1),
      name: $("<span>"),
      status: $("<select>").addClass("statusSelect"),
      note: $("<input>").addClass("noteInput"),
      dataReview: $("<select>").addClass("dataReviewSelect"),
      apply: $("<button>").text("Apply"),
    };
    controls.node
      .change(function () {
        var i = Number.parseInt($(this).val());
        var qcNode = rowData.qcNodes[i];
        updateQcCell(controls, qcNode, rowData);
        rowData.selectedNode = qcNode;
      })
      .change();

    appendQcNodeTableRow(table, "Item:", controls.node);
    appendQcNodeTableRow(table, "Name:", controls.name);
    appendQcNodeTableRow(table, "Status:", controls.status);
    appendQcNodeTableRow(table, "Note:", controls.note);
    appendQcNodeTableRow(table, "Data Review:", controls.dataReview);
    appendQcNodeTableRow(table, "", controls.apply);

    cell.append(table);
    return cell;
  }

  function makeSelectOption(value, label, selected) {
    var option = $("<option>").val(value).text(label);
    if (selected) {
      option.attr("selected", "selected");
    }
    return option;
  }

  function updateQcCell(controls, qcNode, rowData) {
    controls.name.empty();
    if (qcNode.name) {
      controls.name.append($("<a>").attr("href", getNodeUrl(qcNode)).text(qcNode.name));
    } else {
      controls.name.text("n/a");
    }

    controls.status.empty();
    controls.status.off("change");
    Utils.ui.setDisabled(controls.note, true);
    Utils.ui.setDisabled(controls.dataReview, true);
    controls.dataReview.empty();
    controls.apply.off("click");

    switch (qcNode.entityType) {
      case "Sample":
      case "Library":
      case "Library Aliquot":
        updateQcCellDetailedQcStatus(controls, qcNode, rowData);
        break;
      case "Pool":
        controls.status.append(
          QcHierarchy.qcPassedOptions.map(function (item, i) {
            var qcPassed = qcNode.qcPassed === undefined ? null : qcNode.qcPassed;
            return makeSelectOption(i, item.label, qcPassed === item.value);
          })
        );
        rowData.getUpdate = function () {
          return {
            qcPassed: QcHierarchy.qcPassedOptions[controls.status.val()].value,
          };
        };
        break;
      case "Run":
        var qcPassed = qcNode.qcPassed === undefined ? null : qcNode.qcPassed;
        controls.status.append(
          QcHierarchy.qcPassedOptions.map(function (item, i) {
            return makeSelectOption(i, item.label, qcPassed === item.value);
          })
        );
        controls.status
          .change(function () {
            var selected = QcHierarchy.qcPassedOptions[controls.status.val()].value;
            if (selected !== qcPassed) {
              controls.dataReview.val("");
            }
            Utils.ui.setDisabled(
              controls.dataReview,
              !runReviewer || selected === null || selected !== qcPassed
            );
          })
          .change();
        controls.dataReview.append(
          dataReviewOptions.map(function (item) {
            return makeSelectOption(
              item.value,
              item.label,
              stringToBoolean(item.value) === qcNode.dataReview
            );
          })
        );

        rowData.getUpdate = function () {
          return {
            qcPassed: QcHierarchy.qcPassedOptions[controls.status.val()].value,
            dataReview: stringToBoolean(controls.dataReview.val()),
          };
        };
        break;
      case "Run-Partition":
        controls.status.append(makeSelectOption(0, "Not Set", !qcNode.qcStatusId));
        controls.status
          .append(
            Constants.partitionQcTypes.map(function (item) {
              return makeSelectOption(item.id, item.description, qcNode.qcStatusId === item.id);
            })
          )
          .change(function () {
            var selectedId = Number.parseInt($(this).val());
            var selected = null;
            if (selectedId) {
              selected = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(selectedId),
                Constants.partitionQcTypes
              );
              Utils.ui.setDisabled(controls.note, !selected.noteRequired);
            } else {
              Utils.ui.setDisabled(controls.note, true);
            }
            if (!selected || !selected.noteRequired || selectedId !== qcNode.qcStatusId) {
              controls.note.val(null);
            }
          })
          .change();
        if (qcNode.qcStatusId) {
          var selected = Utils.array.findUniqueOrThrow(
            Utils.array.idPredicate(qcNode.qcStatusId),
            Constants.partitionQcTypes
          );
          Utils.ui.setDisabled(controls.note, !selected.noteRequired);
        }

        rowData.getUpdate = function () {
          return {
            qcStatusId: Number.parseInt(controls.status.val()) || null,
            qcNote: controls.note.val() || null,
          };
        };
        break;
      case "Run-Library":
        controls.status.append(makeSelectOption(0, "Pending", !qcNode.qcStatusId));
        controls.status.append(
          Constants.RunItemQcStatuses.map(function (item) {
            return makeSelectOption(item.id, item.description, qcNode.qcStatusId === item.id);
          })
        );
        controls.status
          .change(function () {
            var selectedId = Number.parseInt(controls.status.val()) || null;
            if (selectedId !== qcNode.qcStatusId) {
              controls.dataReview.val("");
            }
            Utils.ui.setDisabled(
              controls.dataReview,
              !runReviewer || selectedId === null || selectedId !== qcNode.qcStatusId
            );
          })
          .change();
        Utils.ui.setDisabled(controls.note, false);
        controls.dataReview.append(
          dataReviewOptions.map(function (item) {
            return makeSelectOption(
              item.value,
              item.label,
              stringToBoolean(item.value) === qcNode.dataReview
            );
          })
        );

        rowData.getUpdate = function () {
          var statusId = Number.parseInt(controls.status.val()) || null;
          var qcPassed = null;
          if (statusId) {
            qcPassed = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(statusId),
              Constants.RunItemQcStatuses
            ).qcPassed;
          }

          return {
            qcStatusId: statusId,
            qcPassed: qcPassed,
            qcNote: controls.note.val() || null,
            dataReview: stringToBoolean(controls.dataReview.val()),
          };
        };
        break;
      default:
        throw new Error("Unknown entity type: " + qcNode.entityType);
    }
    controls.note.val(qcNode.qcNote);

    controls.apply.click(function () {
      var updates = rowData.getUpdate();
      var updated = Object.assign({}, rowData.selectedNode, updates);

      function update() {
        Utils.ajaxWithDialog(
          "Setting Status",
          "PUT",
          Urls.rest.qcStatuses.update,
          updated,
          function (response) {
            rowData.update(updated);
            updateQcCell(controls, qcNode, rowData);
          }
        );
      }

      if (qcChanged(rowData.selectedNode, updates) && rowData.selectedNode.dataReview !== null) {
        warnDataReviewReset(update);
      } else {
        update();
      }
    });
  }

  function qcChanged(before, after) {
    if (before.qcPassed === null || before.qcPassed === undefined) {
      if (after.qcPassed !== null && after.qcPassed !== undefined) {
        return true;
      }
    } else if (after.qcPassed !== before.qcPassed) {
      return true;
    }

    if (before.qcStatusId === null || before.qcStatusId === undefined) {
      if (after.qcStatusId !== null && after.qcStatusId !== undefined) {
        return true;
      }
    } else if (after.qcStatusId !== before.qcStatusId) {
      return true;
    }

    return false;
  }

  function warnDataReviewReset(acceptCallback) {
    Utils.showConfirmDialog(
      "Warning",
      "OK",
      ["Changing QC status will reset the data review. Do you wish to proceed?"],
      acceptCallback
    );
  }

  function updateQcCellDetailedQcStatus(controls, qcNode, rowData) {
    controls.status.append(makeSelectOption(0, "Not Ready", !qcNode.qcStatusId));
    controls.status
      .append(
        Constants.detailedQcStatuses.map(function (item) {
          return makeSelectOption(item.id, item.description, qcNode.qcStatusId === item.id);
        })
      )
      .change(function () {
        var selectedId = Number.parseInt($(this).val());
        var selected = null;
        if (selectedId) {
          selected = Utils.array.findUniqueOrThrow(
            Utils.array.idPredicate(selectedId),
            Constants.detailedQcStatuses
          );
          Utils.ui.setDisabled(controls.note, !selected.noteRequired);
        } else {
          Utils.ui.setDisabled(controls.note, true);
        }
        if (!selected || !selected.noteRequired || selectedId !== qcNode.qcStatusId) {
          controls.note.val(null);
        }
      })
      .change();

    rowData.getUpdate = function () {
      var update = {
        qcStatusId: Number.parseInt(controls.status.val()) || null,
        qcNote: controls.note.val() || null,
      };
      update.qcPassed =
        update.qcStatusId === null
          ? null
          : Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(update.qcStatusId),
              Constants.detailedQcStatuses
            ).status;
      return update;
    };
  }

  function getNodeUrl(qcNode) {
    switch (qcNode.entityType) {
      case "Sample":
        return Urls.ui.samples.edit(qcNode.id);
      case "Library":
        return Urls.ui.libraries.edit(qcNode.id);
      case "Library Aliquot":
        return Urls.ui.libraryAliquots.edit(qcNode.id);
      case "Pool":
        return Urls.ui.pools.edit(qcNode.id);
      case "Run":
        return Urls.ui.runs.edit(qcNode.id);
      default:
        throw new Error("Unexpected entity type: " + qcNode.entityType);
    }
  }

  function appendQcNodeTableRow(table, label, control) {
    table.append($("<tr>").append($("<td>").text(label), $("<td>").append(control)));
  }

  function setAllRunItemQcs(optionValue, note) {
    $(".nodeSelect").each(function () {
      $(this).val($(this).find("option").length - 1);
      $(this).change();
    });
    $(".statusSelect").val(optionValue).change();
    $(".noteInput").val(note);
  }

  function setAllRunLibraryReviews(optionValue) {
    $(".nodeSelect").each(function () {
      $(this).val($(this).find("option").length - 1);
      $(this).change();
    });
    $(".dataReviewSelect").val(optionValue);
  }

  function anyQcChanges() {
    for (var i = 0; i < tableData.length; i++) {
      if (qcChanged(tableData[i].selectedNode, tableData[i].getUpdate())) {
        return true;
      }
    }
    return false;
  }

  function getAllChanges() {
    return tableData
      .map(function (rowData) {
        var update = rowData.getUpdate();
        if (
          Object.keys(update).some(function (key) {
            return rowData.selectedNode[key] !== update[key];
          })
        ) {
          return {
            rowData: rowData,
            update: Object.assign({}, rowData.selectedNode, update),
          };
        } else {
          return null;
        }
      })
      .filter(function (updated) {
        return updated !== null;
      });
  }

  function stringToBoolean(string) {
    switch (string) {
      case "true":
        return true;
      case "false":
        return false;
      default:
        return null;
    }
  }
})(jQuery);
