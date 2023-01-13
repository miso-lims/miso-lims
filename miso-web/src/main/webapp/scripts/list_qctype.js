ListTarget.qctype = {
  name: "QC Types",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "qc-types");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    return config.isAdmin
      ? [
          ListUtils.createBulkDeleteAction("QC Types", "qctypes", function (qcType) {
            return qcType.name + " (" + qcType.qcTarget + " QC)";
          }),
        ]
      : [];
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          {
            name: "Add",
            handler: function () {
              window.location = Urls.ui.qcTypes.create;
            },
          },
        ]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.labelHyperlinkColumn(
        "Name",
        Urls.ui.qcTypes.edit,
        Utils.array.getId,
        "name",
        0,
        true
      ),
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
        bSortable: false,
      },
      {
        sTitle: "Target",
        mData: "qcTarget",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Units",
        mData: "units",
        include: true,
        iSortPriority: 0,
        bSortable: false,
      },
      {
        sTitle: "Corresponding Field",
        mData: "correspondingField",
        include: true,
        iSortPriority: 0,
        bSortable: true,
      },
      {
        sTitle: "Auto Update Field",
        mData: "autoUpdateField",
        include: true,
        iSortPriority: 0,
        bSortable: true,
      },
    ];
  },
};
