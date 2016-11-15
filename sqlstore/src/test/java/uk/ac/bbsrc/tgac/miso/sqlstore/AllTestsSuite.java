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

@Suite.SuiteClasses({ SQLAlertDAOTest.class, SQLLibraryQCDAOTest.class, SQLTargetedResequencingDAOTest.class, SQLRunDAOTest.class,
    SQLPoolQCDAOTest.class, SQLStatusDAOTest.class,
    SQLStudyDAOTest.class, SQLKitDAOTest.class, SQLSampleDAOTest.class, SQLSampleQCDAOTest.class, SQLSequencerServiceRecordDAOTest.class,
    SQLProjectDAOTest.class, SQLNoteDAOTest.class, SQLSequencerReferenceDAOTest.class, SQLRunQCDAOTest.class, SQLEmPCRDAOTest.class,
    SQLSecurityDAOTest.class, SQLSequencerPartitionContainerDAOTest.class, SQLBoxDAOTest.class, SQLSequencerServiceRecordDAOTest.class,
    SQLExperimentDAOTest.class, SQLPlatformDAOTest.class, SQLSequencerServiceRecordDAOTest.class, SQLPoolDAOTest.class,
    SQLLibraryDAOTest.class, SQLLibraryDilutionDAOTest.class, SQLChangeLogDAOTest.class, SQLPrintJobDAOTest.class, SQLSecurityProfileDAOTest.class })

public class AllTestsSuite {
}
