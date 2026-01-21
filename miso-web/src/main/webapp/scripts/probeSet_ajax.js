var ProbeSet = (function () {
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
      var probes = ProbeSet.getProbes().filter(function (probe) {
        return !removeProbes.some(function (removal) {
          return removal.id === probe.id;
        });
      });
      ProbeSet.setProbes(probes);
    },

    showSaveProbeSetDialog: function (probes, onSuccess, onFail) {
      var fields = [
        {
          label: "Probe Set Name",
          type: "text",
          property: "name",
          required: true,
        },
      ];
      Utils.showDialog("Create Probe Set", "Save", fields, function (results) {
        var probeSet = {
          name: results.name,
          probes: probes,
        };
        Utils.ajaxWithDialog(
          "Saving Probe Set",
          "POST",
          Urls.rest.probeSets.create,
          probeSet,
          function () {
            Utils.showOkDialog("Create Probe Set", ["Probe set saved."], onSuccess);
          },
          onFail
        );
      });
    },
  };
})();
