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
  headerWarnings: function(pool){
    var warnings = [];
    warnings = Warning.addWarnings([
      [pool.duplicateIndices, "This pool contains duplicate indices!"],
      [pool.nearDuplicateIndices && !pool.duplicateIndices, "This pool contains near-duplicate indices!"],
      [pool.hasEmptySequence, "This pool contains at least one library with no index!"],
      [pool.hasLowQualityLibraries, "This pool contains at least one low quality library!"]
      ], warnings);
    return Warning.generateHeaderWarnings(warnings);
  },
  tableWarnings: function(data, type, pool){
    var warnings = [];
    warnings = Warning.addWarnings([
      [pool.duplicateIndices, "(DUPLICATE INDICES)"],
      [pool.nearDuplicateIndices && !pool.duplicateIndices, "(NEAR-DUPLICATE INDICES)"],
      [pool.hasEmptySequence, "(MISSING INDEX)"],
      [pool.hasLowQualityLibraries, "(LOW QUALITY LIBRARIES)"]
      ], warnings);
    return Warning.generateTableWarnings(data, warnings);
  },
  tileWarnings: function(pool){
    var warnings = [];
    warnings = Warning.addWarnings([
      [pool.duplicateIndices, "DUPLICATE INDICES"],
      [pool.nearDuplicateIndices && !pool.duplicateIndices, "NEAR-DUPLICATE INDICES"],
      [pool.hasEmptySequence, "MISSING INDEX"],
      [pool.hasLowQualityLibraries, "LOW QUALITY LIBRARIES"]
      ], warnings);
    return Warning.generateTileWarnings(warnings);
  },
  
};
