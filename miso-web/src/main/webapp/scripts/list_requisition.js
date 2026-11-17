ListTarget.requisition = (function () {
  var TYPE_LABEL = "Requisitions";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("requisitions");
    },
    createUrl: function (config, projectId) {
      return Urls.rest.requisitions.datatable;
    },
    createBulkActions: function (config, projectId) {
      var actions = BulkUtils.actions.qc("Requisition");
      actions.push({
        name: "Stop",
        action: showBulkStopDialog,
      });
      actions.push(
        ListUtils.createBulkDeleteAction(TYPE_LABEL, "requisitions", Utils.array.getAlias)
      );
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return [
        {
          name: "Add",
          handler: function () {
            Utils.page.pageRedirect(Urls.ui.requisitions.create);
          },
        },
      ];
    },
    createColumns: function (config, projectId) {
      return [
        ListUtils.labelHyperlinkColumn(
          "Alias",
          Urls.ui.requisitions.edit,
          Utils.array.getId,
          "alias",
          1,
          true
        ),
        {
          sTitle: "Assay",
          mData: "assayIds",
          bSortable: false,
          mRender: function (data, type, full) {
            if (!data || !data.length) {
              return "n/a";
            }

            return data
              .map(function (assayId) {
                var assay = Utils.array.findUniqueOrThrow(
                  Utils.array.idPredicate(assayId),
                  Constants.assays
                );
                return "<div>" + assay.alias + "</div>";
              })
              .join("");
          },
        },
        {
          sTitle: "Stopped",
          mData: "stopped",
          bSortable: false,
          mRender: function (data, type, full) {
            if (type === "display") {
              if (data) {
                return "<span title='Stopped'>🛑</span>";
              } else if (
                full.pauses &&
                full.pauses.some(function (pause) {
                  return !pause.endDate || pause.endDate > Utils.getCurrentDate();
                })
              ) {
                return "<span title='Paused'>⌛</span>";
              }
              return "";
            }
            return data;
          },
        },
        {
          sTitle: "Entered",
          mData: "creationTime",
          mRender: ListUtils.render.dateWithTimeTooltip,
        },
        {
          sTitle: "Modified",
          mData: "lastModified",
          mRender: ListUtils.render.dateWithTimeTooltip,
          iSortPriority: 1,
          bSortDirection: false,
        },
      ];
    },
    searchTermSelector: function (searchTerms) {
      return [
        searchTerms["id"],
        searchTerms["requisitionStatus"],
        searchTerms["entered"],
        searchTerms["creator"],
        searchTerms["changed"],
        searchTerms["changedby"],
      ];
    },
  };

  function showBulkStopDialog(items) {
    Utils.showDialog(
      "Stop Requisitions",
      "Confirm",
      [
        {
          label: "Reason",
          property: "stopReason",
          type: "text",
          required: true,
        },
      ],
      function (results) {
        var data = {
          requisitionIds: items.map(Utils.array.getId),
          stopReason: results.stopReason,
        };
        Utils.saveWithProgressDialog(
          "Stopping Requisitions",
          "POST",
          Urls.rest.requisitions.bulkStop,
          data,
          Urls.rest.requisitions.bulkSaveProgress,
          Utils.page.pageReload
        );
      }
    );
  }
})();
