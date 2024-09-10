PaneTarget.workflow = (function () {
  var title = "Workflows";
  var url = Urls.rest.workflows.list;

  var transform = function (data) {
    return Tile.make(
      [
        Tile.title(data.name),
        Tile.lines([
          data.complete ? "Complete" : "Step: " + data.stepNumber,
          data.message,
          "Last Modified: " + data.lastModified,
        ]),
      ],
      function () {
        window.location = Urls.ui.workflows.edit(data.workflowId);
      }
    );
  };

  return {
    createPane: function (paneId) {
      var divs = Pane.createPane(paneId, title);

      var createWorkflowTiles = favouriteWorkflows.map(function (data) {
        return Tile.make([Tile.title("Begin New " + data.description)], function () {
          window.location = Urls.ui.workflows.create(data.workflowName);
        });
      });

      createWorkflowTiles.push(
        Tile.make([Tile.title("Begin New Workflow")], function () {
          Utils.showWizardDialog(
            "Begin New Workflow",
            Constants.workflows.map(function (workflow) {
              return {
                name: workflow.description,
                handler: function () {
                  window.location = Urls.ui.workflows.create(workflow.workflowName);
                },
              };
            })
          );
        })
      );

      createWorkflowTiles.push(
        Tile.make([Tile.title("Edit Favourite Workflows")], function () {
          Utils.showWizardDialog(
            "Edit Favourite Workflows",
            Constants.workflows.map(function (workflow) {
              if (
                favouriteWorkflows
                  .map(function (favourite) {
                    return favourite.workflowName;
                  })
                  .indexOf(workflow.workflowName) < 0
              ) {
                return {
                  name: "Add " + workflow.description + " to Favourites",
                  handler: function () {
                    Utils.ajaxWithDialog(
                      "Adding Workflow to Favourites",
                      "POST",
                      Urls.rest.workflows.addFavourite(workflow.workflowName),
                      null,
                      Utils.page.pageReload
                    );
                  },
                };
              } else {
                return {
                  name: "Remove " + workflow.description + " from Favourites",
                  handler: function () {
                    Utils.ajaxWithDialog(
                      "Removing Workflow from Favourites",
                      "POST",
                      Urls.rest.workflows.removeFavourite(workflow.workflowName),
                      null,
                      Utils.page.pageReload
                    );
                  },
                };
              }
            })
          );
        })
      );

      Pane.updateTiles(divs.content, transform, url, null, createWorkflowTiles);
    },
  };
})();
