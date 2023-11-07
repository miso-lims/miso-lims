var Requisition = (function () {
  var pausesListId = "listPauses";

  var form = null;

  return {
    setForm: function (formApi) {
      form = formApi;
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
