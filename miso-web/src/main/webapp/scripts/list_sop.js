ListTarget.sop = {
  name: "SOPs",

  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "standard-operating-procedures");
  },

  createUrl: function (config, projectId) {
    return Urls.rest.sops.categoryDatatable(config.category);
  },

  getQueryUrl: null,
  showNewOptionSop: false,


  createStaticActions: function (config, projectId) {
    if (!config.isAdmin) return [];

    var goCreate = function () {
      Utils.page.pageRedirect(Urls.ui.sops.create); // should be /sop/new
    };

    return [
      {
        name: "Add",
        action: goCreate,
        handler: goCreate,
      },
    ];
  },

  /**
   * Bulk actions (selection required) -> Copy, Delete
   * Provide BOTH action(items) and handler(items) for compatibility.
   */
  createBulkActions: function (config, projectId) {
    if (!config.isAdmin) return [];

    var doCopy = function (items) {
      // Some runners pass items; if not, fall back to selection helpers
      if (!items) {
        items = ListUtils.getSelectedItems ? ListUtils.getSelectedItems() : null;
        if (!items && typeof ListUtils.getSelected === "function") {
          items = ListUtils.getSelected();
        }
      }

      if (!items || !items.length) {
        Utils.showOkDialog("Copy SOP", ["Nothing is selected."]);
        return;
      }
      if (items.length !== 1) {
        Utils.showOkDialog("Copy SOP", ["Please select exactly one SOP to copy."]);
        return;
      }

      var id = items[0].id || items[0];
      Utils.page.pageRedirect(Urls.ui.sops.create + "?baseId=" + id);
    };

    var copyOne = {
      name: "Copy",
      action: doCopy,
      handler: doCopy,
    };

    var deleteAction = ListUtils.createBulkDeleteAction("SOPs", "sops", function (sop) {
      return sop.alias || "SOP " + (sop.id || "");
    });

    return [copyOne, deleteAction];
  },

  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "ID",
        mData: "id",
        bVisible: false,
        bSortable: false,
      },
      {
        sTitle: "Alias",
        mData: "alias",
        iSortPriority: 1,
        bSortDirection: true,
        mRender: function (data, type, full) {
          if (type === "display") {
            var label = data ? Utils.escapeHtml(data) : "SOP " + (full.id || "");
            return '<a href="' + Urls.ui.sops.edit(full.id) + '">' + label + "</a>";
          }
          return data || "";
        },
      },
      {
        sTitle: "Version",
        mData: "version",
        iSortPriority: 2,
        bSortDirection: true,
      },
      {
        sTitle: "Category",
        mData: "category",
        iSortPriority: 3,
        bSortDirection: true,
        mRender: function (data, type) {
          if (
            type === "display" &&
            data &&
            Constants.sopCategories &&
            Constants.sopCategories[data]
          ) {
            return Constants.sopCategories[data];
          }
          return data || "";
        },
      },
      {
        sTitle: "SOP",
        mData: "url",
        mRender: function (data, type) {
          if (type === "display" && data) {
            var href = Utils.escapeHtml(data);
            return '<a href="' + href + '" target="_blank" rel="noopener noreferrer">View SOP</a>';
          }
          return data || "";
        },
      },
      {
        sTitle: "Archived",
        mData: "archived",
        mRender: ListUtils.render.archived,
      },
    ];
  },

  searchTermSelector: function (searchTerms) {
    return [searchTerms["id"]];
  },
};
