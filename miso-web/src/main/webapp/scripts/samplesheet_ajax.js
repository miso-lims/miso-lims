var SampleSheet = (function () {
  return {
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
})();
