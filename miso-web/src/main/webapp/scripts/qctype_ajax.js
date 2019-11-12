var QcType = (function($) {

  var controlsListId = 'listControls';

  var form = null;
  var isAdmin = false;

  return {

    setForm: function(formApi) {
      form = formApi;
    },

    setAdmin: function(admin) {
      isAdmin = admin;
    },

    setControls: function(controls) {
      FormUtils.setTableData(ListTarget.qccontrol, {
        isAdmin: isAdmin
      }, controlsListId, controls, form)
    },

    getControls: function() {
      return FormUtils.getTableData(controlsListId);
    },

    addControl: function(alias) {
      var controls = QcType.getControls();
      if (controls.some(function(control) {
        return control.alias === alias;
      })) {
        Utils.showOkDialog('Error', ['There is already a control with this alias']);
        return;
      }
      controls.push({
        alias: alias
      });
      QcType.setControls(controls);
    },

    removeControls: function(aliases) {
      var controls = QcType.getControls().filter(function(control) {
        return aliases.indexOf(control.alias) === -1;
      });
      QcType.setControls(controls);
    }

  };

})(jQuery);