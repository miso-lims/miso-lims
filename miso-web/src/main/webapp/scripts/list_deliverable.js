ListTarget.deliverable = (function ($) {
  return {
    name: "Deliverables",
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "deliverables");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      if (config.projectId != null && config.projectId >= 0) {
        return [
          {
            name: "Remove",
            action: Project.removeDeliverables,
          },
        ];
      }
      var actions = BulkTarget.deliverable.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(
          ListUtils.createBulkDeleteAction("Deliverables", "deliverables", function (item) {
            return item.name;
          })
        );
      }
      return actions;
    },
    createStaticActions: function (config, projectId) {
      if (config.projectId != null && config.projectId >= 0) {
        return [
          {
            name: "Add",
            handler: function () {
              showAddDeliverableDialog(config.deliverables);
            },
          },
        ];
      }
      return config.isAdmin
        ? [ListUtils.createStaticAddAction("Deliverables", Urls.ui.deliverables.bulkCreate, true)]
        : [];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Name",
          mData: "name",
        },
        {
          sTitle: "Category",
          mData: "categoryName",
        },
        {
          sTitle: "Analysis Review Required",
          mData: "analysisReviewRequired",
          mRender: ListUtils.render.booleanChecks,
        },
      ];
    },
  };

  function showAddDeliverableDialog(deliverables) {
    Utils.showWizardDialog(
      "Add Deliverable",
      deliverables.map(function (deliverable) {
        return {
          name: deliverable.name,
          handler: function () {
            Project.addDeliverable(deliverable);
          },
        };
      })
    );
  }
})(jQuery);
