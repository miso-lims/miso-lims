ListTarget.instrumentposition = {
  name: "Instrument Positions",
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
              InstrumentModel.removeInstrumentPositions(items.map(Utils.array.getAlias));
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
                "Add Instrument Position",
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
                  if (!/^[-_\w]{1,10}$/g.test(output.alias)) {
                    Utils.showOkDialog("Error", [
                      "Position alias must consist of 1-10 letters, numbers, and symbols [ _- ]",
                    ]);
                    return;
                  }
                  InstrumentModel.addInstrumentPosition(output.alias);
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
