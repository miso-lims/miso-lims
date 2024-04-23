BulkTarget = window.BulkTarget || {};
BulkTarget.pool = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   * }
   */

  var customPoolNotes =
    "Switch between Pool and Library Aliquot views using the 'Choose Library Aliquots'/'Edit Pools' buttons";

  // used for custom pooling - table is rebuilt when switching between aliquot/pool views
  var aliquots = null;
  var pools = null;

  var aliquotPoolTarget = {
    getDescription: function (config) {
      return customPoolNotes;
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Library Aliquot Name",
          type: "text",
          disabled: true,
          data: "name",
        },
        {
          title: "Alias",
          type: "text",
          disabled: true,
          data: "alias",
        },
        {
          title: "Box Alias",
          type: "text",
          disabled: true,
          data: "box.alias",
        },
        {
          title: "Position",
          type: "text",
          disabled: true,
          data: "boxPosition",
        },
        {
          title: "Size (bp)",
          type: "int",
          disabled: true,
          data: "dnaSize",
        },
        {
          title: "Pool",
          type: "dropdown",
          data: "pool",
          required: true,
          source: function () {
            return !pools
              ? []
              : pools
                  .filter(function (pool) {
                    return pool.alias;
                  })
                  .map(function (pool) {
                    return pool.alias;
                  });
          },
        },
      ];
    },
    getCustomActions: function (config, api) {
      return [
        {
          name: "Edit Pools",
          action: function () {
            switchToPoolsTable(api, config);
          },
        },
      ];
    },
    confirmSave: function (data, config, api) {
      var deferred = $.Deferred();
      deferred.reject(true);

      // wait a second to allow async HandsOnTable stuff to run
      window.setTimeout(function () {
        switchToPoolsTable(api, config);
        $("#save").click();
      }, 1000);

      return deferred.promise();
    },
  };

  return {
    getSaveUrl: function () {
      return Urls.rest.pools.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.pools.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("pools");
    },
    getDescription: function (config) {
      return config.aliquotsToPool ? customPoolNotes : null;
    },
    getBulkActions: function (config) {
      return [
        BulkUtils.actions.edit(Urls.ui.pools.bulkEdit),
        {
          name: "Create Orders",
          excludeOnOrders: true,
          action: function (pools) {
            Utils.page.post(Urls.ui.sequencingOrders.bulkCreate, {
              poolIds: pools.map(Utils.array.getId).join(","),
            });
          },
        },
        BulkUtils.actions.print("pool"),
        BulkUtils.actions.download(
          Urls.rest.pools.spreadsheet,
          Constants.poolSpreadsheets,
          function (pools, spreadsheet) {
            var errors = [];
            return errors;
          }
        ),
        BulkUtils.actions.download(
          Urls.rest.pools.contentsSpreadsheet,
          Constants.libraryAliquotSpreadsheets,
          function (aliquots, spreadsheet) {
            var errors = [];
            return errors;
          },
          "Download Contents"
        ),
        {
          name: "Create Samplesheet",
          action: createSamplesheet,
        },
        BulkUtils.actions.parents(
          Urls.rest.pools.parents,
          BulkUtils.relations
            .categoriesForDetailed()
            .concat([BulkUtils.relations.library(), BulkUtils.relations.libraryAliquot()])
        ),
        BulkUtils.actions.children(Urls.rest.pools.children, [BulkUtils.relations.run()]),
      ]
        .concat(BulkUtils.actions.qc("Pool"))
        .concat([
          {
            name: "Attach Files",
            action: function (items) {
              if (items.length === 1) {
                ListTarget.attachment.showUploadDialog("pool", items[0].id, false);
              } else {
                var ids = items.map(Utils.array.getId).join(",");
                ListTarget.attachment.showUploadDialog("pool", "shared", false, ids);
              }
            },
          },
          BulkUtils.actions.transfer("poolIds"),
        ]);
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.name,
        BulkUtils.columns.simpleAlias(255),
        BulkUtils.columns.description,
      ]
        .concat(BulkUtils.columns.boxable(config, api))
        .concat([
          {
            title: "Creation Date",
            type: "date",
            data: "creationDate",
            required: true,
            initial: Utils.getCurrentDate(),
          },
          BulkUtils.columns.dnaSize,
        ])
        .concat(BulkUtils.columns.concentration())
        .concat(BulkUtils.columns.volume())
        .concat([
          {
            title: "QC Status",
            type: "dropdown",
            data: "qcPassed",
            source: [
              {
                label: "Ready",
                value: true,
              },
              {
                label: "Failed",
                value: false,
              },
              {
                label: "Not Ready",
                value: null,
              },
            ],
            getItemLabel: Utils.array.get("label"),
            getItemValue: Utils.array.get("value"),
          },
        ]);
    },
    getCustomActions: function (config) {
      var actions = BulkUtils.actions.boxable(false);
      if (config.aliquotsToPool) {
        actions.unshift({
          name: "Choose Library Aliquots",
          action: function (api) {
            switchToAliquotsTable(api, config);
          },
        });
      }
      return actions;
    },
    confirmSave: function (data, config, api) {
      var deferred = $.Deferred();

      if (config.aliquotsToPool) {
        // ensure Pool aliases are unique, so we can sort the aliquots correctly
        pools = data;

        var duplicateAliases = false;
        for (var i = 0; i < pools.length; i++) {
          for (var j = i + 1; j < pools.length; j++) {
            if (pools[i].alias === pools[j].alias) {
              duplicateAliases = true;
            }
          }
        }
        if (duplicateAliases) {
          Utils.showOkDialog("Error", ["There are duplicate Pool aliases"], function () {
            deferred.reject();
          });
          return deferred.promise();
        }

        // sort aliquots and ensure they're all added to pools
        pools.forEach(function (pool) {
          pool.pooledElements = [];
        });

        if (!aliquots) {
          aliquots = config.aliquotsToPool;
        }
        var aliquotPoolMissing = false;
        aliquots.forEach(function (aliquot) {
          if (!aliquot.pool) {
            aliquotPoolMissing = true;
            return deferred.promise();
          }
          var pool = Utils.array.findFirstOrNull(function (pool) {
            return pool.alias === aliquot.pool;
          }, pools);
          if (!pool) {
            aliquotPoolMissing = true;
          } else {
            pool.pooledElements.push(aliquot);
          }
        });
        if (aliquotPoolMissing) {
          Utils.showOkDialog("Error", ["All aliquots must be added to pools"], function () {
            switchToAliquotsTable(api, config);
            deferred.reject();
          });
          return deferred.promise();
        }

        // check that all pools have aliquots in them
        var empties = pools.filter(function (pool) {
          return !pool.pooledElements || !pool.pooledElements.length;
        });
        if (empties.length) {
          Utils.showOkDialog(
            "Error",
            [
              empties.length > 1
                ? "There are empty pools"
                : "Pool '" + empties[0].alias + "' is empty",
            ],
            function () {
              switchToAliquotsTable(api, config);
              deferred.reject();
            }
          );
          return deferred.promise();
        }
      }

      var missingBarcodesCount = data.filter(function (item) {
        return !item.identificationBarcode;
      }).length;
      if (config.pageMode === "edit" || Constants.automaticBarcodes || !missingBarcodesCount) {
        deferred.resolve();
      } else {
        Utils.showConfirmDialog(
          "Missing Barcodes",
          "Save",
          [
            "Pools should usually have barcodes. Are you sure you wish to save " +
              missingBarcodesCount +
              (missingBarcodesCount > 1 ? " pools without barcodes" : " pool without one") +
              "?",
          ],
          function () {
            deferred.resolve();
          },
          function () {
            deferred.reject();
          }
        );
      }
      return deferred.promise();
    },
  };

  function createSamplesheet(pools) {
    var platformTypes = Utils.array.deduplicateString(
      pools.map(function (pool) {
        return pool.platformType;
      })
    );
    if (platformTypes.length > 1) {
      Utils.showOkDialog("Error", [
        "Cannot create a sample sheet from pools for different platforms.",
      ]);
      return;
    }
    if (platformTypes[0] != "ILLUMINA") {
      Utils.showOkDialog("Error", ["Can only create sample sheets for Illumina sequencers."]);
      return;
    }
    var instrumentModels = Constants.instrumentModels.filter(function (model) {
      return (
        model.instrumentType == "SEQUENCER" &&
        model.platformType == platformTypes[0] &&
        model.active
      );
    });
    if (instrumentModels.length == 0) {
      Utils.showOkDialog("Error", [
        "No instruments are available for these pools.",
        "Please add a sequencer first.",
      ]);
      return;
    }
    function showCreateDialog(modelId) {
      Utils.showDialog(
        "Create Samplesheet",
        "Download",
        [
          {
            property: "experimentType",
            label: "Type",
            required: true,
            type: "select",
            getLabel: function (type) {
              return type.description;
            },
            values: Constants.illuminaExperimentTypes,
          },
          {
            property: "sequencingParameters",
            label: "Sequencing Parameters",
            required: true,
            type: "select",
            getLabel: Utils.array.getName,
            values: Constants.sequencingParameters.filter(function (param) {
              return param.instrumentModelId == modelId;
            }),
          },
          {
            property: "genomeFolder",
            type: "text",
            label: "Genome Folder",
            value: Constants.genomeFolder,
            required: true,
          },
          {
            property: "customRead1Primer",
            type: "text",
            label: "Custom Read 1 Primer Well",
            required: false,
          },
          {
            property: "customIndexPrimer",
            type: "text",
            label: "Custom Index Primer Well",
            required: false,
          },
          {
            property: "customRead2Primer",
            type: "text",
            label: "Custom Read 2 Primer Well",
            required: false,
          },
          {
            property: "pools",
            label: "Lanes Configuration",
            required: true,
            type: "order",
            getLabel: Utils.array.getAlias,
            values: pools,
          },
        ],
        function (result) {
          Utils.ajaxDownloadWithDialog(Urls.rest.pools.samplesheet, {
            customRead1Primer: result.customRead1Primer,
            customIndexPrimer: result.customIndexPrimer,
            customRead2Primer: result.customRead2Primer,
            experimentType: result.experimentType.name,
            genomeFolder: result.genomeFolder,
            sequencingParametersId: result.sequencingParameters.id,
            poolIds: result.pools.map(Utils.array.getId),
          });
        },
        null
      );
    }
    Utils.showWizardDialog(
      "Create Samplesheet",
      Constants.instrumentModels
        .filter(function (p) {
          return (
            p.platformType === platformTypes[0] && p.instrumentType === "SEQUENCER" && p.active
          );
        })
        .sort(Utils.sorting.standardSort("alias"))
        .map(function (instrumentModel) {
          return {
            name: instrumentModel.alias,
            handler: function () {
              showCreateDialog(instrumentModel.id);
            },
          };
        })
    );
  }

  function switchToPoolsTable(api, config) {
    aliquots = api.getData();
    api.rebuildTable(BulkTarget.pool, pools);
  }

  function switchToAliquotsTable(api, config) {
    pools = api.getData();
    if (!aliquots) {
      aliquots = config.aliquotsToPool;
    }
    api.rebuildTable(aliquotPoolTarget, aliquots);
  }
})(jQuery);
