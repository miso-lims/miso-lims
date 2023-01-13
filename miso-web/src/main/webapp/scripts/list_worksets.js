ListTarget.workset = {
  name: "Worksets",
  getUserManualUrl: function () {
    return Urls.external.userManual("worksets");
  },
  createUrl: function (config, projectId) {
    return Urls.rest.worksets.categoryDatatable(config.category);
  },
  createBulkActions: function (config, projectId) {
    return [
      {
        name: "Merge",
        action: function (items) {
          var fields = [
            {
              label: "Alias",
              property: "alias",
              type: "text",
              required: true,
            },
            {
              label: "Description",
              property: "description",
              type: "textarea",
              rows: 3,
              required: false,
            },
          ];
          Utils.showDialog("New Workset", "Create", fields, function (input) {
            var mergeData = {
              ids: items.map(Utils.array.getId),
              alias: input.alias,
              description: input.description,
            };
            Utils.ajaxWithDialog(
              "Merging Worksets",
              "POST",
              Urls.rest.worksets.merge,
              mergeData,
              function (mergedWorkset) {
                Utils.showOkDialog(
                  "Merged Worksets",
                  ["New workset '" + input.alias + "' created."],
                  function () {
                    Utils.page.pageRedirect(Urls.ui.worksets.edit(mergedWorkset.id));
                  }
                );
              }
            );
          });
        },
      },
      {
        name: "Delete",
        action: function (items) {
          var lines = [
            "Are you sure you wish to delete the following Worksets? This cannot be undone.",
            "Note: a Workset may only be deleted by its creator or an admin.",
          ];
          var ids = [];
          jQuery.each(items, function (index, workset) {
            lines.push("* " + workset.alias);
            ids.push(workset.id);
          });
          Utils.showConfirmDialog("Delete Worksets", "Delete", lines, function () {
            Utils.ajaxWithDialog(
              "Deleting Worksets",
              "POST",
              Urls.rest.worksets.bulkDelete,
              ids,
              Utils.page.pageReload
            );
          });
        },
      },
    ];
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Add",
        handler: function () {
          window.location = Urls.ui.worksets.create;
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.idHyperlinkColumn("ID", Urls.ui.worksets.edit, "id", Utils.array.getId, 0, true),
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.worksets.edit,
        Utils.array.getId,
        "alias",
        0,
        true
      ),
      {
        sTitle: "Items",
        mData: "itemCount",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Stage",
        mData: "stage",
        sDefaultContent: "",
      },
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Creator",
        mData: "creator",
        include: config.creator === "all",
        iSortPriority: 0,
      },
      {
        sTitle: "Modified",
        mData: "lastModified",
        include: true,
        iSortPriority: 1,
      },
    ];
  },
  searchTermSelector: function (searchTerms) {
    return [
      searchTerms["id"],
      searchTerms["stage"],
      searchTerms["entered"],
      searchTerms["changed"],
      searchTerms["creator"],
      searchTerms["changedby"],
    ];
  },
};
