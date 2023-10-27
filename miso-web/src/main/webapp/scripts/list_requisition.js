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
          mData: "assayId",
          mRender: ListUtils.render.textFromId(Constants.assays, "alias", "n/a"),
        },
        {
          sTitle: "Stopped",
          mData: "stopped",
          mRender: function (data, type, full) {
            if (type === "display") {
              return data ? "🛑" : "";
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
        searchTerms["entered"],
        searchTerms["creator"],
        searchTerms["changed"],
        searchTerms["changedby"],
      ];
    },
  };
})();
