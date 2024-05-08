if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.library = (function ($) {
  /*
   * Expected config {
   *   detailedSample: boolean,
   *   workstations: array,
   *   thermalCyclers: array,
   *   sops: array
   * }
   */

  var allowUniqueDualIndexSelection = false;

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("libraries");
    },
    getSaveUrl: function (library) {
      if (library.id) {
        return Urls.rest.libraries.update(library.id);
      } else {
        throw new Error("Page not intended for new library creation");
      }
    },
    getSaveMethod: function (library) {
      return "PUT";
    },
    getEditUrl: function (library) {
      return Urls.ui.libraries.edit(library.id);
    },
    getSections: function (config, object) {
      var platformName = Utils.array.findUniqueOrThrow(function (item) {
        return item.key === object.platformType;
      }, Constants.platformTypes).name;

      return [
        {
          title: "Library Information",
          fields: [
            {
              title: "Library ID",
              data: "id",
              type: "read-only",
            },
            {
              title: "Name",
              data: "name",
              type: "read-only",
            },
            {
              title: "Parent Sample",
              data: "parentSampleAlias",
              type: "read-only",
              getLink: function (library) {
                return Urls.ui.samples.edit(library.parentSampleId);
              },
            },
            {
              title: "Batch",
              data: "batchId",
              type: "read-only",
              getDisplayValue: function (library) {
                return library.batchId || "n/a";
              },
              getLink: function (library) {
                return library.batchId ? Urls.ui.libraries.batch(library.batchId) : null;
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 100,
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Matrix Barcode",
              data: "identificationBarcode",
              type: "text",
              maxLength: 255,
            },
            FormUtils.makeRequisitionField(object),
            FormUtils.makeEffectiveRequisitionField(object),
            {
              title: "Assays",
              data: "requisitionAssayIds",
              type: "special",
              makeControls: function () {
                return makeAssayControls(object);
              },
            },
            {
              title: "Date of Receipt",
              data: "receivedDate",
              type: "date",
            },
            {
              title: "Creation Date",
              data: "creationDate",
              type: "date",
            },
          ]
            .concat(FormUtils.makeSopFields(object, config.sops))
            .concat(
              [
                {
                  title: "Workstation",
                  data: "workstationId",
                  type: "dropdown",
                  source: config.workstations,
                  sortSource: Utils.sorting.standardSort("alias"),
                  getItemLabel: Utils.array.getAlias,
                  getItemValue: Utils.array.getId,
                },
                {
                  title: "Thermal Cycler",
                  data: "thermalCyclerId",
                  type: "dropdown",
                  source: config.thermalCyclers,
                  sortSource: Utils.sorting.standardSort("name"),
                  getItemLabel: Utils.array.getName,
                  getItemValue: Utils.array.getId,
                },
                {
                  title: "Accession",
                  data: "accession",
                  type: "read-only",
                  getLink: function (library) {
                    return Urls.external.enaAccession(library.accession);
                  },
                  include: object.accession,
                },
                {
                  title: "Paired",
                  data: "paired",
                  type: "checkbox",
                  include: !config.detailedSample,
                },
                {
                  title: "Platform",
                  data: "platformType",
                  type: "dropdown",
                  required: true,
                  source: Constants.platformTypes.filter(function (platformType) {
                    return platformType.active || platformType.key === object.platformType;
                  }),
                  sortSource: Utils.sorting.standardSort("key"),
                  getItemLabel: function (item) {
                    return item.key;
                  },
                  getItemValue: function (item) {
                    return item.key;
                  },
                  onChange: function (newValue, form) {
                    var platformType = Utils.array.findUniqueOrThrow(function (item) {
                      return item.key === newValue;
                    }, Constants.platformTypes);
                    form.updateField("libraryTypeId", {
                      source: Constants.libraryTypes.filter(function (item) {
                        return item.platform === platformType.name;
                      }),
                    });
                    form.updateField("indexFamilyId", {
                      source: Constants.indexFamilies.filter(function (item) {
                        return item.platformType === platformType.name;
                      }),
                    });
                    form.updateField("kitDescriptorId", {
                      source: Constants.kitDescriptors.filter(function (item) {
                        return (
                          item.kitType === "Library" &&
                          item.platformType === platformType.key &&
                          (!item.archived || item.id === object.kitDescriptorId)
                        );
                      }),
                    });
                  },
                },
                {
                  title: "Library Type",
                  data: "libraryTypeId",
                  type: "dropdown",
                  required: true,
                  source: Constants.libraryTypes.filter(function (libraryType) {
                    return libraryType.platform === platformName;
                  }),
                  sortSource: Utils.sorting.standardSort("description"),
                  getItemLabel: function (item) {
                    return item.description;
                  },
                  getItemValue: function (item) {
                    return item.id;
                  },
                },
                {
                  title: "Library Design",
                  data: "libraryDesignId",
                  type: "dropdown",
                  include: config.detailedSample,
                  source: Constants.libraryDesigns.filter(function (design) {
                    return design.sampleClassId === object.parentSampleClassId;
                  }),
                  sortSource: Utils.sorting.standardSort("name"),
                  getItemLabel: function (item) {
                    return item.name;
                  },
                  getItemValue: function (item) {
                    return item.id;
                  },
                  onChange: function (newValue, form) {
                    if (newValue) {
                      var design = Utils.array.findUniqueOrThrow(
                        Utils.array.idPredicate(newValue),
                        Constants.libraryDesigns
                      );
                      form.updateField("libraryDesignCodeId", {
                        disabled: true,
                        value: design.designCodeId,
                      });
                      form.updateField("librarySelectionTypeId", {
                        disabled: true,
                        value: design.selectionId,
                      });
                      form.updateField("libraryStrategyTypeId", {
                        disabled: true,
                        value: design.strategyId,
                      });
                    } else {
                      form.updateField("libraryDesignCodeId", {
                        disabled: false,
                      });
                      form.updateField("librarySelectionTypeId", {
                        disabled: false,
                      });
                      form.updateField("libraryStrategyTypeId", {
                        disabled: false,
                      });
                    }
                  },
                },
                {
                  title: "Design Code",
                  data: "libraryDesignCodeId",
                  type: "dropdown",
                  include: config.detailedSample,
                  required: true,
                  source: Constants.libraryDesignCodes,
                  sortSource: Utils.sorting.standardSort("code"),
                  getItemLabel: function (item) {
                    return item.code + " (" + item.description + ")";
                  },
                  getItemValue: function (item) {
                    return item.id;
                  },
                },
                {
                  title: "Library Selection Type",
                  data: "librarySelectionTypeId",
                  type: "dropdown",
                  source: Constants.librarySelections,
                  sortSource: Utils.sorting.standardSort("name"),
                  getItemLabel: function (item) {
                    return item.name;
                  },
                  getItemValue: function (item) {
                    return item.id;
                  },
                },
                {
                  title: "Library Strategy Type",
                  data: "libraryStrategyTypeId",
                  type: "dropdown",
                  source: Constants.libraryStrategies,
                  sortSource: Utils.sorting.standardSort("name"),
                  getItemLabel: function (item) {
                    return item.name;
                  },
                  getItemValue: function (item) {
                    return item.id;
                  },
                },
                {
                  title: "Index Family",
                  data: "indexFamilyId",
                  type: "dropdown",
                  nullLabel: "No indices",
                  source: Constants.indexFamilies.filter(function (family) {
                    return family.platformType === platformName;
                  }),
                  sortSource: Utils.sorting.standardSort("name"),
                  getItemLabel: function (item) {
                    return item.name;
                  },
                  getItemValue: function (item) {
                    return item.id;
                  },
                  onChange: function (newValue, form) {
                    if (!newValue) {
                      form.updateField("index2Id", {
                        source: [],
                        disabled: true,
                      });
                      form.updateField("index1Id", {
                        source: [],
                        disabled: true,
                        required: false,
                      });
                    } else {
                      // update index 2 dropdown before index 1 because index 1 may effect index 2
                      var indices2 = getIndices(newValue, 2);
                      form.updateField("index2Id", {
                        source: indices2,
                        disabled: !indices2 || !indices2.length,
                      });
                      var indices1 = getIndices(newValue, 1);
                      if (!indices1.length) {
                        Utils.showOkDialog("Error", [
                          "Selected index family has no indices for position 1",
                        ]);
                      }
                      form.updateField("index1Id", {
                        source: indices1,
                        disabled: false,
                        required: true,
                      });
                    }
                  },
                },
                makeIndexColumn(object, 1),
                makeIndexColumn(object, 2),
                {
                  title: "Has UMIs",
                  data: "umis",
                  type: "checkbox",
                },
              ]
                .concat(FormUtils.makeDetailedQcStatusFields())
                .concat([
                  {
                    title: "Low Quality Sequencing",
                    data: "lowQuality",
                    type: "checkbox",
                  },
                  FormUtils.makeDnaSizeField(),
                  {
                    title: "Discarded",
                    data: "discarded",
                    type: "checkbox",
                    onChange: function (newValue, form) {
                      form.updateField("volume", {
                        disabled: newValue,
                      });
                    },
                  },
                  {
                    title: "Initial Volume",
                    data: "initialVolume",
                    type: "decimal",
                    precision: 16,
                    scale: 10,
                    min: 0,
                    onChange: function (newValue, form) {
                      form.updateField("volume", {
                        required: newValue,
                      });
                    },
                  },
                  {
                    title: "Volume",
                    data: "volume",
                    type: "decimal",
                    precision: 16,
                    scale: 10,
                  },
                  FormUtils.makeUnitsField(object, "volume"),
                  {
                    title: "Parent ng Used",
                    data: "ngUsed",
                    type: "decimal",
                    precision: 14,
                    scale: 10,
                  },
                  {
                    title: "Parent Volume Used",
                    data: "volumeUsed",
                    type: "decimal",
                    precision: 16,
                    scale: 10,
                    min: 0,
                  },
                  {
                    title: "Concentration",
                    data: "concentration",
                    type: "decimal",
                    precision: 14,
                    scale: 10,
                  },
                  FormUtils.makeUnitsField(object, "concentration"),
                  FormUtils.makeBoxLocationField(),
                  {
                    title: "Location Note",
                    data: "locationBarcode",
                    type: "text",
                    maxLength: 255,
                  },
                  {
                    title: "Kit",
                    data: "kitDescriptorId",
                    type: "dropdown",
                    required: true,
                    source: Constants.kitDescriptors.filter(function (item) {
                      return (
                        item.kitType === "Library" &&
                        item.platformType === object.platformType &&
                        (!item.archived || item.id === object.kitDescriptorId)
                      );
                    }),
                    sortSource: Utils.sorting.standardSort("name"),
                    getItemLabel: function (item) {
                      return item.name;
                    },
                    getItemValue: function (item) {
                      return item.id;
                    },
                  },
                  {
                    title: "Kit Lot",
                    data: "kitLot",
                    type: "text",
                    maxLength: 100,
                    regex: Utils.validation.uriComponentRegex,
                  },
                  {
                    title: "Spike-In",
                    data: "spikeInId",
                    type: "dropdown",
                    source: Constants.spikeIns,
                    sortSource: Utils.sorting.standardSort("alias"),
                    getItemLabel: function (item) {
                      return item.alias;
                    },
                    getItemValue: function (item) {
                      return item.id;
                    },
                    onChange: function (newValue, form) {
                      var options = {
                        disabled: !newValue,
                        required: !!newValue,
                      };
                      if (!newValue) {
                        options.value = "";
                      }
                      form.updateField("spikeInDilutionFactor", options);
                      form.updateField("spikeInVolume", options);
                    },
                  },
                  {
                    title: "Spike-In Dilution Factor",
                    data: "spikeInDilutionFactor",
                    type: "dropdown",
                    source: Constants.dilutionFactors,
                    sortSource: function (a, b) {
                      return a.length - b.length;
                    },
                    nullLabel: "n/a",
                  },
                  {
                    title: "Spike-In Volume",
                    data: "spikeInVolume",
                    type: "decimal",
                    precision: 14,
                    scale: 10,
                  },
                ])
            ),
        },
        {
          title: "Details",
          include: config.detailedSample,
          fields: [
            {
              title: "External Names",
              data: "effectiveExternalNames",
              type: "read-only",
            },
            {
              title: "Tissue Origin",
              data: "effectiveTissueOriginAlias",
              getDisplayValue: function (item) {
                return (
                  item.effectiveTissueOriginAlias +
                  " (" +
                  item.effectiveTissueOriginDescription +
                  ")"
                );
              },
              type: "read-only",
            },
            {
              title: "Tissue Type",
              data: "effectiveTissueTypeAlias",
              getDisplayValue: function (item) {
                return (
                  item.effectiveTissueTypeAlias + " (" + item.effectiveTissueTypeDescription + ")"
                );
              },
              type: "read-only",
            },
            {
              title: "Effective Group ID",
              data: "effectiveGroupId",
              type: "read-only",
              getDisplayValue: function (library) {
                if (
                  library.hasOwnProperty("effectiveGroupId") &&
                  library.effectiveGroupId !== null
                ) {
                  return library.effectiveGroupId + " (" + library.effectiveGroupIdSample + ")";
                } else {
                  return "None";
                }
              },
            },
            {
              title: "Group ID",
              data: "groupId",
              type: "text",
              maxLength: 100,
              regex: Utils.validation.alphanumRegex,
            },
            {
              title: "Group Description",
              data: "groupDescription",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Archived",
              data: "archived",
              type: "checkbox",
            },
          ],
        },
      ];
    },
  };

  function makeIndexColumn(library, position) {
    var field = {
      title: "Index " + position,
      data: "index" + position + "Id",
      type: "dropdown",
      source: getIndices(library.indexFamilyId, position),
      sortSource: Utils.sorting.standardSort("label"),
      getItemLabel: function (item) {
        return item.label;
      },
      getItemValue: function (item) {
        return item.id;
      },
    };
    if (position === 1) {
      field.onChange = function (newValue, form) {
        if (!allowUniqueDualIndexSelection) {
          updateUdiSelection(form);
          return;
        }
        var indexFamilyId = form.get("indexFamilyId");
        if (!indexFamilyId) return;
        var indexFamily = Utils.array.findUniqueOrThrow(
          Utils.array.idPredicate(indexFamilyId),
          Constants.indexFamilies
        );
        if (indexFamily.uniqueDualIndex) {
          if (!newValue) {
            return;
          }
          var index1 = Utils.array.findUniqueOrThrow(
            Utils.array.idPredicate(newValue),
            indexFamily.indices
          );
          var index2 = indexFamily.indices.find(function (index) {
            return index.position === 2 && index.name === index1.name;
          });
          if (index2) {
            form.updateField("index2Id", {
              value: index2.id,
            });
          }
        }
      };
    } else {
      field.onChange = function (newValue, form) {
        updateUdiSelection(form);
      };
    }
    return field;
  }

  function updateUdiSelection(form) {
    var indexFamilyId = form.get("indexFamilyId");
    if (!indexFamilyId) {
      allowUniqueDualIndexSelection = true;
      return;
    }
    var index1Id = form.get("index1Id");
    var index2Id = form.get("index2Id");
    if (!index2Id) {
      allowUniqueDualIndexSelection = true;
    } else if (!index1Id) {
      allowUniqueDualIndexSelection = false;
    } else {
      var indexFamily = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(indexFamilyId),
        Constants.indexFamilies
      );
      var index1 = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(index1Id),
        indexFamily.indices
      );
      var index2 = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(index2Id),
        indexFamily.indices
      );
      allowUniqueDualIndexSelection = index1.name === index2.name;
    }
  }

  function getIndices(indexFamilyId, position) {
    if (!indexFamilyId) {
      return [];
    }
    var indexFamily = Utils.array.findUniqueOrThrow(function (family) {
      return family.id == indexFamilyId;
    }, Constants.indexFamilies);
    return indexFamily.indices.filter(function (index) {
      return index.position === position;
    });
  }

  function makeAssayControls(library) {
    return FormUtils.makeAssaysFieldWithButtons(library.requisitionAssayIds, function (assay) {
      Assay.utils.showMetrics(assay, "LIBRARY_PREP");
    });
  }
})(jQuery);
