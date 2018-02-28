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

var Tile = {
  title: function(label) {
    var title = document.createElement("DIV");

    title.setAttribute("class", "name");
    title.setAttribute("style", "font-weight:bold");
    title.innerText = label;

    return title;
  },
  titleAndStatus: function(label, status) {
    var title = this.title(label);

    title.append(status);

    return title;
  },
  error: function(message) {
    var errorP = document.createElement("P");
    errorP.setAttribute("class", "parsley-custom-error-message");
    errorP.innerText = "⚠ " + message;
    return errorP;
  },
  lines: function(lines, special) {
    var p = document.createElement("P");
    if (special) {
      p.setAttribute("style", "font-style:italic");
    }

    lines.filter(function(line) {
      return !!line;
    }).forEach(function(line, index, arr) {
      p.appendChild(document.createTextNode(line));
      if (index < arr.length - 1) {
        p.appendChild(document.createElement("BR"));
      }
    });
    return p;
  },
  statusOk: function() {
    var status = document.createElement("IMG");
    status.setAttribute("src", "/styles/images/tile-ok.svg");
    return status;
  },
  statusBad: function() {
    var status = document.createElement("IMG");
    status.setAttribute("src", "/styles/images/tile-bad.svg");
    return status;
  },
  statusBusy: function() {
    var status = document.createElement("IMG");
    status.setAttribute("src", "/styles/images/tile-busy.svg");
    return status;
  },
  make: function(tileparts, clickHandler) {
    var div = document.createElement("DIV");
    div.setAttribute("class", "tile");
    tileparts.forEach(function(part) {
      div.appendChild(part);
    });
    div.onclick = clickHandler;
    return div;
  },
};
