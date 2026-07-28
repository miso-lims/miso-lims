ListTarget.sopfield = (function ($) {
  "use strict";

  function addFieldDialog() {
    Utils.showDialog(
      "Add SOP Field",
      "Add",
      [
        {
          label: "Name",
          type: "text",
          property: "name",
          maxlength: 255,
          required: true,
        },
        {
          label: "Type",
          type: "select",
          property: "fieldType",
          values: ["TEXT", "NUMBER", "INSTRUMENT", "WORKSTATION"],
          required: true,
        },
        {
          label: "Instrument Model",
          type: "select",
          property: "instrumentModel",
          values: Constants.instrumentModels,
          getLabel: Utils.array.getAlias,
          nullLabel: "N/A",
          showIf: function (output) {
            return output.fieldType === "INSTRUMENT";
          },
        },
        {
          label: "Units",
          type: "text",
          property: "units",
          maxlength: 50,
        },
      ],
      function (result) {
        Sop.addField({
          id: null,
          name: result.name,
          fieldType: result.fieldType,
          units: result.units || null,
          instrumentModelId: result.instrumentModel ? result.instrumentModel.id : null,
        });
      }
    );
  }

  return {
    name: "SOP Fields",

    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "standard-operating-procedures");
    },

    createUrl: function () {
      throw new Error("Must be provided statically");
    },

    createBulkActions: function (config) {
      if (!config.isAdmin) return [];
      return [
        {
          name: "Remove",
          action: Sop.removeFields,
        },
      ];
    },

    createStaticActions: function (config) {
      if (!config.isAdmin) return [];
      return [
        {
          name: "Add",
          handler: addFieldDialog,
        },
      ];
    },

    createColumns: function () {
      return [
        {
          sTitle: "Name",
          mData: "name",
        },
        {
          sTitle: "Type",
          mData: "fieldType",
          mRender: function (data, type, full) {
            return data + (full.instrumentModelAlias ? " (" + full.instrumentModelAlias + ")" : "");
          },
        },
        {
          sTitle: "Units",
          mData: "units",
        },
      ];
    },
  };
})(jQuery);
