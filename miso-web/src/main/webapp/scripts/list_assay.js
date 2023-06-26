ListTarget.assay = (function ($) {
  var TYPE_LABEL = "Assays";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("requisitions", "assays");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    getQueryUrl: null,
    showNewOptionSop: true,
    createBulkActions: function (config, projectId) {
      if (config.projectId != null && config.projectId >= 0) {
        return [
          {
            name: "Remove",
            action: Project.removeAssays,
          },
        ];
      } else if (config.isAdmin) {
        return [
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
      }
      return [];
    },
    createStaticActions: function (config, projectId) {
      if (config.projectId != null && config.projectId >= 0) {
        return [
          {
            name: "Add",
            handler: showAddAssayDialog,
          },
        ];
      } else if (config.isAdmin) {
        return [
          {
            name: "Add",
            handler: function () {
              Utils.page.pageRedirect(Urls.ui.assays.create);
            },
          },
        ];
      }
      return [];
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

  function showAddAssayDialog() {
    var nonArchivedAssays = Constants.assays.filter(function (x) {
      return !x.archived;
    });
    Utils.showWizardDialog(
      "Add Assay",
      nonArchivedAssays.map(function (assay) {
        return {
          name: assay.alias,
          handler: function () {
            Project.addAssay(assay);
          },
        };
      })
    );
  }
})(jQuery);
