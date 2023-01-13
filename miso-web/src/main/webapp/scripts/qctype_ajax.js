var QcType = (function ($) {
  var controlsListId = "listControls";
  var kitsListId = "listKits";

  var form = null;
  var isAdmin = false;

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setAdmin: function (admin) {
      isAdmin = admin;
    },

    setControls: function (controls) {
      FormUtils.setTableData(
        ListTarget.qccontrol,
        {
          isAdmin: isAdmin,
        },
        controlsListId,
        controls,
        form
      );
    },

    getControls: function () {
      return FormUtils.getTableData(controlsListId);
    },

    addControl: function (alias) {
      var controls = QcType.getControls();
      if (
        controls.some(function (control) {
          return control.alias === alias;
        })
      ) {
        Utils.showOkDialog("Error", ["There is already a control with this alias"]);
        return;
      }
      controls.push({
        alias: alias,
      });
      QcType.setControls(controls);
    },

    removeControls: function (aliases) {
      var controls = QcType.getControls().filter(function (control) {
        return aliases.indexOf(control.alias) === -1;
      });
      QcType.setControls(controls);
    },

    setKits: function (kits) {
      FormUtils.setTableData(
        ListTarget.kit,
        {
          isUserAdmin: isAdmin,
          isQcTypePage: true,
        },
        kitsListId,
        kits,
        form
      );
    },

    getKits: function () {
      return FormUtils.getTableData(kitsListId);
    },

    addKit: function (kit) {
      var kits = QcType.getKits();
      if (
        kits.some(function (existingKit) {
          return existingKit.id == kit.id;
        })
      ) {
        Utils.showOkDialog("Error", ["This kit has already been added"]);
        return;
      }
      kits.push(kit);
      QcType.setKits(kits);
    },

    removeKits: function (kits) {
      var idsToRemove = kits.map(Utils.array.getId);
      var keepers = QcType.getKits().filter(function (kit) {
        return idsToRemove.indexOf(kit.id) === -1;
      });
      QcType.setKits(keepers);
    },
  };
})(jQuery);
