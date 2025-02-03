BulkTarget = window.BulkTarget || {};
BulkTarget.sample = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, propagate, edit}
   *   box: optional new box created to put items in
   *   sourceCategory: required for detailed sample propagate
   *   targetCategory: required for detailed sample create/propagate/edit; not required for library receipt
   *   isLibraryReceipt: optional boolean (default: false)
   *   targetSampleClass: required for library receipt
   *   recipientGroups: groups to include in Received By column
   *   project: project to create sample in,
   *   projects: all projects
   *   sortLibraryPropagate: string; column for default sort when propagating libraries
   *   sops: array
   * }
   */

  var originalProjectIdsBySampleId = {};
  var originalEffectiveGroupIdsByRow = {};
  var parentLocationsByRow = null;
  var metricCategories = [];

  var showLabColumn = false;
  var editSubcategories = [];

  return {
    getSaveUrl: function () {
      return Urls.rest.samples.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.samples.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("samples");
    },
    getCustomActions: function (config) {
      var allowMatchParentPos =
        Constants.isDetailedSample &&
        (config.pageMode === "propagate" ||
          (config.pageMode === "edit" && config.targetCategory !== "Identity"));
      var actions = BulkUtils.actions.boxable(allowMatchParentPos, parentLocationsByRow);
      actions.push({
        name: "View Metrics",
        action: function (api) {
          BulkUtils.actions.viewMetrics(
            api,
            metricCategories,
            "Samples found for multiple metric categories:",
            config.pageMode
          );
        },
      });
      return actions;
    },
    getBulkActions: function (config) {
      var actions = [
        {
          name: "Edit",
          action: function (samples) {
            if (
              samples.some(function (sample) {
                return sample.sampleClassId;
              }) &&
              !Constants.isDetailedSample
            ) {
              Utils.showOkDialog("Error", [
                "There are detailed samples, but MISO is not configured for this.",
              ]);
              return;
            }

            var categories = Utils.array.deduplicateString(
              getSampleClasses(samples).map(function (sampleClass) {
                return sampleClass.sampleCategory;
              })
            );
            if (categories.length > 1) {
              Utils.showOkDialog("Error", [
                "You have selected samples of categories " +
                  categories.join(" & ") +
                  ". Please select samples from only one category.",
              ]);
              return;
            }
            Utils.page.post(Urls.ui.samples.bulkEdit, {
              ids: samples.map(Utils.array.getId).join(","),
            });
          },
        },
        {
          name: "Propagate",
          action: function (samples) {
            Utils.warnIfConsentRevoked(samples, function () {
              var idsString = samples.map(Utils.array.getId).join(",");
              var classes = getSampleClasses(samples);
              var sourceCategories = Utils.array.deduplicateString(
                classes.map(function (sampleClass) {
                  return sampleClass.sampleCategory;
                })
              );
              if (sourceCategories.length > 1) {
                Utils.showOkDialog("Error", [
                  "You have selected samples of categories " +
                    sourceCategories.join(" & ") +
                    ". Please select samples from only one category.",
                ]);
                return;
              }

              // In the case of plain samples, this will be empty, which is fine.
              var targets = getCommonChildCategories(classes).map(function (category) {
                return {
                  name: category,
                  action: function (replicates, newBoxId) {
                    Utils.page.post(Urls.ui.samples.bulkPropagate, {
                      boxId: newBoxId,
                      parentIds: idsString,
                      replicates: replicates,
                      targetCategory: category,
                    });
                  },
                };
              });
              if (!Constants.isDetailedSample || sourceCategories[0] === "Aliquot") {
                targets.push({
                  name: "Library",
                  action: function (replicates, newBoxId) {
                    var params = {
                      boxId: newBoxId,
                      ids: idsString,
                      replicates: replicates,
                    };
                    if (config.sortLibraryPropagate) {
                      params.sort = config.sortLibraryPropagate;
                    }
                    Utils.page.post(Urls.ui.libraries.bulkPropagate, params);
                  },
                });
              }

              if (targets.length == 0) {
                Utils.showOkDialog("Error", [
                  "No propagation is possible from the selected samples.",
                ]);
                return;
              }

              Utils.showDialog(
                targets.length > 1 ? "Propagate Samples" : "Propagate to " + targets[0].name,
                "Propagate",
                [
                  {
                    property: "replicates",
                    type: "int",
                    label: "Replicates",
                    value: 1,
                    required: true,
                  },
                  samples.length > 1
                    ? {
                        property: "customReplication",
                        type: "checkbox",
                        label: "Specify replicates per sample",
                        value: false,
                      }
                    : null,
                  targets.length > 1
                    ? {
                        property: "target",
                        type: "select",
                        label: "To",
                        values: targets,
                        getLabel: Utils.array.getName,
                      }
                    : null,
                  ListUtils.createBoxField,
                ].filter(function (x) {
                  return !!x;
                }),
                function (result) {
                  var loadPage = function (boxId, replicates) {
                    (result.target || targets[0]).action(replicates, boxId);
                  };
                  var createBox = function (sampleCount, replicates) {
                    Utils.createBoxDialog(
                      result,
                      function (result) {
                        return sampleCount;
                      },
                      function (newBox) {
                        loadPage(newBox.id, replicates);
                      }
                    );
                  };
                  if (result.customReplication) {
                    var replicateFields = [];
                    for (var i = 0; i < samples.length; i++) {
                      replicateFields.push({
                        property: "replicates" + i,
                        type: "int",
                        label: samples[i].alias,
                        value: result.replicates,
                        required: true,
                      });
                    }
                    Utils.showDialog(
                      "Propagate Samples - Replicates",
                      "OK",
                      replicateFields,
                      function (replicatesResult) {
                        var replicates = [];
                        for (var key in replicatesResult) {
                          replicates.push(replicatesResult[key]);
                        }
                        var replicatesString = replicates.join(",");
                        if (result.createBox) {
                          createBox(
                            replicates.reduce(function (total, num) {
                              return total + num;
                            }),
                            replicatesString
                          );
                        } else {
                          loadPage(null, replicatesString);
                        }
                      }
                    );
                  } else if (result.createBox) {
                    createBox(result.replicates * samples.length, result.replicates);
                  } else {
                    loadPage(null, result.replicates);
                  }
                }
              );
            });
          },
        },
        BulkUtils.actions.print("sample"),
        BulkUtils.actions.download(
          Urls.rest.samples.spreadsheet,
          Constants.sampleSpreadsheets.filter(function (sheet) {
            return Constants.isDetailedSample || sheet.allowedClasses.indexOf("Plain") !== -1;
          }),
          function (samples, spreadsheet) {
            var errors = [];
            var invalidSamples = [];
            samples.forEach(function (sample) {
              if (!spreadsheet.sheet.allowedClasses.includes(getSampleCategory(sample))) {
                invalidSamples.push(sample);
              }
            });
            if (invalidSamples.length > 0) {
              errors.push("Error: Invalid sample class types");
              errors.push("Allowed types: " + spreadsheet.sheet.allowedClasses.join(", "));
              errors.push("Invalid samples:");
              invalidSamples.forEach(function (sample) {
                errors.push("* " + sample.alias + " (" + getSampleCategory(sample) + ")");
              });
            }
            return errors;
          }
        ),
      ];

      if (Constants.isDetailedSample) {
        actions.push(
          BulkUtils.actions.parents(
            Urls.rest.samples.parents,
            BulkUtils.relations.categoriesForDetailed()
          )
        );
      }

      actions.push(
        BulkUtils.actions.children(
          Urls.rest.samples.children,
          BulkUtils.relations
            .categoriesForDetailed()
            .concat([
              BulkUtils.relations.library(),
              BulkUtils.relations.libraryAliquot(),
              BulkUtils.relations.pool(),
              BulkUtils.relations.run(),
            ])
        )
      );

      actions = actions.concat(BulkUtils.actions.qc("Sample"));

      if (config && config.worksetId) {
        actions.push(
          BulkUtils.actions.removeFromWorkset(
            "samples",
            Urls.rest.worksets.removeSamples(config.worksetId)
          )
        );
      } else {
        actions.push(
          BulkUtils.actions.addToWorkset("samples", "sampleIds", Urls.rest.worksets.addSamples)
        );
      }

      actions.push(
        BulkUtils.actions.attachFile("sample", function (sample) {
          return sample.projectId;
        }),
        BulkUtils.actions.transfer("sampleIds")
      );

      return actions;
    },
    getDefaultSortFunction: function (config) {
      if (config.pageMode === "propagate") {
        return Utils.sorting.standardSort("parentId");
      }
      return null;
    },
    prepareData: function (data, config) {
      parentLocationsByRow = {};
      data.forEach(function (sample, index) {
        originalEffectiveGroupIdsByRow[index] = sample.effectiveGroupId;
        if (Constants.isDetailedSample && sample.parentBoxPosition) {
          parentLocationsByRow[index] = sample.parentBoxPosition;
        }
        if (config.pageMode === "edit") {
          originalProjectIdsBySampleId[sample.id] = sample.projectId;
        } else {
          if (Constants.isDetailedSample) {
            if (sample.relatedSlides && sample.relatedSlides.length === 1) {
              sample.referenceSlideId = sample.relatedSlides[0].id;
            }
            if (config.targetSampleClass) {
              sample.sampleClassId = config.targetSampleClass.id;
            } else {
              var sampleClassOptions = getSampleClassOptions(
                config.targetSampleClass,
                config.targetCategory,
                sample.parentSampleClassId
              );
              if (sampleClassOptions.length === 1) {
                sample.sampleClassId = sampleClassOptions[0].id;
              }
            }
          }
        }
        if (sample.labId) {
          showLabColumn = true;
        }
      });
      if (config.pageMode === "edit" && Constants.isDetailedSample) {
        editSubcategories = getSampleClasses(data)
          .filter(function (sampleClass) {
            return sampleClass.sampleSubcategory;
          })
          .map(function (sampleClass) {
            return sampleClass.sampleSubcategory;
          });
      }
      metricCategories = [];
      if (
        config.pageMode === "create" ||
        data.some(function (sample) {
          return sample.requisitionId;
        })
      ) {
        metricCategories.push("RECEIPT");
      }
      if (config.targetCategory === "Stock") {
        metricCategories.push("EXTRACTION");
      }
    },
    getFixedColumns: function (config) {
      switch (config.pageMode) {
        case "edit":
          return 2;
        case "propagate":
          return 2;
        default:
          return 0;
      }
    },
    getColumns: function (config, api) {
      var targetCategory = config.isLibraryReceipt ? "Aliquot" : config.targetCategory;
      // (Detailed sample) Columns to show
      var show = {};

      // We assume we have a linear progression of information that must be
      // collected as a sample progressed through the hierarchy.
      var progression = ["Identity", "Tissue", "Tissue Processing", "Stock", "Aliquot"];
      // First, set all the groups of detailed columns we will show to off.
      for (var i = 0; i < progression.length; i++) {
        show[progression[i]] = false;
      }
      // Determine the indices of the first and less steps in the progression.
      var endProgression = targetCategory ? progression.indexOf(targetCategory) : -1;
      var startProgression;
      if (!Constants.isDetailedSample) {
        startProgression = -1;
      } else if (config.isLibraryReceipt || config.pageMode == "create") {
        startProgression = 0;
      } else if (config.pageMode == "edit") {
        startProgression = endProgression;
      } else {
        startProgression = progression.indexOf(config.sourceCategory);
        // Increment to display columns of next category in progression unless
        // source and target category are the same (happens during
        // editing or propagation within a category).
        if (progression.indexOf(targetCategory) > startProgression) {
          startProgression += 1;
        }
      }
      // Now, mark all the appropriate column groups active
      for (i = startProgression; i <= endProgression && i != -1; i++) {
        show[progression[i]] = true;
      }

      var sampleClassOptions = Constants.isDetailedSample
        ? getSampleClassOptions(config.targetSampleClass, targetCategory)
        : null;

      var columns = [];

      if (!config.isLibraryReceipt) {
        columns.push(BulkUtils.columns.name, BulkUtils.columns.generatedAlias(config));

        if (Constants.isDetailedSample) {
          // parent columns go at start if propagating, or after the sample name and alias if editing
          var parentColumns = [
            {
              title: "Parent Name",
              type: "text",
              data: "parentName",
              disabled: true,
            },
            {
              title: "Parent Alias",
              type: "text",
              data: "parentAlias",
              disabled: true,
            },
            {
              title: "Parent Location",
              type: "text",
              data: "parentBoxPositionLabel",
              disabled: true,
              customSorting: [
                {
                  name: "Parent Location (by rows)",
                  sort: function (a, b) {
                    return Utils.sorting.sortBoxPositions(a, b, true);
                  },
                },
                {
                  name: "Parent Location (by columns)",
                  sort: function (a, b) {
                    return Utils.sorting.sortBoxPositions(a, b, false);
                  },
                },
              ],
            },
            {
              title: "Parent Sample Class",
              type: "text",
              data: "parentSampleClassAlias",
              disabled: true,
              include: config.pageMode === "propagate",
              getData: function (sample) {
                return Utils.array.maybeGetProperty(
                  Utils.array.findFirstOrNull(function (item) {
                    return item.id == sample.parentSampleClassId;
                  }, Constants.sampleClasses),
                  "alias"
                );
              },
              omit: true,
            },
          ];
          if (config.pageMode === "propagate") {
            columns = parentColumns.concat(columns);
          } else if (config.pageMode === "edit" && targetCategory !== "Identity") {
            columns = columns.concat(parentColumns);
          }
        }

        columns.push(BulkUtils.columns.description);

        if (
          config.pageMode === "create" &&
          (!Constants.isDetailedSample || targetCategory !== "Identity")
        ) {
          columns = columns.concat(BulkUtils.columns.receipt(config));
        }
      }

      columns.push({
        title: "Project",
        type: "dropdown",
        data: "projectId",
        required: true,
        disabled: !!config.project,
        source: function (sample, api) {
          return config.projects.filter(function (project) {
            return (
              project.status === "Active" ||
              project.id === sample.projectId ||
              (config.project && project.id === config.project.id)
            );
          });
        },
        sortSource: Utils.sorting.standardSort(Constants.isDetailedSample ? "code" : "id"),
        getItemLabel: Constants.isDetailedSample
          ? function (item) {
              return item.code;
            }
          : Utils.array.getName,
        getItemValue: Utils.array.getId,
        initial: config.project
          ? config.project[Constants.isDetailedSample ? "code" : "name"]
          : null,
        onChange: function (rowIndex, newValue, api) {
          if (targetCategory !== "Identity" && config.pageMode === "create") {
            // For library receipt, the regular API is intercepted for sample columns to redirect
            // them to access other sample properties. We want to avoid that when dealing with
            // requisition fields, which belong to the item being received - sample or library
            var nonInterceptApi = api.bypassIntercept || api;
            if (nonInterceptApi.getValue(rowIndex, "requisitionId") === "Create New") {
              var projectSelected = api.getValueObject(rowIndex, "projectId");
              if (projectSelected && projectSelected.assayIds !== null) {
                BulkUtils.updateProjectAssays(projectSelected, nonInterceptApi, rowIndex);
              } else {
                nonInterceptApi.updateField(rowIndex, "requisitionAssayIds", {
                  source: [],
                  value: null,
                });
              }
            }
          }
          var project = config.projects.find(function (item) {
            if (Constants.isDetailedSample) {
              return item.code === newValue;
            } else {
              return item.name === newValue;
            }
          });
          if (Constants.isDetailedSample) {
            var subprojects = project
              ? Constants.subprojects.filter(function (subproject) {
                  return subproject.parentProjectId === project.id;
                })
              : [];
            var changes = {
              source: subprojects,
              disabled: !subprojects.length,
            };
            if (!subprojects.length) {
              changes.value = null;
            }
            api.updateField(rowIndex, "subprojectId", changes);
          }

          if (project && project.defaultSciName && config.pageMode !== "edit") {
            api.updateField(rowIndex, "scientificNameId", {
              value: project.defaultSciName,
            });
          }
        },
      });

      if (
        config.pageMode === "create" &&
        !config.isLibraryReceipt &&
        targetCategory !== "Identity"
      ) {
        columns = columns.concat(BulkUtils.columns.requisition("projectId"));
      }

      if (targetCategory !== "Identity" && !config.isLibraryReceipt) {
        columns.push(BulkUtils.columns.assay(config.pageMode));
        columns = columns.concat(BulkUtils.columns.boxable(config, api));
      }

      columns.push(
        {
          title: "Subproject",
          type: "dropdown",
          data: "subprojectId",
          include: Constants.isDetailedSample,
          source: function (sample, api) {
            if (!sample.projectId) {
              return [];
            }
            var project = config.projects.find(Utils.array.idPredicate(sample.projectId));
            return Constants.subprojects.filter(function (subproject) {
              return subproject.parentProjectId === project.id;
            });
          },
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
        },
        {
          title: "Sample Class",
          type: "dropdown",
          disabled: config.pageMode === "edit" || config.isLibraryReceipt,
          data: "sampleClassId",
          include: Constants.isDetailedSample,
          required: true,
          source: function (data, limitedApi) {
            if (config.pageMode === "edit") {
              return Constants.sampleClasses.filter(Utils.array.idPredicate(data.sampleClassId));
            } else if (data.parentSampleClassId) {
              return filterSampleClassesByParentId(sampleClassOptions, data.parentSampleClassId);
            } else {
              return sampleClassOptions;
            }
          },
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          onChange: function (rowIndex, newValue, api) {
            var selected = null;
            if (newValue) {
              selected = Utils.array.findFirstOrNull(
                Utils.array.aliasPredicate(newValue),
                Constants.sampleClasses
              );
            }
            if (selected) {
              // enable/disable columns based on sampleSubcategory
              columns
                .filter(function (column) {
                  return (
                    (!column.hasOwnProperty("include") || column.include) &&
                    column.sampleSubcategory
                  );
                })
                .forEach(function (column) {
                  var subcategoryMatch =
                    (Array.isArray(column.sampleSubcategory) &&
                      column.sampleSubcategory.indexOf(selected.sampleSubcategory) !== -1) ||
                    (typeof column.sampleSubcategory === "string" &&
                      column.sampleSubcategory === selected.sampleSubcategory);
                  // for library receipt, need to correct since 'sample.' gets added again on updateField calls
                  var dataField = config.isLibraryReceipt
                    ? column.data.replace("sample.", "")
                    : column.data;
                  var newValue = undefined;
                  if (!subcategoryMatch) {
                    newValue = null;
                  } else if (config.pageMode !== "edit" && column.initial) {
                    newValue = column.initial;
                  }
                  api.updateField(rowIndex, dataField, {
                    disabled: !subcategoryMatch,
                    required: subcategoryMatch ? column.required : false,
                    value: newValue,
                  });
                });
              if (selected.defaultSampleType && config.pageMode !== "edit") {
                api.updateField(rowIndex, "sampleType", {
                  value: selected.defaultSampleType,
                });
              }
            }
          },
        },
        {
          title: "Sample Type",
          type: "dropdown",
          data: "sampleType",
          required: true,
          source: function (data, api) {
            // include current sample type in-case archived
            var source = Constants.sampleTypes;
            if (data.sampleType && source.indexOf(data.sampleType) === -1) {
              source = source.concat([data.sampleType]);
            }
            return source;
          },
          sortSource: Utils.sorting.standardSortItems,
        },
        {
          title: "Sci. Name",
          type: "dropdown",
          data: "scientificNameId",
          required: true,
          source: Constants.scientificNames,
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          initial:
            config.project && config.project.defaultSciName
              ? config.project.defaultSciName
              : config.defaultSciName,
        },
        {
          title: "External Name",
          type: "text",
          data: "externalName",
          description: "Name or other identifier for the donor or organism in an external system",
          include: show["Identity"],
          includeSaved: targetCategory === "Identity",
          required: true,
          onChange: function (rowIndex, newValue, api) {
            var enteredNames = separateExternalNames(newValue);
            if (targetCategory === "Identity") {
              return;
            } else if (!enteredNames || !enteredNames.length) {
              api.updateField(rowIndex, "identityId", {
                source: [],
                value: null,
                formatter: null,
              });
              return;
            }
            var label = Constants.isDetailedSample ? "code" : "name";
            var selectedProject = Utils.array.findFirstOrNull(function (project) {
              return project[label] === api.getValue(rowIndex, "projectId");
            }, config.projects);
            if (selectedProject == null) {
              // the user needs to select a project
              api.updateField(rowIndex, "identityId", {
                value: "Delete external name, select a project, then re-enter external name.",
              });
              return;
            }
            api.updateField(rowIndex, "identityId", {
              source: [],
              value: "(searching...)",
              formatter: null,
            });
            // we search by null project in case the user wants to choose an identity from another project
            $.ajax({
              url:
                Urls.rest.samples.identitiesLookup +
                "?" +
                Utils.page.param({
                  exactMatch: true,
                }),
              data: JSON.stringify({
                identitiesSearches: enteredNames,
                project: null,
              }),
              contentType: "application/json; charset=utf8",
              dataType: "json",
              type: "POST",
            })
              .done(function (data) {
                // sort with identities from selected project on top
                var potentialIdentities = [];
                if (data && data.length) {
                  potentialIdentities = data.sort(function (a, b) {
                    var aSortId = a.projectId == selectedProject.id ? 0 : a.projectId;
                    var bSortId = b.projectId == selectedProject.id ? 0 : b.projectId;
                    return aSortId - bSortId;
                  });
                }
                var setValue = null;
                var exactMatches = potentialIdentities.filter(function (identity) {
                  return (
                    identity.projectId === selectedProject.id &&
                    anyMatch(enteredNames, separateExternalNames(identity.externalName))
                  );
                });
                if (exactMatches.length === 1) {
                  setValue = exactMatches[0].alias + " -- " + exactMatches[0].externalName;
                } else {
                  var firstReceiptLabel = "First Receipt (" + selectedProject[label] + ")";
                  potentialIdentities.unshift({
                    id: null,
                    label: firstReceiptLabel,
                  });
                  setValue = firstReceiptLabel;
                }
                api.updateField(rowIndex, "identityId", {
                  source: potentialIdentities,
                  value: setValue,
                  formatter: potentialIdentities.length > 1 ? "multipleOptions" : null,
                });
              })
              .fail(function (response, textStatus, serverStatus) {
                var error = JSON.parse(response.responseText);
                api.showError(error.detail);
              });
          },
        },
        {
          title: "Identity Alias",
          type: "dropdown",
          data: "identityId",
          include: show["Identity"] && targetCategory !== "Identity",
          includeSaved: false,
          required: true,
          source: [],
          getItemLabel: function (item) {
            return item.label || item.alias + " -- " + item.externalName;
          },
          getItemValue: Utils.array.getId,
          description:
            "A yellow background indicates that multiple identities correspond to the external name.",
          onChange: function (rowIndex, newValue, api) {
            var formatter = null;
            var source = api.getSourceData(rowIndex, "identityId");
            if (source && source.length) {
              var identity = source.find(function (item) {
                return newValue === item.alias + " -- " + item.externalName;
              });
              if (identity) {
                var existingExternalNames = identity.externalName
                  .toLowerCase()
                  .split(",")
                  .map(function (name) {
                    return name.trim();
                  });
                var newExternalNames = api
                  .getValue(rowIndex, "externalName")
                  .toLowerCase()
                  .split(",")
                  .map(function (name) {
                    return name.trim();
                  });
                if (
                  !newExternalNames.every(function (externalName) {
                    return existingExternalNames.indexOf(externalName) != -1;
                  })
                ) {
                  formatter = "notification";
                }
              }
            }
            api.updateField(rowIndex, "externalName", {
              formatter: formatter,
            });
          },
        },
        {
          title: "Donor Sex",
          type: "dropdown",
          data: "donorSex",
          include: show["Identity"],
          includeSaved: targetCategory === "Identity",
          required: true,
          source: Constants.donorSexes,
          initial: "Unknown",
        },
        {
          title: "Consent",
          type: "dropdown",
          data: "consentLevel",
          include: show["Identity"],
          includeSaved: targetCategory === "Identity",
          required: true,
          source: Constants.consentLevels,
          initial: "This Project",
        },
        {
          title: "Piece Type",
          type: "dropdown",
          data: "tissuePieceTypeId",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Tissue Piece",
          required: true,
          source: Constants.tissuePieceTypes,
          sortSource: Utils.sorting.standardSort("name"),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
        }
      );

      if (
        !Constants.isDetailedSample ||
        (targetCategory !== "Identity" && targetCategory !== "Tissue")
      ) {
        columns.push(BulkUtils.columns.sop(config.sops));
      }

      if (Constants.isDetailedSample && !config.isLibraryReceipt) {
        var showEffective = targetCategory !== "Identity" && config.pageMode === "edit";
        columns = columns.concat(
          BulkUtils.columns.groupId(showEffective, function (rowIndex) {
            return originalEffectiveGroupIdsByRow[rowIndex];
          })
        );
      }

      columns.push(
        BulkUtils.columns.creationDate(
          Constants.isDetailedSample && !config.isLibraryReceipt,
          config.pageMode == "propagate",
          "sample"
        ),
        {
          title: "Tissue Origin",
          type: "dropdown",
          data: "tissueOriginId",
          include: show["Tissue"],
          includeSaved: targetCategory === "Tissue",
          required: true,
          source: Constants.tissueOrigins,
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.getId,
        },
        {
          title: "Tissue Type",
          type: "dropdown",
          data: "tissueTypeId",
          include: show["Tissue"],
          includeSaved: targetCategory === "Tissue",
          required: true,
          source: Constants.tissueTypes,
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.getId,
        },
        {
          title: "Passage #",
          type: "int",
          data: "passageNumber",
          include: show["Tissue"],
          includeSaved: targetCategory === "Tissue",
          min: 1,
        },
        {
          title: "Times Received",
          type: "int",
          data: "timesReceived",
          include: show["Tissue"],
          includeSaved: targetCategory === "Tissue",
          min: 1,
          max: 1000000000,
        },
        {
          title: "Tube Number",
          type: "int",
          data: "tubeNumber",
          include: show["Tissue"],
          includeSaved: targetCategory === "Tissue",
          min: 1,
          max: 1000000000,
        },
        {
          title: "Lab",
          type: "dropdown",
          data: "labId",
          include: showLabColumn,
          source: Constants.labs,
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          description:
            "The external lab that a tissue came from. This field is intended for historical data only as the lab should " +
            "normally be recorded in a receipt transfer instead",
        },
        {
          title: "Secondary ID",
          type: "text",
          data: "secondaryIdentifier",
          include: show["Tissue"] && !config.isLibraryReceipt,
          includeSaved: targetCategory === "Tissue",
          maxLength: 255,
          description: "Identifier for the tissue sample in an external system",
        },
        {
          title: "Material",
          type: "dropdown",
          data: "tissueMaterialId",
          include: show["Tissue"],
          includeSaved: targetCategory === "Tissue",
          source: Constants.tissueMaterials,
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
        },
        {
          title: "Region",
          type: "text",
          data: "region",
          include: show["Tissue"],
          includeSaved: targetCategory === "Tissue",
          maxLength: 255,
        },
        {
          title: "Timepoint",
          type: "text",
          data: "timepoint",
          include: show["Tissue"],
          includeSaved: targetCategory === "Tissue",
          maxLength: 50,
          description: "When the sample was taken",
        },
        {
          title: "Initial Slides",
          type: "int",
          data: "initialSlides",
          include: config.pageMode === "edit" && targetCategory === "Tissue Processing",
          sampleSubcategory: "Slide",
          required: true,
          min: 0,
        },
        {
          title: "Slides",
          type: "int",
          data: "slides",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Slide",
          required: true,
          min: 0,
        },
        {
          title: "Thickness",
          type: "int",
          data: "thickness",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Slide",
          min: 1,
        },
        {
          title: "Stain",
          type: "dropdown",
          data: "stainId",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Slide",
          source: Constants.stains,
          sortSource: Utils.sorting.standardSort("name"),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
        },
        {
          title: "% Tumour",
          type: "decimal",
          data: "percentTumour",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Slide",
          precision: 11,
          scale: 8,
          min: 0,
          max: 100,
        },
        {
          title: "% Necrosis",
          type: "decimal",
          data: "percentNecrosis",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Slide",
          precision: 11,
          scale: 8,
          min: 0,
          max: 100,
        },
        {
          title: "Marked Area (mm²)",
          type: "decimal",
          data: "markedArea",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Slide",
          precision: 11,
          scale: 8,
          min: 0,
        },
        {
          title: "Marked Area % Tumour",
          type: "decimal",
          data: "markedAreaPercentTumour",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Slide",
          precision: 11,
          scale: 8,
          min: 0,
          max: 100,
        },
        {
          title: "Slides Consumed",
          type: "int",
          data: "slidesConsumed",
          include: targetCategory === "Tissue Processing",
          sampleSubcategory: "Tissue Piece",
          required: true,
          min: 0,
        },
        {
          title: "Slides Consumed",
          type: "int",
          data: "slidesConsumed",
          include: config.pageMode !== "create" && targetCategory === "Stock",
          min: 0,
        },
        {
          title: "Initial Cell Conc.",
          type: "decimal",
          data: "initialCellConcentration",
          include: show["Tissue Processing"],
          sampleSubcategory: ["Single Cell", "Single Cell (stock)", "Single Cell (aliquot)"],
          includeSaved: targetCategory === "Tissue Processing",
          precision: 14,
          scale: 10,
          description: "Initial concentration of cells in the sample at the time of receipt",
        },
        {
          title: "Target Cell Recovery",
          type: "int",
          data: "targetCellRecovery",
          min: 0,
          include: show["Tissue Processing"],
          sampleSubcategory: ["Single Cell", "Single Cell (stock)", "Single Cell (aliquot)"],
          includeSaved: targetCategory === "Tissue Processing",
        },
        {
          title: "Loading Cell Conc.",
          type: "decimal",
          data: "loadingCellConcentration",
          include: show["Tissue Processing"],
          sampleSubcategory: ["Single Cell", "Single Cell (stock)", "Single Cell (aliquot)"],
          includeSaved: targetCategory === "Tissue Processing",
          precision: 14,
          scale: 10,
          description: "Concentration of cells prepared for loading into the instrument",
        },
        {
          title: "Digestion",
          type: "text",
          data: "digestion",
          include: show["Tissue Processing"],
          sampleSubcategory: ["Single Cell", "Single Cell (stock)", "Single Cell (aliquot)"],
          includeSaved: targetCategory === "Tissue Processing",
          required: true,
        },
        {
          title: "STR Status",
          type: "dropdown",
          data: "strStatus",
          include: show["Stock"] && !config.isLibraryReceipt,
          includeSaved: targetCategory === "Stock",
          required: true,
          source: Constants.strStatuses,
          initial: "Not Submitted",
          description: "Status of short tandem repeat analysis",
        },
        {
          title: "DNAse",
          type: "dropdown",
          data: "dnaseTreated",
          include: show["Stock"],
          sampleSubcategory: ["RNA (stock)", "RNA (aliquot)"],
          includeSaved: targetCategory === "Stock",
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
          initial: "True",
        }
      );

      if (
        (!Constants.isDetailedSample ||
          show["Stock"] ||
          show["Aliquot"] ||
          show["Tissue Processing"]) &&
        !config.isLibraryReceipt
      ) {
        columns = columns.concat(BulkUtils.columns.volume(true, config));
        if (Constants.isDetailedSample && targetCategory !== "Tissue Processing") {
          columns = columns.concat(BulkUtils.columns.parentUsed);
        }
        columns = columns.concat(BulkUtils.columns.concentration());
      }

      columns.push(
        {
          title: "Target Cell Recovery",
          type: "int",
          data: "targetCellRecovery",
          min: 0,
          include: show["Stock"],
          sampleSubcategory: ["Single Cell (stock)", "Single Cell (aliquot)"],
          includeSaved: targetCategory === "Stock",
        },
        {
          title: "Cell Viability",
          type: "decimal",
          data: "cellViability",
          include: show["Stock"],
          sampleSubcategory: ["Single Cell (stock)", "Single Cell (aliquot)"],
          includeSaved: targetCategory === "Stock",
          precision: 14,
          scale: 10,
        },
        {
          title: "Loading Cell Conc.",
          type: "decimal",
          data: "loadingCellConcentration",
          include: show["Stock"],
          sampleSubcategory: ["Single Cell (stock)", "Single Cell (aliquot)"],
          includeSaved: targetCategory === "Stock",
          precision: 14,
          scale: 10,
        },
        referenceSlideColumn(
          config.pageMode !== "create" && targetCategory === "Tissue Processing",
          "Tissue Piece"
        ),
        referenceSlideColumn(config.pageMode !== "create" && targetCategory === "Stock")
      );

      if (!config.isLibraryReceipt) {
        columns = columns.concat(BulkUtils.columns.detailedQcStatus());
      }

      columns.push(
        {
          title: "Purpose",
          type: "dropdown",
          data: "samplePurposeId",
          include: show["Aliquot"] && !config.isLibraryReceipt,
          includeSaved: targetCategory === "Aliquot",
          source: function (sample, api) {
            return Constants.samplePurposes.filter(function (item) {
              return !item.archived || sample.samplePurposeId === item.id;
            });
          },
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
        },
        {
          title: "Input into Library",
          type: "decimal",
          data: "inputIntoLibrary",
          include: show["Aliquot"],
          sampleSubcategory: "Single Cell (aliquot)",
          includeSaved: targetCategory === "Aliquot",
          precision: 14,
          scale: 10,
        }
      );

      if (Constants.isDetailedSample) {
        var filterBySubcategory = function (subcategories) {
          columns = columns.filter(function (column) {
            if (!column.sampleSubcategory) {
              return true;
            } else if (Array.isArray(column.sampleSubcategory)) {
              return column.sampleSubcategory.some(function (columnSubcategory) {
                return subcategories.indexOf(columnSubcategory) !== -1;
              });
            } else {
              return subcategories.indexOf(column.sampleSubcategory) !== -1;
            }
          });
        };

        if (config.targetSampleClass) {
          filterBySubcategory(
            config.targetSampleClass.sampleSubcategory
              ? [config.targetSampleClass.sampleSubcategory]
              : []
          );
        } else if (config.pageMode === "edit") {
          filterBySubcategory(editSubcategories);
        }
      }

      columns.forEach(function (column) {
        if (column.sampleSubcategory) {
          var message =
            "Only applicable to " + Utils.array.list(column.sampleSubcategory) + " sample classes";
          if (column.description) {
            column.description = column.description + "; " + message;
          } else {
            column.description = message;
          }
        }
      });

      return columns;
    },

    confirmSave: function (data, config) {
      var deferred = $.Deferred();
      if (Constants.isDetailedSample) {
        data.forEach(function (sample) {
          var sampleClass = Utils.array.findUniqueOrThrow(
            Utils.array.idPredicate(sample.sampleClassId),
            Constants.sampleClasses
          );
          sample.type = sampleClass.sampleSubcategory || sampleClass.sampleCategory;
        });
      }
      if (config.pageMode === "create") {
        BulkUtils.checkPausedRequisitions(data, deferred);
      } else if (config.pageMode === "edit") {
        showSaveWarnings(data, config, deferred);
      } else {
        deferred.resolve();
      }
      return deferred.promise();
    },
  };

  function showSaveWarnings(data, config, deferred) {
    var changed = data
      .filter(function (sample) {
        return sample.projectId !== originalProjectIdsBySampleId[sample.id];
      })
      .map(function (sample) {
        return {
          sample: sample,
          originalProject: Utils.array.findUniqueOrThrow(
            Utils.array.idPredicate(originalProjectIdsBySampleId[sample.id]),
            config.projects
          ),
          newProject: Utils.array.findUniqueOrThrow(
            Utils.array.idPredicate(sample.projectId),
            config.projects
          ),
        };
      });

    var projectLabel = Constants.isDetailedSample ? "code" : "name";
    var consentWarnings = changed
      .filter(function (change) {
        return (
          change.sample.identityConsentLevel &&
          change.sample.identityConsentLevel !== "All Projects"
        );
      })
      .map(function (change) {
        return (
          "• " +
          (change.sample.alias || change.sample.parentAlias) +
          ": " +
          change.originalProject[projectLabel] +
          " → " +
          change.newProject[projectLabel] +
          "; Consent: " +
          change.sample.identityConsentLevel
        );
      });
    var libraryWarnings = changed
      .filter(function (change) {
        return change.sample.libraryCount > 0;
      })
      .map(function (change) {
        return (
          "• " +
          (change.sample.alias || change.sample.parentAlias) +
          ": " +
          change.originalProject[projectLabel] +
          " → " +
          change.newProject[projectLabel] +
          "; " +
          change.sample.libraryCount +
          " librar" +
          (change.sample.libraryCount > 1 ? "ies" : "y") +
          " affected"
        );
      });
    var messages = [];
    if (consentWarnings.length) {
      messages.push("The following project changes may violate consent:");
      messages = messages.concat(consentWarnings);
    }
    if (libraryWarnings.length) {
      messages.push("The following project changes affect existing libraries:");
      messages = messages.concat(libraryWarnings);
    }
    if (messages.length) {
      messages.push("Are you sure you wish to save?");
      Utils.showConfirmDialog(
        "Project Changes",
        "Save",
        messages,
        deferred.resolve,
        deferred.reject
      );
    } else {
      deferred.resolve();
    }
  }

  function getSampleCategory(sample) {
    if (!Constants.isDetailedSample) {
      return "Plain";
    }
    return Constants.sampleClasses.find(function (sampleClass) {
      return sample.sampleClassId == sampleClass.id;
    }).sampleCategory;
  }

  function getSampleClasses(samples) {
    var classIds = Utils.array.deduplicateNumeric(
      samples.map(function (sample) {
        return sample.sampleClassId || -1;
      })
    );
    return Constants.sampleClasses.filter(function (sampleClass) {
      return classIds.indexOf(sampleClass.id) !== -1;
    });
  }

  function getCommonChildCategories(sampleClasses) {
    var childCategoriesPerClass = sampleClasses.map(function (includedClass) {
      return Constants.sampleClasses
        .filter(function (sampleClass) {
          return Constants.sampleValidRelationships.some(function (relationship) {
            return (
              relationship.parentId === includedClass.id &&
              relationship.childId === sampleClass.id &&
              sampleClass.archived === false &&
              relationship.archived === false
            );
          });
        })
        .map(function (sampleClass) {
          return sampleClass.sampleCategory;
        });
    });

    return ["Identity", "Tissue", "Tissue Processing", "Stock", "Aliquot"].filter(function (
      category
    ) {
      return childCategoriesPerClass.every(function (childCategories) {
        return childCategories.indexOf(category) !== -1;
      });
    });
  }

  function referenceSlideColumn(include, sampleSubcategory) {
    var col = {
      title: "Reference Slide",
      type: "dropdown",
      data: "referenceSlideId",
      include: include,
      source: function (data, api) {
        return data.relatedSlides || [];
      },
      sortSource: Utils.sorting.standardSort("id"),
      getItemLabel: function (item) {
        return item.name + " (" + item.alias + ")";
      },
      getItemValue: Utils.array.getId,
      description:
        "Indicates a slide whose attributes such as marked area and % tumour are relevant to this sample." +
        " May be used for calculating extraction input per yield, for example.",
    };
    if (sampleSubcategory) {
      col.sampleSubcategory = sampleSubcategory;
    }
    return col;
  }

  function getSampleClassOptions(targetSampleClass, targetCategory, parentSampleClassId) {
    var options = null;
    if (targetSampleClass) {
      options = [targetSampleClass];
    } else {
      options = Constants.sampleClasses.filter(function (sampleClass) {
        return (
          sampleClass.sampleCategory === targetCategory &&
          !sampleClass.archived &&
          sampleClass.directCreationAllowed
        );
      });
    }
    if (parentSampleClassId) {
      options = filterSampleClassesByParentId(options, parentSampleClassId);
    }
    return options;
  }

  function filterSampleClassesByParentId(sampleClasses, parentSampleClassId) {
    return sampleClasses.filter(function (sampleClass) {
      return Constants.sampleValidRelationships.find(function (relationship) {
        return (
          relationship.parentId === parentSampleClassId && relationship.childId === sampleClass.id
        );
      });
    });
  }

  function separateExternalNames(names) {
    if (!names) {
      return [];
    }
    return names
      .split(",")
      .map(function (name) {
        return name.trim();
      })
      .filter(function (name) {
        return !!name;
      });
  }

  function anyMatch(arr1, arr2) {
    return arr1.some(function (x) {
      return arr2.includes(x);
    });
  }
})(jQuery);
