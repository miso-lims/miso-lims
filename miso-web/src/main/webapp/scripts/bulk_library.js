BulkTarget = window.BulkTarget || {};
BulkTarget.library = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, propagate, edit}
   *   isLibraryReceipt: boolean
   *   libraryAliasMaybeRequired: boolean
   *   sampleAliasMaybeRequired: boolean
   *   showLibraryAlias: boolean
   *   showDescription: boolean
   *   showVolume: boolean
   *   recipientGroups
   *   thermalCyclers: array
   *   workstations: array
   *   templatesByProjectId: map
   *   sops: array
   */

  var originalDataByRow = {};
  var parentLocationsByRow = null;
  var allowUniqueDualIndexSelectionByRow = {};

  return {
    getSaveUrl: function () {
      return Urls.rest.libraries.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.libraries.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("samples");
    },
    getCustomActions: function (config) {
      function findDefaultStatus(pass, defaultText) {
        var potential = Constants.detailedQcStatuses.filter(function (item) {
          return item.status === pass && item.noteRequired === false;
        });
        if (potential.length === 1) {
          return potential[0];
        }
        return potential.find(function (item) {
          return item.description === defaultText;
        });
      }

      var defaultPassStatus = findDefaultStatus(true, "Ready");
      var defaultFailStatus = findDefaultStatus(false, "Failed: QC");

      return BulkUtils.actions
        .boxable(config.pageMode === "propagate", parentLocationsByRow)
        .concat(
          {
            name: "Check QCs",
            action: function (api) {
              Utils.showDialog(
                "QC Criteria",
                "Check",
                [
                  {
                    label: "Concentration",
                    type: "compare",
                    property: "concentrationComparator",
                  },
                  {
                    label: "Volume",
                    type: "compare",
                    property: "volumeComparator",
                  },
                  {
                    label: "Size",
                    type: "compare",
                    property: "sizeComparator",
                  },
                  {
                    label: "Status for Pass",
                    type: "select",
                    property: "passStatus",
                    values: Constants.detailedQcStatuses
                      .filter(function (item) {
                        return item.status === true;
                      })
                      .map(function (item) {
                        return item.description;
                      }),
                    value: defaultPassStatus ? defaultPassStatus.description : undefined,
                  },
                  {
                    label: "Status for Fail",
                    type: "select",
                    property: "failStatus",
                    values: Constants.detailedQcStatuses
                      .filter(function (item) {
                        return item.status === false;
                      })
                      .map(function (item) {
                        return item.description;
                      }),
                    value: defaultFailStatus ? defaultFailStatus.description : undefined,
                  },
                ],
                function (output) {
                  var rowCount = api.getRowCount();
                  var changes = [];
                  for (var row = 0; row < rowCount; row++) {
                    var pass =
                      output.concentrationComparator(api.getValue(row, "concentration")) &&
                      output.volumeComparator(api.getValue(row, "volume")) &&
                      output.sizeComparator(api.getValue(row, "dnaSize"));
                    changes.push([
                      row,
                      "detailedQcStatusId",
                      pass ? output.passStatus : output.failStatus,
                    ]);
                  }
                  api.updateData(changes);
                }
              );
            },
          },
          {
            name: "View Metrics",
            action: function (api) {
              BulkUtils.actions.viewMetrics(api, ["LIBRARY_PREP"]);
            },
          }
        );
    },
    getBulkActions: function (config) {
      return [
        BulkUtils.actions.edit(Urls.ui.libraries.bulkEdit),
        {
          name: "Make aliquots",
          action: function (items) {
            Utils.warnIfConsentRevoked(items, function () {
              BulkUtils.actions.showDialogForBoxCreation(
                "Make Aliquots",
                "Create",
                [],
                Urls.ui.libraryAliquots.bulkPropagate,
                function (result) {
                  return {
                    ids: items.map(Utils.array.getId).join(","),
                  };
                },
                function (result) {
                  return items.length;
                }
              );
            });
          },
        },
        BulkUtils.actions.print("library"),
        BulkUtils.actions.download(
          Urls.rest.libraries.spreadsheet,
          Constants.librarySpreadsheets,
          function (libraries, spreadsheet) {
            var errors = [];
            return errors;
          }
        ),

        BulkUtils.actions.parents(
          Urls.rest.libraries.parents,
          BulkUtils.relations.categoriesForDetailed()
        ),
        BulkUtils.actions.children(Urls.rest.libraries.children, [
          BulkUtils.relations.libraryAliquot(),
          BulkUtils.relations.pool(),
          BulkUtils.relations.run(),
        ]),
      ]
        .concat(BulkUtils.actions.qc("Library"))
        .concat([
          config.worksetId
            ? BulkUtils.actions.removeFromWorkset(
                "libraries",
                Urls.rest.worksets.removeLibraries(config.worksetId)
              )
            : BulkUtils.actions.addToWorkset(
                "libraries",
                "libraryIds",
                Urls.rest.worksets.addLibraries
              ),
          BulkUtils.actions.attachFile("library", function (library) {
            return library.projectId;
          }),
          BulkUtils.actions.transfer("libraryIds"),
        ]);
    },
    prepareData: function (data, config) {
      parentLocationsByRow = {};
      allowUniqueDualIndexSelectionByRow = {};
      data.forEach(function (library, index) {
        originalDataByRow[index] = {
          effectiveGroupId: library.effectiveGroupId,
          projectId: library.projectId,
          libraryTypeId: library.libraryTypeId,
          indexFamilyId: library.indexFamilyId,
          kitDescriptorId: library.kitDescriptorId,
        };
        if (library.sampleBoxPosition) {
          parentLocationsByRow[index] = library.sampleBoxPosition;
        }
        allowUniqueDualIndexSelectionByRow[index] = false;
      });
    },
    getFixedColumns: function (config) {
      switch (config.pageMode) {
        case "propagate":
          return 2;
        case "edit":
          return config.showLibraryAlias ? 2 : 1;
        case "create":
          return config.sampleAliasMaybeRequired ? 1 : 0;
        default:
          throw new Error("Unexpected pageMode: " + config.pageMode);
      }
    },
    getColumns: function (config, api) {
      var columns = [
        {
          title: "Sample Name",
          type: "text",
          data: "parentSampleName",
          include: config.pageMode === "propagate",
          disabled: true,
        },
        {
          title: "Sample Alias",
          type: "text",
          data: "parentSampleAlias",
          setData: function (library, value, rowIndex, api) {
            if (config.isLibraryReceipt && config.sampleAliasMaybeRequired) {
              library.sample.alias = value;
            }
          },
          include: config.pageMode === "propagate" || config.sampleAliasMaybeRequired,
          disabled: !config.isLibraryReceipt,
        },
        {
          title: "Sample Location",
          type: "text",
          data: "sampleBoxPositionLabel",
          disabled: true,
          include: config.pageMode === "propagate",
          customSorting: [
            {
              name: "Sample Location (by rows)",
              sort: function (a, b) {
                return Utils.sorting.sortBoxPositions(a, b, true);
              },
            },
            {
              name: "Sample Location (by columns)",
              sort: function (a, b) {
                return Utils.sorting.sortBoxPositions(a, b, false);
              },
            },
          ],
        },
        BulkUtils.columns.name,
      ];

      if (config.showLibraryAlias) {
        columns.push(BulkUtils.columns.generatedAlias(config));
      }
      columns.push(
        {
          title: "Project",
          type: "text",
          data: Constants.isDetailedSample ? "projectCode" : "projectName",
          include: !config.isLibraryReceipt,
          disabled: true,
        },
        BulkUtils.columns.assay()
      );

      if (config.isLibraryReceipt) {
        var sampleProp = function (dataProperty) {
          return "sample." + dataProperty;
        };

        var interceptApi = function (api) {
          return {
            getCache: api.getCache,
            showError: api.showError,
            getRowCount: api.getRowCount,
            getValue: function (row, dataProperty) {
              return api.getValue(row, sampleProp(dataProperty));
            },
            getValueObject: function (row, dataProperty) {
              return api.getValueObject(row, sampleProp(dataProperty));
            },
            getSourceData: function (row, dataProperty) {
              return api.getSourceData(row, sampleProp(dataProperty));
            },
            updateField: function (rowIndex, dataProperty, options) {
              api.updateField(rowIndex, sampleProp(dataProperty), options);
            },
            updateData: function (changes) {
              // changes = [[row, prop, value]...]
              api.updateData(
                changes.map(function (change) {
                  return [change[0], sampleProp(change[1]), change[2]];
                })
              );
            },
            isSaved: function () {
              return api.isSaved();
            },
          };
        };

        var samColumns = BulkTarget.sample.getColumns(config, api);
        samColumns.forEach(function (samCol) {
          if (samCol.setData) {
            throw new Error("sample column setData function not handled for library receipt");
          }
          samCol.data = sampleProp(samCol.data);
          samCol.includeSaved = false;
          if (samCol.getData) {
            var originalGetData = samCol.getData;
            samCol.getData = function (library) {
              return originalGetData(library.sample);
            };
          }
          if (samCol.onChange) {
            var originalOnChange = samCol.onChange;
            samCol.onChange = function (rowIndex, newValue, api) {
              originalOnChange(rowIndex, newValue, interceptApi(api));
            };
          }
          if (typeof samCol.source === "function") {
            var originalSource = samCol.source;
            samCol.source = function (library, api) {
              return originalSource(library.sample, interceptApi(api));
            };
          }
        });

        if (config.templatesByProjectId) {
          var projectColumn = Utils.array.findUniqueOrThrow(function (samCol) {
            return samCol.data === "sample.projectId";
          }, samColumns);

          var originalOnChange = projectColumn.onChange;
          projectColumn.onChange = function (rowIndex, newValue, api) {
            originalOnChange(rowIndex, newValue, api);

            var projectLabel = Constants.isDetailedSample ? "code" : "name";
            var project = config.projects.find(function (proj) {
              return proj[projectLabel] === newValue;
            });
            var source = [];
            if (project && config.templatesByProjectId[project.id]) {
              var parentSampleClassAlias = api.getValue(rowIndex, "sample.sampleClassId");
              var parentSampleClass = Utils.array.findUniqueOrThrow(
                Utils.array.aliasPredicate(parentSampleClassAlias),
                Constants.sampleClasses
              );
              source = config.templatesByProjectId[project.id].filter(function (template) {
                return (
                  !template.designId ||
                  Constants.libraryDesigns.some(function (design) {
                    return (
                      design.id === template.designId &&
                      design.sampleClassId === parentSampleClass.id
                    );
                  })
                );
              });
            }
            api.updateField(rowIndex, "template", {
              source: source,
            });
          };
        }

        columns = columns.concat(samColumns);
      } else {
        columns.push(
          {
            title: "Tissue Origin",
            type: "text",
            disabled: true,
            data: "effectiveTissueOriginAlias",
            getData: function (item, api) {
              return (
                item.effectiveTissueOriginAlias + " (" + item.effectiveTissueOriginDescription + ")"
              );
            },
            omit: true,
            include: Constants.isDetailedSample,
          },
          {
            title: "Tissue Type",
            type: "text",
            disabled: true,
            data: "effectiveTissueTypeAlias",
            getData: function (item, api) {
              return (
                item.effectiveTissueTypeAlias + " (" + item.effectiveTissueTypeDescription + ")"
              );
            },
            omit: true,
            include: Constants.isDetailedSample,
          }
        );
      }

      if (config.showDescription) {
        columns.push(BulkUtils.columns.description);
      }

      if (config.isLibraryReceipt) {
        columns = columns.concat(BulkUtils.columns.receipt(config));
      }

      columns = columns.concat(BulkUtils.columns.boxable(config, api));
      if (config.templatesByProjectId && !api.isSaved()) {
        var posColumn = Utils.array.findUniqueOrThrow(function (col) {
          return col.data === "boxPosition";
        }, columns);
        posColumn.onChange = function (rowIndex, newValue, api) {
          if (!api.isSaved()) {
            // If template is set and specifies indices, update indices
            var template = api.getValueObject(rowIndex, "template");
            if (template && template.indexFamilyId) {
              var indexFamily = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(template.indexFamilyId),
                Constants.indexFamilies
              );
              updateIndicesFromTemplate(rowIndex, template, indexFamily, newValue, api);
            }
          }
        };
      }

      columns.push(
        BulkUtils.columns.creationDate(
          !config.isLibraryReceipt,
          config.pageMode == "propagate",
          "library"
        )
      );
      if (!config.isLibraryReceipt) {
        columns.push(BulkUtils.columns.sop(config.sops, config.pageMode === "propagate"));
      }
      columns.push(
        {
          title: "Workstation",
          type: "dropdown",
          data: "workstationId",
          include: !config.isLibraryReceipt,
          source: config.workstations,
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
        },
        {
          title: "Thermal Cycler",
          type: "dropdown",
          data: "thermalCyclerId",
          include: !config.isLibraryReceipt,
          source: config.thermalCyclers,
          sortSource: Utils.sorting.standardSort("name"),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          required:
            config.pageMode === "propagate" &&
            config.thermalCyclers &&
            config.thermalCyclers.length,
        }
      );

      columns = columns.concat(
        BulkUtils.columns.groupId(!config.isLibraryReceipt, function (rowIndex) {
          return originalDataByRow[rowIndex].effectiveGroupId;
        })
      );

      columns.push(
        {
          title: "Template",
          type: "dropdown",
          data: "template",
          omit: true,
          include: !!config.templatesByProjectId,
          includeSaved: false,
          source: function (library, api) {
            var projectId = library.sample ? library.sample.projectId : library.projectId;
            if (!projectId || !config.templatesByProjectId[projectId]) {
              return [];
            }
            return config.templatesByProjectId[projectId].filter(function (template) {
              return (
                !template.designId ||
                Constants.libraryDesigns.some(function (design) {
                  return (
                    design.id === template.designId &&
                    design.sampleClassId === library.parentSampleClassId
                  );
                })
              );
            });
          },
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          onChange: function (rowIndex, newValue, api) {
            var template = api.getValueObject(rowIndex, "template");
            if (template && config.showVolume) {
              // volume can still be changed
              if (template.defaultVolume) {
                api.updateField(rowIndex, "volume", {
                  value: template.defaultVolume,
                });
              }
              if (template.volumeUnits) {
                var unit = Utils.array.findUniqueOrThrow(
                  Utils.array.namePredicate(template.volumeUnits),
                  Constants.volumeUnits
                );
                api.updateField(rowIndex, "volumeUnits", {
                  value: Utils.decodeHtmlString(unit.units),
                });
              }
            }

            if (Constants.isDetailedSample) {
              if (template && template.designId) {
                var designName = getPropertyForItemId(
                  Constants.libraryDesigns,
                  template.designId,
                  "name"
                );
                api.updateField(rowIndex, "libraryDesignId", {
                  value: designName,
                  disabled: true,
                });
                // design change will trigger code, selection, and strategy updates
              } else if (
                template &&
                (template.designCodeId || template.selectionId || template.strategyId)
              ) {
                var selectedDesignName = api.getValue(rowIndex, "libraryDesignId");
                api.updateField(rowIndex, "libraryDesignId", {
                  value: null,
                  disabled: true,
                });
                // design change will trigger code, selection, and strategy updates
                // .. but null > null doesn't trigger
                if (!selectedDesignName) {
                  var design = api.getValueObject(rowIndex, "libraryDesignId");
                  updateDesignFields(rowIndex, api, template, design);
                }
              } else {
                api.updateField(rowIndex, "libraryDesignId", {
                  disabled: false,
                });
                var design = api.getValueObject(rowIndex, "libraryDesignId");
                updateDesignFields(rowIndex, api, template, design);
              }
            } else {
              updateFromTemplate(
                rowIndex,
                "librarySelectionTypeId",
                api,
                template,
                "selectionId",
                Constants.librarySelections,
                "id",
                "name"
              );
              updateFromTemplate(
                rowIndex,
                "libraryStrategyTypeId",
                api,
                template,
                "strategyId",
                Constants.libraryStrategies,
                "id",
                "name"
              );
            }
            updateFromTemplate(
              rowIndex,
              "platformType",
              api,
              template,
              "platformType",
              Constants.platformTypes,
              "name",
              "key"
            );
            updateFromTemplate(
              rowIndex,
              "libraryTypeId",
              api,
              template,
              "libraryTypeId",
              Constants.libraryTypes,
              "id",
              "description"
            );
            updateFromTemplate(
              rowIndex,
              "indexFamilyId",
              api,
              template,
              "indexFamilyId",
              Constants.indexFamilies,
              "id",
              "name"
            );
            if (template && template.indexFamilyId) {
              var indexFamily = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(template.indexFamilyId),
                Constants.indexFamilies
              );
              var boxPos = api.getValue(rowIndex, "boxPosition");
              updateIndicesFromTemplate(rowIndex, template, indexFamily, boxPos, api);
            } else {
              // Note: can't use api.getValueObject as source won't be initialized yet during initialization onChange
              var indexFamilyName = api.getValue(rowIndex, "indexFamilyId");
              var indexFamily = Constants.indexFamilies.find(
                Utils.array.namePredicate(indexFamilyName)
              );
              api.updateField(rowIndex, "index1Id", {
                disabled: !indexFamily,
              });
              api.updateField(rowIndex, "index2Id", {
                disabled:
                  !indexFamily ||
                  !indexFamily.indices.some(function (index) {
                    return index.position === 2;
                  }),
              });
            }
            updateFromTemplate(
              rowIndex,
              "kitDescriptorId",
              api,
              template,
              "kitDescriptorId",
              Constants.kitDescriptors,
              "id",
              "name"
            );
          },
        },
        {
          title: "Design",
          type: "dropdown",
          data: "libraryDesignId",
          include: Constants.isDetailedSample,
          source: function (library, api) {
            return Constants.libraryDesigns.filter(function (design) {
              return design.sampleClassId == library.parentSampleClassId;
            });
          },
          sortSource: Utils.sorting.standardSort("name"),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          onChange: function (rowIndex, newValue, api) {
            if (!api.isSaved()) {
              var design = api.getValueObject(rowIndex, "libraryDesignId");
              var template = config.templatesByProjectId
                ? api.getValueObject(rowIndex, "template")
                : null;
              updateDesignFields(rowIndex, api, template, design);
            }
          },
        },
        {
          title: "Code",
          type: "dropdown",
          data: "libraryDesignCodeId",
          include: Constants.isDetailedSample,
          required: true,
          source: Constants.libraryDesignCodes,
          sortSource: Utils.sorting.standardSort("code"),
          getItemLabel: getLibraryDesignCodeLabel,
          getItemValue: Utils.array.getId,
        },
        {
          title: "Platform",
          type: "dropdown",
          disabled: config.pageMode === "edit",
          data: "platformType",
          required: true,
          source: Constants.platformTypes,
          getItemLabel: Utils.array.get("key"),
          getItemValue: Utils.array.get("key"),
          onChange: function (rowIndex, newValue, api) {
            var selectedPlatform = Constants.platformTypes.find(function (pt) {
              return pt.key === newValue;
            });
            api.updateField(rowIndex, "libraryTypeId", {
              source: newValue
                ? Constants.libraryTypes.filter(function (lt) {
                    return (
                      lt.platform === selectedPlatform.name &&
                      (!lt.archived || lt.id === originalDataByRow[rowIndex].libraryTypeId)
                    );
                  })
                : [],
            });
            var indexFamilies = [];
            if (newValue) {
              indexFamilies = Constants.indexFamilies
                .filter(function (family) {
                  return (
                    family.platformType === selectedPlatform.name &&
                    (!family.archived || family.id === originalDataByRow[rowIndex].indexFamilyId)
                  );
                })
                .sort(Utils.sorting.standardSort("name"));
              indexFamilies.unshift({
                id: null,
                name: "No indices",
              });
            }
            api.updateField(rowIndex, "indexFamilyId", {
              source: indexFamilies,
            });
            api.updateField(rowIndex, "kitDescriptorId", {
              source: newValue
                ? Constants.kitDescriptors.filter(function (kit) {
                    return (
                      kit.kitType === "Library" &&
                      kit.platformType === selectedPlatform.key &&
                      (!kit.archived || kit.id === originalDataByRow[rowIndex].kitDescriptorId)
                    );
                  })
                : [],
            });
          },
        },
        {
          title: "Type",
          type: "dropdown",
          data: "libraryTypeId",
          getData: function (library) {
            return getPropertyForItemId(
              Constants.libraryTypes,
              library.libraryTypeId,
              "description"
            );
          },
          required: true,
          source: [], // initialized in platformType onChange
          sortSource: Utils.sorting.standardSort("description"),
          getItemLabel: Utils.array.get("description"),
          getItemValue: Utils.array.getId,
        },
        {
          title: "Selection",
          type: "dropdown",
          data: "librarySelectionTypeId",
          required: true,
          source: Constants.librarySelections,
          sortSource: Utils.sorting.standardSort("name"),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
        },
        {
          title: "Strategy",
          type: "dropdown",
          data: "libraryStrategyTypeId",
          required: true,
          source: Constants.libraryStrategies,
          sortSource: Utils.sorting.standardSort("name"),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
        },
        {
          title: "Index Kit",
          type: "dropdown",
          data: "indexFamilyId",
          getData: function (library) {
            if (library.indexFamilyId) {
              return getPropertyForItemId(Constants.indexFamilies, library.indexFamilyId, "name");
            } else if (config.pageMode === "edit" || api.isSaved()) {
              return "No indices";
            } else {
              // user must explicitly choose if no indices (null)
              return "";
            }
          },
          required: true,
          source: [], // initialized in platformType onChange
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          onChange: function (rowIndex, newValue, api) {
            var indexFamily = newValue
              ? Utils.array.findFirstOrNull(
                  Utils.array.namePredicate(newValue),
                  Constants.indexFamilies
                )
              : null;
            var index1Changes = null;
            var index2Changes = null;
            if (indexFamily) {
              var template =
                !api.isSaved() && config.templatesByProjectId
                  ? api.getValueObject(rowIndex, "template")
                  : null;
              var boxPos = api.getValue(rowIndex, "boxPosition");

              var getIndexChanges = function (pos, templateMapProperty) {
                var indices = indexFamily.indices.filter(function (index) {
                  return index.position === pos;
                });
                if (pos === 1 && !indices.length) {
                  Utils.showOkDialog("Error", [
                    "Selected index family has no indices for position 1",
                  ]);
                }
                var changes = {
                  source: indices,
                  required: !!indices.length,
                  disabled:
                    !indices.length ||
                    (template &&
                      boxPos &&
                      template[templateMapProperty] &&
                      template[templateMapProperty][boxPos]),
                };
                if (!indices.length) {
                  changes.value = null;
                }
                return changes;
              };
              index1Changes = getIndexChanges(1, "indexOneIds");
              index2Changes = getIndexChanges(2, "indexTwoIds");
            } else {
              index1Changes = {
                source: [],
                required: false,
                disabled: true,
                value: null,
              };
              index2Changes = index1Changes;
            }
            api.updateField(rowIndex, "index1Id", index1Changes);
            api.updateField(rowIndex, "index2Id", index2Changes);
          },
        },
        makeIndexColumn(1),
        makeIndexColumn(2),
        {
          title: "Has UMIs",
          type: "dropdown",
          data: "umis",
          required: true,
          source: [
            {
              label: "True",
              value: true,
            },
            {
              label: "False",
              value: false,
            },
          ],
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
        },
        {
          title: "Kit",
          type: "dropdown",
          data: "kitDescriptorId",
          getData: function (library) {
            return getPropertyForItemId(Constants.kitDescriptors, library.kitDescriptorId, "name");
          },
          required: true,
          source: [], // initialized in platformType onChange
          sortSource: Utils.sorting.standardSort("name"),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
        },
        {
          title: "Kit Lot",
          type: "text",
          data: "kitLot",
          maxLength: 255,
          include: !config.isLibraryReceipt,
          required: config.pageMode === "propagate",
          regex: Utils.validation.uriComponentRegex,
        }
      );

      columns = columns.concat(BulkUtils.columns.detailedQcStatus());
      columns.push(BulkUtils.columns.dnaSize);

      if (config.showVolume) {
        columns = columns.concat(BulkUtils.columns.volume(true, config));
        if (!config.isLibraryReceipt) {
          columns = columns.concat(BulkUtils.columns.parentUsed);
        }
      }
      columns = columns.concat(BulkUtils.columns.concentration());

      columns.push(
        {
          title: "Spike-In",
          type: "dropdown",
          data: "spikeInId",
          source: Constants.spikeIns,
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          onChange: function (rowIndex, newValue, api) {
            var changes = {
              disabled: !newValue,
              required: !!newValue,
            };
            if (!newValue) {
              changes.value = null;
            }
            api.updateField(rowIndex, "spikeInDilutionFactor", changes);
            api.updateField(rowIndex, "spikeInVolume", changes);
          },
        },
        {
          title: "Spike-In Dilution Factor",
          type: "dropdown",
          data: "spikeInDilutionFactor",
          source: Constants.dilutionFactors,
        },
        {
          title: "Spike-In Volume",
          type: "decimal",
          data: "spikeInVolume",
          precision: 14,
          scale: 10,
        }
      );

      return columns;
    },
  };

  function makeIndexColumn(position) {
    var column = {
      title: "Index " + position,
      type: "dropdown",
      data: "index" + position + "Id",
      source: function (library, api) {
        if (!library.indexFamilyId) {
          return [];
        }
        var indexFamily = Utils.array.findUniqueOrThrow(
          Utils.array.idPredicate(library.indexFamilyId),
          Constants.indexFamilies
        );
        return indexFamily.indices.filter(function (index) {
          return index.position === position;
        });
      },
      sortSource: Utils.sorting.standardSort("label"),
      getItemLabel: Utils.array.get("label"),
      getItemValue: Utils.array.getId,
    };
    if (position === 1) {
      column.onChange = function (rowIndex, newValue, api) {
        if (!allowUniqueDualIndexSelectionByRow[rowIndex]) {
          updateUdiSelection(rowIndex, api);
          return;
        }
        if (!newValue) {
          return;
        }
        var indexFamilyName = api.getValue(rowIndex, "indexFamilyId");
        var indexFamily = Utils.array.findFirstOrNull(
          Utils.array.namePredicate(indexFamilyName),
          Constants.indexFamilies
        );
        if (indexFamily && indexFamily.uniqueDualIndex) {
          var index1 = indexFamily.indices.find(function (index) {
            return index.position === 1 && index.label === newValue;
          });
          if (index1) {
            var index2 = indexFamily.indices.find(function (index) {
              return index.position === 2 && index.name === index1.name;
            });
            if (index2) {
              api.updateField(rowIndex, "index2Id", {
                value: index2.label,
              });
            }
          }
        }
      };
    } else {
      column.onChange = function (rowIndex, newValue, api) {
        updateUdiSelection(rowIndex, api);
      };
    }
    return column;
  }

  function updateUdiSelection(rowIndex, api) {
    var indexFamilyName = api.getValue(rowIndex, "indexFamilyId");
    var indexFamily = Constants.indexFamilies.find(Utils.array.namePredicate(indexFamilyName));
    if (!indexFamily) {
      allowUniqueDualIndexSelectionByRow[rowIndex] = true;
      return;
    }
    var index1 = api.getValueObject(rowIndex, "index1Id");
    var index2 = api.getValueObject(rowIndex, "index2Id");
    if (!index2) {
      allowUniqueDualIndexSelectionByRow[rowIndex] = true;
    } else if (!index1) {
      allowUniqueDualIndexSelectionByRow[rowIndex] = false;
    } else {
      allowUniqueDualIndexSelectionByRow[rowIndex] = index1.name === index2.name;
    }
  }

  function getPropertyForItemId(items, id, property) {
    if (!id) {
      return null;
    }
    var item = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(id), items);
    return item[property];
  }

  function updateDesignFields(rowIndex, api, template, design) {
    updateFromDesignOrTemplate(
      rowIndex,
      api,
      "libraryDesignCodeId",
      design,
      "designCodeId",
      template,
      "designCodeId",
      Constants.libraryDesignCodes,
      getLibraryDesignCodeLabel
    );
    updateFromDesignOrTemplate(
      rowIndex,
      api,
      "librarySelectionTypeId",
      design,
      "selectionId",
      template,
      "selectionId",
      Constants.librarySelections,
      Utils.array.getName
    );
    updateFromDesignOrTemplate(
      rowIndex,
      api,
      "libraryStrategyTypeId",
      design,
      "strategyId",
      template,
      "strategyId",
      Constants.libraryStrategies,
      Utils.array.getName
    );
  }

  function updateFromDesignOrTemplate(
    rowIndex,
    api,
    dataProperty,
    design,
    designItemIdField,
    template,
    templateItemIdField,
    items,
    getItemLabel
  ) {
    var item = null;
    if (design) {
      item = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(design[designItemIdField]),
        items
      );
    } else if (template && template[templateItemIdField]) {
      item = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(template[templateItemIdField]),
        items
      );
    } else {
      api.updateField(rowIndex, dataProperty, {
        disabled: false,
      });
      return;
    }
    api.updateField(rowIndex, dataProperty, {
      value: getItemLabel(item),
      disabled: true,
    });
  }

  function updateFromTemplate(
    rowIndex,
    dataProperty,
    api,
    template,
    templateProperty,
    items,
    idField,
    labelField
  ) {
    if (template && template[templateProperty]) {
      var item = Utils.array.findUniqueOrThrow(function (x) {
        return x[idField] === template[templateProperty];
      }, items);
      api.updateField(rowIndex, dataProperty, {
        value: item[labelField],
        disabled: true,
      });
    } else {
      api.updateField(rowIndex, dataProperty, {
        disabled: false,
      });
    }
  }

  function updateIndicesFromTemplate(rowIndex, template, indexFamily, boxPos, api) {
    if (boxPos && template.indexOneIds && template.indexOneIds[boxPos]) {
      api.updateField(rowIndex, "index1Id", {
        value: Utils.array.findUniqueOrThrow(
          Utils.array.idPredicate(template.indexOneIds[boxPos]),
          indexFamily.indices
        ).label,
        disabled: true,
      });
    } else {
      api.updateField(rowIndex, "index1Id", {
        disabled: false,
      });
    }
    if (boxPos && template.indexTwoIds && template.indexTwoIds[boxPos]) {
      api.updateField(rowIndex, "index2Id", {
        value: Utils.array.findUniqueOrThrow(
          Utils.array.idPredicate(template.indexTwoIds[boxPos]),
          indexFamily.indices
        ).label,
        disabled: true,
      });
    } else {
      api.updateField(rowIndex, "index2Id", {
        disabled: indexFamily.indices.some(function (index) {
          return index.position === 2;
        }),
      });
    }
  }

  function getLibraryDesignCodeLabel(item) {
    return item.code + " (" + item.description + ")";
  }
})(jQuery);
