var Sop = (function () {
  "use strict";

  var fieldsListId = "listSopFields";

  var form = null;
  var listConfig = {};

  function getFields() {
    return FormUtils.getTableData(fieldsListId) || [];
  }

  function setFields(fields) {
    FormUtils.setTableData(ListTarget.sopfield, listConfig, fieldsListId, fields || [], form);
  }

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setListConfig: function (config) {
      listConfig = config || {};
    },

    setFields: setFields,

    getFields: getFields,

    addField: function (newField) {
      var fields = getFields();
      fields.push(newField);
      setFields(fields);
    },

    removeFields: function (toRemove) {
      if (!toRemove || !toRemove.length) return;

      var fields = getFields().filter(function (field) {
        return toRemove.indexOf(field) === -1;
      });

      setFields(fields);
    },
  };
})();
