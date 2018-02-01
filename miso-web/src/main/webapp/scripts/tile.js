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

TileTarget = {};

Tile = (function() {
  var showPane = function(elementId, target, inner) {
    var div = document.getElementById(elementId);
    div.setAttribute("class", "dashboard_widget");
    while (div.hasChildNodes()) {
      div.removeChild(div.lastChild);
    }
    var title = document.createElement("DIV");
    title.setAttribute("class", "widget_title ui-corner-top");
    title.innerText = target.name;
    div.appendChild(title);

    var content = document.createElement("DIV");
    content.setAttribute("class", "widget ui-corner-bottom");
    content.appendChild(inner);
    div.appendChild(content);
  };

  return {
    title: function(label, status) {
      var title = document.createElement("DIV");
      title.setAttribute("class", "name");
      title.setAttribute("style", "font-weight:bold");
      title.innerText = label;
      title.appendChild(status);
      return title;
    },
    error: function(message) {
      var errorP = document.createElement("P");
      errorP.setAttribute("class", "parsley-error");
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
      var status = document.createElement("SPAN");
      status.setAttribute("style", "color:#66ce16");
      status.innerText = "⬤";
      return status;
    },
    statusBad: function() {
      var status = document.createElement("SPAN");
      status.setAttribute("style", "color:#db0f34");
      status.innerText = "⬛";
      return status;
    },
    statusBusy: function() {
      var status = document.createElement("SPAN");
      status.setAttribute("style", "color:#4286f4");
      status.innerText = "🞜";
      return status;
    },
    make: function(tileparts, clickHandler) {
      var div = document.createElement("DIV");
      div.setAttribute("class", "tile");
      tileparts.forEach(function(part) {
        div.appendChild(part);
      })
      div.onclick = clickHandler;
      return div;
    },
    createPane: function(elementId, target, items) {
      var innerContent = document.createElement("DIV");
      items.map(target.transform).forEach(function(item) {
        if (item) {
          innerContent.appendChild(item);
        }
      });
      showPane(elementId, target, innerContent);
    },
    createPaneAjax: function(elementId, target, url) {
      var loader = document.createElement("IMG");
      loader.src = "/styles/images/ajax-loader.gif";
      showPane(elementId, target, loader);

      jQuery.ajax({
        'dataType': 'json',
        'type': 'GET',
        'url': url,
        'contentType': 'application/json; charset=utf8',
        'success': function(data, textStatus, xhr) {
          Tile.createPane(elementId, target, data);
        },
        'error': function(xhr, textStatus, errorThrown) {
          var errorMessage = document.createElement("P");
          try {
            var responseObj = JSON.parse(xhr.responseText);
            if (responseObj.detail) {
              errorMessage.innerText = responseObj.detail;
            }
          } catch (e) {
            errorMessage.innerText = errorThrown;
          }
          showPane(elementId, target, errorMessage);
        }
      });
    }
  };
})();
