var Requisition = (function () {
  var assaysListId = "listAssays";
  var pausesListId = "listPauses";

  var form = null;
  var assaysListConfig = {};
  /*
   * Expected config: {
   *   numberOfRequisitionedItems: int; edit mode only,
   *   potentialAssayIds: [int]; edit mode only
   * }
   */

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setAssaysListConfig: function (config) {
      assaysListConfig = config;
    },

    setAssays: function (assays) {
      FormUtils.setTableData(ListTarget.assay, assaysListConfig, assaysListId, assays, form);
    },

    getAssays: function () {
      return FormUtils.getTableData(assaysListId);
    },

    addAssay: function (assay) {
      var assays = Requisition.getAssays();
      if (assays.some(Utils.array.idPredicate(assay.id))) {
        Utils.showOkDialog("Error", ["That assay is already included"]);
        return;
      }
      assays.push(assay);
      Requisition.setAssays(assays);
    },

    removeAssays: function (removeAssays) {
      var assays = Requisition.getAssays().filter(function (assay) {
        return !removeAssays.some(function (removeAssay) {
          return removeAssay.id == assay.id;
        });
      });
      Requisition.setAssays(assays);
    },

    setPauses: function (pauses) {
      FormUtils.setTableData(ListTarget.requisitionpause, {}, pausesListId, pauses, form);
    },

    getPauses: function () {
      return FormUtils.getTableData(pausesListId);
    },

    addPause: function (pause) {
      var pauses = Requisition.getPauses();
      if (
        pauses.some(function (existing) {
          return existing.startDate == pause.startDate;
        })
      ) {
        Utils.showOkDialog("Error", ["Cannot have multiple pauses with the same start date"]);
        return;
      }
      pauses.push(pause);
      Requisition.setPauses(pauses);
    },

    removePauses: function (removePauses) {
      var pauses = Requisition.getPauses().filter(function (pause) {
        return !removePauses.some(function (removePause) {
          return removePause.startDate == pause.startDate;
        });
      });
      Requisition.setPauses(pauses);
    },

    updatePause: function (updatedPause) {
      // note: this assumes start date cannot be updated
      var pauses = Requisition.getPauses().filter(function (pause) {
        return pause.startDate != updatedPause.startDate;
      });
      pauses.push(updatedPause);
      Requisition.setPauses(pauses);
    },
  };
})();
