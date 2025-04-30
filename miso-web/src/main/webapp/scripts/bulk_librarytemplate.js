BulkTarget = window.BulkTarget || {};
BulkTarget.librarytemplate = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  var originalDataByRow = {};

  return {
    getSaveUrl: function () {
      return Urls.rest.libraryTemplates.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.libraryTemplates.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("libraries", "library-templates");
    },
    getBulkActions: function (config) {
      return [BulkUtils.actions.edit(Urls.ui.libraryTemplates.bulkEdit)];
    },
    prepareData: function (data, config) {
      data.forEach(function (x, index) {
        originalDataByRow[index] = {
          libraryTypeId: x.libraryTypeId,
          kitDescriptorId: x.kitDescriptorId,
          indexFamilyId: x.indexFamilyId,
        };
      });
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(255),
        {
          title: "Design",
          type: "dropdown",
          data: "designId",
          source: Constants.libraryDesigns,
          getItemLabel: getDesignLabel,
          getItemValue: Utils.array.getId,
          sortSource: true,
          onChange: function (rowIndex, newValue, api) {
            var design = Constants.libraryDesigns.find(function (x) {
              return getDesignLabel(x) === newValue;
            });

            var code = null;
            if (design) {
              // Disable and set code, selection, and strategy
              code = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(design.designCodeId),
                Constants.libraryDesignCodes
              );
              api.updateField(rowIndex, "designCodeId", {
                value: getDesignCodeLabel(code),
                disabled: true,
              });
              api.updateField(rowIndex, "selectionId", {
                value: design.selectionName,
                disabled: true,
              });
              api.updateField(rowIndex, "strategyId", {
                value: design.strategyName,
                disabled: true,
              });
            } else {
              api.updateField(rowIndex, "designCodeId", {
                disabled: false,
              });
              api.updateField(rowIndex, "selectionId", {
                disabled: false,
              });
              api.updateField(rowIndex, "strategyId", {
                disabled: false,
              });
            }
          },
          include: Constants.isDetailedSample,
        },
        {
          title: "Code",
          type: "dropdown",
          data: "designCodeId",
          source: Constants.libraryDesignCodes,
          getItemLabel: getDesignCodeLabel,
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
        },
        {
          title: "Platform",
          type: "dropdown",
          data: "platformType",
          source: function (data, api) {
            return Constants.platformTypes.filter(function (x) {
              return x.active || data.platformType === x.name;
            });
          },
          getItemLabel: Utils.array.get("key"),
          getItemValue: Utils.array.getName,
          sortSource: true,
          onChange: function (rowIndex, newValue, api) {
            // Filter type, kit, and index kit options
            var platformType = Constants.platformTypes.find(function (x) {
              return x.key === newValue;
            });
            if (!platformType) {
              return;
            }
            api.updateField(rowIndex, "libraryTypeId", {
              source: Constants.libraryTypes.filter(function (x) {
                return (
                  x.platform === platformType.name &&
                  (!x.archived || originalDataByRow[rowIndex].libraryTypeId === x.id)
                );
              }),
            });
            api.updateField(rowIndex, "kitDescriptorId", {
              source: Constants.kitDescriptors.filter(function (x) {
                return (
                  x.platformType === platformType.key &&
                  x.kitType === "Library" &&
                  (!x.archived || originalDataByRow[rowIndex].kitDescriptorId === x.id)
                );
              }),
            });
            api.updateField(rowIndex, "indexFamilyId", {
              source: Constants.libraryIndexFamilies.filter(function (x) {
                return (
                  x.platformType === platformType.name &&
                  (!x.archived || originalDataByRow[rowIndex].indexFamilyId === x.id)
                );
              }),
            });
          },
        },
        {
          title: "Type",
          type: "dropdown",
          data: "libraryTypeId",
          getData: function (item, api) {
            if (item.libraryTypeId) {
              var libraryType = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(item.libraryTypeId),
                Constants.libraryTypes
              );
              return libraryType.description;
            } else {
              return null;
            }
          },
          source: [], // set by platformType onChange
          getItemLabel: Utils.array.get("description"),
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Selection",
          type: "dropdown",
          data: "selectionId",
          getData: function (item, api) {
            if (item.selectionId) {
              var selection = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(item.selectionId),
                Constants.librarySelections
              );
              return selection.name;
            } else {
              return null;
            }
          },
          source: Constants.librarySelections,
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Strategy",
          type: "dropdown",
          data: "strategyId",
          getData: function (item, api) {
            if (item.strategyId) {
              var strategy = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(item.strategyId),
                Constants.libraryStrategies
              );
              return strategy.name;
            } else {
              return null;
            }
          },
          source: Constants.libraryStrategies,
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Kit",
          type: "dropdown",
          data: "kitDescriptorId",
          getData: function (item, api) {
            if (item.kitDescriptorId) {
              var kit = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(item.kitDescriptorId),
                Constants.kitDescriptors
              );
              return kit.name;
            } else {
              return null;
            }
          },
          source: [], // set by platformType onChange
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Index Kit",
          type: "dropdown",
          data: "indexFamilyId",
          getData: function (item, api) {
            if (item.indexFamilyId) {
              var family = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(item.indexFamilyId),
                Constants.libraryIndexFamilies
              );
              return family.name;
            } else {
              return null;
            }
          },
          source: [], // set by platformType onChange
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Default Volume",
          type: "decimal",
          data: "defaultVolume",
          precision: 16,
          scale: 10,
          onChange: function (rowIndex, newValue, api) {
            api.updateField(rowIndex, "volumeUnits", {
              required: !!newValue,
            });
          },
        },
        {
          title: "Vol. Units",
          type: "dropdown",
          data: "volumeUnits",
          source: Constants.volumeUnits,
          getItemLabel: function (item) {
            return Utils.decodeHtmlString(item.units);
          },
          getItemValue: Utils.array.getName,
          initializeOnEdit: true,
        },
      ];
    },
  };

  function getDesignLabel(design) {
    return design.name + " - " + design.sampleClassAlias;
  }

  function getDesignCodeLabel(code) {
    return code.code + " (" + code.description + ")";
  }
})(jQuery);
