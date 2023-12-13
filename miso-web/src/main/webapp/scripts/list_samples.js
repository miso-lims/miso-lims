ListTarget.sample = (function () {
  return {
    name: "Samples",
    getUserManualUrl: function () {
      return Urls.external.userManual("samples");
    },
    createUrl: function (config, projectId) {
      if (projectId) {
        if (config.arrayed) {
          return Urls.rest.samples.projectArrayedDatatable(projectId);
        } else {
          return Urls.rest.samples.projectDatatable(projectId);
        }
      } else if (config.worksetId) {
        return Urls.rest.samples.worksetDatatable(config.worksetId);
      } else if (config.requisitionId) {
        if (config.supplemental) {
          return Urls.rest.samples.requisitionSupplementalDatatable(config.requisitionId);
        } else {
          return Urls.rest.samples.requisitionDatatable(config.requisitionId);
        }
      }
      return Urls.rest.samples.datatable;
    },
    getQueryUrl: function () {
      return Urls.rest.samples.query;
    },
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.sample.getBulkActions(config);

      if (config.requisitionId) {
        if (config.supplemental) {
          actions.unshift({
            name: "Remove",
            action: function (items) {
              Utils.ajaxWithDialog(
                "Removing Supplemental Samples",
                "POST",
                Urls.rest.requisitions.removeSupplementalSamples(config.requisitionId),
                items.map(Utils.array.getId),
                Utils.page.pageReload
              );
            },
          });
        } else {
          actions.unshift(
            {
              name: "Remove",
              action: samplesUpdateFunction(
                Urls.rest.requisitions.removeSamples(config.requisitionId)
              ),
            },
            {
              name: "Move to Req.",
              action: function (items) {
                showMoveToRequisitionDialog(
                  items,
                  config.requisitionId,
                  config.requisition.alias,
                  config.requisition.assayId
                );
              },
            }
          );
        }
      }

      if (config.worksetId) {
        actions.push(
          BulkUtils.actions.moveFromWorkset(
            "samples",
            Urls.rest.worksets.moveSamples(config.worksetId)
          )
        );
      }

      actions.push({
        name: "Delete",
        action: function (items) {
          var lines = [
            "Are you sure you wish to delete the following samples? This cannot be undone.",
            "Note: a Sample may only be deleted by its creator or an admin.",
          ];
          var ids = [];
          jQuery.each(items, function (index, sample) {
            lines.push("* " + sample.name + " (" + sample.alias + ")");
            ids.push(sample.id);
          });
          Utils.showConfirmDialog("Delete Samples", "Delete", lines, function () {
            Utils.ajaxWithDialog(
              "Deleting Samples",
              "POST",
              Urls.rest.samples.bulkDelete,
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
        if (config.supplemental) {
          actions.push({
            name: "Add",
            handler: function () {
              Utils.showSearchByNamesDialog(
                "Add Supplemental Samples",
                Urls.rest.samples.query,
                function (items) {
                  Utils.ajaxWithDialog(
                    "Adding Supplemental Samples",
                    "POST",
                    Urls.rest.requisitions.addSupplementalSamples(config.requisitionId),
                    items.map(Utils.array.getId),
                    Utils.page.pageReload
                  );
                }
              );
            },
          });
        } else {
          actions.push({
            name: "Add",
            handler: function () {
              Utils.showSearchByNamesDialog(
                "Add Samples",
                Urls.rest.samples.query,
                function (data, textStatus, xhr, queryNames) {
                  if (data.length !== queryNames.length) {
                    var missingCount = queryNames.length - data.length;
                    Utils.showOkDialog("Error", [
                      missingCount +
                        " of the items " +
                        (missingCount === 1 ? "was" : "were") +
                        " not found",
                    ]);
                    return;
                  }
                  samplesUpdateFunction(Urls.rest.requisitions.addSamples(config.requisitionId))(
                    data
                  );
                }
              );
            },
          });
        }
      }

      actions.push({
        name: "Create",
        handler: function () {
          var fields = [
            {
              property: "quantity",
              type: "int",
              label: "Quantity",
              value: 1,
              required: true,
            },
          ];

          if (Constants.isDetailedSample) {
            fields.unshift({
              property: "sampleCategory",
              type: "select",
              label: "Sample Category",
              values: getCreatableCategories(),
              required: true,
            });
          }
          BulkUtils.actions.showDialogForBoxCreation(
            "Create Samples",
            "Create",
            fields,
            Urls.ui.samples.bulkCreate,
            function (result) {
              if (result.quantity < 1) {
                Utils.showOkDialog("Create Samples", [
                  "That's a peculiar number of samples to create.",
                ]);
                return;
              }
              if (
                result.createBox &&
                Constants.isDetailedSample &&
                result.sampleCategory == "Identity"
              ) {
                Utils.showOkDialog("Error", ["Identities cannot be placed in boxes"]);
                return;
              }
              return {
                quantity: result.quantity,
                projectId: projectId,
                targetCategory: Constants.isDetailedSample ? result.sampleCategory : null,
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
          mRender: Warning.tableWarningRenderer(WarningTarget.sample, function (sample) {
            return Urls.ui.samples.edit(sample.id);
          }),
          sClass: "nowrap",
        },
        ListUtils.labelHyperlinkColumn(
          "Alias",
          Urls.ui.samples.edit,
          Utils.array.getId,
          "alias",
          0,
          true
        ),
        {
          sTitle: "External Name",
          mData: "effectiveExternalNames",
          include: Constants.isDetailedSample,
        },
        {
          sTitle: "Tissue Attributes",
          mData: "effectiveTissueTypeAlias",
          include: Constants.isDetailedSample,
          mRender: function (data, type, full) {
            if (type !== "display") {
              return data;
            } else if (!data) {
              return "n/a";
            }
            var label = full.effectiveTissueOriginAlias + " " + full.effectiveTissueTypeAlias;
            if (full.effectiveTimepoint) {
              if (full.effectiveTimepoint.length > 12) {
                label += " " + full.effectiveTimepoint.substring(0, 12) + "&#8230;"; // #8230=ellipsis
              } else {
                label += " " + full.effectiveTimepoint;
              }
            }
            return (
              '<div class="tooltip" style="width: 100%;">' +
              "<span>" +
              label +
              "</span>" +
              '<span class="tooltiptext">' +
              "Tissue origin: " +
              full.effectiveTissueOriginAlias +
              " (" +
              full.effectiveTissueOriginDescription +
              ")<br/>" +
              "Tissue type: " +
              full.effectiveTissueTypeAlias +
              " (" +
              full.effectiveTissueTypeDescription +
              ")<br/>" +
              (full.effectiveTimepoint ? "Timepoint: " + full.effectiveTimepoint + "<br/>" : "") +
              "</span>" +
              "</div>"
            );
          },
        },
        {
          sTitle: "Sample Class",
          mData: "sampleClassId",
          include: Constants.isDetailedSample,
          mRender: ListUtils.render.textFromId(Constants.sampleClasses, "alias", "Plain"),
          iSortPriority: 0,
        },
        {
          sTitle: "Type",
          mData: "sampleType",
          include: !Constants.isDetailedSample,
          iSortPriority: 0,
        },
        ListUtils.columns.detailedQcStatus,
        {
          sTitle: "Location",
          mData: "locationLabel",
          bSortable: false,
          mRender: function (data, type, full) {
            return full.boxId
              ? "<a href='" + Urls.ui.boxes.edit(full.boxId) + "'>" + data + "</a>"
              : data;
          },
          include: true,
          iSortPriority: 0,
        },
        {
          sTitle: "Created",
          mData: "creationDate",
          sDefaultContent: "",
          include: Constants.isDetailedSample,
          iSortPriority: 0,
        },
        {
          sTitle: "Modified",
          mData: "lastModified",
          mRender: ListUtils.render.dateWithTimeTooltip,
          include: Constants.isDetailedSample,
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
        searchTerms["requisition"],
        searchTerms["entered"],
        searchTerms["changed"],
        searchTerms["received"],
        searchTerms["creator"],
        searchTerms["changedby"],
        searchTerms["box"],
        searchTerms["freezer"],
        searchTerms["requisition"],
        searchTerms["distributed"],
        searchTerms["distributedto"],
      ];
      var detailedSampleTerms = [
        searchTerms["created"],
        searchTerms["class"],
        searchTerms["tissueOrigin"],
        searchTerms["tissueType"],
        searchTerms["timepoint"],
        searchTerms["lab"],
        searchTerms["external"],
        searchTerms["subproject"],
        searchTerms["groupid"],
        searchTerms["ghost"],
      ];
      if (Constants.isDetailedSample) {
        return plainSampleTerms.concat(detailedSampleTerms);
      } else {
        return plainSampleTerms;
      }
    },
  };

  function getCreatableCategories() {
    return ["Identity", "Tissue", "Tissue Processing", "Stock", "Aliquot"].filter(function (
      category
    ) {
      return Constants.sampleClasses.some(function (sampleClass) {
        return (
          !sampleClass.archived &&
          sampleClass.directCreationAllowed &&
          sampleClass.sampleCategory === category
        );
      });
    });
  }

  function samplesUpdateFunction(saveUrl) {
    return function (items) {
      var callback = function (update) {
        switch (update.status) {
          case "completed":
            Utils.page.pageReload();
            break;
          case "failed":
            var lines = ["Failed to save samples:"];
            if (update.detail === "Validation failed" && update.data) {
              update.data.forEach(function (failure) {
                var sample = items[failure.row];
                lines.push(sample.alias + " (" + sample.name + "):");
                failure.fields.forEach(function (field) {
                  lines.push("* " + field.field + ": " + field.errors.join("; "));
                });
              });
            }
            Utils.showOkDialog("Error", lines);
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
        Urls.rest.requisitions.samplesUpdateProgress,
        callback
      );
    };
  }

  function showMoveToRequisitionDialog(
    samples,
    sourceRequisitionId,
    sourceRequisitionAlias,
    sourceRequisitionAssayId
  ) {
    var actions = [
      {
        name: "Stop",
        handler: function () {
          var suggestedAlias = sourceRequisitionAlias + " - STOPPED";
          checkExistingRequisitions(
            sourceRequisitionId,
            suggestedAlias,
            sourceRequisitionAssayId,
            true,
            samples
          );
        },
      },
      {
        name: "Change Assay",
        handler: function () {
          var fields = [makeAssayField(sourceRequisitionAssayId)];
          Utils.showDialog("Choose assay", "Continue", fields, function (results) {
            var suggestedAlias = sourceRequisitionAlias + " - " + results.assay.alias;
            checkExistingRequisitions(
              sourceRequisitionId,
              suggestedAlias,
              results.assay.id,
              false,
              samples
            );
          });
        },
      },
      {
        name: "Other",
        handler: function () {
          var suggestedAlias = sourceRequisitionAlias + " - ";
          var fields = [
            {
              label: "Requisition Alias",
              property: "alias",
              type: "text",
              required: true,
              value: suggestedAlias,
            },
          ];
          Utils.showDialog("Move to Another Requisition", "Continue", fields, function (results) {
            checkExistingRequisitions(
              sourceRequisitionId,
              results.alias,
              sourceRequisitionAssayId,
              false,
              samples
            );
          });
        },
      },
    ];
    Utils.showWizardDialog("Move to Another Requisition", actions, "Purpose of move:");
  }

  function checkExistingRequisitions(
    sourceRequisitionId,
    suggestedAlias,
    assayId,
    stopped,
    samples
  ) {
    var url =
      Urls.rest.requisitions.search +
      "?" +
      Utils.page.param({
        q: suggestedAlias,
      });
    var callback = function (data) {
      if (data.length) {
        var options = data.map(function (existing) {
          return {
            name: existing.alias,
            handler: function () {
              moveToRequisition(
                sourceRequisitionId,
                existing.alias,
                existing.assayId,
                existing.stopped,
                samples,
                existing.id
              );
            },
          };
        });
        options.push({
          name: "New Requisition",
          handler: function () {
            showNewRequisitionDialog(
              sourceRequisitionId,
              suggestedAlias,
              assayId,
              stopped,
              samples
            );
          },
        });
        Utils.showWizardDialog("Move to Another Requisition", options);
      } else {
        showNewRequisitionDialog(sourceRequisitionId, suggestedAlias, assayId, stopped, samples);
      }
    };
    Utils.ajaxWithDialog("Checking for existing requisition", "GET", url, null, callback);
  }

  function showNewRequisitionDialog(
    sourceRequisitionId,
    suggestedAlias,
    assayId,
    stopped,
    samples
  ) {
    var fields = [
      {
        label: "Alias",
        property: "alias",
        type: "text",
        required: true,
        value: suggestedAlias,
      },
      makeAssayField(assayId),
      {
        label: "Stopped",
        property: "stopped",
        type: "checkbox",
        value: stopped,
      },
    ];
    Utils.showDialog("Move to New Requisition", "Create and Move", fields, function (results) {
      moveToRequisition(
        sourceRequisitionId,
        results.alias,
        results.assay.id,
        results.stopped,
        samples
      );
    });
  }

  function moveToRequisition(
    sourceRequisitionId,
    targetAlias,
    assayId,
    stopped,
    samples,
    existingRequisitionId
  ) {
    var data = {
      requisitionId: existingRequisitionId,
      requisitionAlias: targetAlias,
      assayId: assayId,
      stopped: stopped,
      sampleIds: samples.map(Utils.array.getId),
    };
    var callback = function (data) {
      Utils.page.pageRedirect(Urls.ui.requisitions.edit(data.id));
    };
    Utils.ajaxWithDialog(
      "Moving Samples",
      "POST",
      Urls.rest.requisitions.moveSamples(sourceRequisitionId),
      data,
      callback
    );
  }

  function makeAssayField(selectedAssayId) {
    var selectedAssay = selectedAssayId
      ? Utils.array.findUniqueOrThrow(Utils.array.idPredicate(selectedAssayId), Constants.assays)
      : null;
    return {
      label: "Assay",
      property: "assay",
      type: "select",
      required: false,
      values: Constants.assays.filter(function (item) {
        return !item.archived || item.id === selectedAssayId;
      }),
      getLabel: function (item) {
        return item.alias + " v" + item.version;
      },
      value: selectedAssay ? selectedAssay.alias + " v" + selectedAssay.version : undefined,
    };
  }
})();
