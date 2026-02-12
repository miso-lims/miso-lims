var Sample = (function () {
  var probesListId = "listProbes";

  var form = null;
  var listConfig = {};

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setListConfig: function (config) {
      listConfig = config;
    },

    setProbes: function (probes) {
      FormUtils.setTableData(ListTarget.probe, listConfig, probesListId, probes, form);
    },

    getProbes: function () {
      return FormUtils.getTableData(probesListId);
    },

    removeProbes: function (removeProbes) {
      var probes = Sample.getProbes().filter(function (probe) {
        return !removeProbes.some(function (removal) {
          return removal.id === probe.id;
        });
      });
      Sample.setProbes(probes);
    },

    showAddProbeSetDialog: function (samples, applyProbes) {
      if (
        samples.some(function (sample) {
          return sample.probes && sample.probes.length;
        })
      ) {
        Utils.showConfirmDialog(
          "Select Probe Set - Warning",
          "Yes",
          [
            "The probe set will replace all probes already associated with the " +
              (samples.length > 1 ? "samples" : "sample") +
              ". Are you sure you wish to proceed?",
          ],
          function () {
            showAddProbeSetDialogConfirmed(applyProbes);
          }
        );
      } else {
        showAddProbeSetDialogConfirmed(applyProbes);
      }
    },
  };

  function showAddProbeSetDialogConfirmed(applyProbes) {
    var fields = [
      {
        label: "Name",
        property: "name",
        type: "text",
        required: true,
      },
    ];
    Utils.showDialog("Find Probe Set", "Search", fields, function (results) {
      var url = Urls.rest.probeSets.query + "?" + Utils.page.param({ q: results.name });
      Utils.ajaxWithDialog("Searching...", "GET", url, null, function (probeSets) {
        if (!probeSets || !probeSets.length) {
          Utils.showOkDialog("Apply Probe Set", [
            "No probe sets found matching '" + results.name + "'",
          ]);
          return;
        } else if (probeSets.length === 1) {
          applyProbes(getSampleProbes(probeSets[0]));
        } else {
          Utils.showWizardDialog(
            "Select Probe Set",
            probeSets.map(function (probeSet) {
              return {
                name: probeSet.name,
                handler: function () {
                  applyProbes(getSampleProbes(probeSet));
                },
              };
            })
          );
        }
      });
    });
  }

  function getSampleProbes(probeSet) {
    // Clear IDs - these are changing from ProbeSetProbes to SampleProbes
    // Note: this mutates the probe set probes directly, but that shouldn't matter
    probeSet.probes.forEach(function (probe) {
      probe.id = null;
    });
    return probeSet.probes;
  }
})();
