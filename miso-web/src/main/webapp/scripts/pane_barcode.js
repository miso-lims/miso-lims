PaneTarget.barcode = (function () {
  var title = "Search";
  var url = Urls.rest.barcodables.search;

  var transform = function (searchResult) {
    return Tile.make(
      [
        Tile.title(searchResult.entityType),
        Tile.lines(
          [
            "Name: " + (searchResult.name || "n/a"),
            searchResult.alias ? "Alias: " + searchResult.alias : null,
            searchResult.identificationBarcode
              ? "Barcode: " + searchResult.identificationBarcode
              : null,
          ].filter(function (x) {
            return !!x;
          })
        ),
      ],
      function () {
        if (searchResult.url) {
          window.location = window.location.origin + searchResult.url;
        } else {
          Utils.showOkDialog("Error", ["This item does not have its own page"]);
        }
      }
    );
  };

  var createHelpMessage = function () {
    var message = document.createElement("P");

    message.innerText =
      "Search barcodable items by name, alias, or barcode.  " +
      "Alternatively, scan a barcode to see any matching items here.";

    return message;
  };

  return {
    createPane: function (paneId) {
      var divs = Pane.createSearchPane(paneId, title);

      Pane.updateDiv(divs.content, createHelpMessage());
      Pane.registerSearchHandlers(divs.input, transform, url, divs.content, true);
      Pane.setFocusOnReady(divs.input);
    },
  };
})();
