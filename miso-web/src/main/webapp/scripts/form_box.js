if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.box = (function ($) {
  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("boxes");
    },
    getSaveUrl: function (box) {
      if (box.id) {
        return Urls.rest.boxes.update(box.id);
      } else {
        return Urls.rest.boxes.create;
      }
    },
    getSaveMethod: function (box) {
      return box.id ? "PUT" : "POST";
    },
    getEditUrl: function (box) {
      return Urls.ui.boxes.edit(box.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Box Information",
          fields: [
            {
              title: "Box ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (box) {
                return box.id || "Unsaved";
              },
            },
            {
              title: "Name",
              data: "name",
              type: "read-only",
              getDisplayValue: function (box) {
                return box.name || "Unsaved";
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Matrix Barcode",
              data: "identificationBarcode",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Box Use",
              data: "useId",
              type: "dropdown",
              source: Constants.boxUses,
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              required: true,
            },
            {
              title: "Box Size",
              data: "sizeId",
              type: "dropdown",
              source: Constants.boxSizes,
              getItemLabel: Utils.array.get("label"),
              getItemValue: Utils.array.getId,
              required: true,
            },
            {
              title: "Freezer Location",
              type: "special",
              makeControls: function (form) {
                return makeFreezerControls(form, object);
              },
            },
            {
              title: "Location Note",
              data: "locationBarcode",
              type: "text",
              maxLength: 255,
            },
          ],
        },
      ];
    },
  };

  function makeFreezerControls(form, box) {
    var container = $("<div>").css({
      width: "95%",
      display: "flex",
      "align-items": "center",
    });
    container.append(
      $("<span>")
        .attr("id", "freezerLocationLabel")
        .css({
          flex: 1,
          margin: "2px",
        })
        .text(box.storageDisplayLocation || "Not specified")
    );
    container.append(
      $("<button>")
        .attr("id", "scanFreezerLocation")
        .addClass("ui-state-default")
        .attr("type", "button")
        .css("margin-right", "2px")
        .text("Scan")
        .click(function () {
          scanFreezerLocation(box);
        })
    );
    container.append(
      $("<button>")
        .attr("id", "selectFreezerLocation")
        .addClass("ui-state-default")
        .attr("type", "button")
        .css("margin", "2px")
        .text("Select")
        .click(function () {
          selectFreezerLocation(box);
        })
    );
    container.append(
      $("<button>")
        .attr("id", "removeFreezerLocation")
        .addClass("ui-state-default")
        .attr("type", "button")
        .css("margin-left", "2px")
        .text("Remove")
        .click(function () {
          removeFreezerLocation(box);
        })
    );
    return container;
  }

  function scanFreezerLocation(box) {
    Utils.showDialog(
      "Scan Freezer Location",
      "OK",
      [
        {
          label: "Location Barcode",
          property: "barcode",
          required: true,
          type: "text",
        },
      ],
      function (results) {
        var url =
          Urls.rest.storageLocations.queryByBarcode +
          "?" +
          Utils.page.param({
            q: results.barcode,
          });
        Utils.ajaxWithDialog("Finding Location...", "GET", url, null, function (data) {
          selectLocation(data, box);
        });
      }
    );
  }

  function selectFreezerLocation(box) {
    Utils.ajaxWithDialog(
      "Finding Freezers...",
      "GET",
      Urls.rest.storageLocations.freezers,
      null,
      function (data) {
        if (data && data.length) {
          showFreezerLocationSelect(box, data);
        } else {
          Utils.showOkDialog("Error", "No freezers found");
        }
      }
    );
  }

  function showFreezerLocationSelect(box, options, parentLocation) {
    var prompt = parentLocation ? parentLocation.fullDisplayLocation : null;
    var labelProperty = parentLocation ? "displayLocation" : "fullDisplayLocation";
    var actions = options.sort(Utils.sorting.standardSort(labelProperty)).map(function (location) {
      return {
        name: location[labelProperty],
        handler: function () {
          selectLocation(location, box);
        },
      };
    });
    Utils.showWizardDialog("Select Location", actions, prompt);
  }

  function selectLocation(location, box) {
    if (location.availableStorage) {
      box.storageLocationId = location.id;
      $("#freezerLocationLabel").text(location.fullDisplayLocation);
    } else {
      Utils.ajaxWithDialog(
        "Finding Storage...",
        "GET",
        Urls.rest.storageLocations.children(location.id),
        null,
        function (data) {
          if (data && data.length) {
            showFreezerLocationSelect(box, data, location);
          } else {
            Utils.showOkDialog("Select Location", [
              "No space in the selected location:",
              location.fullDisplayLocation,
            ]);
          }
        }
      );
    }
  }

  function removeFreezerLocation(box) {
    box.storageLocationId = null;
    $("#freezerLocationLabel").text("Not specified");
  }
})(jQuery);
