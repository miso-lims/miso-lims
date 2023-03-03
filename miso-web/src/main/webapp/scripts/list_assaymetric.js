ListTarget.assaymetric = (function () {
  return {
    name: "Metrics",
    createBulkActions: function (config, projectId) {
      return !config.isAdmin || config.pageMode == "view"
        ? []
        : [
            {
              name: "Remove",
              action: Assay.removeMetrics,
            },
          ];
    },
    createStaticActions: function (config, projectId) {
      return !config.isAdmin || config.pageMode == "view"
        ? []
        : [
            {
              name: "Add",
              handler: showAddMetricDialog,
            },
          ];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Metric",
          mData: metricPropertyDataFunction("label"),
        },
        {
          sTitle: "Category Sort",
          mData: function (full) {
            var metric = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(full.id),
              Constants.metrics
            );
            return Assay.utils.getSortPriority(metric);
          },
          bVisible: false,
        },
        {
          sTitle: "Category",
          mData: metricPropertyDataFunction("category"),
          mRender: function (data, type, full) {
            var category = Utils.array.findUniqueOrThrow(function (x) {
              return x.value === data;
            }, Constants.metricCategories);
            return category.label;
          },
          iDataSort: 1,
          iSortPriority: 1,
          bSortDirection: true,
        },
        {
          sTitle: "Subcategory",
          mData: metricPropertyDataFunction("subcategoryId"),
          mRender: ListUtils.render.textFromId(Constants.metricSubcategories, "alias", ""),
        },
        {
          sTitle: "Threshold Type",
          mData: metricPropertyDataFunction("thresholdType"),
          mRender: function (data, type, full) {
            var category = Utils.array.findUniqueOrThrow(function (x) {
              return x.value === data;
            }, Constants.thresholdTypes);
            return category.sign;
          },
        },
        {
          sTitle: "Minimum",
          mData: "minimumThreshold",
          mRender: function (data, type, full) {
            return data || "n/a";
          },
        },
        {
          sTitle: "Maximum",
          mData: "maximumThreshold",
          mRender: function (data, type, full) {
            return data || "n/a";
          },
        },
        {
          sTitle: "Units",
          mData: metricPropertyDataFunction("units"),
          mRender: function (data, type, full) {
            return data || "n/a";
          },
        },
      ];
    },
  };

  function metricPropertyDataFunction(property) {
    return function (full) {
      var metric = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(full.id),
        Constants.metrics
      );
      return metric[property];
    };
  }

  function showAddMetricDialog() {
    Utils.showWizardDialog(
      "Add Metric",
      Constants.metricCategories.map(function (category) {
        return {
          name: category.label,
          handler: function () {
            var metrics = Constants.metrics
              .filter(function (metric) {
                return metric.category === category.value;
              })
              .sort(Utils.sorting.standardSort("alias"));
            if (!metrics.length) {
              Utils.showOkDialog("Error", [
                "No metrics found. Metrics must be created from the Metrics list page before" +
                  " they can be added to assays.",
              ]);
              return;
            }
            var subcategoryIds = [];
            metrics.forEach(function (metric) {
              if (subcategoryIds.indexOf(metric.subcategoryId) === -1) {
                subcategoryIds.push(metric.subcategoryId);
              }
            });
            if (subcategoryIds.length > 1) {
              Utils.showWizardDialog(
                "Add " + category.label + " Metric",
                subcategoryIds.map(function (subcategoryId) {
                  var subcategory = null;
                  if (!subcategoryId) {
                    subcategory = {
                      id: null,
                      alias: "No subcategory",
                    };
                  } else {
                    subcategory = Utils.array.findUniqueOrThrow(
                      Utils.array.idPredicate(subcategoryId),
                      Constants.metricSubcategories
                    );
                  }
                  return {
                    name: subcategory.alias,
                    handler: function () {
                      metrics = metrics.filter(function (metric) {
                        return metric.subcategoryId === subcategory.id;
                      });
                      showFinalMetricSelectionDialog(
                        metrics,
                        subcategory.id ? subcategory.alias : category.label
                      );
                    },
                  };
                })
              );
            } else {
              showFinalMetricSelectionDialog(metrics, category.label);
            }
          },
        };
      })
    );
  }

  function showFinalMetricSelectionDialog(metrics, categoryLabel) {
    Utils.showWizardDialog(
      "Add " + categoryLabel + " Metric",
      metrics.map(function (metric) {
        var label = metric.label + " (";
        if (metric.units) {
          label += metric.units + " ";
        }
        var thresholdType = Utils.array.findUniqueOrThrow(function (x) {
          return x.value === metric.thresholdType;
        }, Constants.thresholdTypes);
        label += Utils.decodeHtmlString(thresholdType.sign) + ")";

        return {
          name: label,
          handler: function () {
            addSelectedMetric(metric);
          },
        };
      })
    );
  }

  function addSelectedMetric(metric) {
    var thresholdType = Utils.array.findUniqueOrThrow(function (x) {
      return x.value === metric.thresholdType;
    }, Constants.thresholdTypes);
    var fields = [];
    var unitDisplay = metric.units ? " (" + metric.units + ")" : "";
    if (thresholdType.lowerBound) {
      fields.push({
        label: "Lower Bound" + unitDisplay,
        type: "float",
        required: true,
        property: "lowerBound",
      });
    }
    if (thresholdType.upperBound) {
      fields.push({
        label: "Upper Bound" + unitDisplay,
        type: "float",
        required: true,
        property: "upperBound",
      });
    }
    if (fields.length) {
      Utils.showDialog("Add Metric", "Add", fields, function (results) {
        Assay.addMetric({
          id: metric.id,
          minimumThreshold: results.lowerBound || null,
          maximumThreshold: results.upperBound || null,
        });
      });
    } else {
      Assay.addMetric({
        id: metric.id,
        minimumThreshold: null,
        maximumThreshold: null,
      });
    }
  }
})();
