if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.container = (function ($) {
  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("sequencing_containers");
    },
    getSaveUrl: function (container) {
      return container.id ? Urls.rest.containers.update(container.id) : Urls.rest.containers.create;
    },
    getSaveMethod: function (container) {
      return container.id ? "PUT" : "POST";
    },
    getEditUrl: function (container) {
      return Urls.ui.containers.edit(container.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Container Information",
          fields: [
            {
              title: "Container ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (container) {
                return container.id || "Unsaved";
              },
            },
            {
              title: "Movie Time",
              data: "movieTime",
              type: "text",
              required: true,
            },
            {
              title: "Serial Number",
              data: "identificationBarcode",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Container Model",
              data: "model.id",
              type: "dropdown",
              required: true,
              source: getValidContainerModels(object),
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("alias"),
              onChange: function (newValue, form) {
                if (!newValue) {
                  form.updateField("clusteringKitId", {
                    source: [],
                  });
                  form.updateField("multiplexingKitId", {
                    source: [],
                  });
                  return;
                }
                var selectedModel = Utils.array.findUniqueOrThrow(
                  Utils.array.idPredicate(newValue),
                  Constants.containerModels
                );
                var platform = Utils.array.findUniqueOrThrow(
                  Utils.array.namePredicate(selectedModel.platformType),
                  Constants.platformTypes
                );
                form.updateField("clusteringKitId", {
                  source: getKits("Clustering", platform.key, object.clusteringKitId),
                });
                form.updateField("multiplexingKitId", {
                  source: getKits("Multiplexing", platform.key, object.multiplexingKitId),
                });
              },
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Clustering Kit",
              data: "clusteringKitId",
              type: "dropdown",
              source: (function () {
                var platform = Utils.array.findUniqueOrThrow(
                  Utils.array.namePredicate(object.model.platformType),
                  Constants.platformTypes
                );
                return getKits("Clustering", platform.key, object.clusteringKitId);
              })(),
              getItemLabel: function (item) {
                return item.name;
              },
              getItemValue: function (item) {
                return item.id;
              },
              onChange: function (newValue, form) {
                var opts = {
                  disabled: !newValue,
                };
                if (!newValue) {
                  opts.value = null;
                }
                form.updateField("clusteringKitLot", opts);
              },
            },
            {
              title: "Clustering Kit Lot",
              data: "clusteringKitLot",
              type: "text",
              maxLength: 100,
            },
            {
              title: "Multiplexing Kit",
              data: "multiplexingKitId",
              type: "dropdown",
              source: (function () {
                var platform = Utils.array.findUniqueOrThrow(
                  Utils.array.namePredicate(object.model.platformType),
                  Constants.platformTypes
                );
                return getKits("Multiplexing", platform.key, object.multiplexingKitId);
              })(),
              getItemLabel: function (item) {
                return item.name;
              },
              getItemValue: function (item) {
                return item.id;
              },
              onChange: function (newValue, form) {
                var opts = {
                  disabled: !newValue,
                };
                if (!newValue) {
                  opts.value = null;
                }
                form.updateField("multiplexingKitLot", opts);
              },
            },
            {
              title: "Multiplexing Kit Lot",
              data: "multiplexingKitLot",
              type: "text",
              maxLength: 100,
            },
            {
              title: "Pore Version",
              data: "poreVersionId",
              type: "dropdown",
              source: Constants.poreVersions,
              getItemLabel: function (item) {
                return item.alias;
              },
              getItemValue: function (item) {
                return item.id;
              },
              include: config.platformType === "OXFORDNANOPORE",
            },
            {
              title: "Received Date",
              data: "receivedDate",
              type: "date",
              include: config.platformType === "OXFORDNANOPORE",
            },
            {
              title: "Returned Date",
              data: "returnedDate",
              type: "date",
              include: config.platformType === "OXFORDNANOPORE",
            },
          ],
        },
      ];
    },
  };

  function getValidContainerModels(container) {
    var currentModel = Utils.array.findUniqueOrThrow(
      Utils.array.idPredicate(container.model.id),
      Constants.containerModels
    );
    var platformType = Utils.array.findUniqueOrThrow(
      Utils.array.namePredicate(currentModel.platformType),
      Constants.platformTypes
    );
    var instrumentModels = null;
    if (container.lastRunInstrumentModelId) {
      instrumentModels = [
        Utils.array.findUniqueOrThrow(
          Utils.array.idPredicate(container.lastRunInstrumentModelId),
          Constants.instrumentModels
        ),
      ];
    } else {
      instrumentModels = Constants.instrumentModels.filter(function (instrumentModel) {
        return currentModel.instrumentModelIds.indexOf(instrumentModel.id) !== -1;
      });
    }
    return instrumentModels
      .flatMap(function (instrumentModel) {
        return instrumentModel.containerModels;
      })
      .reduce(function (accumulator, currentValue) {
        if (
          currentValue.partitionCount === currentModel.partitionCount &&
          !accumulator.find(function (model) {
            return model.id === currentValue.id;
          })
        ) {
          accumulator.push(currentValue);
        }
        return accumulator;
      }, []);
  }

  function getKits(type, platform, savedValueId) {
    return Constants.kitDescriptors.filter(function (kit) {
      return (
        kit.kitType === type &&
        kit.platformType === platform &&
        (!kit.archived || kit.id === savedValueId)
      );
    });
  }
})(jQuery);
