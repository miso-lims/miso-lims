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

PaneTarget.project = (function() {
  var title = "Project";
  var url = "/miso/rest/projects/search";

  var transform = function(project) {
    return Tile.make([Tile.title(project.shortName || project.alias),
        Tile.lines(["Name: " + project.name, project.shortName ? ("Alias: " + project.alias) : null].filter(function(x) {
          return !!x;
        }))], function() {
      window.location = window.location.origin + '/miso/project/' + project.id;
    });
  };

  var createHelpMessage = function() {
    var message = document.createElement("P");

    message.innerText = "Search projects by name, alias, short name, or description.";

    return message;
  };

  return {
    createPane: function(paneId) {
      var divs = Pane.createSearchPane(paneId, title);

      Pane.updateDiv(divs.content, createHelpMessage());
      Pane.registerSearchHandlers(divs.input, transform, url, divs.content, true);
    }
  }
})();
