ListTarget.orderaliquot = {
  name: 'Library Aliquots',
  createUrl: function(config, projectId) {
    throw new Error("Can only be created statically");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return [{
      name: 'Remove',
      action: function(orderAliquots) {
        PoolOrder.removeAliquots(orderAliquots.map(Utils.array.getId));
      }
    }, {
      name: 'Edit Proportions',
      action: function(orderAliquots) {
        var fields = [];
        orderAliquots.forEach(function(orderAliquot) {
          fields.push({
            type: 'int',
            label: orderAliquot.aliquot.name + ' (' + orderAliquot.aliquot.alias + ')',
            value: orderAliquot.proportion,
            property: 'aliquot' + orderAliquot.aliquot.id + 'Proportion',
            required: true
          });
        });
        Utils.showDialog('Edit Proportions', 'OK', fields, function(output) {
          var alis = PoolOrder.getAliquots();
          alis.forEach(function(ali) {
            if (output['aliquot' + ali.aliquot.id + 'Proportion']) {
              ali.proportion = output['aliquot' + ali.aliquot.id + 'Proportion'];
            }
          });
          PoolOrder.setAliquots(alis);
        });
      }
    }];
  },
  createStaticActions: function(config, projectId) {
    return [{
      name: 'Add',
      handler: function() {
        Utils.showDialog("Add Aliquots", "Search", [{
          label: "Names, Aliases, or Barcodes",
          type: "textarea",
          property: "names",
          rows: 15,
          cols: 40,
          required: true
        }], function(result) {
          var names = result.names.split(/[ \t\r\n]+/).filter(function(name) {
            return name.length > 0;
          });
          if (names.length == 0) {
            return;
          }
          Utils.ajaxWithDialog('Searching', 'POST', Urls.rest.libraryAliquots.query, names, function(aliquots) {
            var dupes = [];
            PoolOrder.getAliquots().forEach(function(orderAli) {
              if (aliquots.map(Utils.array.getId).indexOf(orderAli.aliquot.id) !== -1) {
                dupes.push(orderAli.aliquot);
              }
            });
            if (dupes.length) {
              Utils.showOkDialog('Error', ['The following aliquots are already included in this order:'].concat(dupes
                  .map(function(aliquot) {
                    return '* ' + aliquot.name + ' (' + aliquot.alias + ')';
                  })));
            } else {
              Utils.showDialog('Edit Proportions', 'Add', aliquots.map(function(aliquot) {
                return {
                  label: aliquot.name + ' (' + aliquot.alias + ')',
                  type: 'int',
                  property: 'aliquot' + aliquot.id + 'Proportion',
                  required: true,
                  value: 1
                };
              }), function(proportionResults) {
                PoolOrder.addAliquots(aliquots.map(function(aliquot) {
                  return {
                    id: aliquot.id,
                    aliquot: aliquot,
                    proportion: proportionResults['aliquot' + aliquot.id + 'Proportion']
                  };
                }));
              })
            }
          });
        });
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [{
      'sTitle': 'Name',
      'mData': 'aliquot.id', // for sorting purposes
      'include': true,
      'iSortPriority': 1,
      "mRender": function(data, type, full) {
        if (type === 'display') {
          return '<a href="' + Urls.ui.libraryAliquots.edit(data) + '">' + full.aliquot.name + '</a>';
        }
        return data;
      }
    }, ListUtils.labelHyperlinkColumn("Alias", Urls.ui.libraryAliquots.edit, function(orderAliquot) {
      return orderAliquot.aliquot.id;
    }, "aliquot.alias", 0, true), {
      'sTitle': 'Proportion',
      'sType': 'numeric',
      'mData': 'proportion',
      'include': !config.add,
      'iSortPriority': 0
    }, ListUtils.idHyperlinkColumn("Sample Name", Urls.ui.samples.edit, "aliquot.sampleId", function(orderAli) {
      return orderAli.aliquot.sampleId;
    }, 0, true, "noPrint"), ListUtils.labelHyperlinkColumn("Sample Alias", Urls.ui.samples.edit, function(orderAli) {
      return orderAli.aliquot.sampleId;
    }, "aliquot.sampleAlias", 0, true), {
      'sTitle': 'Indices',
      'mData': 'aliquot.indexIds',
      'include': true,
      'bSortable': false,
      'iSortPriority': 0,
      'mRender': function(data, type, full) {
        if (!data || !data.length) {
          return '(none)';
        }
        var indices = Constants.indexFamilies.reduce(function(acc, family) {
          return acc.concat(family.indices.filter(function(index) {
            return data.indexOf(index.id) != -1;
          }));
        }, []).sort(function(a, b) {
          return a.position - b.position;
        });

        return indices.map(function(index) {
          return index.label;
        }).join(', ');
      }
    }, {
      "sTitle": "Description",
      "mData": null,
      "mRender": Warning.tableWarningRenderer(WarningTarget.orderaliquot.makeTarget(config.duplicateSequences, config.nearDuplicateIndices)),
      "include": true,
      "iSortPriority": 0
    }, {
      'sTitle': 'Last Modified',
      'mData': 'aliquot.lastModified',
      'include': true,
      'iSortPriority': 0
    }, {
      "sTitle": "QC Passed",
      "mData": "aliquot.libraryQcPassed",
      "include": true,
      "iSortPriority": 0,
      "mRender": ListUtils.render.booleanChecks,
      "bSortable": false
    }];
  }
};
