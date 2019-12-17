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

var WarningTarget = {};
var Warning = (function($) {

  /*
   * warning format: {
   *   include: boolean,
   *   tileMessage: string,
   *   tableMessage: string,
   *   headerMessage: string,
   *   level: string ('error'|'important'|'info')
   * }
   * 
   * error = red, important = blue, info = grey
   * if a particular message is not provided, the warning will not be shown in that format
   */

  return {
    generateHeaderWarnings: function(containerId, target, item) {
      // Note: header warnings currently ignore the warning level and show all warnings in red
      var warnings = getWarnings(target, item, 'headerMessage');
      if (warnings.length == 0) {
        return;
      }
      var container = $('#' + containerId);
      warnings.forEach(function(warning) {
        container.append($('<p>').addClass('big big-' + (warning.level || 'error')).text(warning.headerMessage));
      });
    },

    tableWarningRenderer: function(target, makeLink) {
      return function(data, type, full) {
        if (type !== 'display') {
          return data || '';
        }
        var html = '';
        if (data) {
          var link = makeLink ? makeLink(full) : null;
          if (link) {
            html += '<a href="' + link + '">';
          }
          html += data;
          if (link) {
            html += '</a>';
          }
        }
        var warnings = getWarnings(target, full, 'tableMessage');
        if (warnings && warnings.length) {
          var level = 'info';
          if (warnings.some(function(warning) {
            return !warning.level || warning.level === 'error';
          })) {
            level = 'error';
          } else if (warnings.some(function(warning) {
            return !warning.level || warning.level === 'important';
          })) {
            level = 'important';
          }
          html += ' <div class="tooltip"><span class="message-' + level + ' warning-icon">⚠</span><span class="tooltiptext">'
              + warnings.map(function(warning) {
                return warning.tableMessage;
              }).join(';<br>') + '</span></div>';
        }
        return html;
      };
    },

    hasTileWarnings: function(target, item) {
      return getWarnings(target, item, 'tileMessage').length > 0;
    },

    generateTileWarnings: function(target, item) {
      return getWarnings(target, item, 'tileMessage').map(function(warning) {
        var errorP = document.createElement('P');
        errorP.setAttribute('class', 'message-' + (warning.level || 'error'));
        errorP.innerText = "⚠ " + warning.tileMessage;
        return errorP;
      });
    }
  }

  function getWarnings(target, item, messageProperty) {
    return target.getWarnings(item).filter(function(warning) {
      return warning.include && warning.hasOwnProperty(messageProperty);
    });
  }

}(jQuery));
