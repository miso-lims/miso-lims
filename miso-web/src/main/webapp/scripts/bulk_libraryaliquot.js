BulkTarget = window.BulkTarget || {};
BulkTarget.libraryaliquot = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {propagate, edit}
   *   box: optional new box created to put items in
   *   defaultTargetedSequencingByProject
   * }
   */

  var originalDataByRow = {};
  var parentVolumesByRow = {};
  var parentLocationsByRow = null;

  return {
    getSaveUrl: function () {
      return Urls.rest.libraryAliquots.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.libraryAliquots.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("library_aliquots");
    },
    getCustomActions: function (config) {
      var actions = BulkUtils.actions.boxable(
        config.pageMode === "propagate",
        parentLocationsByRow
      );
      actions.push({
        name: "View Metrics",
        action: function (api) {
          BulkUtils.actions.viewMetrics(
            api,
            ["LIBRARY_PREP", "LIBRARY_QUALIFICATION"],
            "Select category to view metrics." +
              " Relevant category depends on the assay test that this library aliquot will be used for. Sequencing" +
              " metrics may be included.",
            config.pageMode
          );
        },
      });
      return actions;
    },
    getBulkActions: function (config) {
      var editAction = BulkUtils.actions.edit(Urls.ui.libraryAliquots.bulkEdit);
      editAction.allowOnLibraryPage = true;

      return [
        editAction,
        {
          name: "Propagate",
          action: function (items) {
            Utils.warnIfConsentRevoked(
              items,
              function () {
                BulkUtils.actions.showDialogForBoxCreation(
                  "Create Library Aliquots",
                  "Create",
                  [],
                  Urls.ui.libraryAliquots.bulkRepropagate,
                  function (result) {
                    return {
                      ids: items.map(Utils.array.getId).join(","),
                    };
                  },
                  function (result) {
                    return items.length;
                  }
                );
              },
              getLabel
            );
          },
        },
        {
          name: "Create Order",
          action: function (items) {
            Utils.warnIfConsentRevoked(
              items,
              function () {
                window.location =
                  Urls.ui.poolOrders.create +
                  "?" +
                  Utils.page.param({
                    aliquotIds: items.map(Utils.array.getId).join(","),
                  });
              },
              getLabel
            );
          },
        },
        {
          name: "Pool Together",
          title: "Create one pool from many library aliquots",
          action: function (items) {
            Utils.warnIfConsentRevoked(
              items,
              function () {
                var fields = [];
                BulkUtils.actions.showDialogForBoxCreation(
                  "Create Pools",
                  "Create",
                  fields,
                  Urls.ui.libraryAliquots.bulkPoolTogether,
                  function (result) {
                    return {
                      ids: items.map(Utils.array.getId).join(","),
                    };
                  },
                  function (result) {
                    return 1;
                  }
                );
              },
              getLabel
            );
          },
          allowOnLibraryPage: false,
        },
        {
          name: "Pool Separately",
          title: "Create a pool for each library aliquot",
          action: function (items) {
            Utils.warnIfConsentRevoked(
              items,
              function () {
                var fields = [];
                BulkUtils.actions.showDialogForBoxCreation(
                  "Create Pools",
                  "Create",
                  fields,
                  Urls.ui.libraryAliquots.bulkPoolSeparate,
                  function (result) {
                    return {
                      ids: items.map(Utils.array.getId).join(","),
                    };
                  },
                  function (result) {
                    return items.length;
                  }
                );
              },
              getLabel
            );
          },
          allowOnLibraryPage: true,
        },
        {
          name: "Pool Custom",
          title: "Divide library aliquots into several pools",
          action: function (items) {
            Utils.warnIfConsentRevoked(
              items,
              function () {
                var fields = [
                  {
                    label: "Quantity",
                    property: "quantity",
                    type: "int",
                  },
                ];
                BulkUtils.actions.showDialogForBoxCreation(
                  "Create Pools",
                  "Create",
                  fields,
                  Urls.ui.libraryAliquots.bulkPoolCustom,
                  function (result) {
                    return {
                      ids: items.map(Utils.array.getId).join(","),
                      quantity: result.quantity,
                    };
                  },
                  function (result) {
                    return result.quantity;
                  }
                );
              },
              getLabel
            );
          },
          allowOnLibraryPage: true,
        },
        BulkUtils.actions.print("libraryaliquot"),
        BulkUtils.actions.download(
          Urls.rest.libraryAliquots.spreadsheet,
          Constants.libraryAliquotSpreadsheets,
          function (aliquots, spreadsheet) {
            var errors = [];
            return errors;
          }
        ),

        BulkUtils.actions.parents(
          Urls.rest.libraryAliquots.parents,
          BulkUtils.relations.categoriesForDetailed().concat([BulkUtils.relations.library()])
        ),
        BulkUtils.actions.children(Urls.rest.libraryAliquots.children, [
          BulkUtils.relations.pool(),
          BulkUtils.relations.run(),
        ]),
        config.worksetId
          ? BulkUtils.actions.removeFromWorkset(
              "library aliquots",
              Urls.rest.worksets.removeLibraryAliquots(config.worksetId)
            )
          : BulkUtils.actions.addToWorkset(
              "library aliquots",
              "libraryAliquotIds",
              Urls.rest.worksets.addLibraryAliquots
            ),
        BulkUtils.actions.transfer("libraryAliquotIds"),
      ];
    },
    getFixedColumns: function (config) {
      return 2;
    },
    getColumns: function (config, api) {
      var columns = [
        {
          title: "Parent Name",
          type: "text",
          data: "parentName",
          disabled: true,
          include: config.pageMode === "propagate",
        },
        {
          title: "Parent Alias",
          type: "text",
          data: "parentAlias",
          disabled: true,
          getData: function (aliquot) {
            return aliquot.parentAliquotAlias || aliquot.libraryAlias;
          },
          include: config.pageMode === "propagate",
          omit: true,
        },
        {
          title: "Parent Location",
          type: "text",
          data: "parentBoxPositionLabel",
          disabled: true,
          include: config.pageMode === "propagate",
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
        BulkUtils.columns.name,
        BulkUtils.columns.generatedAlias(config),
        {
          title: "Project",
          type: "text",
          disabled: true,
          data: Constants.isDetailedSample ? "projectCode" : "projectName",
        },
        BulkUtils.columns.assay(),
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
            return item.effectiveTissueTypeAlias + " (" + item.effectiveTissueTypeDescription + ")";
          },
          omit: true,
          include: Constants.isDetailedSample,
        },
        BulkUtils.columns.description,
      ];

      columns = columns.concat(BulkUtils.columns.boxable(config, api));
      columns = columns.concat(
        BulkUtils.columns.groupId(true, function (rowIndex) {
          return originalDataByRow[rowIndex].effectiveGroupIds;
        })
      );

      columns.push({
        title: "Design Code",
        type: "dropdown",
        data: "libraryDesignCodeId",
        include: Constants.isDetailedSample,
        source: Constants.libraryDesignCodes,
        getItemLabel: function (item) {
          return item.code;
        },
        getItemValue: Utils.array.getId,
        sortSource: Utils.sorting.standardSort("code"),
        required: true,
        onChange: function (rowIndex, newValue, api) {
          var designCode = Utils.array.findFirstOrNull(function (designCode) {
            return designCode.code === newValue;
          }, Constants.libraryDesignCodes);
          var changes = {
            required: designCode ? designCode.targetedSequencingRequired : false,
          };
          api.updateField(rowIndex, "kitDescriptorId", changes);
          api.updateField(rowIndex, "targetedSequencingId", changes);
        },
      });

      columns = columns.concat(BulkUtils.columns.detailedQcStatus());
      columns.push(BulkUtils.columns.dnaSize);
      columns = columns.concat(BulkUtils.columns.concentration());
      columns = columns.concat(BulkUtils.columns.volume(false, config));
      columns = columns.concat(BulkUtils.columns.parentUsed);

      columns.push(
        BulkUtils.columns.creationDate(true, true, true, "library aliquot"),
        {
          title: "Kit",
          type: "dropdown",
          data: "kitDescriptorId",
          source: function (data) {
            return Constants.kitDescriptors.filter(function (kit) {
              return (
                kit.kitType === "Library" &&
                kit.platformType === data.libraryPlatformType &&
                (!kit.archived || kit.id === data.kitDescriptorId)
              );
            });
          },
          sortSource: Utils.sorting.standardSort("name"),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          onChange: function (rowIndex, newValue, api) {
            api.updateField(rowIndex, "kitLot", {
              disabled: !newValue,
              required: !!newValue,
            });
            var kitDescriptor = !newValue
              ? null
              : Constants.kitDescriptors.find(function (x) {
                  return x.kitType === "Library" && x.name === newValue;
                });
            var tarSeqs =
              !newValue || !kitDescriptor
                ? []
                : Constants.targetedSequencings.filter(function (tarseq) {
                    return (
                      tarseq.kitDescriptorIds.indexOf(kitDescriptor.id) !== -1 &&
                      (!tarseq.archived ||
                        originalDataByRow[rowIndex].targetedSequencingId === tarseq.id)
                    );
                  });
            // select default targeted sequencing for project if valid
            var selectedTarseqAlias = undefined;
            if (
              config.pageMode === "propagate" &&
              !api.getValue(rowIndex, "targetedSequencingId")
            ) {
              var defaultTarseqId =
                config.defaultTargetedSequencingByProject[originalDataByRow[rowIndex].projectId];
              var selectedTarseq = tarSeqs.find(Utils.array.idPredicate(defaultTarseqId));
              if (selectedTarseq) {
                selectedTarseqAlias = selectedTarseq.alias;
              }
            }
            api.updateField(rowIndex, "targetedSequencingId", {
              source: tarSeqs,
              disabled: !tarSeqs.length,
              value: newValue ? selectedTarseqAlias : null,
            });
          },
        },
        {
          title: "Kit Lot",
          type: "text",
          data: "kitLot",
          maxLength: 255,
          required: config.pageMode === "propagate",
          regex: Utils.validation.uriComponentRegex,
        },
        {
          title: "Targeted Sequencing",
          type: "dropdown",
          data: "targetedSequencingId",
          getData: function (aliquot) {
            if (!aliquot.targetedSequencingId) {
              return null;
            }
            var tarSeq = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(aliquot.targetedSequencingId),
              Constants.targetedSequencings
            );
            return tarSeq.alias;
          },
          source: [], // initialized in kitDescriptorId onChange
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
        }
      );
      return columns;
    },
    getDefaultSortFunction: function (config) {
      if (config.pageMode === "propagate") {
        return function (a, b) {
          // on this page, either all rows will have parent aliquots, or no rows will
          if (a.parentAliquotId && b.parentAliquotId) {
            return Utils.sorting.standardSort("parentAliquotId")(a, b);
          } else {
            return Utils.sorting.standardSort("libraryId")(a, b);
          }
        };
      }
      return null;
    },
    prepareData: function (data, config) {
      parentLocationsByRow = {};
      data.forEach(function (aliquot, index) {
        originalDataByRow[index] = {
          effectiveGroupIds: aliquot.effectiveGroupId,
          targetedSequencingId: aliquot.targetedSequencingId,
          projectId: aliquot.projectId,
        };
        // prepare parent volumes for validation in confirmSave
        if (aliquot.parentVolume !== undefined && aliquot.parentVolume !== null) {
          if (aliquot.volumeUsed) {
            parentVolumesByRow[index] = Utils.decimalStrings.add(
              aliquot.parentVolume,
              aliquot.volumeUsed
            );
          } else {
            parentVolumesByRow[index] = aliquot.parentVolume;
          }
        }
        if (aliquot.parentBoxPosition) {
          parentLocationsByRow[index] = aliquot.parentBoxPosition;
        }
      });
    },
    confirmSave: function (data) {
      var deferred = jQuery.Deferred();

      var overused = data.filter(function (aliquot, index) {
        return (
          aliquot.volumeUsed &&
          parentVolumesByRow.hasOwnProperty(index) &&
          Utils.decimalStrings
            .subtract(parentVolumesByRow[index], aliquot.volumeUsed)
            .startsWith("-")
        );
      }).length;

      if (overused) {
        Utils.showConfirmDialog(
          "Not Enough Library Volume",
          "Save",
          [
            "Saving will cause " +
              overused +
              (overused > 1
                ? " libraries to have negative volumes. "
                : " library to have a negative volume. ") +
              "Are you sure you want to proceed?",
          ],
          function () {
            deferred.resolve();
          },
          function () {
            deferred.reject();
          }
        );
      } else {
        deferred.resolve();
      }

      return deferred.promise();
    },
  };

  function getLabel(item) {
    return item.name + " (" + item.alias + ")";
  }
})(jQuery);
