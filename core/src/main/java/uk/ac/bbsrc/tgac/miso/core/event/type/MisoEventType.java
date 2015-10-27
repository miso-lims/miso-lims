/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

package uk.ac.bbsrc.tgac.miso.core.event.type;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.type
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.1.5
 */
public enum MisoEventType {
  ALL_SAMPLES_QC_PASSED, LIBRARY_PREPARATION_COMPLETED, ALL_LIBRARIES_QC_PASSED, POOL_CONSTRUCTION_COMPLETE, ALL_RUNS_COMPLETED, PRIMARY_ANALYSIS_COMPLETED, RUN_STARTED, RUN_COMPLETED, RUN_FAILED, RUN_QC_ADDED, STATUS_CHANGED_EVENT, POOL_READY
}
