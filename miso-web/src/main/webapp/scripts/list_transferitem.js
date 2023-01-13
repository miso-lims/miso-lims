ListTarget.transferitem = (function () {
  return {
    name: "Items",
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      var actions = [];
      if (config.editSend) {
        actions.push({
          name: "Remove",
          action: function (items) {
            Transfer.removeItems(items);
          },
        });
      }
      if (config.editReceipt) {
        actions.push(
          {
            name: "Set Received",
            action: makeUpdateBooleanHandler("Mark Item Receipt", "Received", "received"),
          },
          {
            name: "Set QC",
            action: makeUpdateBooleanHandler("Mark Item QC", "QC Passed", "qcPassed"),
          },
          {
            name: "Set Location",
            action: function (items) {
              Utils.showDialog(
                "Box Search",
                "Search",
                [
                  {
                    label: "Name, Alias, or Barcode",
                    property: "query",
                    type: "text",
                  },
                ],
                function (results) {
                  if (!results.query) {
                    Utils.showConfirmDialog(
                      "Remove from Box",
                      "Remove",
                      ["Remove selected items from their current box locations?"],
                      function () {
                        items.forEach(function (item) {
                          item.boxId = null;
                          item.boxAlias = null;
                          item.boxPosition = null;
                        });
                        Transfer.updateItems(items);
                      }
                    );
                    return;
                  }
                  queryBoxes(results.query, items);
                }
              );
            },
          },
          {
            name: "Receipt Wizard",
            action: function (items) {
              Utils.showDialog(
                "Receipt Wizard",
                "OK",
                [
                  makeBooleanField("Received", "received"),
                  makeBooleanField("QC Passed", "qcPassed"),
                  {
                    label: "QC Note",
                    property: "qcNote",
                    type: "text",
                  },
                  {
                    label: "Move items to another box",
                    property: "moveItems",
                    type: "checkbox",
                  },
                  {
                    label: "Move box to another freezer",
                    property: "moveBox",
                    type: "checkbox",
                  },
                ],
                function (results) {
                  if (results.qcPassed === false && !results.qcNote) {
                    Utils.showOkDialog("Error", ["QC note is required when QC is failed"]);
                    return;
                  } else if (!new RegExp(Utils.validation.sanitizeRegex).test(results.qcNote)) {
                    Utils.showOkDialog("Error", ["QC note contains invalid characters"]);
                    return;
                  }
                  var applyChanges = function () {
                    items.forEach(function (item) {
                      item.received = results.received;
                      item.qcPassed = results.qcPassed;
                      item.qcNote = results.qcNote;
                    });
                  };
                  if (results.moveItems) {
                    moveItems(items, results.moveBox, applyChanges);
                  } else if (results.moveBox) {
                    moveBox(items, applyChanges);
                  } else {
                    applyChanges();
                    Transfer.updateItems(items);
                  }
                }
              );
            },
          }
        );
      }
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return !config.editSend
        ? []
        : [
            {
              name: "Add",
              handler: function () {
                Utils.showWizardDialog("Add Items", [
                  makeAddHandler("Samples", "Add Samples", Urls.rest.samples.query, "Sample"),
                  makeAddHandler(
                    "Libraries",
                    "Add Libraries",
                    Urls.rest.libraries.query,
                    "Library"
                  ),
                  makeAddHandler(
                    "Library Aliquots",
                    "Add Library Aliquots",
                    Urls.rest.libraryAliquots.query,
                    "Library Aliquot"
                  ),
                  makeAddHandler("Pools", "Add Pools", Urls.rest.pools.query, "Pool"),
                  makeAddHandler("Boxes", "Add Items from Boxes", Urls.rest.boxables.queryByBox),
                ]);
              },
            },
          ];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Type",
          mData: "type",
          include: true,
          iSortPriority: 1,
        },
        {
          sTitle: "Name",
          mData: "id", // For sorting purposes
          mRender: function (data, type, full) {
            if (type === "display") {
              return data ? '<a href="' + getEditUrl(full) + '">' + full.name + "</a>" : "";
            } else {
              return data;
            }
          },
          include: true,
          iSortPriority: 2,
        },
        {
          sTitle: "Alias",
          mData: "alias",
          mRender: function (data, type, full) {
            if (type === "display") {
              return data ? '<a href="' + getEditUrl(full) + '">' + data + "</a>" : "";
            } else {
              return data;
            }
          },
          include: true,
          iSortPriority: 0,
        },
        {
          sTitle: "Received",
          mData: "received",
          mRender: ListUtils.render.booleanChecks,
          include: true,
          iSortPriority: 0,
        },
        {
          sTitle: "Location",
          mData: "boxId",
          mRender: function (data, type, full) {
            if (type === "display") {
              if (data) {
                return (
                  "<a href='" +
                  Urls.ui.boxes.edit(data) +
                  "'>" +
                  full.boxAlias +
                  " " +
                  full.boxPosition +
                  "</a>"
                );
              } else if (full.boxAlias) {
                return full.boxAlias + " " + full.boxPosition;
              } else {
                return "Unknown";
              }
            }
            return data;
          },
          include: true,
          bSortable: false,
        },
        {
          sTitle: "QC Passed",
          mData: "qcPassed",
          mRender: ListUtils.render.booleanChecks,
          include: true,
          iSortPriority: 0,
        },
        {
          sTitle: "QC Note",
          mData: "qcNote",
          include: true,
          iSortPriority: 0,
        },
      ];
    },
  };

  function getEditUrl(item) {
    switch (item.type) {
      case "Sample":
        return Urls.ui.samples.edit(item.id);
      case "Library":
        return Urls.ui.libraries.edit(item.id);
      case "Library Aliquot":
        return Urls.ui.libraryAliquots.edit(item.id);
      case "Pool":
        return Urls.ui.pools.edit(item.id);
      default:
        throw new Error("Unhandled transfer item type: " + item.type);
    }
  }

  function makeAddHandler(handlerName, title, queryUrl, itemType) {
    return {
      name: handlerName,
      handler: function () {
        Utils.showSearchByNamesDialog(title, queryUrl, function (items) {
          var dupes = [];
          Transfer.getItems().forEach(function (transferItem) {
            if (items.map(Utils.array.getId).indexOf(transferItem.id) !== -1) {
              dupes.push(transferItem);
            }
          });
          if (dupes.length) {
            Utils.showOkDialog(
              "Error",
              ["The following items are already included in this transfer:"].concat(
                dupes.map(function (item) {
                  return "* " + item.name + " (" + item.alias + ")";
                })
              )
            );
          } else {
            Transfer.addItems(
              items.map(function (item) {
                itemType = itemType || getEntityTypeLabel(item.entityType);
                return {
                  type: itemType,
                  id: item.id,
                  name: item.name,
                  alias: item.alias,
                  boxId: item.box ? item.box.id : null,
                  boxAlias: item.box ? item.box.alias : null,
                  boxPosition: item.boxPosition,
                  received: null,
                  qcPassed: null,
                  qcNote: null,
                };
              })
            );
          }
        });
      },
    };
  }

  function getEntityTypeLabel(entityType) {
    switch (entityType) {
      case "SAMPLE":
        return "Sample";
      case "LIBRARY":
        return "Library";
      case "LIBRARY_ALIQUOT":
        return "Library Aliquot";
      case "POOL":
        return "Pool";
      default:
        throw new Error("Invalid entity type");
    }
  }

  function makeUpdateBooleanHandler(title, fieldLabel, property, includeQcNote) {
    var isQc = property === "qcPassed";
    return function (items) {
      var fields = [makeBooleanField(fieldLabel, "value")];
      if (isQc) {
        fields.push({
          label: "QC Note",
          property: "qcNote",
          type: "text",
        });
      }
      Utils.showDialog(title, "Update", fields, function (results) {
        if (isQc) {
          if (results.value === false && !results.qcNote) {
            Utils.showOkDialog("Error", ["QC note is required when QC is failed"]);
            return;
          } else if (!new RegExp(Utils.validation.sanitizeRegex).test(results.qcNote)) {
            Utils.showOkDialog("Error", ["QC note contains invalid characters"]);
            return;
          }
        }
        items.forEach(function (item) {
          item[property] = results.value;
          if (isQc) {
            item.qcNote = results.qcNote;
          }
        });
        Transfer.updateItems(items);
      });
    };
  }

  function makeBooleanField(fieldLabel, property) {
    return {
      label: fieldLabel,
      property: property,
      type: "select",
      values: [true, false, null],
      getLabel: function (value) {
        switch (value) {
          case true:
            return "Yes";
          case false:
            return "No";
          case null:
            return "Unknown";
        }
      },
      value: "Yes",
    };
  }

  function moveItems(items, moveBoxToo, callback) {
    Utils.showDialog(
      "Box Search",
      "Search",
      [
        {
          label: "Box Name, Alias, or Barcode",
          property: "query",
          type: "text",
        },
      ],
      function (results) {
        queryBoxes(results.query, items, moveBoxToo, callback);
      }
    );
  }

  function queryBoxes(query, items, moveBoxToo, callback) {
    Utils.ajaxWithDialog(
      "Searching for Boxes",
      "GET",
      Urls.rest.boxes.searchPartial +
        "?" +
        Utils.page.param({
          q: query,
          b: true,
        }),
      null,
      function (boxes) {
        Utils.showWizardDialog(
          "Add to Box",
          boxes.map(function (box) {
            return {
              name: box.name + " - " + box.alias,
              handler: function () {
                Utils.showDialog(
                  "Add to Box",
                  "Add",
                  items.map(function (item) {
                    return {
                      label: item.name + " - " + item.alias,
                      property: item.name + "Position",
                      type: "select",
                      values: [null].concat(Utils.getEmptyBoxPositions(box)),
                      getLabel: function (value) {
                        return value || "n/a";
                      },
                    };
                  }),
                  function (positionResults) {
                    var applyChanges = function () {
                      items.forEach(function (item) {
                        if (positionResults[item.name + "Position"]) {
                          item.boxId = box.id;
                          item.boxAlias = box.alias;
                          item.boxPosition = positionResults[item.name + "Position"];
                        } else {
                          item.boxId = null;
                          item.boxAlias = null;
                          item.boxPosition = null;
                        }
                      });
                      if (callback) {
                        callback();
                      }
                    };
                    if (moveBoxToo) {
                      moveBox(items, applyChanges);
                    } else {
                      applyChanges();
                      Transfer.updateItems(items);
                    }
                  }
                );
              },
            };
          })
        );
      }
    );
  }

  function moveBox(items, callback) {
    Utils.ajaxWithDialog(
      "Finding Storage",
      "GET",
      Urls.rest.storageLocations.freezers,
      null,
      function (freezers) {
        Utils.showDialog(
          "Set Box Location",
          "Next",
          [
            {
              label: "Barcode",
              property: "barcode",
              type: "text",
            },
            {
              label: "or Select",
              property: "freezer",
              type: "select",
              values: freezers,
              getLabel: function (freezer) {
                return freezer.fullDisplayLocation;
              },
            },
          ],
          function (results) {
            if (results.barcode) {
              continueLocationSelect(
                Urls.rest.storageLocations.queryByBarcode +
                  "?" +
                  Utils.page.params({
                    q: results.barcode,
                  }),
                items,
                callback
              );
            } else if (!results.freezer) {
              Utils.showOkDialog("Error", ["Must select a freezer or location barcode"]);
            } else {
              continueLocationSelect(
                Urls.rest.storageLocations.children(results.freezer.id),
                items,
                callback
              );
            }
          }
        );
      }
    );
  }

  function continueLocationSelect(url, items, callback) {
    Utils.ajaxWithDialog("Finding Storage", "GET", url, null, function (locations) {
      if (!locations || (Array.isArray(locations) && !locations.length)) {
        Utils.showOkDialog("Error", ["No storage found"]);
      } else {
        if (!Array.isArray(locations)) {
          locations = [locations];
        }
        locations.sort(Utils.sorting.standardSort("displayLocation"));
        Utils.showWizardDialog(
          "Set Box Location",
          locations.map(function (location) {
            return {
              name: location.displayLocation + (location.availableStorage ? "*" : ""),
              handler: function () {
                if (location.availableStorage) {
                  items.forEach(function (item) {
                    item.newBoxLocationId = location.id;
                  });
                  callback();
                  Transfer.updateItems(items);
                } else {
                  continueLocationSelect(
                    Urls.rest.storageLocations.children(location.id),
                    items,
                    callback
                  );
                }
              },
            };
          })
        );
      }
    });
  }
})();
