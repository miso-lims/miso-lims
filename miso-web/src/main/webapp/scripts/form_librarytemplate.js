FormTarget = FormTarget || {};
FormTarget.librarytemplate = (function ($) {
  var unspecified = "Unspecified";

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("libraries", "library-templates");
    },
    getSaveUrl: function (template) {
      return template.id
        ? Urls.rest.libraryTemplates.update(template.id)
        : Urls.rest.libraryTemplates.create;
    },
    getSaveMethod: function (template) {
      return template.id ? "PUT" : "POST";
    },
    getEditUrl: function (template) {
      return Urls.ui.libraryTemplates.edit(template.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Template Information",
          fields: [
            {
              title: "Template ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (template) {
                return template.id || "Unsaved";
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Design",
              data: "designId",
              type: "dropdown",
              include: Constants.isDetailedSample,
              source: Constants.libraryDesigns,
              getItemLabel: function (item) {
                return (
                  item.name +
                  " - " +
                  Utils.array.findUniqueOrThrow(
                    Utils.array.idPredicate(item.sampleClassId),
                    Constants.sampleClasses
                  ).alias
                );
              },
              getItemValue: Utils.array.getId,
              nullLabel: unspecified,
              onChange: function (newValue, form) {
                var design = newValue
                  ? Utils.array.findUniqueOrThrow(
                      Utils.array.idPredicate(newValue),
                      Constants.libraryDesigns
                    )
                  : null;
                updateFieldFromDesign("designCodeId", design, "designCodeId", form);
                updateFieldFromDesign("selectionId", design, "selectionId", form);
                updateFieldFromDesign("strategyId", design, "strategyId", form);
              },
            },
            {
              title: "Code",
              data: "designCodeId",
              type: "dropdown",
              include: Constants.isDetailedSample,
              source: Constants.libraryDesignCodes,
              getItemLabel: function (item) {
                return item.code;
              },
              getItemValue: Utils.array.getId,
              nullLabel: unspecified,
            },
            {
              title: "Platform",
              data: "platformType",
              type: "dropdown",
              source: Constants.platformTypes.filter(function (pt) {
                return pt.active || object.platformType === pt.name;
              }),
              getItemLabel: function (item) {
                return item.key;
              },
              getItemValue: Utils.array.getName,
              nullLabel: unspecified,
              onChange: function (newValue, form) {
                var platformType = getPlatformType(newValue);
                form.updateField("libraryTypeId", {
                  source: getLibraryTypeOptions(platformType, object),
                });
                form.updateField("kitDescriptorId", {
                  source: getKitDescriptorOptions(platformType, object.kitDescriptorId),
                });
                form.updateField("indexFamilyId", {
                  source: getIndexFamilyOptions(platformType, object),
                });
              },
            },
            {
              title: "Type",
              data: "libraryTypeId",
              type: "dropdown",
              source: getLibraryTypeOptions(getPlatformType(object.platformType), object),
              getItemLabel: function (item) {
                return item.description;
              },
              getItemValue: Utils.array.getId,
              nullLabel: unspecified,
            },
            {
              title: "Selection",
              data: "selectionId",
              type: "dropdown",
              source: Constants.librarySelections,
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              nullLabel: unspecified,
            },
            {
              title: "Strategy",
              data: "strategyId",
              type: "dropdown",
              source: Constants.libraryStrategies,
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              nullLabel: unspecified,
            },
            {
              title: "Kit",
              data: "kitDescriptorId",
              type: "dropdown",
              source: getKitDescriptorOptions(
                getPlatformType(object.platformType),
                object.kitDescriptorId
              ),
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              nullLabel: unspecified,
            },
            {
              title: "Index Kit",
              data: "indexFamilyId",
              type: "dropdown",
              source: getIndexFamilyOptions(getPlatformType(object.platformType), object),
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              nullLabel: unspecified,
            },
            {
              title: "Default Volume",
              data: "defaultVolume",
              type: "decimal",
              precision: 14,
              scale: 10,
              min: 0,
            },
            {
              title: "Volume Units",
              data: "volumeUnits",
              type: "dropdown",
              source: Constants.volumeUnits,
              getItemLabel: function (item) {
                return Utils.decodeHtmlString(item.units);
              },
              getItemValue: Utils.array.getName,
              nullLabel: unspecified,
            },
          ],
        },
      ];
    },
    confirmSave: function (object) {
      LibraryTemplate.applyIndices(object);
      LibraryTemplate.applyProjects(object);
    },
  };

  function updateFieldFromDesign(dataProperty, design, designProperty, form) {
    if (design) {
      form.updateField(dataProperty, {
        value: design[designProperty],
        disabled: true,
      });
    } else {
      form.updateField(dataProperty, {
        disabled: false,
      });
    }
  }

  function getPlatformType(platformTypeName) {
    return !platformTypeName
      ? null
      : Utils.array.findUniqueOrThrow(
          Utils.array.namePredicate(platformTypeName),
          Constants.platformTypes
        );
  }

  function getLibraryTypeOptions(platformType, originalObject) {
    return !platformType
      ? []
      : Constants.libraryTypes.filter(function (lt) {
          return (
            lt.platform === platformType.name &&
            (!lt.archived || originalObject.libraryTypeId === lt.id)
          );
        });
  }

  function getKitDescriptorOptions(platformType, originalKitDescriptorId) {
    return !platformType
      ? []
      : Constants.kitDescriptors.filter(function (kit) {
          return (
            kit.platformType == platformType.key &&
            kit.kitType == "Library" &&
            (!kit.archived || kit.id === originalKitDescriptorId)
          );
        });
  }

  function getIndexFamilyOptions(platformType, originalObject) {
    return !platformType
      ? []
      : Constants.libraryIndexFamilies.filter(function (family) {
          return (
            family.platformType == platformType.name &&
            (!family.archived || family.id === originalObject.indexFamilyId)
          );
        });
  }
})(jQuery);
