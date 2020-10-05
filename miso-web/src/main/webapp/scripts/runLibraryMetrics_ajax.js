var RunLibraryMetrics = (function($) {

  var tableSelector = '#metricsTable';

  var poolQcOptions = [{
    value: null,
    label: 'Not Ready'
  }, {
    value: true,
    label: 'Ready'
  }, {
    value: false,
    label: 'Failed'
  }];

  var runLibraryQcOptions = [{
    value: null,
    label: 'Pending'
  }, {
    value: true,
    label: 'Passed'
  }, {
    value: false,
    label: 'Failed'
  }];

  return {
    buildTable: function(data) {
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
        row.append(makeEffectiveQcCell(rowData));
        row.append(makeQcCell(rowData));
        tbody.append(row);
      });
      table.append(tbody);
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

  function makeEffectiveQcCell(rowData) {
    var cell = $('<td>');
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

  function makeQcCell(rowData) {
    var cell = $('<td>');
    var table = $('<table>');

    var controls = {
      node: $('<select>').append(rowData.qcNodes.map(function(qcNode, i) {
        return makeSelectOption(i, qcNode.typeLabel + ' ' + qcNode.label);
      })).val(rowData.qcNodes.length - 1),
      name: $('<span>'),
      status: $('<select>'),
      note: $('<input>'),
      apply: $('<button>').text('Apply')
    };
    controls.node.change(function() {
      var i = Number.parseInt($(this).val());
      var qcNode = rowData.qcNodes[i];
      updateQcCell(controls, qcNode);
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

  function updateQcCell(controls, qcNode) {
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
      updateQcCellDetailedQcStatus(controls, qcNode, Urls.rest.samples.updateQcStatus(qcNode.id));
      break;
    case 'Library':
      updateQcCellDetailedQcStatus(controls, qcNode, Urls.rest.libraries.updateQcStatus(qcNode.id));
      break;
    case 'Library Aliquot':
      updateQcCellDetailedQcStatus(controls, qcNode, Urls.rest.libraryAliquots.updateQcStatus(qcNode.id));
      break;
    case 'Pool':
      controls.status.append(poolQcOptions.map(function(item, i) {
        var qcPassed = qcNode.qcPassed === undefined ? null : qcNode.qcPassed;
        return makeSelectOption(i, item.label, qcPassed === item.value);
      }));
      controls.apply.click(function() {
        var selectedStatus = poolQcOptions[controls.status.val()].value;
        Utils.ajaxWithDialog('Setting Status', 'PUT', Urls.rest.pools.updateQcStatus(qcNode.id) + $.param({
          qcPassed: selectedStatus
        }), null, function(response) {
          qcNode.qcPassed = selectedStatus;
        });
      });
      break;
    case 'Run':
      controls.status.append(Constants.healthTypes.map(function(item) {
        return makeSelectOption(item.label, item.label, qcNode.runStatus === item.label);
      }));
      controls.apply.click(function() {
        var selectedStatus = controls.status.val();
        Utils.ajaxWithDialog('Setting Status', 'PUT', Urls.rest.runs.updateStatus(qcNode.id) + $.param({
          status: selectedStatus
        }), null, function(response) {
          qcNode.runStatus = selectedStatus;
        });
      });
      break;
    case 'RunPartition':
      controls.status.append(makeSelectOption(0, 'Not Set', !qcNode.qcStatusId));
      controls.status.append(Constants.partitionQcTypes.map(function(item) {
        return makeSelectOption(item.id, item.description, qcNode.qcStatusId === item.id);
      })).change(function() {
        var selectedId = Number.parseInt($(this).val());
        var selected = null;
        if (selectedId) {
          selected = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(qcNode.qcStatusId), Constants.partitionQcTypes);
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
      controls.apply.click(function() {
        var update = {
          qcStatusId: Number.parseInt(controls.status.val()) || null,
          note: controls.note.val() || null
        };
        Utils.ajaxWithDialog('Setting Status', 'PUT', Urls.rest.runs.updatePartitionQcStatus(qcNode.ids[0], qcNode.ids[1]), update,
            function(response) {
              qcNode.qcStatusId = update.qcStatusId;
              qcNode.note = update.note;
            });
      });
      break;
    case 'RunPartitionLibrary':
      controls.status.append(runLibraryQcOptions.map(function(item, i) {
        var qcPassed = qcNode.qcPassed === undefined ? null : qcNode.qcPassed;
        return makeSelectOption(i, item.label, qcPassed === item.value);
      }));
      Utils.ui.setDisabled(controls.note, false);
      controls.apply.click(function() {
        var update = {
          qcPassed: runLibraryQcOptions[controls.status.val()].value,
          note: controls.note.val() || null
        };
        Utils.ajaxWithDialog('Setting Status', 'PUT', Urls.rest.runs.updateLibraryQcStatus(qcNode.ids[0], qcNode.ids[1], qcNode.ids[2]),
            update, function(response) {
              qcNode.qcPassed = update.qcPassed;
              qcNode.note = update.note;
            });
      });
      break;
    default:
      throw new Error('Unknown entity type: ' + qcNode.entityType);
    }
    controls.note.val(qcNode.qcNote);
  }

  function updateQcCellDetailedQcStatus(controls, qcNode, updateUrl) {
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

    controls.apply.click(function() {
      var update = {
        qcStatusId: Number.parseInt(controls.status.val()) || null,
        note: controls.note.val() || null
      };
      Utils.ajaxWithDialog('Setting Status', 'PUT', updateUrl, update, function(response) {
        qcNode.qcStatusId = update.qcStatusId;
        qcNode.qcNote = update.note;
      });
    });
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

})(jQuery);