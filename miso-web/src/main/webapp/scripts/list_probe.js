ListTarget.probe = {
  name: "Probes",
  createUrl: function (config, projectId) {
    throw new Error("Static data only");
  },
  createBulkActions: function (config, projectId) {
    return [
      {
        name: "Remove",
        action: Sample.removeProbes,
      },
    ];
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Edit All",
        handler: function () {
          Utils.showDialog(
            "Edit Probes",
            "OK",
            [
              {
                label: "Additional Probes",
                property: "addProbes",
                type: "int",
              },
            ],
            function (results) {
              if (results.addProbes && results.addProbes < 0) {
                Utils.showOkDialog("Error", ["Can't add a negative number of probes."]);
                return;
              }
              Utils.page.pageRedirect(
                Urls.ui.samples.bulkEditProbes(config.sampleId) +
                  "?" +
                  Utils.page.param({
                    addProbes: results.addProbes,
                  })
              );
            }
          );
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Identifier",
        mData: "identifier",
      },
      {
        sTitle: "Name",
        mData: "name",
      },
      {
        sTitle: "Sequence",
        mData: "sequence",
      },
    ];
  },
};
