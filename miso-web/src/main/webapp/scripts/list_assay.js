ListTarget.assay = (function ($) {
  var TYPE_LABEL = "Assays";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("requisitions", "assays");
    },
    showNewOptionSop: true,
    createBulkActions: function (config, projectId) {
      return !config.isAdmin
        ? []
        : [
            {
              name: "Copy",
              action: function (items) {
                if (items.length > 1) {
                  Utils.showOkDialog("Error", ["Select an individual assay to copy"]);
                  return;
                }
                window.location = Urls.ui.assays.create + "?" + $.param({ baseId: items[0].id });
              },
            },
            ListUtils.createBulkDeleteAction(TYPE_LABEL, "assays", function (x) {
              return x.alias + " v" + x.version;
            }),
          ];
    },
    createStaticActions: function (config, projectId) {
      return !config.isAdmin
        ? []
        : [
            {
              name: "Add",
              handler: function () {
                Utils.page.pageRedirect(Urls.ui.assays.create);
              },
            },
          ];
    },
    createColumns: function (config, projectId) {
      return [
        ListUtils.labelHyperlinkColumn(
          "Alias",
          Urls.ui.assays.edit,
          Utils.array.getId,
          "alias",
          1,
          true
        ),
        {
          sTitle: "Version",
          mData: "version",
        },
        {
          sTitle: "Archived",
          mData: "archived",
          mRender: ListUtils.render.archived,
        },
      ];
    },
  };
})(jQuery);
