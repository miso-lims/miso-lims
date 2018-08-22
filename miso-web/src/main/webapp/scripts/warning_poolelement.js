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
    tableWarnings: function(duplicateSequences, nearDuplicateSequences){
      return function(data, type, dilution){
        var indices = Constants.indexFamilies.reduce(function(acc, family) {
          return acc.concat(family.indices.filter(function(index) {
            return dilution.indexIds.indexOf(index.id) != -1;
          }));
        }, []).sort(function(a, b) {
          return a.position - b.position;
        });

        var combined = indices.map(function(index) {
          return index.sequence;
        }).join('');
        
        var warnings = [];
        warnings = Warning.addWarnings([
          [duplicateSequences && duplicateSequences.indexOf(combined) != -1, "(DUPLICATE INDEX)"],
          [nearDuplicateSequences && nearDuplicateSequences.indexOf(combined) != -1 && 
            !(duplicateSequences && duplicateSequences.indexOf(combined) != -1), "(NEAR-DUPLICATE INDEX)"],
          [Utils.validation.isEmpty(combined), "(NO INDEX)"],
          [dilution.identityConsentLevel === 'Revoked', '(CONSENT REVOKED)']
          ], warnings);
        return Warning.generateTableWarnings(data, warnings);
      };
    }
};
