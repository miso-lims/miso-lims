ListTarget.library = (function () {
  return {
    name: "Libraries",
    getUserManualUrl: function () {
      return Urls.external.userManual("libraries");
    },
    createUrl: function (config, projectId) {
      if (projectId) {
        return Urls.rest.libraries.projectDatatable(projectId);
      } else if (config.worksetId) {
        return Urls.rest.libraries.worksetDatatable(config.worksetId);
      } else if (config.batchId) {
        return Urls.rest.libraries.batchDatatable(config.batchId);
      } else if (config.requisitionId) {
        switch (config.relation) {
          case "requisitioned":
            return Urls.rest.libraries.requisitionDatatable(config.requisitionId);
          case "supplemental":
            return Urls.rest.libraries.requisitionSupplementalDatatable(config.requisitionId);
          case "indirect":
            return Urls.rest.libraries.requisitionRelatedDatatable(config.requisitionId);
          default:
            throw Error("Unexpected requisition relation: " + config.relation);
        }
      } else if (config.workstationId) {
        return Urls.rest.libraries.workstationDatatable(config.workstationId);
      }
      return Urls.rest.libraries.datatable;
    },
    getQueryUrl: function () {
      return Urls.rest.libraries.query;
    },
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.library.getBulkActions(config);

      if (config.requisitionId) {
        if (config.relation == "supplemental") {
          actions.unshift({
            name: "Remove",
            action: function (items) {
              Utils.ajaxWithDialog(
                "Removing Supplemental Libraries",
                "POST",
                Urls.rest.requisitions.removeSupplementalLibraries(config.requisitionId),
                items.map(Utils.array.getId),
                Utils.page.pageReload
              );
            },
          });
        } else if (config.relation == "requisitioned") {
          actions.unshift(
            {
              name: "Remove",
              action: librariesUpdateFunction(
                Urls.rest.requisitions.removeLibraries(config.requisitionId)
              ),
            },
            ListUtils.createMoveToRequisitionAction(
              config.requisition,
              Urls.rest.requisitions.moveLibraries(config.requisitionId)
            )
          );
        }
      }

      if (config.worksetId) {
        actions.push(
          BulkUtils.actions.moveFromWorkset(
            "libraries",
            Urls.rest.worksets.moveLibraries(config.worksetId)
          )
        );
      }

      actions.push({
        name: "Delete",
        action: function (items) {
          var lines = [
            "Are you sure you wish to delete the following libraries? This cannot be undone.",
            "Note: a Library may only be deleted by its creator or an admin.",
          ];
          var ids = [];
          jQuery.each(items, function (index, library) {
            lines.push("* " + library.name + " (" + library.alias + ")");
            ids.push(library.id);
          });
          Utils.showConfirmDialog("Delete Libraries", "Delete", lines, function () {
            Utils.ajaxWithDialog(
              "Deleting Libraries",
              "POST",
              Urls.rest.libraries.bulkDelete,
              ids,
              function () {
                Utils.page.pageReload();
              }
            );
          });
        },
      });

      return actions;
    },
    createStaticActions: function (config, projectId) {
      var actions = [];

      if (config.requisitionId) {
        if (config.relation == "supplemental") {
          actions.push({
            name: "Add",
            handler: function () {
              Utils.showSearchByNamesDialog(
                "Add Supplemental Libraries",
                Urls.rest.libraries.query,
                function (items, textStatus, xhr, queryNames) {
                  if (items.length !== queryNames.length) {
                    Utils.showSomeNotFoundError(queryNames, items);
                    return;
                  }
                  Utils.ajaxWithDialog(
                    "Adding Supplemental Libraries",
                    "POST",
                    Urls.rest.requisitions.addSupplementalLibraries(config.requisitionId),
                    items.map(Utils.array.getId),
                    Utils.page.pageReload
                  );
                }
              );
            },
          });
        } else if (config.relation == "requisitioned") {
          actions.push({
            name: "Add",
            handler: function () {
              Utils.showSearchByNamesDialog(
                "Add Libraries",
                Urls.rest.libraries.query,
                function (data, textStatus, xhr, queryNames) {
                  if (data.length !== queryNames.length) {
                    Utils.showSomeNotFoundError(queryNames, data);
                    return;
                  }
                  librariesUpdateFunction(
                    Urls.rest.requisitions.addLibraries(config.requisitionId)
                  )(data);
                }
              );
            },
          });
        }
      }

      actions.push({
        name: "Receive",
        include: true,
        handler: function () {
          var fields = [
            {
              property: "quantity",
              type: "int",
              label: "Quantity",
              value: 1,
            },
          ];
          if (Constants.isDetailedSample) {
            var aliquotClasses = Utils.array
              .removeArchived(Constants.sampleClasses)
              .filter(function (sampleClass) {
                return sampleClass.sampleCategory === "Aliquot";
              })
              .sort(Utils.sorting.sampleClassComparator);
            if (!aliquotClasses.length) {
              Utils.showOkDialog("Error", [
                "An aliquot sample class is required to create libraries, but none were found.",
              ]);
              return;
            }
            fields.unshift({
              property: "sampleClass",
              type: "select",
              label: "Aliquot Class",
              values: aliquotClasses,
              getLabel: Utils.array.getAlias,
            });
          }
          BulkUtils.actions.showDialogForBoxCreation(
            "Receive Libraries",
            "Receive",
            fields,
            Urls.ui.libraries.bulkReceive,
            function (result) {
              if (result.quantity < 1) {
                Utils.showOkDialog("Receive Libraries", [
                  "That's a peculiar number of libraries to receive.",
                ]);
                return;
              }
              return {
                quantity: result.quantity,
                projectId: projectId,
                sampleClassId: Constants.isDetailedSample ? result.sampleClass.id : null,
              };
            },
            function (result) {
              return result.quantity;
            }
          );
        },
      });
      return actions;
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "ID",
          mData: "id",
          bVisible: false,
        },
        {
          sTitle: "Name",
          mData: "name",
          include: true,
          iSortPriority: 1,
          iDataSort: 0, // Use ID for sorting
          mRender: Warning.tableWarningRenderer(WarningTarget.library, function (library) {
            return Urls.ui.libraries.edit(library.id);
          }),
          sClass: "nowrap",
        },
        ListUtils.labelHyperlinkColumn(
          "Alias",
          Urls.ui.libraries.edit,
          Utils.array.getId,
          "alias",
          0,
          true
        ),
        ListUtils.columns.project(projectId),
        {
          sTitle: "Tissue Origin",
          mData: "effectiveTissueOriginAlias",
          include: Constants.isDetailedSample,
          mRender: ListUtils.render.textWithHoverTitle(
            "effectiveTissueOriginAlias",
            "effectiveTissueOriginDescription"
          ),
          iSortPriority: 0,
        },
        {
          sTitle: "Tissue Type",
          mData: "effectiveTissueTypeAlias",
          include: Constants.isDetailedSample,
          mRender: ListUtils.render.textWithHoverTitle(
            "effectiveTissueTypeAlias",
            "effectiveTissueTypeDescription"
          ),
          iSortPriority: 0,
        },
        ListUtils.columns.detailedQcStatus,
        {
          sTitle: "Design",
          mData: "libraryDesignCodeId",
          include: Constants.isDetailedSample,
          mRender: ListUtils.render.textFromId(Constants.libraryDesignCodes, "code"),
          bSortable: false,
        },
        {
          sTitle: "Size (bp)",
          mData: "dnaSize",
          sDefaultContent: "",
          include: true,
          iSortPriority: 0,
        },
        {
          sTitle: "Indices",
          mData: "index1Label",
          mRender: function (data, type, full) {
            return data ? (full.index2Label ? data + ", " + full.index2Label : data) : "None";
          },
          include: true,
          iSortPriority: 0,
          bSortable: false,
        },
        {
          sTitle: "Location",
          mData: "locationLabel",
          include: true,
          iSortPriority: 0,
          mRender: function (data, type, full) {
            return full.boxId
              ? "<a href='" + Urls.ui.boxes.edit(full.boxId) + "'>" + data + "</a>"
              : data;
          },
          bSortable: false,
        },
        {
          sTitle: "Volume",
          mData: "volume",
          sDefaultContent: "",
          include: true,
          mRender: ListUtils.render.measureWithUnits(Constants.volumeUnits, "volumeUnits"),
        },
        {
          sTitle: "Concentration",
          mData: "concentration",
          sDefaultContent: "",
          include: true,
          mRender: ListUtils.render.measureWithUnits(
            Constants.concentrationUnits,
            "concentrationUnits"
          ),
        },
        {
          sTitle: "Modified",
          mData: "lastModified",
          include: true,
          iSortPriority: 2,
        },
        {
          sTitle: "Added",
          mData: "worksetAddedTime",
          sDefaultContent: "n/a",
          mRender: ListUtils.render.naIfNull,
          include: config.worksetId,
          bSortable: false,
        },
      ];
    },
    searchTermSelector: function (searchTerms) {
      var plainSampleTerms = [
        searchTerms["id"],
        searchTerms["barcode"],
        searchTerms["created"],
        searchTerms["entered"],
        searchTerms["changed"],
        searchTerms["creator"],
        searchTerms["changedby"],
        searchTerms["platform"],
        searchTerms["index"],
        searchTerms["box"],
        searchTerms["freezer"],
        searchTerms["kitname"],
        searchTerms["distributed"],
        searchTerms["distributedto"],
        searchTerms["workstation"],
      ];
      var detailedSampleTerms = [
        searchTerms["tissueOrigin"],
        searchTerms["tissueType"],
        searchTerms["design"],
        searchTerms["groupid"],
      ];
      if (Constants.isDetailedSample) {
        return plainSampleTerms.concat(detailedSampleTerms);
      } else {
        return plainSampleTerms;
      }
    },
  };

  function librariesUpdateFunction(saveUrl) {
    return function (items) {
      var callback = function (update) {
        switch (update.status) {
          case "completed":
            Utils.page.pageReload();
            break;
          case "failed":
            Utils.asyncSaveErrorsDialog(update, items, function (library) {
              return library.alias + " (" + library.name + ")";
            });
            break;
          default:
            Utils.showOkDialog("Error", [
              "Unexpected operation status. The save may still be in progress or completed.",
            ]);
        }
      };
      Utils.saveWithProgressDialog(
        "POST",
        saveUrl,
        items.map(Utils.array.getId),
        Urls.rest.requisitions.librariesUpdateProgress,
        callback
      );
    };
  }
})();
