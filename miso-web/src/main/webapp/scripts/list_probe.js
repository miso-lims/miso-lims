ListTarget.probe = {
  name: "Probes",
  createUrl: function (config, projectId) {
    throw new Error("Static data only");
  },
  createBulkActions: function (config, projectId) {
    return [
      {
        name: "Remove",
        action: config.sample ? Sample.removeProbes : ProbeSet.removeProbes,
      },
    ];
  },
  createStaticActions: function (config, projectId) {
    var actions = [
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
              var params =
                "?" +
                Utils.page.param({
                  addProbes: results.addProbes,
                });
              if (config.sample) {
                Utils.page.pageRedirect(Urls.ui.samples.bulkEditProbes(config.sample.id) + params);
              } else {
                Utils.page.pageRedirect(
                  Urls.ui.probeSets.bulkEditProbes(config.probeSet.id) + params
                );
              }
            }
          );
        },
      },
    ];
    if (config.sample) {
      actions.push({
        name: "Apply Probe Set",
        handler: function () {
          Sample.showAddProbeSetDialog([config.sample], Sample.setProbes);
        },
      });
    }
    return actions;
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
