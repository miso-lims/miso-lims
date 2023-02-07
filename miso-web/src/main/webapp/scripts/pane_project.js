PaneTarget.project = (function () {
  var title = "Project";

  var transform = function (project) {
    return Tile.make(
      [
        Tile.title(project.shortName || project.alias),
        Tile.lines(
          ["Name: " + project.name, project.shortName ? "Alias: " + project.alias : null].filter(
            function (x) {
              return !!x;
            }
          )
        ),
      ],
      function () {
        window.location = Urls.ui.projects.edit(project.id);
      }
    );
  };

  var createHelpMessage = function () {
    var message = document.createElement("P");

    message.innerText = "Search projects by name, alias, short name, or description.";

    return message;
  };

  return {
    createPane: function (paneId) {
      var divs = Pane.createSearchPane(paneId, title);

      Pane.updateDiv(divs.content, createHelpMessage());
      Pane.registerSearchHandlers(
        divs.input,
        transform,
        Urls.rest.projects.search,
        divs.content,
        true
      );
    },
  };
})();
