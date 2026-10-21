ListTarget.samplesheetparameter = (function () {
  var TYPE_LABEL = "Sample Sheet Parameters";

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
              name: "Edit Options",
              action: function (items) {
                if (items.length !== 1) {
                  Utils.showOkDialog("Error", ["Only one parameter can be edited at a time"]);
                  return;
                }
                if (items[0].type !== "DROPDOWN") {
                  Utils.showOkDialog("Error", ["The selected parameter is not a dropdown"]);
                  return;
                }
                Utils.showWizardDialog("Edit Options", [
                  {
                    name: "Add Options",
                    handler: function () {
                      showAddParameterOptionsDialog(items[0]);
                    },
                  },
                  {
                    name: "Edit Options",
                    handler: function () {
                      showEditParameterOptionsDialog(items[0]);
                    },
                  },
                  {
                    name: "Delete Options",
                    handler: function () {
                      showDeleteParameterOptionsDialog(items[0]);
                    },
                  },
                ]);
              },
            },
            {
              name: "Remove",
              action: function (items) {
                SampleSheet.removeParameters(items);
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
              handler: showAddParameterDialog,
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
          sTitle: "Type",
          mData: "type",
        },
        {
          sTitle: "Options",
          mData: "source",
          mRender: function (data, type, full) {
            if (full.type !== "DROPDOWN") {
              return "n/a";
            }
            if (!data) {
              return "0 items";
            }
            return data.length + " items";
          },
        },
        {
          sTitle: "Input",
          mData: "multivalue",
          mRender: function (data, type, full) {
            if (type !== "display") {
              return data;
            }
            if (!data) {
              return "Single";
            }
            switch (data) {
              case "INSTRUMENT_POSITION":
                return "Per instrument position";
              default:
                throw new Error("Unexpected multivalue type: " + data);
            }
          },
        },
        {
          sTitle: "Default",
          mData: "defaultValue",
        },
      ];
    },
  };

  function showAddParameterDialog() {
    var fields = [
      {
        label: "Name",
        property: "name",
        type: "text",
        required: true,
      },
      {
        label: "Type",
        property: "type",
        type: "select",
        required: true,
        values: ["TEXT", "INT", "DECIMAL", "DATE", "DROPDOWN"],
      },
      {
        label: "Separate labels from values",
        property: "includeLabels",
        type: "checkbox",
        showIf: function (output) {
          return output.type === "DROPDOWN";
        },
        description:
          "If true, you will be prompted to include display labels for the options. Users will choose from the display labels when generating a sample sheet, but the main value (not the label) will be used in the sample sheet",
      },
      {
        label: "Additional properties",
        property: "additionalProperties",
        type: "int",
        value: 0,
        showIf: function (output) {
          return output.type === "DROPDOWN";
        },
        description:
          "Allows you to define additional properties for each option. These additional properties may be included in the sample sheet in addition to the main value",
      },
      {
        label: "Input",
        property: "multivalue",
        type: "select",
        values: [null, "INSTRUMENT_POSITION"],
        getLabel: function (value) {
          if (!value) {
            return "Per run";
          }
          switch (value) {
            case "INSTRUMENT_POSITION":
              return "Per instrument position";
            default:
              throw new Error("Inexpected multivalue type: " + value);
          }
        },
        description: "Items for which to take input",
      },
      {
        label: "Default",
        property: "defaultValue",
        type: "text",
        showIf: function (output) {
          return output.type !== "DROPDOWN";
        },
      },
    ];
    Utils.showDialog("Add Parameter", "Continue", fields, function (results) {
      if (results.type === "DROPDOWN") {
        if (results.additionalProperties) {
          showParameterValueSchemaDropdown(results);
        } else {
          showParameterValueDropdown(results);
        }
      } else {
        addParameter(results);
      }
    });
  }

  function showParameterValueSchemaDropdown(previousResults) {
    var fields = [];
    for (var i = 0; i < previousResults.additionalProperties; i++) {
      fields.push({
        label: "Additional property " + i,
        property: "additionalProperty" + i,
        type: "text",
        required: true,
      });
    }
    Utils.showDialog("Add Parameter - Dropdown Options", "Continue", fields, function (results) {
      var additionalProperties = [];
      for (var i = 0; i < previousResults.additionalProperties; i++) {
        additionalProperties.push(results["additionalProperty" + i]);
      }
      previousResults.additionalProperties = additionalProperties;
      showParameterValueDropdown(previousResults, addParameter);
    });
  }

  function showParameterValueDropdown(previousResults, callback) {
    fields = getParameterOptionFields(previousResults);
    fields.push(
      {
        label: "Set as default",
        property: "setDefault",
        type: "checkbox",
      },
      {
        label: "Add More",
        property: "addMore",
        type: "checkbox",
        value: true,
      }
    );
    Utils.showDialog("Add Dropdown Parameter Option", "Continue", fields, function (results) {
      var addMore = results.addMore;
      if (!previousResults.source) {
        previousResults.source = [];
      }
      if (results.setDefault) {
        previousResults.defaultValue = results.label || results.value;
      }

      delete results.setDefault;
      delete results.addMore;
      previousResults.source.push(results);

      if (addMore) {
        showParameterValueDropdown(previousResults, callback);
      } else {
        callback(previousResults);
      }
    });
  }

  function getParameterOptionFields(setup, existingOption) {
    fields = [];
    if (setup.includeLabels) {
      fields.push({
        label: "Label",
        property: "label",
        type: "text",
        required: true,
        value: existingOption ? existingOption.label : null,
      });
    }
    fields.push({
      label: "Value",
      property: "value",
      type: "text",
      required: true,
      value: existingOption ? existingOption.value : null,
    });
    if (setup.additionalProperties) {
      setup.additionalProperties.forEach(function (property) {
        fields.push({
          label: property,
          property: property,
          type: "text",
          value: existingOption ? existingOption[property] : null,
        });
      });
    }
    return fields;
  }

  function addParameter(dialogResults) {
    SampleSheet.addParameter({
      name: dialogResults.name,
      type: dialogResults.type,
      source: dialogResults.source,
      multivalue: dialogResults.multivalue,
      defaultValue: dialogResults.defaultValue,
    });
  }

  function extractParameterOptionConfig(parameter) {
    if (!parameter.source.length) {
      throw new Error("Can't determine parameter schema because there are no existing options");
    }
    var setup = {};
    Object.keys(parameter.source[0]).forEach(function (key) {
      switch (key) {
        case "value":
          break;
        case "label":
          setup.includeLabels = true;
          break;
        default:
          if (!setup.additionalProperties) {
            setup.additionalProperties = [];
          }
          setup.additionalProperties.push(key);
      }
    });
    return setup;
  }

  function showAddParameterOptionsDialog(parameter) {
    var setup = extractParameterOptionConfig(parameter);
    setup.source = parameter.source;
    showParameterValueDropdown(setup, function (results) {
      // source is already updated directly
      if (results.defaultValue) {
        parameter.defaultValue = results.defaultValue;
      }
      SampleSheet.refreshParameters();
    });
  }

  function showEditParameterOptionsDialog(parameter) {
    var setup = extractParameterOptionConfig(parameter);
    showWizardForParameterOptions(parameter, "Edit Dropdown Parameter Option", function (option) {
      var fields = getParameterOptionFields(setup, option);
      fields.push({
        label: "Set as default",
        property: "setDefault",
        type: "checkbox",
        value: parameter.defaultValue === (option.label || option.value),
      });
      Utils.showDialog("Edit Option", "Save", fields, function (results) {
        option.value = results.value;
        if (setup.includeLabels) {
          option.label = results.label;
        }
        setup.additionalProperties.forEach(function (property) {
          option[property] = results[property];
        });
        if (results.setDefault) {
          parameter.defaultValue = results.label || results.value;
        }
        SampleSheet.refreshParameters();
      });
    });
  }

  function showDeleteParameterOptionsDialog(parameter) {
    if (parameter.source.length === 1) {
      Utils.showOkDialog("Error", [
        "Can't delete the last option. Add a new option first, or delete the entire parameter.",
      ]);
      return;
    }
    showWizardForParameterOptions(parameter, "Delete Dropdown Parameter Option", function (option) {
      parameter.source = parameter.source.filter(function (x) {
        if (option.label) {
          return x.label !== option.label;
        }
        return x.value !== option.value;
      });
      if (parameter.defaultValue === (option.label || option.value)) {
        parameter.defaultValue = null;
      }
      SampleSheet.refreshParameters();
    });
  }

  function showWizardForParameterOptions(parameter, title, action) {
    var actions = parameter.source.map(function (option) {
      return {
        name: option.label || option.value,
        handler: function () {
          action(option);
        },
      };
    });
    Utils.showWizardDialog(title, actions);
  }
})();
