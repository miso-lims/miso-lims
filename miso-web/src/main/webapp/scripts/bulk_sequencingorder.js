BulkTarget = window.BulkTarget || {};
BulkTarget.sequencingorder = (function () {
  return {
    getSaveUrl: function () {
      return Urls.rest.sequencingOrders.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.sequencingOrders.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("sequencing_orders");
    },
    getBulkActions: function (config) {
      return [];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Pool Name",
          type: "text",
          data: "pool.name",
          disabled: true,
        },
        {
          title: "Pool Alias",
          type: "text",
          data: "pool.alias",
          disabled: true,
        },
        {
          title: "Purpose",
          type: "dropdown",
          data: "purposeId",
          source: Constants.runPurposes,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          required: true,
        },
        BulkUtils.columns.description,
        {
          title: "Instrument Model",
          type: "dropdown",
          data: "instrumentModelId",
          omit: true,
          includeSaved: false,
          source: function (data, api) {
            return Constants.instrumentModels.filter(function (instrumentModel) {
              return (
                instrumentModel.instrumentType === "SEQUENCER" &&
                instrumentModel.platformType === data.pool.platformType &&
                instrumentModel.active
              );
            });
          },
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          required: true,
          onChange: function (rowIndex, newValue, api) {
            if (newValue) {
              var instrumentModel = Constants.instrumentModels.find(function (x) {
                return x.instrumentType === "SEQUENCER" && x.alias === newValue;
              });
              api.updateField(rowIndex, "containerModelId", {
                source: !instrumentModel
                  ? []
                  : instrumentModel.containerModels.filter(function (x) {
                      return !x.archived;
                    }),
                value: instrumentModel ? undefined : null,
              });
              api.updateField(rowIndex, "parameters.id", {
                source: !instrumentModel
                  ? []
                  : Constants.sequencingParameters.filter(function (x) {
                      return x.instrumentModelId == instrumentModel.id;
                    }),
                value: instrumentModel ? undefined : null,
              });
            }
          },
        },
        {
          title: "Container Model",
          type: "dropdown",
          data: "containerModelId",
          source: function (data, api) {
            if (data.containerModelId) {
              // for display after save only - no bulk editing
              return Constants.containerModels.filter(
                Utils.array.idPredicate(data.containerModelId)
              );
            } else {
              return []; // set by container model inchange
            }
          },
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          required: true,
        },
        {
          title: "Sequencing Parameters",
          type: "dropdown",
          data: "parameters.id",
          source: function (data, api) {
            if (data.parameters && data.parameters.id) {
              // for display after save only - no bulk editing
              return Constants.sequencingParameters.filter(
                Utils.array.idPredicate(data.parameters.id)
              );
            } else {
              return []; // set by container model inchange
            }
          },
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: true,
          required: true,
        },
        {
          title: "Partitions",
          type: "int",
          data: "partitions",
          required: true,
          min: 1,
        },
      ];
    },
  };
})();
