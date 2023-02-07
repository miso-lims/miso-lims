if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.run = (function ($) {
  /*
   * Expected config {
   *   isAdmin: boolean,
   *   isRunReviewer: boolean,
   *   sops: array
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("sequencing_runs");
    },
    getSaveUrl: function (run) {
      if (run.id) {
        return Urls.rest.runs.update(run.id);
      } else {
        return Urls.rest.runs.create;
      }
    },
    getSaveMethod: function (run) {
      return run.id ? "PUT" : "POST";
    },
    getEditUrl: function (run) {
      return Urls.ui.runs.edit(run.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Run Information",
          fields: [
            {
              title: "Run ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (run) {
                return run.id || "Unsaved";
              },
            },
            {
              title: "Name",
              data: "name",
              type: "read-only",
              getDisplayValue: function (run) {
                return run.name || "Unsaved";
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
              title: "Accession",
              data: "accession",
              type: "read-only",
              getLink: function (run) {
                return Urls.external.enaAccession(run.accession);
              },
              include: object.accession,
            },
            {
              title: "Platform",
              data: "platformType",
              type: "read-only",
            },
            {
              title: "Sequencer",
              data: "instrumentId",
              type: "read-only",
              getDisplayValue: function (run) {
                return run.instrumentName + " - " + run.instrumentModelAlias;
              },
              getLink: function (run) {
                return Urls.ui.instruments.edit(run.instrumentId);
              },
            },
          ]
            .concat(FormUtils.makeSopFields(object, config.sops))
            .concat([
              {
                title: "Sequencing Parameters",
                data: "sequencingParametersId",
                type: "dropdown",
                nullLabel: "SELECT",
                source: Constants.sequencingParameters.filter(function (param) {
                  return param.instrumentModelId === object.instrumentModelId;
                }),
                getItemLabel: Utils.array.getName,
                getItemValue: Utils.array.getId,
                required: true,
              },
              {
                title: "Sequencing Kit",
                data: "sequencingKitId",
                type: "dropdown",
                nullLabel: "N/A",
                source: Constants.kitDescriptors.filter(function (kit) {
                  return (
                    kit.kitType === "Sequencing" &&
                    kit.platformType === object.platformType &&
                    (!kit.archived || kit.id === object.sequencingKitId)
                  );
                }),
                getItemLabel: Utils.array.getName,
                getItemValue: Utils.array.getId,
                onChange: function (newValue, form) {
                  var opts = {
                    disabled: !newValue,
                  };
                  if (!newValue) {
                    opts.value = null;
                  }
                  form.updateField("sequencingKitLot", opts);
                },
              },
              {
                title: "Sequencing Kit Lot",
                data: "sequencingKitLot",
                type: "text",
                maxLength: 100,
              },
              {
                title: "Description",
                data: "description",
                type: "text",
                maxLength: 255,
              },
              {
                title: "Run Path",
                data: "runPath",
                type: "text",
                maxLength: 255,
              },
              {
                title: "Workflow Type",
                include: object.platformType === "Illumina",
                data: "workflowType",
                type: "dropdown",
                nullLabel: "N/A",
                source: Constants.illuminaWorkflowTypes,
                getItemLabel: function (item) {
                  return item.label;
                },
                getItemValue: function (item) {
                  return item.value;
                },
              },
              {
                title: "Index Sequencing",
                data: "dataManglingPolicy",
                type: "dropdown",
                source: Constants.dataManglingPolicies,
                getItemLabel: function (item) {
                  return item.label;
                },
                getItemValue: function (item) {
                  return item.value;
                },
                nullLabel: "Instrument Default",
              },
              {
                title: "Number of Cycles",
                include: object.platformType === "Illumina",
                data: "numCycles",
                type: "int",
                min: "0",
              },
              {
                title: "Called Cycles",
                include: object.platformType === "Illumina",
                data: "calledCycles",
                type: "int",
                min: "0",
              },
              {
                title: "Imaged Cycles",
                include: object.platformType === "Illumina",
                data: "imagedCycles",
                type: "int",
                min: "0",
              },
              {
                title: "Scored Cycles",
                include: object.platformType === "Illumina",
                data: "scoredCycles",
                type: "int",
                min: "0",
              },
              {
                title: "Cycles",
                include: object.platformType === "LS454",
                data: "cycles",
                type: "int",
                min: "0",
              },
              {
                title: "Paired End",
                include: ["Illumina", "Solid", "LS454"].indexOf(object.platformType) !== -1,
                data: "pairedEnd",
                type: "checkbox",
              },
              {
                title: "MinKNOW Version",
                include: object.platformType === "Oxford Nanopore",
                data: "minKnowVersion",
                type: "text",
                maxLength: 100,
              },
              {
                title: "Protocol Version",
                include: object.platformType === "Oxford Nanopore",
                data: "protocolVersion",
                type: "text",
                maxLength: 100,
              },
              {
                title: "Status",
                data: "status",
                type: "dropdown",
                source: Constants.healthTypes.filter(function (status) {
                  return status.allowedFromSequencer;
                }),
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
                    disabled: !status.isDone || (form.get("endDate") && !config.isAdmin),
                  };
                  if (!status.isDone) {
                    updates.value = null;
                  }
                  form.updateField("endDate", updates);
                },
                required: true,
                // Only editable by admin if run is done
                disabled: !object.status
                  ? false
                  : getStatus(object.status).isDone && !config.isAdmin,
              },
              {
                title: "Start Date",
                data: "startDate",
                type: "date",
                required: true,
                disabled: object.startDate && !config.isAdmin,
              },
              {
                title: "Completion Date",
                data: "endDate",
                type: "date",
              },
              (function () {
                var qcPassed = FormUtils.makeQcPassedField();
                qcPassed.onChange = function (newValue, form) {
                  if (config.isRunReviewer) {
                    form.updateField("dataReview", {
                      disabled: newValue === null,
                      value: newValue !== null && newValue === object.qcPassed ? undefined : null,
                    });
                  } else if (newValue === null || newValue !== object.qcPassed) {
                    form.updateField("dataReview", {
                      label: "Pending",
                    });
                  }
                  if (newValue === null || newValue !== object.qcPassed) {
                    form.updateField("dataReviewer", {
                      label: "n/a",
                    });
                    form.updateField("dataReviewDate", {
                      label: "n/a",
                    });
                  }
                };
                return qcPassed;
              })(),
              FormUtils.makeQcUserField(),
              FormUtils.makeQcDateField(),
              {
                title: "Data Review",
                data: "dataReview",
                type: "dropdown",
                include: config.isRunReviewer,
                source: [
                  {
                    label: "Pass",
                    value: true,
                  },
                  {
                    label: "Fail",
                    value: false,
                  },
                ],
                convertToBoolean: true,
                getItemLabel: function (item) {
                  return item.label;
                },
                getItemValue: function (item) {
                  return item.value;
                },
                nullLabel: "Pending",
              },
              {
                title: "Data Review",
                data: "dataReview",
                type: "read-only",
                getDisplayValue: function (item) {
                  if (item.dataReview === true) {
                    return "Pass";
                  } else if (item.dataReview === false) {
                    return "Fail";
                  } else {
                    return "Pending";
                  }
                },
                include: !config.isRunReviewer,
              },
              {
                title: "Data Reviewer",
                data: "dataReviewer",
                type: "read-only",
                getDisplayValue: function (run) {
                  return run.dataReviewer || "n/a";
                },
              },
              {
                title: "Data Review Date",
                data: "dataReviewDate",
                type: "read-only",
                getDisplayValue: function (run) {
                  return run.dataReviewDate || "n/a";
                },
              },
            ]),
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
