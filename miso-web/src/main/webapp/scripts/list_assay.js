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
      } else if (config.requisitionId != null) {
        return [
          {
            name: "Remove",
            action: Requisition.removeAssays,
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
            handler: showAddProjectAssayDialog,
          },
        ];
      } else if (config.requisitionId != null) {
        return [
          {
            name: "Add",
            handler: function () {
              showAddRequisitionAssayDialog(config);
            },
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

  function showAddProjectAssayDialog() {
    var nonArchivedAssays = Constants.assays.filter(function (x) {
      return !x.archived;
    });
    Utils.showWizardDialog(
      "Add Assay",
      nonArchivedAssays.map(function (assay) {
        return {
          name: Assay.utils.makeLabel(assay),
          handler: function () {
            Project.addAssay(assay);
          },
        };
      })
    );
  }

  function showAddRequisitionAssayDialog(config) {
    var potentialAssays = [];
    if (config.numberOfRequisitionedItems) {
      potentialAssays = config.potentialAssayIds.map(function (assayId) {
        return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(assayId), Constants.assays);
      });
    } else {
      potentialAssays = Constants.assays;
    }
    potentialAssays = potentialAssays.filter(function (potentialAssay) {
      return (
        !potentialAssay.archived &&
        !Requisition.getAssays().some(function (assay) {
          return assay.id === potentialAssay.id;
        })
      );
    });
    if (!potentialAssays.length) {
      Utils.showOkDialog("Error", [
        "No potential assays to add. An assay must be assigned to all projects associated with the requisition, and must not be archived.",
      ]);
      return;
    }
    Utils.showWizardDialog(
      "Add assay",
      potentialAssays.map(function (potentialAssay) {
        return {
          name: potentialAssay.alias + " v" + potentialAssay.version,
          handler: function () {
            Requisition.addAssay(potentialAssay);
          },
        };
      })
    );
  }
})(jQuery);
