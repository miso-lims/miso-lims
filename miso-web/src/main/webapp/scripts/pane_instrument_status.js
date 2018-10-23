/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

PaneTarget.instrument_status = (function() {
  var title = "Instrument Status";
  var url = "/miso/rest/instrumentstatus";

  var transform = function(data) {
    var isRunning = data.run && data.run.status === 'Running';
    var status = null;
    var details = [];
    if (data.run) {
      details.push(data.run.name + ' (' + data.run.alias + ')');
    }
    if (isRunning) {
      status = Tile.statusBusy();
      details.push("Busy since " + data.run.startDate + (data.run.progress ? (" " + data.run.progress) : ""));
    } else if (data.outOfService) {
      status = Tile.statusBad();
      details.push("Out of service since " + data.outOfServiceTime);
    } else {
      status = Tile.statusOk();
      if (data.run) {
        details.push("Idle since " + data.run.endDate);
      } else {
        details.push("Idle");
      }
    }
    return Tile.make([Tile.titleAndStatus(data.instrument.name, status), Tile.lines(details)], function() {
      Utils.showWizardDialog(data.instrument.name, [{
        "name": "View Instrument (" + data.instrument.name + ")",
        "handler": function() {
          window.location = window.location.origin + '/miso/instrument/' + data.instrument.id;
        }
      }, data.run ? {
        "name": "View " + (data.run.status === 'Running' ? '' : "Last") + " Run (" + data.run.alias + ")",
        "handler": function() {
          window.location = window.location.origin + '/miso/run/' + data.run.id;
        }
      } : null].filter(function(x) {
        return !!x;
      }).concat(data.pools.map(function(pool) {
        return {
          "name": "View " + pool.name + " (" + pool.alias + ")",
          "handler": function() {
            window.location = window.location.origin + '/miso/pool/' + pool.id;
          }
        };
      })));
    });
  };

  return {
    createPane: function(paneId) {
      var divs = Pane.createPane(paneId, title);

      Pane.updateTiles(divs.content, transform, url, null, []);
    }
  };
})();
