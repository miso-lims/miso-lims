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

WarningTarget.partition = {
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
      warnings = Warning.addWarnings([
        [data.prioritySubprojectAliases && data.prioritySubprojectAliases.length > 0,
          '<span class="parsley-subproject-error">PRIORITY ('
          + (data.prioritySubprojectAliases.length == 1 ? data.prioritySubprojectAliases[0] : 'MULTIPLE')
          + ')</span>'],
        [data.duplicateIndices, "(DUPLICATE INDICES)"],
        [data.nearDuplicateIndices && !data.duplicateIndices, "(NEAR-DUPLICATE INDICES)"],
        [data.hasEmptySequence, "(MISSING INDEX)"],
        [data.hasLowQualityLibraries, "(LOW QUALITY LIBRARIES)"],
        [data.pooledElements && data.pooledElements.some(function(dilution){
          return dilution.identityConsentLevel === 'Revoked';
        }), "(CONSENT REVOKED)"]
        ], warnings);
      return Warning.generateTableWarnings("<a href=\"/miso/pool/" + data.id + "\">" + prettyName + "</a>", warnings);
    } else {
      return prettyName;
    }

  }
};
