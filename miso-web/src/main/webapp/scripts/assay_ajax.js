var Assay = (function() {
  
  var metricsListId = 'listMetrics';
  
  var form = null;
  var metricsListConfig = {};
  
  return {
    setForm: function(formApi) {
      form = formApi;
    },

    setMetricsListConfig: function(config) {
      metricsListConfig = config;
    },

    setMetrics: function(metrics) {
      FormUtils.setTableData(ListTarget.assaymetric, metricsListConfig, metricsListId, metrics, form);
    },

    getMetrics: function() {
      return FormUtils.getTableData(metricsListId);
    },

    addMetric: function(addMetric) {
      var metrics = Assay.getMetrics();
      metrics.push(addMetric);
      Assay.setMetrics(metrics);
    },

    removeMetrics: function(removeMetrics) {
      var metrics = Assay.getMetrics().filter(function(metric) {
        return !removeMetrics.some(function(removal) {
          return removal.id === metric.id;
        });
      })
      Assay.setMetrics(metrics);
    }
  };
  
})();