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
    var revokedDilutions = [];
    if (pool.pooledElements) {
      revokedDilutions = pool.pooledElements.filter(function(dilution) {
        return dilution.identityConsentLevel === 'Revoked';
      }).map(function(dilution) {
        return dilution.name;
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
          level: 'info'
        }, {
          include: pool.duplicateIndices,
          headerMessage: 'This pool contains duplicate indices!',
          tableMessage: '(DUPLICATE INDICES)',
          tileMessage: 'DUPLICATE INDICES'
        }, {
          include: pool.nearDuplicateIndices && !pool.duplicateIndices,
          headerMessage: 'This pool contains near-duplicate indices!',
          tableMessage: '(NEAR-DUPLICATE INDICES)',
          tileMessage: 'NEAR-DUPLICATE INDICES'
        }, {
          include: pool.hasEmptySequence,
          headerMessage: 'This pool contains at least one library with no index!',
          tableMessage: '(MISSING INDEX)',
          tileMessage: 'MISSING INDEX'
        }, {
          include: pool.hasLowQualityLibraries,
          headerMessage: 'This pool contains at least one low quality library!',
          tableMessage: '(LOW QUALITY LIBRARIES)',
          tileMessage: 'LOW QUALITY LIBRARIES'
        }, {
          include: revokedDilutions.length > 0,
          headerMessage: "Donor has revoked consent for " + revokedDilutions.toString(),
          tableMessage: '(CONSENT REVOKED)',
          tileMessage: 'CONSENT REVOKED'
        }];
  }

};
