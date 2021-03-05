var RunLibraryMetrics = (function($) {

  var tableSelector = '#metricsTable';

  var tableData = null;

  return {
    buildTable: function(data) {
      tableData = data;

      var table = $(tableSelector);
      table.empty();

      var headerRow = $('<tr>');
      headerRow.append($('<th>').text('Library Aliquot'));
      data[0].metrics.forEach(function(metric) {
        headerRow.append(makeMetricHeader(metric));
      });
      headerRow.append($('<th>').text('Effective QC Status'));
      headerRow.append($('<th>').text('QC Status'));
      table.append($('<thead>').append($(headerRow)));

      var tbody = $('<tbody>');
      data.forEach(function(rowData) {
        var row = $('<tr>')
        row.append($('<td>').text(rowData.libraryAliquot.alias));
        rowData.metrics.forEach(function(metric) {
          var metricCell = $('<td>').text(metric.value);
          if (!metricPassed(metric)) {
            metricCell.addClass('failed');
          }
          row.append(metricCell);
        });
        var effectiveQcCell = $('<td>');
        updateEffectiveQcCell(rowData, effectiveQcCell)
        row.append(effectiveQcCell);
        row.append(makeQcCell(rowData, effectiveQcCell));
        tbody.append(row);
        rowData.update = function(update) {
          Object.assign(rowData.selectedNode, update);
          updateEffectiveQcCell(rowData, effectiveQcCell);
        };
      });
      table.append(tbody);
    },
    showSetAllDialog: function() {
      var fields = [{
        label: 'Status',
        property: 'option',
        type: 'select',
        values: Constants.runLibraryQcStatuses,
        getLabel: Utils.array.get('description')
      }, {
        label: 'Note',
        property: 'note',
        type: 'text'
      }];
      Utils.showDialog('Set All Run-Library QCs', 'Set', fields, function(results) {
        setAllRunLibraryQcs(results.option.id, results.note);
      });
    },
    saveAll: function() {
      var changes = getAllChanges();
      if (!changes.length) {
        Utils.showOkDialog('Error', ['No changes to save']);
        return;
      }
      Utils.ajaxWithDialog('Saving...', 'PUT', Urls.rest.qcStatuses.bulkUpdate, changes.map(Utils.array.get('update')), function() {
        changes.forEach(function(change) {
          change.rowData.update(change.update);
        });
      });
    },
    hasUnsavedChanges: function() {
      return getAllChanges().length > 0;
    }

  };

  function makeMetricHeader(metric) {
    return $('<th>').text(metric.title + ' (threshold: ' + makeThresholdTypeLabel(metric) + ' ' + metric.threshold + ')');
  }

  function makeThresholdTypeLabel(metric) {
    switch (metric.thresholdType) {
    case 'gt':
      return '>';
    case 'ge':
      return '>=';
    case 'lt':
      return '<';
    case 'le':
      return '<=';
    default:
      throw new Error('Unknown thresholdType: metric.thresholdType');
    }
  }

  function metricPassed(metric) {
    switch (metric.thresholdType) {
    case 'gt':
      return metric.value > metric.threshold;
    case 'ge':
      return metric.value >= metric.threshold;
    case 'lt':
      return metric.value < metric.threshold;
    case 'le':
      return metric.value <= metric.threshold;
    default:
      throw new Error('Unknown thresholdType: metric.thresholdType');
    }
  }

  function updateEffectiveQcCell(rowData, cell) {
    cell.removeClass('failed');
    var status = 'Pending';
    for (var i = 0; i < rowData.qcNodes.length; i++) {
      var qcNode = rowData.qcNodes[i];
      if (qcNode.qcPassed === false) {
        status = 'Failed (' + qcNode.typeLabel + ')';
        cell.addClass('failed');
        break;
      } else if (i === rowData.qcNodes.length - 1 && qcNode.qcPassed === true) {
        status = 'Passed (' + qcNode.typeLabel + ')';
      }
    }
    return cell.text(status);
  }

  function makeQcCell(rowData, effectiveQcCell) {
    var cell = $('<td>');
    var table = $('<table>');

    var controls = {
      effectiveQc: effectiveQcCell,
      node: $('<select>').addClass('nodeSelect').append(rowData.qcNodes.map(function(qcNode, i) {
        return makeSelectOption(i, qcNode.typeLabel + ' ' + qcNode.label);
      })).val(rowData.qcNodes.length - 1),
      name: $('<span>'),
      status: $('<select>').addClass('statusSelect'),
      note: $('<input>').addClass('noteInput'),
      apply: $('<button>').text('Apply')
    };
    controls.node.change(function() {
      var i = Number.parseInt($(this).val());
      var qcNode = rowData.qcNodes[i];
      updateQcCell(controls, qcNode, rowData);
      rowData.selectedNode = qcNode;
    }).change();

    appendQcNodeTableRow(table, 'Item:', controls.node);
    appendQcNodeTableRow(table, 'Name:', controls.name);
    appendQcNodeTableRow(table, 'Status:', controls.status);
    appendQcNodeTableRow(table, 'Note:', controls.note);
    appendQcNodeTableRow(table, '', controls.apply);

    cell.append(table);
    return cell;
  }

  function makeSelectOption(value, label, selected) {
    var option = $('<option>').val(value).text(label);
    if (selected) {
      option.attr('selected', 'selected');
    }
    return option;
  }

  function updateQcCell(controls, qcNode, rowData) {
    controls.name.empty();
    if (qcNode.name) {
      controls.name.append($('<a>').attr('href', getNodeUrl(qcNode)).text(qcNode.name));
    } else {
      controls.name.text('n/a');
    }

    controls.status.empty();
    controls.status.off('change');
    Utils.ui.setDisabled(controls.note, true);
    controls.apply.off('click');

    switch (qcNode.entityType) {
    case 'Sample':
    case 'Library':
    case 'Library Aliquot':
      updateQcCellDetailedQcStatus(controls, qcNode, rowData);
      break;
    case 'Pool':
    case 'Run':
      controls.status.append(QcHierarchy.qcPassedOptions.map(function(item, i) {
        var qcPassed = qcNode.qcPassed === undefined ? null : qcNode.qcPassed;
        return makeSelectOption(i, item.label, qcPassed === item.value);
      }));

      rowData.getUpdate = function() {
        return {
          qcPassed: QcHierarchy.qcPassedOptions[controls.status.val()].value
        };
      };
      break;
    case 'Run-Partition':
      controls.status.append(makeSelectOption(0, 'Not Set', !qcNode.qcStatusId));
      controls.status.append(Constants.partitionQcTypes.map(function(item) {
        return makeSelectOption(item.id, item.description, qcNode.qcStatusId === item.id);
      })).change(function() {
        var selectedId = Number.parseInt($(this).val());
        var selected = null;
        if (selectedId) {
          selected = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(selectedId), Constants.partitionQcTypes);
          Utils.ui.setDisabled(controls.note, !selected.noteRequired);
        } else {
          Utils.ui.setDisabled(controls.note, true)
        }
        if (!selected || !selected.noteRequired || selectedId !== qcNode.qcStatusId) {
          controls.note.val(null);
        }
      }).change();
      if (qcNode.qcStatusId) {
        var selected = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(qcNode.qcStatusId), Constants.partitionQcTypes);
        Utils.ui.setDisabled(controls.note, !selected.noteRequired);
      }

      rowData.getUpdate = function() {
        return {
          qcStatusId: Number.parseInt(controls.status.val()) || null,
          qcNote: controls.note.val() || null
        };
      };
      break;
    case 'Run-Library':
      controls.status.append(makeSelectOption(0, 'Pending', !qcNode.qcStatusId));
      controls.status.append(Constants.runLibraryQcStatuses.map(function(item) {
        return makeSelectOption(item.id, item.description, qcNode.qcStatusId === item.id);
      }));
      Utils.ui.setDisabled(controls.note, false);

      rowData.getUpdate = function() {
        return {
          qcStatusId: Number.parseInt(controls.status.val()) || null,
          qcNote: controls.note.val() || null
        };
      };
      break;
    default:
      throw new Error('Unknown entity type: ' + qcNode.entityType);
    }
    controls.note.val(qcNode.qcNote);

    controls.apply.click(function() {
      var updated = Object.assign({}, rowData.selectedNode, rowData.getUpdate());
      Utils.ajaxWithDialog('Setting Status', 'PUT', Urls.rest.qcStatuses.update, updated, function(response) {
        rowData.update(updated);
      });
    });
  }

  function updateQcCellDetailedQcStatus(controls, qcNode, rowData) {
    controls.status.append(makeSelectOption(0, 'Not Ready', !qcNode.qcStatusId));
    controls.status.append(Constants.detailedQcStatuses.map(function(item) {
      return makeSelectOption(item.id, item.description, qcNode.qcStatusId === item.id);
    })).change(function() {
      var selectedId = Number.parseInt($(this).val());
      var selected = null;
      if (selectedId) {
        selected = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(selectedId), Constants.detailedQcStatuses);
        Utils.ui.setDisabled(controls.note, !selected.noteRequired);
      } else {
        Utils.ui.setDisabled(controls.note, true);
      }
      if (!selected || !selected.noteRequired || selectedId !== qcNode.qcStatusId) {
        controls.note.val(null);
      }
    }).change();

    rowData.getUpdate = function() {
      var update = {
        qcStatusId: Number.parseInt(controls.status.val()) || null,
        qcNote: controls.note.val() || null
      };
      update.qcPassed = update.qcStatusId === null ? null : Utils.array.findUniqueOrThrow(Utils.array.idPredicate(update.qcStatusId),
          Constants.detailedQcStatuses).status;
      return update;
    }
  }

  function getNodeUrl(qcNode) {
    switch (qcNode.entityType) {
    case 'Sample':
      return Urls.ui.samples.edit(qcNode.id);
    case 'Library':
      return Urls.ui.libraries.edit(qcNode.id);
    case 'Library Aliquot':
      return Urls.ui.libraryAliquots.edit(qcNode.id);
    case 'Pool':
      return Urls.ui.pools.edit(qcNode.id);
    case 'Run':
      return Urls.ui.runs.edit(qcNode.id);
    default:
      throw new Error('Unexpected entity type: ' + qcNode.entityType);
    }
  }

  function appendQcNodeTableRow(table, label, control) {
    table.append($('<tr>').append($('<td>').text(label), $('<td>').append(control)));
  }

  function setAllRunLibraryQcs(optionValue, note) {
    $('.nodeSelect').each(function() {
      $(this).val($(this).find('option').length - 1);
      $(this).change();
    });
    $('.statusSelect').val(optionValue);
    $('.noteInput').val(note);
  }

  function getAllChanges() {
    return tableData.map(function(rowData) {
      var update = rowData.getUpdate();
      if (Object.keys(update).some(function(key) {
        return rowData.selectedNode[key] !== update[key];
      })) {
        return {
          rowData: rowData,
          update: Object.assign({}, rowData.selectedNode, update)
        };
      } else {
        return null;
      }
    }).filter(function(updated) {
      return updated !== null;
    });
  }

})(jQuery);