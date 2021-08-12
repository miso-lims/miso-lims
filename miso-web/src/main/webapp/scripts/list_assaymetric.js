ListTarget.assaymetric = (function() {
  
  return {
    
    name: "Metrics",
    createBulkActions: function(config, projectId) {
      return !config.isAdmin || config.pageMode == 'view' ? [] : [{
        name: 'Remove',
        action: Assay.removeMetrics
      }];
    },
    createStaticActions: function(config, projectId) {
      return !config.isAdmin || config.pageMode == 'view' ? [] : [{
        name: 'Add',
        handler: showAddMetricDialog
      }];
    },
    createColumns: function(config, projectId) {
      return [{
        sTitle: 'Alias',
        mData: metricPropertyDataFunction('alias')
      }, {
        sTitle: 'Category Sort',
        mData: function(full) {
          return getSortPriority(full.id);
        },
        bVisible: false
      }, {
        sTitle: 'Category',
        mData: metricPropertyDataFunction('category'),
        mRender: function(data, type, full) {
          var category = Utils.array.findUniqueOrThrow(function(x) {
            return x.value === data;
          }, Constants.metricCategories);
          return category.label;
        },
        iDataSort: 1,
        iSortPriority: 1,
        bSortDirection: true
      }, {
        sTitle: 'Subcategory',
        mData: metricPropertyDataFunction('subcategoryId'),
        mRender: ListUtils.render.textFromId(Constants.metricSubcategories, 'alias', '')
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
  
  function showAddMetricDialog() {
    Utils.showWizardDialog('Add Metric', Constants.metricCategories.map(function(category) {
      return {
        name: category.label,
        handler: function() {
          var metrics = Constants.metrics.filter(function(metric) {
            return metric.category === category.value;
          }).sort(Utils.sorting.standardSort('alias'));
          if (!metrics.length) {
            Utils.showOkDialog('Error', ['No metrics found. Metrics must be created from the Metrics list page before'
                + ' they can be added to assays.']);
            return;
          }
          Utils.showWizardDialog('Add ' + category.label + ' Metric', metrics.map(function(metric) {
            return {
              name: metric.alias,
              handler: function() {
                addSelectedMetric(metric);
              }
            };
          }));
        }
      };
    }));
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
  
  function getSortPriority(metricId) {
    var metric = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(metricId), Constants.metrics);
    var category = Utils.array.findUniqueOrThrow(function(x) {
      return x.value === metric.category;
    }, Constants.metricCategories);
    var subcategory = !metric.subcategoryId ? null
        : Utils.array.findUniqueOrThrow(Utils.array.idPredicate(metric.subcategoryId), Constants.metricSubcategories);
    
    // no subcategory: top, subcategory with no priority: bottom
    var subcategoryPriority = subcategory ? (subcategory.sortPriority || 500) : 0;
    // metric with no priority: bottom
    var metricPriority = metric.sortPriority || 500;
    
    return category.sortPriority * 1000000 + subcategoryPriority * 1000 + metricPriority;
  }

})();
