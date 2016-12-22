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

package uk.ac.bbsrc.tgac.miso.sqlstore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 12-Jul-2011
 * @since 0.0.3
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
    SQLAlertDAOTest.class, //
    SQLBoxDAOTest.class, //
    SQLChangeLogDAOTest.class, //
    SQLEmPCRDAOTest.class, //
    SQLExperimentDAOTest.class, //
    SQLKitComponentDAOTest.class, //
    SQLKitComponentDescriptorDAOTest.class, //
    SQLKitDescriptorDAOTest.class, //
    SQLLibraryDAOTest.class, //
    SQLLibraryDilutionDAOTest.class, //
    SQLLibraryQCDAOTest.class, //
    SQLNoteDAOTest.class, //
    SQLPlatformDAOTest.class, //
    SQLPoolDAOTest.class, //
    SQLPoolQCDAOTest.class, //
    SQLPrintJobDAOTest.class, //
    SQLPrintServiceDAOTest.class, //
    SQLProjectDAOTest.class, //
    SQLReferenceGenomeDAOTest.class, //
    SQLRunDAOTest.class, //
    SQLRunQCDAOTest.class, //
    SQLSampleDAOTest.class, //
    SQLSampleQCDAOTest.class, //
    SQLSecurityDAOTest.class, //
    SQLSecurityProfileDAOTest.class, //
    SQLSequencerPartitionContainerDAOTest.class, //
    SQLSequencerPoolPartitionDAOTest.class, //
    SQLSequencerReferenceDAOTest.class, //
    SQLSequencerServiceRecordDAOTest.class, //
    SQLStatusDAOTest.class, //
    SQLStudyDAOTest.class, //
    SQLTargetedSequencingDAOTest.class, //
    SQLWatcherDAOTest.class //
})

public class AllTestsSuite {
}
