ListTarget.samplesheetfield = (function () {
  var TYPE_LABEL = "Sample Sheet Field";

  /**
   * Expected config: {
   *   sectionIndex: int; used to locate the section in the samplesheet object and on the page
   *   commonSources: [
   *     {
   *       label: string,
   *       value: string,
   *       properties: (optional) [
   *         {
   *           label: string,
   *           value: string
   *         }
   *       ]
   *     }
   *   ]
   * }
   */

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "sample-sheets");
    },
    createUrl: function (config, projectId) {
      throw new Error("Static data only");
    },
    createBulkActions: function (config, projectId) {
      return !config.isAdmin
        ? []
        : [
            {
              name: "Reposition",
              action: function (items) {
                var actions = SampleSheet.getSectionFields(config.sectionIndex)
                  .filter(function (field) {
                    return !items.some(Utils.array.namePredicate(field.name));
                  })
                  .map(function (field) {
                    return {
                      name: "After " + field.name,
                      handler: function () {
                        SampleSheet.repositionSectionFields(items, field, config.sectionIndex);
                      },
                    };
                  });
                actions.unshift({
                  name: "To the top",
                  handler: function () {
                    SampleSheet.repositionSectionFields(items, null, config.sectionIndex);
                  },
                });
                Utils.showWizardDialog("Reposition Fields", actions);
              },
            },
            {
              name: "Remove",
              action: function (items) {
                SampleSheet.removeSectionFields(items, config.sectionIndex);
              },
            },
          ];
    },
    createStaticActions: function (config, projectId) {
      return !config.isAdmin
        ? []
        : [
            {
              name: "Add",
              handler: function () {
                showAddFieldDialog(config);
              },
            },
          ];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Name",
          mData: "name",
        },
        {
          sTitle: "Sources",
          mData: "sources",
          mRender: function (data, type, full) {
            if (type !== "display") {
              return data;
            }
            var texts = data.map(function (source) {
              if (source.value) {
                return '"' + source.value + '"';
              }
              var text = source.source;
              if (source.sourceProperty) {
                text += " → " + source.sourceProperty;
              }
              return text;
            });
            if (texts.length === 1) {
              return texts[0];
            }
            return (
              "<ul>" +
              texts
                .map(function (text) {
                  return "<li>" + text + "</li>";
                })
                .join("") +
              "</ul>"
            );
          },
        },
        {
          sTitle: "Omit if Empty",
          mData: "omitIfEmpty",
          mRender: function (data, type, full) {
            if (type !== "display") {
              return data;
            }
            return data ? "✔" : "✘";
          },
        },
      ];
    },
  };

  function showAddFieldDialog(config) {
    var fields = makeDialogFields(config, false);
    Utils.showDialog("Add Section Field", "Continue", fields, function (results) {
      var data = {
        name: results.name,
        sources: results.source ? [makeSourceFromResults(results)] : [],
        omitIfEmpty: results.omitIfEmpty,
      };

      if (results.addSource) {
        showAddFieldSourceDialog(config, data);
      } else {
        SampleSheet.addSectionField(data, config.sectionIndex);
      }
    });
  }

  function showAddFieldSourceDialog(config, field) {
    var fields = makeDialogFields(config, true);
    Utils.showDialog("Add Field Source", "Continue", fields, function (results) {
      if (results.source) {
        var source = makeSourceFromResults(results);
        field.sources.push(source);
      }

      if (results.addSource) {
        showAddFieldSourceDialog(config, field);
      } else {
        SampleSheet.addSectionField(field, config.sectionIndex);
      }
    });
  }

  function makeDialogFields(config, sourceOnly) {
    var parameters = SampleSheet.getParameters();
    var fields = [];
    if (!sourceOnly) {
      fields.push({
        label: "Name",
        property: "name",
        type: "text",
        required: true,
      });
    }
    fields.push({
      label: "Source",
      property: "source",
      type: "select",
      nullLabel: "None",
      values: config.commonSources
        .map(function (source) {
          return source.value;
        })
        .concat(
          parameters.map(function (parameter) {
            return parameter.name;
          })
        )
        .concat(["Static Value"]),
      getLabel: function (selectedValue) {
        var commonSource = config.commonSources.find(function (source) {
          return source.value === selectedValue;
        });
        if (commonSource) {
          return commonSource.label;
        }
        return selectedValue;
      },
      description:
        "Where to get the value. Some sources have multiple properties you can choose from.",
    });
    config.commonSources.forEach(function (source) {
      if (!source.properties) {
        return;
      }
      fields.push({
        label: "Source property",
        property: "sourceProperty",
        type: "select",
        required: true,
        values: source.properties,
        getLabel: function (x) {
          return x.label;
        },
        getValue: function (x) {
          return x.value;
        },
        showIf: function (output) {
          return output.source === source.value;
        },
      });
    });
    parameters.forEach(function (parameter) {
      if (parameter.type !== "DROPDOWN") {
        return;
      }
      var properties = Object.keys(parameter.source[0]).filter(function (key) {
        return key !== "label" && key !== "value";
      });
      if (properties.length) {
        fields.push({
          label: "Source property",
          property: "sourceProperty",
          type: "select",
          values: properties,
          nullLabel: "Main value",
          showIf: function (output) {
            return output.source === parameter.name;
          },
        });
      }
    });
    fields.push(
      {
        label: "Value",
        property: "value",
        type: "text",
        required: true,
        showIf: function (output) {
          return output.source === "Static Value";
        },
      },
      {
        label: "Date format",
        property: "dateFormat",
        type: "select",
        values: ["yyyy-MM-dd", "d/M/yyyy", "yyyyMMddHHmmss"],
        showIf: function (output) {
          if (output.source === "Static Value") {
            return false;
          }
          return (
            output.source === "CURRENT_TIME" ||
            !!parameters.find(function (parameter) {
              return parameter.name === output.source && parameter.type === "DATE";
            })
          );
        },
      },
      {
        label: "Aggregate function",
        property: "aggregation",
        type: "select",
        values: ["JOIN_DISTINCT", "MAX_LENGTH"],
        description:
          "Function to apply to the results. JOIN_DISTINCT (default) concatenates the unique results (using separator). MAX_LENGTH returns the maximum string length of the value(s).",
        showIf: function (output) {
          return output.source !== "Static Value";
        },
      },
      {
        label: "Separator",
        property: "separator",
        type: "text",
        description: "Used if there are multiple values for this source",
        showIf: function (output) {
          return output.source !== "Static Value";
        },
      }
    );
    if (!sourceOnly) {
      fields.push({
        label: "Omit if Empty",
        property: "omitIfEmpty",
        type: "checkbox",
        description: "If selected and the column is empty, it will be omitted entirely",
        showIf: function (output) {
          return output.source !== "Static Value";
        },
      });
    }
    fields.push({
      label: "Add additional source(s)",
      property: "addSource",
      type: "checkbox",
    });
    return fields;
  }

  function makeSourceFromResults(results) {
    var source = {};
    if (results.source === "Static Value") {
      source.value = results.value;
    } else {
      source.source = results.source;
      if (results.sourceProperty) {
        source.sourceProperty = results.sourceProperty;
      }
      if (results.dateFormat) {
        source.dateFormat = results.dateFormat;
      }
      if (results.aggregation) {
        source.aggregation = results.aggregation;
      }
      if (results.separator) {
        source.separator = results.separator;
      }
    }
    return source;
  }
})();
