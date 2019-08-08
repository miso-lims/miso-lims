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

WarningTarget.orderaliquot = {
  makeTarget: function(duplicateSequences, nearDuplicateSequences) {
    return {
      getWarnings: function(orderaliquot) {
        var indices =
          orderaliquot.aliquot.indexIds === null
            ? []
            : Constants.indexFamilies
                .reduce(function(acc, family) {
                  return acc.concat(
                    family.indices.filter(function(index) {
                      return (
                        orderaliquot.aliquot.indexIds.indexOf(index.id) != -1
                      );
                    })
                  );
                }, [])
                .sort(function(a, b) {
                  return a.position - b.position;
                });

        var combined = indices
          .map(function(index) {
            return index.sequence;
          })
          .join("-");

        return [
          {
            include: orderaliquot.aliquot.subprojectPriority,
            tableMessage:
              "PRIORITY (" + orderaliquot.aliquot.subprojectAlias + ")",
            level: "important"
          },
          {
            include: orderaliquot.aliquot.libraryLowQuality,
            tableMessage: Constants.warningMessages.lowQualityLibraries + ")"
          },
          {
            include:
              duplicateSequences && duplicateSequences.indexOf(combined) != -1,
            tableMessage: Constants.warningMessages.duplicateIndices
          },
          {
            include:
              nearDuplicateSequences &&
              nearDuplicateSequences.indexOf(combined) != -1 &&
              !(
                duplicateSequences && duplicateSequences.indexOf(combined) != -1
              ),
            tableMessage: Constants.warningMessages.nearDuplicateIndices
          },
          {
            include: orderaliquot.aliquot.indexIds === null,
            tableMessage: Constants.warningMessages.missingIndex
          },
          {
            include: orderaliquot.aliquot.identityConsentLevel === "Revoked",
            tableMessage: Constants.warningMessages.consentRevoked
          }
        ];
      }
    };
  }
};
