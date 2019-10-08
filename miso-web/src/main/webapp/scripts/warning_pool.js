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

WarningTarget.pool = {
  getWarnings: function(pool) {
    var revokedAliquots = [];
    if (pool.pooledElements) {
      revokedAliquots = pool.pooledElements.filter(function(element) {
        return element.identityConsentLevel === 'Revoked';
      }).map(function(element) {
        return element.name;
      })
    }
    return [
        {
          include: pool.prioritySubprojectAliases && pool.prioritySubprojectAliases.length > 0,
          headerMessage: 'Belongs to high priority subproject'
              + (pool.prioritySubprojectAliases.length == 1 ? ' \'' + pool.prioritySubprojectAliases[0] + '\'' : 's: '
                  + pool.prioritySubprojectAliases.join(', ')),
          tableMessage: 'PRIORITY (' + (pool.prioritySubprojectAliases.length == 1 ? pool.prioritySubprojectAliases[0] : 'MULTIPLE') + ')',
          tileMessage: 'PRIORITY ('
              + (pool.prioritySubprojectAliases.length == 1 ? pool.prioritySubprojectAliases[0] : pool.prioritySubprojectAliases.join(', '))
              + ')',
          level: 'important'
        }, {
          include: pool.duplicateIndices,
          headerMessage: 'This pool contains duplicate indices!',
          tableMessage: Constants.warningMessages.duplicateIndices,
          tileMessage: Constants.warningMessages.duplicateIndices
        }, {
          include: pool.nearDuplicateIndices && !pool.duplicateIndices,
          headerMessage: 'This pool contains near-duplicate indices!',
          tableMessage: Constants.warningMessages.nearDuplicateIndices,
          tileMessage: Constants.warningMessages.nearDuplicateIndices
        }, {
          include: pool.hasEmptySequence && (!pool.pooledElements || pool.pooledElements.length > 1),
          headerMessage: 'This pool contains at least one library with no index!',
          tableMessage: Constants.warningMessages.missingIndex,
          tileMessage: Constants.warningMessages.missingIndex
        }, {
          include: pool.hasLowQualityLibraries,
          headerMessage: 'This pool contains at least one low quality library!',
          tableMessage: Constants.warningMessages.lowQualityLibraries,
          tileMessage: Constants.warningMessages.lowQualityLibraries
        }, {
          include: revokedAliquots.length > 0,
          headerMessage: "Donor has revoked consent for " + revokedAliquots.toString(),
          tableMessage: Constants.warningMessages.consentRevoked,
          tileMessage: Constants.warningMessages.consentRevoked
        }];
  }

};
