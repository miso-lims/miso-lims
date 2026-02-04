if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.arrayrun = (function ($) {
  /*
   * Expected config {
   *   isAdmin: boolean,
   *   instruments: array
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("array_runs");
    },
    getSaveUrl: function (arrayrun) {
      return arrayrun.id ? Urls.rest.arrayRuns.update(arrayrun.id) : Urls.rest.arrayRuns.create;
    },
    getSaveMethod: function (arrayrun) {
      return arrayrun.id ? "PUT" : "POST";
    },
    getEditUrl: function (arrayrun) {
      return Urls.ui.arrayRuns.edit(arrayrun.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Array Run Information",
          fields: [
            {
              title: "Array Run ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (arrayrun) {
                return arrayrun.id || "Unsaved";
              },
            },
            {
              title: "Instrument",
              data: "instrumentId",
              type: "dropdown",
              include: !object.id,
              required: true,
              nullValue: "SELECT",
              source: config.instruments,
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("name"),
            },
            {
              title: "Instrument",
              data: "instrumentId",
              type: "read-only",
              include: !!object.id,
              getDisplayValue: function (arrayrun) {
                return arrayrun.instrumentName;
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Run Path",
              data: "filePath",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Array",
              data: "arrayId",
              type: "read-only",
              getDisplayValue: function (arrayrun) {
                return arrayrun.arrayAlias;
              },
              getLink: function (arrayrun) {
                return Urls.ui.arrays.edit(arrayrun.arrayId);
              },
            },
            {
              title: "Change Array",
              type: "special",
              makeControls: function (form) {
                return [
                  $("<button>")
                    .addClass("ui-state-default")
                    .attr("type", "button")
                    .text("Search")
                    .click(function () {
                      Utils.showDialog(
                        "Array Search",
                        "Search",
                        [
                          {
                            label: "Search",
                            property: "query",
                            type: "text",
                            required: true,
                          },
                        ],
                        function (formData) {
                          Utils.ajaxWithDialog(
                            "Searching",
                            "GET",
                            Urls.rest.arrayRuns.arraySearch +
                              "?" +
                              Utils.page.param({
                                q: formData.query,
                              }),
                            null,
                            function (data) {
                              if (!data || !data.length) {
                                Utils.showOkDialog("Search Results", ["No matching arrays found"]);
                                return;
                              } else {
                                Utils.showWizardDialog(
                                  "Search Results",
                                  data.map(function (array) {
                                    return {
                                      name: array.alias,
                                      handler: function () {
                                        form.updateField("arrayId", {
                                          value: array.id,
                                          label: array.alias,
                                          link: Urls.ui.arrays.edit(array.id),
                                        });
                                        SampleArray.updateSamplesTable(array);
                                      },
                                    };
                                  })
                                );
                              }
                            }
                          );
                        }
                      );
                    }),
                  $("<button>")
                    .addClass("ui-state-default")
                    .attr("type", "button")
                    .text("Remove")
                    .click(function () {
                      if (form.get("arrayId")) {
                        Utils.showConfirmDialog(
                          "Remove Array",
                          "Remove",
                          ["Remove the array from this array run?"],
                          function () {
                            form.updateField("arrayId", {
                              value: null,
                              label: "",
                              link: null,
                            });
                            SampleArray.updateSamplesTable(null);
                          }
                        );
                      } else {
                        Utils.showOkDialog("Remove Array", ["No array set"]);
                      }
                    }),
                ];
              },
            },
            {
              title: "Status",
              data: "status",
              type: "dropdown",
              required: true,
              source: Constants.healthTypes,
              getItemLabel: function (item) {
                return item.label;
              },
              getItemValue: function (item) {
                return item.label;
              },
              onChange: function (newValue, form) {
                var status = getStatus(newValue);
                var updates = {
                  required: status.isDone,
                  // Editable if run is done and either there's no value set or user is admin
                  disabled: !status.isDone || (form.get("completionDate") && !config.isAdmin),
                };
                if (!status.isDone) {
                  updates.value = null;
                }
                form.updateField("completionDate", updates);
              },
              // Only editable by admin if run is done
              disabled: !object.status ? false : getStatus(object.status).isDone && !config.isAdmin,
            },
            FormUtils.makeQcPassedField(),
            FormUtils.makeQcUserField(),
            FormUtils.makeQcDateField(),
            {
              title: "Start Date",
              data: "startDate",
              type: "date",
              required: true,
              disabled: object.startDate && !config.isAdmin,
            },
            {
              title: "Completion Date",
              data: "completionDate",
              type: "date",
            },
          ],
        },
      ];
    },
  };

  function getStatus(label) {
    return Utils.array.findUniqueOrThrow(function (item) {
      return item.label === label;
    }, Constants.healthTypes);
  }
})(jQuery);
