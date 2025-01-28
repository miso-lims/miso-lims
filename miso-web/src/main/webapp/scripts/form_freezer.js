if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.freezer = (function ($) {
  /*
   * Expected config {
   *   rooms: array,
   *   locationMaps: array,
   *   storageLabels: array
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("freezers_and_rooms");
    },
    getSaveUrl: function (freezer) {
      return freezer.id
        ? Urls.rest.storageLocations.updateFreezer(freezer.id)
        : Urls.rest.storageLocations.createFreezer;
    },
    getSaveMethod: function (freezer) {
      return freezer.id ? "PUT" : "POST";
    },
    getEditUrl: function (freezer) {
      return Urls.ui.freezers.edit(freezer.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Freezer Information",
          fields: [
            {
              title: "Location ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (freezer) {
                return freezer.id || "Unsaved";
              },
            },
            {
              title: "Room",
              data: "parentLocationId",
              type: "dropdown",
              required: true,
              source: config.rooms,
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("alias"),
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Label",
              data: "labelId",
              type: "dropdown",
              source: config.storageLabels,
              getItemLabel: Utils.array.get("label"),
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("label"),
              description: "A storage label to associate with the freezer",
            },
            {
              title: "Barcode",
              data: "identificationBarcode",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Map",
              data: "mapId",
              type: "dropdown",
              source: config.locationMaps,
              getItemLabel: function (item) {
                return item.filename;
              },
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("filename"),
              onChange: function (newValue, form) {
                var settings = {
                  disabled: !newValue,
                };
                if (!newValue) {
                  settings.value = null;
                }
                form.updateField("mapAnchor", settings);
                updateMapLink(form, config.locationMaps);
              },
              description: "The location map on which the freezer appears",
            },
            {
              title: "Map Anchor",
              data: "mapAnchor",
              type: "text",
              maxLength: 100,
              onChange: function (newValue, form) {
                updateMapLink(form, config.locationMaps);
              },
              description:
                "Anchor tag ID to use in a link to the location map in order to highlight this " +
                "freezer. For this to work, the map must be designed to include such anchor IDs",
            },
            {
              title: "View Map",
              data: "mapLink",
              type: "read-only",
              openNewTab: true,
              omit: true,
              getDisplayValue: function (freezer) {
                return "n/a";
              },
            },
            {
              title: "Probe ID",
              data: "probeId",
              type: "text",
              maxLength: 50,
              description: "Identifier used in a monitoring system",
            },
            {
              title: "Retired",
              type: "checkbox",
              data: "retired",
            },
          ],
        },
      ];
    },
  };

  function updateMapLink(form, locationMaps) {
    var mapId = form.get("mapId");
    var map = !mapId
      ? null
      : Utils.array.findUniqueOrThrow(Utils.array.idPredicate(mapId), locationMaps);
    var mapAnchor = form.get("mapAnchor");
    form.updateField("mapLink", {
      label: mapId ? "Open" : "n/a",
      link: mapId ? Urls.ui.freezerMaps.view(map.filename, mapAnchor) : null,
    });
  }
})(jQuery);
