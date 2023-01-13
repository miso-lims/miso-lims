BulkTarget = window.BulkTarget || {};
BulkTarget.qc = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   qcTarget: string {Sample, Library, Pool, Container}
   *   addControls: int
   *   instruments: array
   * }
   */

  var controlCount = 0;

  return {
    getSaveUrl: function () {
      return Urls.rest.qcs.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.qcs.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("qcs");
    },
    getBulkActions: function (config) {
      return [
        {
          name: "Edit",
          action: function (items) {
            Utils.showDialog(
              "Edit QCs",
              "Edit",
              [
                {
                  property: "controls",
                  type: "int",
                  label: "Add controls per QC",
                  value: 0,
                },
              ],
              function (result) {
                if (!Number.isInteger(result.controls) || result.controls < 0) {
                  Utils.showOkDialog("Error", ["Invalid number of controls entered"]);
                  return;
                }
                Utils.page.post(Urls.ui.qcs.bulkEdit(config.qcTarget), {
                  ids: items.map(Utils.array.getId).join(","),
                  addControls: result.controls,
                });
              }
            );
          },
        },
      ];
    },
    prepareData: function (data, config) {
      controlCount =
        Math.max.apply(
          null,
          data.map(function (item) {
            return item.controls ? item.controls.length : 0;
          })
        ) + (config.addControls || 0);
    },
    getColumns: function (config, api) {
      var columns = [
        {
          title: config.qcTarget + " Alias",
          type: "text",
          data: "entityAlias",
          disabled: true,
        },
        {
          title: "Date",
          type: "date",
          data: "date",
          required: true,
        },
        {
          title: "Type",
          type: "dropdown",
          data: "qcTypeId",
          source: function (data, api) {
            if (config.pageMode === "edit") {
              return [
                Utils.array.findUniqueOrThrow(
                  Utils.array.idPredicate(data.qcTypeId),
                  Constants.qcTypes
                ),
              ];
            } else {
              return Constants.qcTypes.filter(function (qcType) {
                return qcType.qcTarget == config.qcTarget && !qcType.archived;
              });
            }
          },
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: Utils.sorting.standardSort("name"),
          required: true,
          disabled: config.pageMode === "edit",
          onChange: function (rowIndex, newValue, api) {
            var qcType = Utils.array.findFirstOrNull(function (item) {
              return item.qcTarget === config.qcTarget && item.name === newValue;
            }, Constants.qcTypes);

            // Update instruments
            var selectedInstrument = null;
            if (qcType) {
              var selectedInstrumentName = api.getValue(rowIndex, "instrumentId");
              selectedInstrument = Utils.array.findFirstOrNull(function (instrument) {
                return (
                  instrument.instrumentModelId === qcType.instrumentModelId &&
                  instrument.name === selectedInstrumentName
                );
              }, config.instruments);
            }
            var instrumentRequired = qcType && qcType.instrumentModelId;
            api.updateField(rowIndex, "instrumentId", {
              source: instrumentRequired
                ? config.instruments.filter(function (instrument) {
                    return (
                      instrument.instrumentModelId === qcType.instrumentModelId &&
                      (!instrument.dateDecommissioned ||
                        (selectedInstrument && instrument.id === selectedInstrument.id))
                    );
                  })
                : [],
              disabled: !instrumentRequired,
              required: instrumentRequired,
              value: instrumentRequired ? undefined : null,
            });

            // Update kits
            var kitRequired = qcType && qcType.kitDescriptors && qcType.kitDescriptors.length;
            api.updateField(rowIndex, "kitDescriptorId", {
              source: kitRequired ? qcType.kitDescriptors : [],
              disabled: !kitRequired,
              required: kitRequired,
              value: kitRequired ? undefined : null,
            });
            api.updateField(rowIndex, "kitLot", {
              disabled: !kitRequired,
              required: kitRequired,
              value: kitRequired ? undefined : null,
            });

            // Update results format
            if (qcType) {
              if (qcType.precisionAfterDecimal < 0) {
                api.updateField(rowIndex, "results", {
                  type: "dropdown",
                  source: [
                    {
                      label: "Pass",
                      value: "1.0",
                    },
                    {
                      label: "Fail",
                      value: "0.0",
                    },
                  ],
                });
              } else {
                api.updateField(rowIndex, "results", {
                  type: "decimal",
                  precision: qcType.precisionAfterDecimal + 6,
                  scale: qcType.precisionAfterDecimal,
                });
              }
            }

            // Update units
            api.updateField(rowIndex, "units", {
              value: qcType ? qcType.units : null,
            });

            // Update controls
            var controlRequired = qcType && qcType.controls && qcType.controls.length;
            for (var i = 0; i < controlCount; i++) {
              api.updateField(rowIndex, "controls." + i + ".controlId", {
                source: controlRequired ? qcType.controls : [],
                disabled: !controlRequired,
                required: controlRequired && i == 0,
              });
              if (!controlRequired) {
                api.updateField(rowIndex, "controls." + i + ".lot", {
                  disabled: true,
                  required: false,
                  value: null,
                });
                api.updateField(rowIndex, "controls." + i + ".qcPassed", {
                  disabled: true,
                  required: false,
                  value: null,
                });
              }
            }
          },
        },
        {
          title: "Instrument",
          type: "dropdown",
          data: "instrumentId",
          getData: function (qc) {
            if (!qc.instrumentId) {
              return null;
            }
            return Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(qc.instrumentId),
              config.instruments
            ).name;
          },
          source: [], // set in QC type onChange
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Kit",
          type: "dropdown",
          data: "kitDescriptorId",
          getData: function (qc) {
            if (!qc.kitDescriptorId) {
              return null;
            }
            return Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(qc.kitDescriptorId),
              Constants.kitDescriptors
            ).name;
          },
          source: [], // set in QC type onChange
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Kit Lot",
          type: "text",
          data: "kitLot",
          maxLength: 50,
        },
        {
          title: "Result",
          type: "decimal", // switches between decimal and dropdown in QC type onChange
          data: "results",
          getData: function (qc, api) {
            if (qc.results === null) {
              return null;
            }
            var qcType = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(qc.qcTypeId),
              Constants.qcTypes
            );
            if (qcType.precisionAfterDecimal < 0) {
              return qc.results > 0 ? "Pass" : "Fail";
            } else if (qcType.precisionAfterDecimal === 0) {
              return qc.results.split(".")[0];
            } else {
              return qc.results;
            }
          },
          required: true,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
        },
        {
          title: "Units",
          type: "text",
          data: "units",
          omit: true,
          disabled: true,
        },
        BulkUtils.columns.description,
      ];

      // Add control columns
      for (var i = 1; i <= controlCount; i++) {
        (function () {
          var controlNumber = i;
          var index = i - 1;
          columns.push(
            {
              title: "Control " + controlNumber,
              type: "dropdown",
              data: "controls." + index + ".controlId",
              getData: function (qc) {
                if (!qc.controls || !qc.controls.length || qc.controls.length < controlNumber) {
                  return null;
                }
                var qcType = Utils.array.findUniqueOrThrow(
                  Utils.array.idPredicate(qc.qcTypeId),
                  Constants.qcTypes
                );
                var control = Utils.array.findUniqueOrThrow(
                  Utils.array.idPredicate(qc.controls[index].controlId),
                  qcType.controls
                );
                return control.alias;
              },
              source: [],
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              sortSource: true,
              onChange: function (rowIndex, newValue, api) {
                api.updateField(rowIndex, "controls." + index + ".lot", {
                  disabled: !newValue,
                  required: !!newValue,
                  value: newValue ? undefined : null,
                });
                api.updateField(rowIndex, "controls." + index + ".qcPassed", {
                  disabled: !newValue,
                  required: !!newValue,
                  value: newValue ? undefined : null,
                });
              },
            },
            {
              title: "Control " + controlNumber + " Lot",
              type: "text",
              data: "controls." + index + ".lot",
              maxLength: 50,
            },
            {
              title: "Control " + controlNumber + " Passed?",
              type: "dropdown",
              data: "controls." + index + ".qcPassed",
              source: [
                {
                  label: "True",
                  value: true,
                },
                {
                  label: "False",
                  value: false,
                },
              ],
              getItemLabel: Utils.array.get("label"),
              getItemValue: Utils.array.get("value"),
            }
          );
        })();
      }

      return columns;
    },
    confirmSave: function (data, config) {
      data.forEach(function (qc) {
        qc.qcTarget = config.qcTarget;
        if (qc.controls) {
          qc.controls = qc.controls.filter(function (control) {
            return control.controlId;
          });
        }
      });
    },
  };
})();
