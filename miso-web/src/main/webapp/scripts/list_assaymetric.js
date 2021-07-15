ListTarget.assaymetric = (function() {
  
  return {
    
    name: "Metrics",
    createBulkActions: function(config, projectId) {
      return !config.isAdmin || config.pageMode == 'view' ? [] : [{
        name: 'Remove',
        action: function(items) {
          Assay.removeMetrics(items);
        }
      }];
    },
    createStaticActions: function(config, projectId) {
      return !config.isAdmin || config.pageMode == 'view' ? [] : [{
        name: 'Add',
        handler: function() {
          if (!Constants.metrics.length) {
            Utils.showOkDialog('Error', ['No metrics found. Metrics must be created from the Metrics list page before'
                + ' they can be added to assays.']);
            return;
          }
          Utils.showWizardDialog('Add Metric', Constants.metrics.map(function(metric) {
            return {
              name: metric.alias,
              handler: function() {
                addSelectedMetric(metric);
              }
            };
          }));
        }
      }];
    },
    createColumns: function(config, projectId) {
      return [{
        sTitle: 'Alias',
        mData: metricPropertyDataFunction('alias'),
        iSortPriority: 1,
        bSortDirection: true
      }, {
        sTitle: 'Category',
        mData: metricPropertyDataFunction('category'),
        mRender: function(data, type, full) {
          var category = Utils.array.findUniqueOrThrow(function(x) {
            return x.value === data;
          }, Constants.metricCategories);
          return category.label;
        }
      }, {
        sTitle: 'Threshold Type',
        mData: metricPropertyDataFunction('thresholdType'),
        mRender: function(data, type, full) {
          var category = Utils.array.findUniqueOrThrow(function(x) {
            return x.value === data;
          }, Constants.thresholdTypes);
          return category.sign;
        }
      }, {
        sTitle: 'Minimum',
        mData: 'minimumThreshold',
        mRender: function(data, type, full) {
          return data || 'n/a';
        }
      }, {
        sTitle: 'Maximum',
        mData: 'maximumThreshold',
        mRender: function(data, type, full) {
          return data || 'n/a';
        }
      }, {
        sTitle: 'Units',
        mData: metricPropertyDataFunction('units'),
        mRender: function(data, type, full) {
          return data || 'n/a';
        }
      }];
    }
    
  };
  
  function metricPropertyDataFunction(property) {
    return function(full) {
      var metric = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(full.id), Constants.metrics);
      return metric[property];
    }
  }
  
  function addSelectedMetric(metric) {
    var thresholdType = Utils.array.findUniqueOrThrow(function(x) {
      return x.value === metric.thresholdType;
    }, Constants.thresholdTypes);
    var fields = [];
    if (thresholdType.lowerBound) {
      fields.push({
        label: 'Lower Bound',
        type: 'float',
        required: true,
        property: 'lowerBound'
      });
    }
    if (thresholdType.upperBound) {
      fields.push({
        label: 'Upper Bound',
        type: 'float',
        required: true,
        property: 'upperBound'
      });
    }
    if (fields.length) {
      Utils.showDialog('Add Metric', 'Add', fields, function(results) {
        Assay.addMetric({
          id: metric.id,
          minimumThreshold: results.lowerBound || null,
          maximumThreshold: results.upperBound || null
        });
      });
    } else {
      Assay.addMetric({
        id: metric.id,
        minimumThreshold: null,
        maximumThreshold: null
      });
    }
  }

})();
