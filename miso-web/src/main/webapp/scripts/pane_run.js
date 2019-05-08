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

PaneTarget.run = (function() {
  var title = "Run";
  var url = "/miso/rest/runs/search";

  var transform = function(run) {
    return Tile.make([Tile.title(run.alias), Tile.lines(["Name: " + run.name, "Status: " + run.status])], function() {
      window.location = window.location.origin + '/miso/run/' + run.id;
    });
  };

  return {
    createPane: function(paneId) {
      var divs = Pane.createSearchPane(paneId, title);

      Pane.updateTiles(divs.content, transform, '/miso/rest/runs/recent', null, []);
      Pane.registerSearchHandlers(divs.input, transform, url, divs.content, true);
    }
  }
})();
