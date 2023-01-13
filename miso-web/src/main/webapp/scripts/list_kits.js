ListTarget.kit = {
  name: "Kits",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "kit-descriptors");
  },
  createUrl: function (config, projectId) {
    if (config.kitType) {
      return Urls.rest.kitDescriptors.typeDatatable(config.kitType);
    } else {
      return Urls.rest.kitDescriptors.datatable;
    }
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    if (config.isUserAdmin) {
      if (config.isQcTypePage) {
        return [
          {
            name: "Remove",
            action: function (items) {
              QcType.removeKits(items);
            },
          },
        ];
      } else {
        return [
          ListUtils.createBulkDeleteAction("Kits", "kitdescriptors", function (kit) {
            return kit.name + " (" + kit.kitType + ")";
          }),
        ];
      }
    } else {
      return [];
    }
  },
  createStaticActions: function (config, projectId) {
    var actions = [];
    if (config.isUserAdmin) {
      if (config.isQcTypePage) {
        actions.push(
          ListUtils.createStaticAddBySearchAction(
            "Kit",
            "Name or Part Number",
            Urls.rest.kitDescriptors.search,
            {
              kitType: "QC",
            },
            function (kit) {
              QcType.addKit(kit);
            }
          )
        );
      } else {
        actions.push({
          name: "Add",
          handler: function () {
            window.location = Urls.ui.kitDescriptors.create;
          },
        });
      }
    }
    return actions;
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.labelHyperlinkColumn(
        "Kit Name",
        Urls.ui.kitDescriptors.edit,
        Utils.array.getId,
        "name",
        1,
        true
      ),
      {
        sTitle: "Version",
        include: true,
        mData: function (data) {
          return data.hasOwnProperty("version") ? data.version : null;
        },
        bSortable: false,
      },
      {
        sTitle: "Manufacturer",
        include: true,
        iSortPriority: 0,
        mData: "manufacturer",
      },
      {
        sTitle: "Part Number",
        include: true,
        iSortPriority: 0,
        mData: "partNumber",
      },
      {
        sTitle: "Type",
        include: !config.kitType,
        iSortPriority: 0,
        mData: "kitType",
      },
      {
        sTitle: "Stock Level",
        include: true,
        iSortPriority: 0,
        mData: "stockLevel",
      },
      {
        sTitle: "Platform",
        include: !config.platformType,
        iSortPriority: 0,
        mData: "platformType",
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
