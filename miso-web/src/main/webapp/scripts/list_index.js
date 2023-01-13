ListTarget.index = {
  name: "Indices",
  createUrl: function (config, projectId) {
    return config.platformType
      ? Urls.rest.indices.platformDatatable(config.platformType)
      : Urls.rest.indices.datatable;
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    var actions = [];
    if (config.indexFamilyId && config.isAdmin) {
      actions.push(
        BulkUtils.actions.edit(Urls.ui.indices.bulkEdit),
        ListUtils.createBulkDeleteAction("Indices", "indices", function (index) {
          return index.name + " (" + index.sequence + ")";
        })
      );
    }
    if (config.additionalBulkActions) {
      actions = actions.concat(config.additionalBulkActions);
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return !config.indexFamilyId || !config.isAdmin
      ? []
      : [
          {
            name: "Add",
            handler: function () {
              Utils.showDialog(
                "Create Indices",
                "Create",
                [
                  {
                    property: "quantity",
                    type: "int",
                    label: "Quantity",
                    required: true,
                    value: 1,
                  },
                ],
                function (result) {
                  if (result.quantity < 1) {
                    Utils.showOkDialog("Create Indices", ["Quantity must be 1 or more."]);
                    return;
                  }
                  window.location =
                    Urls.ui.indices.bulkCreate +
                    "?" +
                    Utils.page.param({
                      indexFamilyId: config.indexFamilyId,
                      quantity: result.quantity,
                    });
                }
              );
            },
          },
        ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Platform",
        include: !config.platformType && !config.indexFamilyId,
        iSortPriority: 3,
        mData: "family.platformType",
        mRender: ListUtils.render.platformType,
      },
      {
        sTitle: "Family",
        include: !config.indexFamilyId,
        iSortPriority: 2,
        mData: "family.name",
      },
      {
        sTitle: "Index Name",
        include: true,
        iSortPriority: 1,
        mData: "name",
      },
      {
        sTitle: "Sequence(s)",
        include: true,
        iSortPriority: 0,
        bSortable: false,
        mData: "sequence",
        mRender: function (data, type, full) {
          if (type === "display" && full.family.fakeSequence) {
            return full.realSequences.join(", ");
          } else {
            return data;
          }
        },
      },
      {
        sTitle: "Position",
        include: true,
        iSortPriority: 1,
        mData: "position",
      },
    ];
  },
};
