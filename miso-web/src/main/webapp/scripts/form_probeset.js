if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.probeset = (function () {
  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("samples", "sample-probes");
    },
    getSaveUrl: function (probeSet) {
      return probeSet.id ? Urls.rest.probeSets.update(probeSet.id) : Urls.rest.probeSets.create;
    },
    getSaveMethod: function (probeSet) {
      return probeSet.id ? "PUT" : "POST";
    },
    getEditUrl: function (probeSet) {
      return Urls.ui.probeSets.edit(probeSet.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Probe Set Information",
          fields: [
            FormUtils.makeIdField("Probe Set"),
            {
              title: "Name",
              type: "text",
              data: "name",
              required: true,
              maxLength: 255,
            },
          ],
        },
      ];
    },
    confirmSave: function (object, isDialog, form) {
      object.probes = ProbeSet.getProbes();
    },
  };
})();
