ListTarget.probeset = (function ($) {
  var TYPE_LABEL = "Probe Sets";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("samples", "sample-probes");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    getQueryUrl: null,
    createBulkActions: function (config, projectId) {
      return [ListUtils.createBulkDeleteAction("Probe Sets", "probesets", Utils.array.getName)];
    },
    createStaticActions: function (config, projectId) {
      return [
        {
          name: "Create",
          handler: function () {
            window.location = Urls.ui.probeSets.create;
          },
        },
      ];
    },
    createColumns: function (config, projectId) {
      return [
        ListUtils.labelHyperlinkColumn(
          "Name",
          Urls.ui.probeSets.edit,
          Utils.array.getId,
          "name",
          1,
          true
        ),
        {
          sTitle: "Feature Type",
          mData: "featureTypeLabel",
        },
        {
          sTitle: "Probes",
          mData: "probes",
          mRender: function (data, type, full) {
            if (type !== "display") {
              return data;
            }
            return data ? data.length : 0;
          },
        },
      ];
    },
  };
})(jQuery);
