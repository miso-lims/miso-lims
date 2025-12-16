ListTarget.sop = (function ($) {
  "use strict";

  var TYPE_LABEL = "SOPs";

  function doCopy(items) {
    if (items.length > 1) {
      Utils.showOkDialog("Error", ["Select an individual SOP to copy"]);
      return;
    }
    Utils.page.pageRedirect(Urls.ui.sops.create + "?" + $.param({ baseId: items[0].id }));
  }

  function renderSopLink(data, type) {
    if (type === "display" && data) {
      var href = encodeURI(String(data));
      return '<a href="' + href + '" target="_blank" rel="noopener noreferrer">View SOP</a>';
    }
    return data || "";
  }

  return {
    name: TYPE_LABEL,

    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "standard-operating-procedures");
    },

    createUrl: function (config, projectId) {
      return Urls.rest.sops.categoryDatatable(config.category);
    },

    getQueryUrl: null,
    showNewOptionSop: false,

    createBulkActions: function (config, projectId) {
      if (!config.isAdmin) return [];

      return [
        {
          name: "Copy",
          action: doCopy,
        },
        ListUtils.createBulkDeleteAction(TYPE_LABEL, "sops", Utils.array.getAlias),
      ];
    },

    createStaticActions: function (config, projectId) {
      if (!config.isAdmin) return [];
      return [
        {
          name: "Add",
          handler: function () {
            Utils.page.pageRedirect(Urls.ui.sops.create);
          },
        },
      ];
    },

    createColumns: function (config, projectId) {
      return [
        ListUtils.labelHyperlinkColumn(
          "Alias",
          Urls.ui.sops.edit,
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
          sTitle: "SOP",
          mData: "url",
          mRender: renderSopLink,
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
