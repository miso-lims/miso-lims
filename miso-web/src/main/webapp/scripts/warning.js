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

  return {
    generateHeaderWarnings: function(containerId, target, item) {
      // Note: header warnings currently ignore the warning level and show all warnings in red
      var warnings = getWarnings(target, item);
      if (warnings.length == 0) {
        return;
      }
      var container = $('#' + containerId);
      container.append($('<span>').css('float', 'right').append($('<img>').attr('src', '/styles/images/fail.png')));
      warnings.forEach(function(warning) {
        container.append($('<p>').addClass('big-warning').text(warning.headerMessage));
      });
    },

    tableWarningRenderer: function(target, makeLink) {
      return function(data, type, full) {
        if (type !== 'display') {
          return data || '';
        }
        var html = '';
        if (data) {
          if (makeLink) {
            html += '<a href="' + makeLink(full) + '">';
          }
          html += data;
          if (makeLink) {
            html += '</a>';
          }
        }
        getWarnings(target, full).forEach(function(warning) {
          html += ' <span class="message-' + (warning.level || 'error') + '"><strong>' + warning.tableMessage + '</strong></span>';
        });
        return html;
      };
    },

    hasWarnings: function(target, item) {
      return getWarnings(target, item).length > 0;
    },

    generateTileWarnings: function(target, item) {
      return getWarnings(target, item).map(function(warning) {
        var errorP = document.createElement('P');
        errorP.setAttribute('class', 'message-' + (warning.level || 'error'));
        errorP.innerText = "⚠ " + warning.tileMessage;
        return errorP;
      });
    }
  }

  function getWarnings(target, item) {
    // warnings: { include: boolean, tileMessage: string, tableMessage: string, headerMessage: string, level: string ('info'|'error') }
    return target.getWarnings(item).filter(function(warning) {
      return warning.include;
    });
  }

}(jQuery));
