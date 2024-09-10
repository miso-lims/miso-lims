PaneTarget.instrument_status = (function () {
  var title = "Instrument Status";
  var url = Urls.rest.instrumentStatus;

  var transform = function (data) {
    var positionCount = data.positions.length;
    var busyCount = data.positions.filter(function (position) {
      return position.run && position.run.status === "Running";
    }).length;
    var outOfServiceCount = data.positions.filter(function (position) {
      return position.outOfService;
    }).length;

    var status;
    var statusDate = null;
    var statusFunction;

    if (busyCount === 0 && outOfServiceCount === 0) {
      status = "Idle";
      statusFunction = Tile.statusOk;
      data.positions.forEach(function (position) {
        if (
          position.run &&
          position.run.endDate &&
          (!statusDate || position.run.endDate < statusDate)
        ) {
          statusDate = position.run.endDate;
        }
      });
    } else if (busyCount + outOfServiceCount < positionCount) {
      status = "Available";
      statusFunction = Tile.statusMaybe;
      data.positions.forEach(function (position) {
        if (
          position.run &&
          position.run.endDate &&
          !position.outOfService &&
          (!statusDate || position.run.endDate < statusDate)
        ) {
          statusDate = position.run.endDate;
        }
      });
    } else if (outOfServiceCount === positionCount) {
      status = "Out of Service";
      statusFunction = Tile.statusBad;
      statusDate = data.positions
        .map(function (pos) {
          return pos.outOfServiceTime;
        })
        .filter(function (date) {
          return date != null;
        })
        .sort()
        .reverse()[0];
    } else {
      status = "Busy";
      statusFunction = Tile.statusBusy;
      data.positions.forEach(function (position) {
        if (!statusDate || position.run.startDate > statusDate) {
          statusDate = position.run.startDate;
        }
      });
    }

    var lines = [];
    switch (positionCount) {
      case 1:
        if (data.positions[0].run) {
          lines.push(
            data.positions[0].run.status +
              " " +
              data.positions[0].run.name +
              " (" +
              data.positions[0].run.alias +
              ")"
          );
        }
        break;
      case 2:
        if (
          data.positions.some(function (pos) {
            return !pos.outOfService;
          })
        ) {
          data.positions.sort(Utils.sorting.standardSort("position")).forEach(function (pos) {
            if (pos.outOfService) {
              lines.push(pos.position + ": Out of service");
            } else if (pos.run) {
              lines.push(
                pos.position +
                  ": " +
                  pos.run.status +
                  " " +
                  pos.run.name +
                  " (" +
                  pos.run.alias +
                  ")"
              );
            }
          });
        }
        break;
      default:
        if (outOfServiceCount != positionCount) {
          lines.push(busyCount + "/" + positionCount + " positions active");
        }
        if (outOfServiceCount > 0) {
          lines.push(outOfServiceCount + "/" + positionCount + " positions out of service");
        }
        break;
    }
    lines.push(status + (statusDate ? " since " + statusDate : ""));

    return Tile.make(
      [Tile.titleAndStatus(data.instrument.name, statusFunction(status)), Tile.lines(lines, false)],
      function () {
        var links = [
          {
            name: "View Instrument (" + data.instrument.name + ")",
            handler: function () {
              window.location = Urls.ui.instruments.edit(data.instrument.id);
            },
          },
        ];

        data.positions.forEach(function (position) {
          if (position.run) {
            links.push({
              name:
                "View " +
                (position.run.status === "Running" ? "" : "Last") +
                " Run (" +
                position.run.alias +
                ")",
              handler: function () {
                window.location = Urls.ui.runs.edit(position.run.id);
              },
            });
            if (position.pools) {
              position.pools.forEach(function (pool) {
                links.push({
                  name: "View " + pool.name + " (" + pool.alias + ")",
                  handler: function () {
                    window.location = Urls.ui.pools.edit(pool.id);
                  },
                });
              });
            }
          }
        });
        Utils.showWizardDialog(data.instrument.name, links);
      }
    );
  };

  return {
    createPane: function (paneId) {
      var divs = Pane.createPane(paneId, title);

      Pane.updateTiles(divs.content, transform, url, null, []);
    },
  };
})();
