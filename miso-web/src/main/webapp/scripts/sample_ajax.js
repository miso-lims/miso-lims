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
  };
})();
