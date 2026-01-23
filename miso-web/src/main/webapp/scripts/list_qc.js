ListTarget.qc = function (qcTarget) {
  return {
    name: qcTarget + " QCs",
    createUrl: function (config, projectId) {
      throw new Error("QCs can only be generated statically");
    },
    getQueryUrl: null,
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.qc.getBulkActions({
        qcTarget: qcTarget,
      });
      actions.push({
        name: "Delete",
        action: function (items) {
          var lines = [
            "Are you sure you wish to delete the following items? This cannot be undone.",
          ];
          var ids = [];
          jQuery.each(items, function (index, item) {
            var qcType = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(item.qcTypeId),
              Constants.qcTypes
            );
            lines.push("* " + qcType.name + " (" + item.date + ")");
            ids.push(item.id);
          });
          Utils.showConfirmDialog("Delete QCs", "Delete", lines, function () {
            var url =
              Urls.rest.qcs.bulkDelete +
              "?" +
              Utils.page.param({
                qcTarget: qcTarget,
              });
            Utils.ajaxWithDialog("Deleting QCs", "POST", url, ids, function () {
              Utils.page.pageReload();
            });
          });
        },
      });
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return config.entityId
        ? [
            {
              name: "Add QCs",
              handler: function () {
                Utils.showDialog(
                  "Add QCs",
                  "Add",
                  [
                    {
                      property: "copies",
                      type: "int",
                      label: "QCs per " + qcTarget,
                      value: 1,
                    },
                    {
                      property: "controls",
                      type: "int",
                      label: "Controls per QC",
                      value: 1,
                    },
                  ],
                  function (result) {
                    if (!Number.isInteger(result.copies) || result.copies < 1) {
                      Utils.showOkDialog("Error", ["Invalid number of QCs entered"]);
                    } else if (!Number.isInteger(result.controls) || result.controls < 0) {
                      Utils.showOkDialog("Error", ["Invalid number of controls entered"]);
                    } else {
                      Utils.page.post(Urls.ui.qcs.bulkAddFrom(qcTarget), {
                        entityIds: config.entityId,
                        copies: result.copies,
                        controls: result.controls,
                      });
                    }
                  }
                );
              },
            },
          ]
        : [];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Method",
          mData: "qcTypeId",
          mRender: function (data, type, full) {
            return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(data), Constants.qcTypes)
              .name;
          },
        },
        {
          sTitle: "Creator",
          mData: "creator",
        },
        {
          sTitle: "Date",
          mData: "date",
          iSortPriority: 1,
        },
        {
          sTitle: "Results",
          mData: "results",
          mRender: function (data, type, full) {
            if (type === "display") {
              var qcType = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(full.qcTypeId),
                Constants.qcTypes
              );
              if (qcType.precisionAfterDecimal < 0) {
                return ListUtils.render.booleanChecks(data > 0, type, full);
              } else {
                var units = qcType.units;
                return units ? (data + " " + units) : data;
              }
            } else {
              return data;
            }
          },
        },
        {
          sTitle: "Description",
          mData: "description",
          include: true,
          iSortPriority: 0,
        },
      ];
    },
  };
};
