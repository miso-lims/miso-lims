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

WarningTarget.experiment_run_partition = {
  tableWarnings: function(data, type, full) {
    if (!data) {
      if (type === 'display') {
        return "(None)";
      } else {
        return "";
      }
    }
    var prettyName = data.name + " (" + data.alias + ")";
    if (type === 'display') {
      var warnings = [];
      if (data.duplicateIndices) {
        warnings.push("(DUPLICATE INDICES)");
      } else if (data.nearDuplicateIndices) {
        warnings.push("(NEAR-DUPLICATE INDICES)");
      }
      if (data.hasLowQualityLibraries) {
        warnings.push("(LOW QUALITY LIBRARIES)");
      }
      if(data.hasEmptySequence){
        warnings.push("(MISSING INDEX)");
      }

      return Warning.generateTableWarnings("<a href=\"/miso/pool/" + data.id + "\">" + prettyName + "</a>", warnings);
    } else {
      return prettyName;
    }

  }
};
