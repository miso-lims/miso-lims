ListTarget.kit_consumable = {
  name: "Kits",
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return [];
  },
  createStaticActions: function (config, projectId) {
    return config.allowedDescriptors
      ? [
          {
            name: "Add",
            handler: function () {
              Utils.showDialog(
                "Add Kit",
                "Add",
                [
                  {
                    property: "descriptor",
                    type: "select",
                    label: "Kit Type",
                    values: config.allowedDescriptors,
                    getLabel: Utils.array.getName,
                  },
                  {
                    property: "lotNumber",
                    type: "text",
                    label: "Lot Number",
                    required: true,
                  },
                  {
                    property: "date",
                    type: "date",
                    label: "Date",
                    required: true,
                  },
                ],
                function (result) {
                  Utils.ajaxWithDialog(
                    "Adding Kit",
                    "POST",
                    Urls.rest.experiments.addKit(config.experimentId),
                    result,
                    Utils.page.pageReload
                  );
                }
              );
            },
          },
        ]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Name",
        mData: "descriptor.name",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Part Number",
        mData: "descriptor.partNumber",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Lot Number",
        mData: "lotNumber",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Date",
        mData: "date",
        include: true,
        iSortPriority: 1,
      },
    ];
  },
};
