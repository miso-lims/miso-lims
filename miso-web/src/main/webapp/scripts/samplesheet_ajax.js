var SampleSheet = (function ($) {
  var parameterListId = "listParameters";
  var form = null;
  var admin = false;
  var parameterListConfig = {};
  var sectionListId = "listSections";
  var sectionListConfig = {};
  var fieldListsContainerId = "sectionFieldLists";
  var sampleSheetFieldCommonSources = null;

  return {
    setup: function (formApi, isAdmin, commonSources) {
      form = formApi;
      admin = isAdmin;
      sampleSheetFieldCommonSources = commonSources;
      parameterListConfig = {
        isAdmin: isAdmin,
      };
      sectionListConfig = {
        isAdmin: isAdmin,
      };
    },

    setParameters: function (parameters) {
      FormUtils.setTableData(
        ListTarget.samplesheetparameter,
        parameterListConfig,
        parameterListId,
        parameters || [],
        form
      );
    },

    getParameters: function () {
      return FormUtils.getTableData(parameterListId);
    },

    refreshParameters: function () {
      SampleSheet.setParameters(SampleSheet.getParameters());
    },

    addParameter: function (parameter) {
      var parameters = SampleSheet.getParameters();
      if (parameters.some(Utils.array.namePredicate(parameter.name))) {
        Utils.showOkDialog("Error", [
          "There is already a parameter named '" + parameter.name + "'",
        ]);
        return;
      }
      if (
        parameter.name === "None" ||
        sampleSheetFieldCommonSources.some(function (source) {
          return source.value === parameter.name;
        })
      ) {
        Utils.showOkDialog("Error", [
          "Invalid parameter name: '" + parameter.name + "' (reserved word)",
        ]);
        return;
      }
      parameters.push(parameter);
      SampleSheet.setParameters(parameters);
    },

    removeParameters: function (removeParameters) {
      var parameters = SampleSheet.getParameters().filter(function (parameter) {
        return !removeParameters.some(Utils.array.namePredicate(parameter.name));
      });
      SampleSheet.setParameters(parameters);
    },

    setSections: function (sections) {
      var previousSectionCount = SampleSheet.getSections().length;
      for (var i = 0; i < previousSectionCount; i++) {
        FormUtils.removeTable("listSection" + i);
      }

      sections = sections || [];
      FormUtils.setTableData(
        ListTarget.samplesheetsection,
        sectionListConfig,
        sectionListId,
        sections,
        form
      );

      var container = $("#" + fieldListsContainerId);
      container.empty();
      sections.forEach(function (section, index) {
        var listId = "listSection" + index;
        container.append("<h1>" + section.name + "</h1>");
        container.append('<div id="' + listId + '"></div>');
        container.append("<br />");
        SampleSheet.setSectionFields(section.fields, index);
      });
    },

    getSections: function () {
      var sections = FormUtils.getTableData(sectionListId) || [];
      sections.forEach(function (section, index) {
        section.fields = SampleSheet.getSectionFields(index);
      });
      return sections;
    },

    addSection: function (section) {
      var sections = SampleSheet.getSections();
      if (sections.some(Utils.array.namePredicate(section.name))) {
        Utils.showOkDialog("Error", ["There is already a section named '" + section.name + "'"]);
        return;
      }
      sections.push(section);
      SampleSheet.setSections(sections);
    },

    removeSections: function (removeSections) {
      var sections = SampleSheet.getSections().filter(function (section) {
        return !removeSections.some(Utils.array.namePredicate(section.name));
      });
      SampleSheet.setSections(sections);
    },

    repositionSections: function (repositionSections, afterSection) {
      var sections = SampleSheet.getSections().filter(function (section) {
        return !repositionSections.some(Utils.array.namePredicate(section.name));
      });
      var index = 0;
      if (afterSection) {
        index = sections.findIndex(Utils.array.namePredicate(afterSection.name)) + 1;
      }
      for (var i = 0; i < repositionSections.length; i++) {
        sections.splice(index + i, 0, repositionSections[i]);
      }
      SampleSheet.setSections(sections);
    },

    getSectionFields: function (sectionIndex) {
      return FormUtils.getTableData("listSection" + sectionIndex);
    },

    setSectionFields: function (fields, sectionIndex) {
      var listConfig = {
        sectionIndex: sectionIndex,
        isAdmin: admin,
        commonSources: sampleSheetFieldCommonSources,
      };
      FormUtils.setTableData(
        ListTarget.samplesheetfield,
        listConfig,
        "listSection" + sectionIndex,
        fields || [],
        form
      );
    },

    addSectionField: function (field, sectionIndex) {
      var fields = SampleSheet.getSectionFields(sectionIndex);
      if (fields.some(Utils.array.namePredicate(field.name))) {
        Utils.showOkDialog("Error", ["There is already a field named '" + field.name + "'"]);
        return;
      }
      fields.push(field);
      SampleSheet.setSectionFields(fields, sectionIndex);
    },

    removeSectionFields: function (removeFields, sectionIndex) {
      var fields = SampleSheet.getSectionFields(sectionIndex).filter(function (field) {
        return !removeFields.some(Utils.array.namePredicate(field.name));
      });
      SampleSheet.setSectionFields(fields, sectionIndex);
    },

    repositionSectionFields: function (repositionFields, afterField, sectionIndex) {
      var fields = SampleSheet.getSectionFields(sectionIndex).filter(function (field) {
        return !repositionFields.some(Utils.array.namePredicate(field.name));
      });
      var index = 0;
      if (afterField) {
        index = fields.findIndex(Utils.array.namePredicate(afterField.name)) + 1;
      }
      for (var i = 0; i < repositionFields.length; i++) {
        fields.splice(index + i, 0, repositionFields[i]);
      }
      SampleSheet.setSectionFields(fields, sectionIndex);
    },

    fetchSampleSheets: function (platformType, callback) {
      Utils.ajaxWithDialog(
        "Fetching sample sheets",
        "GET",
        Urls.rest.sampleSheets.list + "?" + $.param({ platform: platformType }),
        null,
        function (sampleSheets) {
          if (sampleSheets.length) {
            callback(sampleSheets);
          } else {
            var platformTypeObject = Utils.array.findUniqueOrThrow(function (x) {
              return x.name === platformType;
            }, Constants.platformTypes);
            Utils.showOkDialog("Error", [
              "There are no sample sheets definitions for " + platformTypeObject.key,
            ]);
          }
        }
      );
    },
    makeSampleSheetParameterFields: function (sampleSheet, selectedPositions) {
      var fields = [];
      if (!sampleSheet.parameters) {
        return fields;
      }
      sampleSheet.parameters.forEach(function (parameter) {
        var template = {
          label: parameter.name,
          property: parameter.name,
          value: parameter.defaultValue,
        };
        switch (parameter.type) {
          case "TEXT":
            template.type = "text";
            break;
          case "INT":
            template.type = "int";
            break;
          case "DECIMAL":
            template.type = "float";
            break;
          case "DATE":
            template.type = "date";
            break;
          case "DROPDOWN":
            template.type = "select";
            template.values = parameter.source;
            template.getLabel = function (x) {
              return x.hasOwnProperty("label") ? x.label : x.value;
            };
            break;
          default:
            throw new Error("Unexpected parameter type: " + parameter.type);
        }
        if (parameter.multivalue === null) {
          fields.push(template);
        } else if (parameter.multivalue === "INSTRUMENT_POSITION") {
          selectedPositions.forEach(function (position) {
            var field = Object.assign({}, template);
            field.label = position + " " + field.label;
            field.property = "position_" + position + "_" + field.property;
            fields.push(field);
          });
        } else {
          throw new Error("Unexpected parameter multivalue type: " + parameter.multivalue);
        }
      });

      sampleSheet.sections.forEach(function (section) {
        if (section.optional) {
          fields.push({
            label: "Include " + section.name,
            property: "includeSection_" + section.name,
            type: "checkbox",
          });
        }
      });
      return fields;
    },

    makeSampleSheetParameterData: function (results, sampleSheet) {
      var data = {
        customParameters: {},
        includeSections: {},
      };
      sampleSheet.parameters.forEach(function (parameter) {
        if (parameter.multivalue === null) {
          data.customParameters[parameter.name] =
            parameter.type === "DROPDOWN"
              ? results[parameter.name]["value"]
              : results[parameter.name];
        } else {
          data.customParameters[parameter.name] = {};
          selectedPositions.forEach(function (position) {
            data.customParameters[parameter.name][position] =
              results["position_" + position + "_" + parameter.name];
          });
        }
      });
      sampleSheet.sections.forEach(function (section) {
        if (section.optional) {
          var include = results["includeSection_" + section.name];
          data.includeSections[section.name] = include;
        }
      });
      return data;
    },
  };
})(jQuery);
