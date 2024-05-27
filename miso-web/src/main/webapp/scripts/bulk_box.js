BulkTarget = window.BulkTarget || {};
BulkTarget.box = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.boxes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.boxes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("boxes");
    },
    getBulkActions: function (config) {
      return [
        BulkUtils.actions.edit(Urls.ui.boxes.bulkEdit),
        BulkUtils.actions.transfer("boxIds"),
        BulkUtils.actions.download(
          Urls.rest.boxes.boxSpreadsheet,
          Constants.boxSpreadsheets,
          function (boxes, spreadsheet) {
            var errors = [];
            return errors;
          }
        ),
      ];
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.name,
        BulkUtils.columns.simpleAlias(255),
        BulkUtils.columns.description,
        BulkUtils.columns.matrixBarcode,
        {
          title: "Box Use",
          type: "dropdown",
          data: "useId",
          required: true,
          source: Constants.boxUses,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Box Size",
          type: "dropdown",
          data: "sizeId",
          required: true,
          source: Constants.boxSizes,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
        {
          title: "Freezer Location Barcode",
          type: "text",
          data: "storageLocationBarcode",
          initOnChange: false,
          onChange: function (rowIndex, newValue, api) {
            var updateFreezerLocation = function (source, value) {
              api.updateField(rowIndex, "storageLocationId", {
                source: source || [],
                value: value,
              });
            };
            if (Utils.validation.isEmpty(newValue)) {
              updateFreezerLocation(null, null);
              return;
            }
            updateFreezerLocation(null, "(searching...)");
            $.ajax({
              url:
                Urls.rest.storageLocations.queryByBarcode +
                "?" +
                Utils.page.param({
                  q: newValue,
                }),
              contentType: "application/json; charset=utf8",
              dataType: "json",
            })
              .done(function (data) {
                updateFreezerLocation([data], data.fullDisplayLocation);
              })
              .fail(function (response, textStatus, serverStatus) {
                updateFreezerLocation(null, "(Not found)");
              });
          },
        },
        {
          title: "Freezer Location",
          type: "dropdown",
          data: "storageLocationId",
          disabled: true,
          source: function (data, api) {
            if (!data.storageLocationId) {
              return [];
            }
            return [
              {
                id: data.storageLocationId,
                fullDisplayLocation: data.storageDisplayLocation,
              },
            ];
          },
          getItemLabel: Utils.array.get("fullDisplayLocation"),
          getItemValue: Utils.array.getId,
        },
        {
          title: "Location Note",
          type: "text",
          data: "locationBarcode",
          maxLength: 255,
        },
      ];
    },
  };
})(jQuery);
