ListTarget.qccontrol = {
  name: "QC Controls",
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          {
            name: "Remove",
            action: function (items) {
              QcType.removeControls(items.map(Utils.array.getAlias));
            },
          },
        ];
  },
  createStaticActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          {
            name: "Add",
            handler: function () {
              Utils.showDialog(
                "Add Control",
                "Add",
                [
                  {
                    label: "Alias",
                    property: "alias",
                    type: "text",
                    required: true,
                  },
                ],
                function (output) {
                  if (!RegExp(Utils.validation.sanitizeRegex).test(output.alias)) {
                    Utils.showOkDialog("Error", [
                      "Control alias cannot contain these characters: <>&",
                    ]);
                    return;
                  }
                  QcType.addControl(output.alias);
                }
              );
            },
          },
        ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
        include: true,
        iSortPriority: 1,
      },
    ];
  },
};
