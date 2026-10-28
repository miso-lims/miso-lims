ListTarget.samplesheetsection = (function () {
  var TYPE_LABEL = "Sample Sheet Section";

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
                var actions = SampleSheet.getSections()
                  .filter(function (section) {
                    return !items.some(Utils.array.namePredicate(section.name));
                  })
                  .map(function (section) {
                    return {
                      name: "After " + section.name,
                      handler: function () {
                        SampleSheet.repositionSections(items, section);
                      },
                    };
                  });
                actions.unshift({
                  name: "To the top",
                  handler: function () {
                    SampleSheet.repositionSections(items, null);
                  },
                });
                Utils.showWizardDialog("Reposition Sections", actions);
              },
            },
            {
              name: "Remove",
              action: function (items) {
                SampleSheet.removeSections(items);
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
                showAddSectionDialog();
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
          sTitle: "Optional",
          mData: "optional",
          mRender: function (data, type, full) {
            if (type !== "display") {
              return data;
            }
            return data ? "✔" : "✘";
          },
        },
        {
          sTitle: "Format",
          mData: "format",
          mRender: function (data, type, full) {
            if (type !== "display") {
              return data;
            }
            var text = data;
            if (full.omitFieldNames) {
              text += "; field headings omitted";
            } else if (full.invadeHeader) {
              text += "; field headings in header row";
            }
            return text;
          },
        },
        {
          sTitle: "Output",
          mData: "multivalue",
          mRender: function (data, type, full) {
            if (type === "display" && !data) {
              return "Single";
            }
            return data;
          },
        },
      ];
    },
  };

  function showAddSectionDialog() {
    var fields = [
      {
        label: "Name",
        property: "name",
        type: "text",
        required: true,
      },
      {
        label: "Optional",
        property: "optional",
        type: "checkbox",
        description:
          "When generating a sample sheet, the user can specify which optional sections to include",
      },
      {
        label: "Format",
        property: "format",
        type: "select",
        values: ["COLUMNS", "ROWS"],
        description:
          "Which way the fields are laid out (output items will run in the opposite direction)",
      },
      {
        label: "Output",
        property: "multivalue",
        type: "select",
        nullLabel: "Single",
        values: ["INSTRUMENT_POSITIONS", "LIBRARY_ALIQUOTS", "DISTINCT_LIBRARY_ALIQUOTS"],
        description: "The items for which to show values",
      },
      {
        label: "Invade Header",
        property: "invadeHeader",
        type: "checkbox",
        description:
          'If true, the values for the first field are added to the same row as the section header. The first field\'s name will be omitted since the section header is in its place. Only applicable for format "ROWS"',
        showIf: function (output) {
          return output.format === "ROWS";
        },
      },
      {
        label: "Omit Field Names",
        property: "omitFieldNames",
        type: "checkbox",
      },
    ];
    Utils.showDialog("Add Section", "Add", fields, function (results) {
      results.fields = [];
      SampleSheet.addSection(results);
    });
  }
})();
