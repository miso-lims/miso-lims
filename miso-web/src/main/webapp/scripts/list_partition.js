ListTarget.partition = {
  name: "Partition",
  createUrl: function (config, projectId) {
    throw new Error("Can only be created statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    if (!config.showPool) {
      return [];
    }
    var maxAliquots = 5;
    var platformType = Utils.array.findFirstOrNull(function (pt) {
      return pt.name == config.platformType;
    }, Constants.platformTypes);

    var showPoolTiles = function (response, setConcentrationCallback, backHandler) {
      var dialogArea = document.getElementById("dialog");
      while (dialogArea.hasChildNodes()) {
        dialogArea.removeChild(dialogArea.lastChild);
      }

      response.errors.forEach(function (errorMessage) {
        var errorLine = document.createElement("P");
        errorLine.setAttribute("class", "parsley-error");
        errorLine.innerText = "... " + (response.numMatches - response.items.length) + " more";
        dialogArea.appendChild(errorLine);
      });

      response.items.forEach(function (item) {
        var aliquotInfo = item.pool.pooledElements
          .filter(function (element, index, array) {
            return array.length < maxAliquots || index < maxAliquots - 1;
          })
          .map(function (aliquot) {
            return aliquot.name + " (" + aliquot.alias + ")";
          });
        if (item.pool.pooledElements.length >= maxAliquots) {
          aliquotInfo.push(
            "...and " + (item.pool.pooledElements.length - maxAliquots + 1) + " more aliquots"
          );
        }

        var orderInfo = item.orders
          .filter(function (order) {
            return order.remaining > 0;
          })
          .map(function (order) {
            return (
              order.parameters.instrumentModelAlias +
              " " +
              order.parameters.name +
              ": " +
              order.remaining +
              " " +
              (order.remaining == 1
                ? platformType.partitionName
                : platformType.pluralPartitionName) +
              " remaining"
            );
          });

        var tileParts = [
          Tile.titleAndStatus(
            item.pool.name + " (" + item.pool.alias + ")",
            Warning.hasTileWarnings(WarningTarget.pool, item.pool)
              ? Tile.statusBad("Warning")
              : null
          ),
        ].concat(Warning.generateTileWarnings(WarningTarget.pool, item.pool));
        tileParts.push(Tile.lines(aliquotInfo, false));
        tileParts.push(Tile.lines(orderInfo, true));

        dialogArea.appendChild(
          Tile.make(tileParts, function () {
            dialog.dialog("close");
            setConcentrationCallback(item.pool, function () {
              showPoolTiles(response, setConcentrationCallback, backHandler);
            });
            return false;
          })
        );
      });
      if (response.numMatches > response.items.length) {
        var moreMatches = document.createElement("P");
        moreMatches.innerText =
          "...and " + (response.numMatches - response.items.length) + " more pools not shown";
        dialogArea.appendChild(moreMatches);
      }
      if (response.items.length == 0) {
        var noMatches = document.createElement("P");
        noMatches.innerText = "No pools found.";
        dialogArea.appendChild(noMatches);
      }

      var dialog = jQuery("#dialog").dialog({
        autoOpen: true,
        height: 500,
        width: 600,
        title: "Select Pool",
        modal: true,
        buttons: {
          Back: {
            id: "back",
            text: "Back",
            click: function () {
              dialog.dialog("close");
              backHandler();
            },
          },
          Cancel: {
            id: "cancel",
            text: "Cancel",
            click: function () {
              dialog.dialog("close");
            },
          },
        },
      });
    };

    var assignFromRest = function (url, name, setConcentrationCallback, backHandler) {
      var handler = function () {
        Utils.ajaxWithDialog("Getting Pools", "GET", url, null, function (response) {
          showPoolTiles(response, setConcentrationCallback, backHandler);
        });
      };
      return {
        name: name,
        handler: handler,
      };
    };

    var actions = [
      {
        name: "Assign Pool",
        action: function (partitions) {
          var assign = function (pool, concentration, units) {
            var doAssign = function () {
              var data = {
                partitionIds: partitions.map(Utils.array.getId),
              };
              if (concentration) {
                data.concentration = concentration;
                data.units = units;
              }
              Utils.ajaxWithDialog(
                "Assigning Pool",
                "POST",
                Urls.rest.pools.assign(pool ? pool.id : 0),
                data,
                Utils.page.pageReload
              );
            };
            if (pool) {
              Utils.warnIfConsentRevoked(
                pool.pooledElements,
                function () {
                  doAssign();
                },
                function (item) {
                  return item.name + " (" + item.alias + ")";
                }
              );
            } else {
              doAssign();
            }
          };
          var setConcentration = function (pool, backHandler) {
            Utils.showDialog(
              "Set Loading Concentration",
              "OK",
              [
                {
                  label: "Loading Concentration",
                  property: "loadingConcentration",
                  type: "float",
                  value: "",
                },
                {
                  label: "Units",
                  property: "loadingConcentrationUnits",
                  type: "select",
                  value: "nM",
                  values: Constants.concentrationUnits,
                  getLabel: function (concentrationUnit) {
                    return concentrationUnit.units.replace("&#181;", "µ");
                  },
                },
              ],
              function (output) {
                var conc = output.loadingConcentration;
                var units;
                if (conc) {
                  units = output.loadingConcentrationUnits.name;
                  assign(pool, conc, units);
                } else {
                  assign(pool, null, null);
                }
              },
              backHandler
            );
          };
          var makeSearch = function (defaultQuery, backHandler) {
            return function () {
              Utils.showDialog(
                "Search for Pool to Assign",
                "Search",
                [
                  {
                    type: "text",
                    label: "Search",
                    property: "query",
                    value: defaultQuery,
                  },
                ],
                function (results) {
                  Utils.ajaxWithDialog(
                    "Getting Pools",
                    "GET",
                    Urls.rest.pools.picker.search +
                      "?" +
                      Utils.page.param({
                        platform: platformType.name,
                        query: results.query,
                      }),
                    null,
                    function (response) {
                      showPoolTiles(
                        response,
                        setConcentration,
                        makeSearch(results.query, backHandler)
                      );
                    }
                  );
                },
                backHandler
              );
            };
          };

          var assignActions;
          var assignDialog = function () {
            Utils.showWizardDialog("Assign Pool", assignActions);
          };

          assignActions = [
            {
              name: "No Pool",
              handler: function () {
                assign(0);
              },
            },
            {
              name: "Search",
              handler: makeSearch("", assignDialog),
            },
            config.sequencingParametersId
              ? assignFromRest(
                  Urls.rest.sequencingOrders.picker.chemistry +
                    "?" +
                    Utils.page.param({
                      platform: platformType.name,
                      seqParamsId: config.sequencingParametersId,
                      fulfilled: false,
                    }),
                  "Outstanding Orders (Matched Chemistry)",
                  setConcentration,
                  assignDialog
                )
              : null,
            assignFromRest(
              Urls.rest.sequencingOrders.picker.active +
                "?" +
                Utils.page.param({
                  platform: platformType.name,
                }),
              "Outstanding Orders (All)",
              setConcentration,
              assignDialog
            ),
            assignFromRest(
              Urls.rest.pools.picker.recent +
                "?" +
                Utils.page.param({
                  platform: platformType.name,
                }),
              "Recently Modified",
              setConcentration,
              assignDialog
            ),
          ].filter(function (x) {
            return x;
          });
          assignDialog();
        },
      },
    ];
    if (config.runId) {
      actions.push({
        name: "Set QC",
        action: function (partitions) {
          var setQc = function (id, notes) {
            Utils.ajaxWithDialog(
              "Setting QC",
              "POST",
              Urls.rest.runs.setPartitionQcs(config.runId),
              {
                partitionIds: partitions.map(Utils.array.getId),
                qcTypeId: id,
                notes: notes,
              },
              Utils.page.pageReload
            );
          };

          Utils.showWizardDialog(
            "Set QC",
            Constants.partitionQcTypes.map(function (qcType) {
              return {
                name: qcType.detailedLabel,
                handler: function () {
                  if (qcType.noteRequired) {
                    Utils.showDialog(
                      qcType.description + " Notes",
                      "Set",
                      [
                        {
                          type: "text",
                          label: "Notes",
                          property: "notes",
                        },
                      ],
                      function (results) {
                        setQc(qcType.id, results.notes);
                      }
                    );
                  } else {
                    setQc(qcType.id, null);
                  }
                },
              };
            })
          );
        },
      });
      actions.push({
        name: "Set Purpose",
        action: function (partitions) {
          Utils.showWizardDialog(
            "Set Purpose",
            Constants.runPurposes.sort(Utils.sorting.standardSort("alias")).map(function (purpose) {
              return {
                name: purpose.alias,
                handler: function () {
                  Utils.ajaxWithDialog(
                    "Setting Purpose",
                    "PUT",
                    Urls.rest.runs.setPartitionPurposes(config.runId),
                    {
                      partitionIds: partitions.map(Utils.array.getId),
                      runPurposeId: purpose.id,
                    },
                    Utils.page.pageReload
                  );
                },
              };
            })
          );
        },
      });
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return [];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.labelHyperlinkColumn(
        "Container",
        Urls.ui.containers.edit,
        function (partition) {
          return partition.containerId;
        },
        "containerName",
        2,
        config.showContainer
      ),
      {
        sTitle: "Number",
        mData: "partitionNumber",
        include: true,
        iSortPriority: 1,
        bSortDirection: true,
      },
      {
        sTitle: "Pool",
        mData: function (full) {
          return full.pool ? full.pool.name + " (" + full.pool.alias + ")" : "(none)";
        },
        include: config.showPool,
        iSortPriority: 0,
        mRender: Warning.tableWarningRenderer(WarningTarget.partition, function (full) {
          return full.pool ? "/miso/pool/" + full.pool.id : null;
        }),
      },
      {
        sTitle: "Library Aliquots",
        mData: "pool",
        include: true,
        iSortPriority: 0,
        bSortable: false,
        mRender: function (data, type, full) {
          if (data) {
            return data.libraryAliquotCount;
          } else {
            if (type === "display") {
              return "(None)";
            } else {
              return "";
            }
          }
        },
      },
      {
        sTitle: "Loading Conc.",
        mData: "loadingConcentration",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (type === "display" && !!data) {
            var units = Constants.concentrationUnits.find(function (unit) {
              return unit.name == full.loadingConcentrationUnits;
            });
            if (!!units) {
              return data + " " + units.units;
            }
          }
          return data;
        },
      },
      {
        sTitle: "QC Status",
        mData: "qcType",
        include: config.runId,
        iSortPriority: 0,
        mRender: ListUtils.render.textFromId(
          Constants.partitionQcTypes,
          "detailedLabel",
          "(Unset)"
        ),
      },
      {
        sTitle: "QC Note",
        mData: "qcNotes",
        iSortPriority: 0,
        include: config.runId,
      },
      {
        sTitle: "Purpose",
        mData: "runPurposeId",
        include: config.runId,
        iSortPriority: 0,
        mRender: ListUtils.render.textFromId(Constants.runPurposes, "alias"),
      },
    ];
  },
};
