PaneTarget.run = (function () {
  var title = "Run";
  var url = Urls.rest.runs.search;

  var transform = function (run) {
    return Tile.make(
      [Tile.title(run.alias), Tile.lines(["Name: " + run.name, "Status: " + run.status])],
      function () {
        window.location = Urls.ui.runs.edit(run.id);
      }
    );
  };

  return {
    createPane: function (paneId) {
      var divs = Pane.createSearchPane(paneId, title);

      Pane.updateTiles(divs.content, transform, Urls.rest.runs.recent, null, []);
      Pane.registerSearchHandlers(divs.input, transform, url, divs.content, true);
    },
  };
})();
