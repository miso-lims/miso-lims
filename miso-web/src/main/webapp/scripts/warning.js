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
var Warning = {
  generateHeaderWarnings: function(warnings){
    if(warnings.length == 0){
      return;
    }
    var html = '<span style="float:right;"><img src="/styles/images/fail.png"/></span>';
    warnings.forEach(function(warning){
      html += '<p class="big-warning">' + warning + '</p>';
    });
    return html;
  },
  generateTableWarnings: function(data, warnings){
    var html = data ? data : "";
    warnings.forEach(function(warning){
      html += " <span class='parsley-custom-error-message'><strong>" + warning + "</strong></span>";
    });
    return html;
  },
  generateTileWarnings: function(warnings){
    return warnings.map(Tile.error);
  },

  addWarnings: function(warningConditions, oldWarnings){
    warningConditions.forEach(function(warningCondition){
      if(warningCondition[0]){
        if(warningCondition[2] === null){
            warningCondition[2] = "error";
        }
        oldWarnings.push("<span class='message-" + warningCondition[2] + "'>" + warningCondition[1] + "</span>");
      }
    })
    return oldWarnings;
  },
  
};
