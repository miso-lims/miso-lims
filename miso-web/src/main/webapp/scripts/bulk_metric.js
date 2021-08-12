BulkTarget = window.BulkTarget || {};
BulkTarget.metric = (function() {

  /*
   * Expected config: {
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.metrics.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.metrics.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'metrics');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.metrics.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [BulkUtils.columns.simpleAlias(100), {
        title: 'Category',
        type: 'dropdown',
        data: 'category',
        source: Constants.metricCategories,
        getItemLabel: Utils.array.get('label'),
        getItemValue: Utils.array.get('value'),
        required: true,
        disabled: config.pageMode === 'edit',
        onChange: function(rowIndex, newValue, api) {
          var category = Constants.metricCategories.find(function(x) {
            return x.label === newValue;
          });
          var subcategories = !category ? [] : Constants.metricSubcategories.filter(function(x) {
            return x.category === category.value;
          });
          api.updateField(rowIndex, 'subcategoryId', {
            source: subcategories,
            disabled: !subcategories.length,
            value: subcategories.length ? undefined : null
          });
        }
      }, {
        title: 'Subcategory',
        type: 'dropdown',
        data: 'subcategoryId',
        getData: function(metric) {
          if (metric.subcategoryId) {
            var subcategory = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(metric.subcategoryId),
                Constants.metricSubcategories);
            return subcategory.alias;
          } else {
            return '';
          }
        },
        source: [], // initialized in category onChange
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId,
        disabled: true
      }, {
        title: 'Threshold Type',
        type: 'dropdown',
        data: 'thresholdType',
        source: Constants.thresholdTypes,
        getItemLabel: function(item) {
          return Utils.decodeHtmlString(item.sign);
        },
        getItemValue: Utils.array.get('value'),
        required: true
      }, {
        title: 'Units',
        type: 'text',
        data: 'units'
      }, {
        title: 'Sort Priority',
        type: 'int',
        data: 'sortPriority',
        min: 1,
        max: 255,
        description: 'Defines sorting order for metrics within a category and subcategory. Lower numbers should be'
            + ' sorted higher, and unspecified should be at the bottom. Must be between 1 and 255 inclusive'
      }];
    }
  };

})();