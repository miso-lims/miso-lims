if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.qctype = (function ($) {
  /*
   * Expected config {
   *   isAdmin: boolean
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "qc-types");
    },
    getSaveUrl: function (qcType) {
      return qcType.id ? Urls.rest.qcTypes.update(qcType.id) : Urls.rest.qcTypes.create;
    },
    getSaveMethod: function (qcType) {
      return qcType.id ? "PUT" : "POST";
    },
    getEditUrl: function (qcType) {
      return Urls.ui.qcTypes.edit(qcType.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "QC Type Information",
          fields: config.isAdmin
            ? getAdminFields(config, object)
            : getReadOnlyFields(config, object),
        },
      ];
    },
    confirmSave: function (object) {
      object.controls = QcType.getControls();
      object.kitDescriptors = QcType.getKits();
    },
  };

  function getReadOnlyFields(config, object) {
    $("#save").remove();
    return [
      {
        title: "QC Type ID",
        data: "id",
        type: "read-only",
      },
      {
        title: "Name",
        data: "name",
        type: "read-only",
      },
      {
        title: "Description",
        data: "description",
        type: "read-only",
      },
      {
        title: "Target",
        data: "qcTarget",
        type: "read-only",
      },
      {
        title: "Units",
        data: "units",
        type: "read-only",
      },
      {
        title: "Results Format",
        data: "resultsFormat",
        omit: true,
        type: "read-only",
        getDisplayValue: function (qcType) {
          return qcType.precisionAfterDecimal === -1 ? "Yes/No" : "Numbers";
        },
      },
      {
        title: "Precision After Decimal",
        data: "precisionAfterDecimal",
        type: "read-only",
        include: object.precisionAfterDecimal !== -1,
      },
      {
        title: "Corresponding Field",
        data: "correspondingField",
        type: "read-only",
      },
      {
        title: "Auto Update Field",
        data: "autoUpdateField",
        type: "checkbox",
        disabled: true,
      },
      {
        title: "Instrument Model",
        data: "instrumentModelId",
        type: "read-only",
        getDisplayValue: function (qcType) {
          if (qcType.instrumentModelId) {
            return Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(qcType.instrumentModelId),
              Constants.instrumentModels
            ).alias;
          } else {
            return "n/a";
          }
        },
      },
      {
        title: "Archived",
        data: "archived",
        type: "checkbox",
        disabled: true,
      },
    ];
  }

  function getAdminFields(config, object) {
    return [
      {
        title: "QC Type ID",
        data: "id",
        type: "read-only",
        getDisplayValue: function (qcType) {
          return qcType.id || "Unsaved";
        },
      },
      {
        title: "Name",
        data: "name",
        type: "text",
        required: true,
        maxLength: 255,
      },
      {
        title: "Description",
        data: "description",
        type: "text",
        maxLength: 255,
      },
      {
        title: "Target",
        data: "qcTarget",
        type: "dropdown",
        required: true,
        source: Constants.qcTargets,
        getItemLabel: function (item) {
          return item.qcTarget;
        },
        getItemValue: function (item) {
          return item.qcTarget;
        },
        onChange: function (newValue, form) {
          form.updateField("correspondingField", {
            source: getCorrespondingFields(newValue),
          });
        },
      },
      {
        title: "Units",
        data: "units",
        type: "text",
        maxLength: 20,
      },
      {
        title: "Results Format",
        data: "resultsFormat",
        omit: true,
        type: "dropdown",
        required: true,
        source: [
          {
            label: "Numbers",
            value: 0,
          },
          {
            label: "Yes/No",
            value: -1,
          },
        ],
        getItemLabel: function (item) {
          return item.label;
        },
        getItemValue: function (item) {
          return item.value;
        },
        onChange: function (newValue, form) {
          var options = {
            disabled: newValue === "-1",
          };
          if (newValue === "-1") {
            options.value = -1;
          } else if (form.get("precisionAfterDecimal") === -1) {
            options.value = "";
          }
          form.updateField("precisionAfterDecimal", options);
        },
        initial: object.precisionAfterDecimal === -1 ? -1 : 0,
      },
      {
        title: "Precision After Decimal",
        data: "precisionAfterDecimal",
        type: "int",
        required: true,
        min: -1,
        max: 10,
      },
      {
        title: "Corresponding Field",
        data: "correspondingField",
        type: "dropdown",
        required: true,
        source: object.qcTarget ? getCorrespondingFields(object.qcTarget) : [],
      },
      {
        title: "Auto Update Field",
        data: "autoUpdateField",
        type: "checkbox",
      },
      {
        title: "Instrument Model",
        data: "instrumentModelId",
        type: "dropdown",
        source: Constants.instrumentModels.filter(function (model) {
          return model.active || model.id === object.instrumentModelId;
        }),
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId,
        sortSource: Utils.sorting.standardSort("alias"),
      },
      {
        title: "Archived",
        data: "archived",
        type: "checkbox",
      },
    ];
  }

  function getCorrespondingFields(targetName) {
    var target = Utils.array.findUniqueOrThrow(function (item) {
      return item.qcTarget === targetName;
    }, Constants.qcTargets);
    return target.correspondingFields;
  }
})(jQuery);
