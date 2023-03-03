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
    getBulkActions: function (config) {
      return !config.isAdmin || config.pageMode === "view"
        ? []
        : [BulkUtils.actions.edit(Urls.ui.assayTests.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(50),
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
        BulkUtils.columns.makeBoolean("Negate Tissue Type", "negateTissueType", true),
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
            var isAliquot = newValue === "Aliquot";
            api.updateField(rowIndex, "libraryQualificationDesignCodeId", {
              required: isAliquot,
              disabled: !isAliquot,
              value: isAliquot ? undefined : null,
            });
          },
          description:
            "Describes how libraries are qualified before full depth sequencing - either via library aliquot " +
            "QC, or low-depth sequencing",
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
            true
          );
          col.include = Constants.isDetailedSample;
          col.description =
            "If true, the test must be repeated for each timepoint received; if false, the test must " +
            "only be completed once per identity";
          return col;
        })(),
      ];
    },
  };

  function getDesignCodeLabel(designCode) {
    return designCode.code + " (" + designCode.description + ")";
  }
})(jQuery);
