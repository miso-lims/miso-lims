BulkTarget = window.BulkTarget || {};
BulkTarget.librarytemplate_index = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  var editPositions = null;

  return {
    getSaveUrl: function (config) {
      return Urls.rest.libraryTemplates.bulkSaveIndices(config.libraryTemplate.id);
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.libraryTemplates.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("libraries", "library-templates");
    },
    getBulkActions: function (config) {
      return [];
    },
    getColumns: function (config, api) {
      var index1s = config.indexFamily.indices.filter(function (index) {
        return index.position === 1;
      });
      var index2s = config.indexFamily.indices.filter(function (index) {
        return index.position === 2;
      });
      return [
        {
          title: "Box Position",
          data: "boxPosition",
          type: "text",
          disabled: config.pageMode === "edit",
          required: true,
          regex: "^[A-Z](0[1-9]|1[0-9]|2[0-6])$",
          customSorting: [
            {
              name: "Box Position (by rows)",
              sort: function (a, b) {
                return Utils.sorting.sortBoxPositions(a, b, true);
              },
            },
            {
              name: "Box Position (by columns)",
              sort: function (a, b) {
                return Utils.sorting.sortBoxPositions(a, b, false);
              },
            },
          ],
        },
        {
          title: "Index 1",
          data: "index1Id",
          type: "dropdown",
          source: index1s,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Index 2",
          data: "index2Id",
          type: "dropdown",
          source: index2s,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
      ];
    },
    confirmSave: function (data, config, api) {
      editPositions = data.map(function (x) {
        return x.boxPosition;
      });
    },
    manipulateSavedData: function (data) {
      // Library template is returned, so we need to extract only the indices that were being created/edited
      return editPositions.map(function (position) {
        return {
          boxPosition: position,
          index1Id: data[0].indexOneIds ? data[0].indexOneIds[position] : null,
          index2Id: data[0].indexTwoIds ? data[0].indexTwoIds[position] : null,
        };
      });
    },
  };
})(jQuery);
