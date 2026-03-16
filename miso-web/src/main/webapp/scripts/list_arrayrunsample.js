ListTarget.arrayrunsample = {
  name: "Array Run Samples",
  createUrl: function (config, projectId) {
    return Urls.rest.arrayRuns.samples(config.arrayRunId);
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    if (!config || !config.arrayRunId) {
      return [];
    }
    return [
      {
        name: "Set QC",
        action: function (samples) {
          var fields = [
            {
              label: "Status",
              type: "select",
              property: "status",
              values: [
                {
                  id: null,
                  description: "Pending",
                },
              ].concat(Constants.runItemQcStatuses),
              getLabel: Utils.array.get("description"),
            },
            {
              label: "Note",
              type: "text",
              property: "qcNote",
            },
          ];
          Utils.showDialog("Set QC", "OK", fields, function (output) {
            samples.forEach(function (sample) {
              sample.qcStatusId = output.status.id;
              sample.qcNote = output.qcNote ? output.qcNote : null;
            });
            Utils.ajaxWithDialog(
              "Setting QC",
              "PUT",
              Urls.rest.arrayRuns.samples(config.arrayRunId),
              samples,
              Utils.page.pageReload
            );
          });
        },
      },
    ];
  },

  createStaticActions: function (config, projectId) {
    return [];
  },

  onFirstLoad: function (data, config) {
    $("#listingSamplesTable_wrapper").css("padding-bottom", "");
    $("#listingSamplesTable_wrapper .datatable-scroll").css({
      "margin-bottom": "",
      "min-height": data && data.length ? "180px" : "",
      "overflow-x": "auto",
      "overflow-y": "visible",
    });
  },

  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Position",
        mData: "position",
      },
      ListUtils.labelHyperlinkColumn("Name", Urls.ui.samples.edit, Utils.array.get("sampleId"), "sampleName", 0, true),
      ListUtils.labelHyperlinkColumn("Alias", Urls.ui.samples.edit, Utils.array.get("sampleId"), "sampleAlias", 0, true),
      {
        sTitle: "QC Status",
        mData: "qcStatusId",
        mRender: function (data, type, full) {
          if (data === undefined || data === null) {
            return "Pending";
          }
          var status = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(data), Constants.runItemQcStatuses);
          if (type !== "display") {
            return status.description;
          }
          return (
            '<div class="tooltip"><span>' +
            status.description +
            "</span>" +
            '<span class="tooltiptext">Set by ' +
            full.qcUserName +
            ", " +
            full.qcDate +
            "</span></div>"
          );
        },
      },
      {
        sTitle: "QC Note",
        mData: "qcNote",
        sDefaultContent: "",
      },
    ];
  },
};
