ListTarget.assaytest = (function () {
  return {
    name: "Assay Tests",
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    getQueryUrl: null,
    showNewOptionSop: true,
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.assaytest.getBulkActions(config);
      if (config.isAdmin) {
        if (!config.assayId) {
          // for list page
          actions.push(
            ListUtils.createBulkDeleteAction("Assay Tests", "assaytests", Utils.array.getAlias)
          );
        } else if (config.pageMode === "edit") {
          // for Edit Assay page
          actions.push({
            name: "Remove",
            action: Assay.removeTests,
          });
        }
      }
      return actions;
    },
    createStaticActions: function (config, projectId) {
      var actions = [];
      if (config.isAdmin) {
        if (!config.assayId) {
          // for list page
          actions.push(
            ListUtils.createStaticAddAction("Assay Tests", Urls.ui.assayTests.bulkCreate, true)
          );
        } else if (config.pageMode === "edit") {
          // for Edit Assay page
          actions.push({
            name: "Add",
            handler: showAddTestDialog,
          });
        }
      }
      return actions;
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Alias",
          mData: "alias",
          iSortPriority: 1,
        },
        {
          sTitle: "Tissue Type",
          mData: "tissueTypeId",
          mRender: function (data, type, full) {
            if (!data) {
              return null;
            } else {
              var tissueType = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(data),
                Constants.tissueTypes
              );
              var text = (full.negateTissueType ? "NOT " : "") + tissueType.alias;
              if (type === "display") {
                return '<span title="' + tissueType.description + '">' + text + "</span>";
              } else {
                return text;
              }
            }
          },
          include: Constants.isDetailedSample,
        },
        {
          sTitle: "Extraction Class",
          mData: "extractionClassId",
          mRender: ListUtils.render.textFromId(Constants.sampleClasses, "alias"),
          include: Constants.isDetailedSample,
        },
        {
          sTitle: "Library Design Code",
          mData: "libraryDesignCodeId",
          mRender: function (data, type, full) {
            var code = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(data),
              Constants.libraryDesignCodes
            );
            if (type === "display") {
              return '<span title="' + code.description + '">' + code.code + "</span>";
            } else {
              return code.code;
            }
          },
          include: Constants.isDetailedSample,
        },
        {
          sTitle: "Library Qualification Method",
          mData: "libraryQualificationMethod",
          mRender: function (data, type, full) {
            if (type !== "display") {
              return data;
            }
            var method = Utils.array.findUniqueOrThrow(function (x) {
              return x.value === data;
            }, config.libraryQualificationMethods);
            if (data === "ALIQUOT") {
              var code = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(full.libraryQualificationDesignCodeId),
                Constants.libraryDesignCodes
              );
              return method.label + " (" + code.code + ")";
            } else {
              return method.label;
            }
          },
          include: Constants.isDetailedSample,
        },
      ];
    },
  };

  function showAddTestDialog() {
    Utils.showWizardDialog(
      "Add Assay Test",
      Constants.assayTests.map(function (test) {
        return {
          name: test.alias,
          handler: function () {
            Assay.addTest(test);
          },
        };
      })
    );
  }
})();
