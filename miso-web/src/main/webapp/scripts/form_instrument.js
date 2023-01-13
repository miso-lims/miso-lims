if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.instrument = (function ($) {
  /*
   * Expected config {
   *   isAdmin: boolean,
   *   instrumentTypes: array,
   *   instruments: array,
   *   workstations: array
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("instruments");
    },
    getSaveUrl: function (instrument) {
      return instrument.id
        ? Urls.rest.instruments.update(instrument.id)
        : Urls.rest.instruments.create;
    },
    getSaveMethod: function (instrument) {
      return instrument.id ? "PUT" : "POST";
    },
    getEditUrl: function (instrument) {
      return Urls.ui.instruments.edit(instrument.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Instrument Information",
          fields: config.isAdmin
            ? getAdminFields(config, object)
            : getReadOnlyFields(config, object),
        },
      ];
    },
  };

  function getReadOnlyFields(config, object) {
    return [
      {
        title: "Instrument ID",
        data: "id",
        type: "read-only",
      },
      {
        title: "Instrument Type",
        data: "instrumentType",
        type: "read-only",
        getDisplayValue: function (instrument) {
          return Utils.array.findUniqueOrThrow(function (type) {
            return type.value === instrument.instrumentType;
          }, config.instrumentTypes).label;
        },
      },
      {
        title: "Instrument Model",
        data: "instrumentModelAlias",
        type: "read-only",
        getDisplayValue: function (instrument) {
          return instrument.platformType + " - " + instrument.instrumentModelAlias;
        },
      },
      {
        title: "Name",
        data: "name",
        type: "read-only",
      },
      {
        title: "Serial Number",
        data: "serialNumber",
        type: "read-only",
      },
      {
        title: "Barcode",
        data: "identificationBarcode",
        type: "read-only",
      },
      {
        title: "Workstation",
        data: "workstationId",
        type: "read-only",
        getDisplayValue: function (instrument) {
          return instrument.workstationAlias || "Not specified";
        },
      },
      {
        title: "Upgraded From",
        data: "preUpgradeInstrumentId",
        type: "read-only",
        getDisplayValue: function (instrument) {
          return instrument.preUpgradeInstrumentAlias;
        },
        getLink: function (instrument) {
          return Urls.ui.instruments.edit(instrument.preUpgradeInstrumentId);
        },
        include: !!object.preUpgradeInstrumentId,
      },
      {
        title: "Commissioned",
        data: "dateCommissioned",
        type: "read-only",
      },
      {
        title: "Status",
        data: "status",
        type: "read-only",
      },
      {
        title: "Decommissioned",
        data: "dateDecommissioned",
        type: "read-only",
        include: !!object.dateDecommissioned,
      },
      {
        title: "Upgraded To",
        data: "upgradedInstrumentId",
        type: "read-only",
        getDisplayValue: function (instrument) {
          return Utils.array.findUniqueOrThrow(
            Utils.array.idPredicate(instrument.upgradedInstrumentId),
            config.instruments
          );
        },
        include: !!object.upgradedInstrumentId,
      },
    ];
  }

  function getAdminFields(config, object) {
    return [
      {
        title: "Instrument ID",
        data: "id",
        type: "read-only",
        getDisplayValue: function (instrument) {
          return instrument.id || "Unsaved";
        },
      },
      {
        title: "Instrument Type",
        data: "instrumentType",
        type: "read-only",
        include: !!object.id,
        getDisplayValue: function (instrument) {
          return Utils.array.findUniqueOrThrow(function (type) {
            return type.value === instrument.instrumentType;
          }, config.instrumentTypes).label;
        },
      },
      {
        title: "Instrument Type",
        data: "instrumentType",
        type: "dropdown",
        include: !object.id,
        required: true,
        source: config.instrumentTypes,
        getItemLabel: function (item) {
          return item.label;
        },
        getItemValue: function (item) {
          return item.value;
        },
        initial: "SEQUENCER",
        onChange: function (newValue, form) {
          form.updateField("instrumentModelId", {
            source: Constants.instrumentModels.filter(function (model) {
              return model.instrumentType === newValue;
            }),
          });
          var purposeOptions = {
            required: newValue === "SEQUENCER",
            disabled: newValue !== "SEQUENCER",
          };
          if (newValue !== "SEQUENCER") {
            purposeOptions.value = null;
          }
          form.updateField("defaultRunPurposeId", purposeOptions);
        },
      },
      {
        title: "Instrument Model",
        data: "instrumentModelId",
        type: "dropdown",
        required: true,
        source: Constants.instrumentModels.filter(function (model) {
          return model.instrumentType === object.instrumentType;
        }),
        getItemLabel: function (item) {
          return (
            Utils.array.findUniqueOrThrow(function (type) {
              return type.name === item.platformType;
            }, Constants.platformTypes).key +
            " - " +
            item.alias
          );
        },
        getItemValue: Utils.array.getId,
        sortSource: Utils.sorting.standardSortByCallback(function (item) {
          return item.platformType + item.alias;
        }),
      },
      {
        title: "Name",
        data: "name",
        type: "text",
        required: true,
        maxLength: 30,
      },
      {
        title: "Serial Number",
        data: "serialNumber",
        type: "text",
        maxLength: 30,
      },
      {
        title: "Barcode",
        data: "identificationBarcode",
        type: "text",
        maxLength: 255,
      },
      {
        title: "Workstation",
        data: "workstationId",
        type: "dropdown",
        source: config.workstations,
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId,
        sortSource: Utils.sorting.standardSort("alias"),
      },
      {
        title: "Upgraded From",
        data: "preUpgradeInstrumentId",
        type: "read-only",
        getDisplayValue: function (instrument) {
          return instrument.preUpgradeInstrumentAlias;
        },
        getLink: function (instrument) {
          return Urls.ui.instruments.edit(instrument.preUpgradeInstrumentId);
        },
        include: !!object.preUpgradeInstrumentId,
      },
      {
        title: "Commissioned",
        data: "dateCommissioned",
        type: "date",
      },
      {
        title: "Status",
        data: "status",
        type: "dropdown",
        required: true,
        source: ["Production", "Retired", "Upgraded"],
        onChange: function (newValue, form) {
          var decommissioned = {
            disabled: true,
            required: false,
          };
          var upgrade = {
            disabled: true,
            required: false,
          };
          switch (newValue) {
            case "Production":
              decommissioned.value = null;
              upgrade.value = null;
              break;
            case "Retired":
              decommissioned.disabled = false;
              decommissioned.required = true;
              upgrade.value = null;
              break;
            case "Upgraded":
              decommissioned.disabled = false;
              decommissioned.required = true;
              upgrade.disabled = false;
              upgrade.required = true;
              break;
            default:
              throw new Error("Unexpected status value: " + newValue);
          }
          form.updateField("dateDecommissioned", decommissioned);
          form.updateField("upgradedInstrumentId", upgrade);
        },
      },
      {
        title: "Decommissioned",
        data: "dateDecommissioned",
        type: "date",
        disabled: true,
      },
      {
        title: "Upgraded To",
        data: "upgradedInstrumentId",
        type: "dropdown",
        nullLabel: "N/A",
        source: config.instruments,
        getItemLabel: Utils.array.getName,
        getItemValue: Utils.array.getId,
        sortSource: Utils.sorting.standardSort("name"),
        disabled: true,
      },
      {
        title: "Default Run Purpose",
        data: "defaultRunPurposeId",
        type: "dropdown",
        source: Constants.runPurposes,
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId,
        nullLabel: "N/A",
        sortSource: Utils.sorting.standardSort("alias"),
        required: isSequencer(object.instrumentModelId),
        disabled: !isSequencer(object.instrumentModelId),
      },
    ];
  }

  function isSequencer(instrumentModelId) {
    return (
      instrumentModelId &&
      Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(instrumentModelId),
        Constants.instrumentModels
      ).instrumentType === "SEQUENCER"
    );
  }
})(jQuery);
