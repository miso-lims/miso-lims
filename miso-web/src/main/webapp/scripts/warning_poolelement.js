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

WarningTarget.poolelement = {
  makeTarget: function(duplicateSequences, nearDuplicateSequences) {
    return {
      getWarnings: function(element) {
        var combined = null;
        if (element.indexIds) {
          var indices = Constants.indexFamilies.reduce(function(acc, family) {
            return acc.concat(family.indices.filter(function(index) {
              return element.indexIds.indexOf(index.id) != -1;
            }));
          }, []).sort(function(a, b) {
            return a.position - b.position;
          });

          combined = indices.map(function(index) {
            return index.sequence;
          }).join('-');
        }

        return [
            {
              include: element.subprojectPriority,
              tableMessage: 'PRIORITY (' + element.subprojectAlias + ')',
              level: 'important'
            },
            {
              include: element.libraryLowQuality,
              tableMessage: Constants.warningMessages.lowQualityLibraries + ')'
            },
            {
              include: duplicateSequences && combined && duplicateSequences.indexOf(combined) != -1,
              tableMessage: Constants.warningMessages.duplicateIndices
            },
            {
              include: nearDuplicateSequences && combined && nearDuplicateSequences.indexOf(combined) != -1
                  && !(duplicateSequences && duplicateSequences.indexOf(combined) != -1),
              tableMessage: Constants.warningMessages.nearDuplicateIndices
            }, {
              include: Utils.validation.isEmpty(combined),
              tableMessage: Constants.warningMessages.missingIndex
            }, {
              include: element.identityConsentLevel === 'Revoked',
              tableMessage: Constants.warningMessages.consentRevoked
            }, {
              include: !!element.sequencingControlTypeAlias,
              tableMessage: 'Sequencing Control: ' + element.sequencingControlTypeAlias,
              level: 'info'
            }];
      }
    };
  }
};
