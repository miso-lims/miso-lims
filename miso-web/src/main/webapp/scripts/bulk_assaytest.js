BulkTarget = window.BulkTarget || {};
BulkTarget.assaytest = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   *   libraryQualificationMethods: array
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.assayTests.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.assayTests.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "assay-tests");
    },
    getBulkActions: function (config) {
      return !config.isAdmin || config.pageMode === "view"
        ? []
        : [BulkUtils.actions.edit(Urls.ui.assayTests.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(50),
        {
          title: "Tissue Origin",
          data: "tissueOriginId",
          type: "dropdown",
          source: Constants.tissueOrigins,
          getItemLabel: function (item) {
            return item.alias + " (" + item.description + ")";
          },
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
        },
        BulkUtils.columns.makeBoolean(
          "Negate Tissue Origin",
          "negateTissueOrigin",
          true,
          Constants.isDetailedSample
        ),
        {
          title: "Tissue Type",
          data: "tissueTypeId",
          type: "dropdown",
          source: Constants.tissueTypes,
          getItemLabel: function (item) {
            return item.alias + " (" + item.description + ")";
          },
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
        },
        BulkUtils.columns.makeBoolean(
          "Negate Tissue Type",
          "negateTissueType",
          true,
          Constants.isDetailedSample
        ),
        {
          title: "Extraction Class",
          data: "extractionClassId",
          type: "dropdown",
          source: function (data, api) {
            return Constants.sampleClasses.filter(function (x) {
              return (
                x.sampleCategory === "Stock" && (!x.archived || x.id === data.extractionClassId)
              );
            });
          },
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
          required: true,
        },
        {
          title: "Library Design Code",
          data: "libraryDesignCodeId",
          type: "dropdown",
          source: Constants.libraryDesignCodes,
          getItemLabel: getDesignCodeLabel,
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
          required: true,
        },
        {
          title: "Library Qualification Method",
          data: "libraryQualificationMethod",
          type: "dropdown",
          source: config.libraryQualificationMethods,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
          sortSource: true,
          required: true,
          onChange: function (rowIndex, newValue, api) {
            var isNone = newValue === "None";
            var isAliquot = newValue === "Aliquot";
            api.updateField(rowIndex, "libraryQualificationDesignCodeId", {
              required: isAliquot,
              disabled: isNone,
              value: isNone ? null : undefined,
            });
          },
          description:
            "Describes how libraries are qualified before full depth sequencing - via library aliquot " +
            "QC, low-depth sequencing, or not at all",
        },
        {
          title: "Library Qualification Design Code",
          data: "libraryQualificationDesignCodeId",
          type: "dropdown",
          source: Constants.libraryDesignCodes,
          getItemLabel: getDesignCodeLabel,
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
          required: true,
        },
        (function () {
          var col = BulkUtils.columns.makeBoolean(
            "Repeat Per Timepoint",
            "repeatPerTimepoint",
            true,
            Constants.isDetailedSample
          );
          col.description =
            "If true, the test must be repeated for each timepoint received; if false, the test must " +
            "only be completed once per identity";
          return col;
        })(),
        {
          title: "Permitted Samples",
          data: "permittedSamples",
          type: "dropdown",
          source: Constants.permittedSamples,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
          required: true,
        },
      ];
    },
  };

  function getDesignCodeLabel(designCode) {
    return designCode.code + " (" + designCode.description + ")";
  }
})(jQuery);
