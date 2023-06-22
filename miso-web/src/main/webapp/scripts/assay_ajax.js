var Assay = (function () {
  var metricsListId = "listMetrics";
  var testsListId = "listTests";

  var form = null;
  var listConfig = {};

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setListConfig: function (config) {
      listConfig = config;
    },

    setMetrics: function (metrics) {
      FormUtils.setTableData(ListTarget.assaymetric, listConfig, metricsListId, metrics, form);
    },

    getMetrics: function () {
      return FormUtils.getTableData(metricsListId);
    },

    addMetric: function (addMetric) {
      var metrics = Assay.getMetrics();
      if (
        metrics.find(function (metric) {
          return metric.id === addMetric.id;
        })
      ) {
        Utils.showOkDialog("Error", ["This metric is already included"]);
      } else {
        metrics.push(addMetric);
        Assay.setMetrics(metrics);
      }
    },

    removeMetrics: function (removeMetrics) {
      var metrics = Assay.getMetrics().filter(function (metric) {
        return !removeMetrics.some(function (removal) {
          return removal.id === metric.id;
        });
      });
      Assay.setMetrics(metrics);
    },

    setTests: function (tests) {
      FormUtils.setTableData(ListTarget.assaytest, listConfig, testsListId, tests, form);
    },

    getTests: function () {
      return FormUtils.getTableData(testsListId);
    },

    addTest: function (addTest) {
      var tests = Assay.getTests();
      if (
        tests.find(function (test) {
          return test.id === addTest.id;
        })
      ) {
        Utils.showOkDialog("Error", ["This test is already included"]);
      } else {
        tests.push(addTest);
        Assay.setTests(tests);
      }
    },

    removeTests: function (removeTests) {
      var tests = Assay.getTests().filter(function (test) {
        return !removeTests.some(function (removal) {
          return removal.id === test.id;
        });
      });
      Assay.setTests(tests);
    },

    utils: {
      makeLabel: function (assay) {
        return assay.alias + " v" + assay.version;
      },
      getSortPriority: function (metric, category, subcategory) {
        // category and subcategory are optional and will be determined from the metric if not provided
        if (!category) {
          category = Utils.array.findUniqueOrThrow(function (x) {
            return x.value === metric.category;
          }, Constants.metricCategories);
        }
        if (metric.subcategoryId && !subcategory) {
          subcategory = Utils.array.findUniqueOrThrow(
            Utils.array.idPredicate(metric.subcategoryId),
            Constants.metricSubcategories
          );
        }
        // no subcategory: top, subcategory with no priority: bottom
        var subcategoryPriority = subcategory ? subcategory.sortPriority || 500 : 0;
        // metric with no priority: bottom
        var metricPriority = metric.sortPriority || 500;

        return category.sortPriority * 1000000 + subcategoryPriority * 1000 + metricPriority;
      },
      showMetrics: function (assay, categoryValue) {
        var metricCategory = Utils.array.findUniqueOrThrow(function (x) {
          return x.value === categoryValue;
        }, Constants.metricCategories);
        var data = assay.metrics
          .map(function (assayMetric) {
            var metric = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(assayMetric.id),
              Constants.metrics
            );
            return {
              metric: metric,
              metricSubcategory: getMetricSubcategory(metric),
              assayMetric: assayMetric,
            };
          })
          .filter(function (x) {
            return x.metric.category === metricCategory.value;
          })
          .sort(
            Utils.sorting.standardSortByCallback(function (x) {
              return Assay.utils.getSortPriority(x.metric, metricCategory, x.metricSubcategory);
            })
          );

        lines = [];
        var currentSubcategoryId = null;
        for (var i = 0; i < data.length; i++) {
          var x = data[i];
          if (x.metric.subcategoryId !== currentSubcategoryId) {
            lines.push(x.metricSubcategory.alias + ":");
            currentSubcategoryId = x.metricSubcategory.id;
          }
          lines.push("• " + metricLabelWithThreshold(x.metric, x.assayMetric));
        }
        if (!lines.length) {
          lines.push("(No metrics defined)");
        }
        var title = assay.alias + " v" + assay.version + " " + metricCategory.label + " Metrics";
        Utils.showOkDialog(title, lines);
      },
    },
  };

  function getMetricSubcategory(metric) {
    if (metric.subcategoryId) {
      return Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(metric.subcategoryId),
        Constants.metricSubcategories
      );
    }
    return null;
  }

  function metricLabelWithThreshold(metric, assayMetric) {
    var label = metric.label;
    switch (metric.thresholdType) {
      case "LT":
        label += ": < " + assayMetric.maximumThreshold;
        break;
      case "LE":
        label += ": <= " + assayMetric.maximumThreshold;
        break;
      case "GT":
        label += ": > " + assayMetric.minimumThreshold;
        break;
      case "GE":
        label += ": >= " + assayMetric.minimumThreshold;
        break;
      case "BETWEEN":
        label += ": " + assayMetric.minimumThreshold + " - " + assayMetric.maximumThreshold;
        break;
    }
    if (metric.units) {
      label += " " + metric.units;
    }
    return label;
  }
})();
