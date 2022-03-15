var Assay = (function() {
  
  var metricsListId = 'listMetrics';
  var testsListId = 'listTests';
  
  var form = null;
  var listConfig = {};
  
  return {
    setForm: function(formApi) {
      form = formApi;
    },

    setListConfig: function(config) {
      listConfig = config;
    },

    setMetrics: function(metrics) {
      FormUtils.setTableData(ListTarget.assaymetric, listConfig, metricsListId, metrics, form);
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
    },

    setTests: function(tests) {
      FormUtils.setTableData(ListTarget.assaytest, listConfig, testsListId, tests, form);
    },

    getTests: function() {
      return FormUtils.getTableData(testsListId);
    },

    addTest: function(addTest) {
      var tests = Assay.getTests();
      tests.push(addTest);
      Assay.setTests(tests);
    },

    removeTests: function(removeTests) {
      var tests = Assay.getTests().filter(function(test) {
        return !removeTests.some(function(removal) {
          return removal.id === test.id;
        });
      })
      Assay.setTests(tests);
    },
  };
  
})();