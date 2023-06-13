if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
var Project = (function () {
  var assaysListId = "assays_section";

  var form = null;
  var listConfig = {};

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setListConfig: function (config) {
      listConfig = config;
    },

    setAssays: function (assays) {
      FormUtils.setTableData(ListTarget.assay, listConfig, assaysListId, assays, form);
    },

    getAssays: function () {
      return FormUtils.getTableData(assaysListId);
    },

    addAssay: function (addAssay) {
      var assays = Project.getAssays();
      assays.push(addAssay);
      Project.setAssays(assays);
    },

    removeAssays: function (removeAssays) {
      var assays = Project.getAssays().filter(function (assay) {
        return !removeAssays.some(function (removal) {
          return removal.id === assay.id;
        });
      });
      Project.setAssays(assays);
    },
  };
})();
