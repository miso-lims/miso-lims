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

package uk.ac.bbsrc.tgac.miso.integration.test;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.*;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.Resource;
import org.irods.jargon.core.pub.domain.Zone;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.integration.test
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 22-May-2012
 * @since 0.1.6
 */
public class JargonTests {
  protected static final Logger log = LoggerFactory.getLogger(JargonTests.class);
  private static IRODSFileSystem irodsFileSystem;
  private static ZoneAO zoneAO;
  private static ResourceAO resourceAO;
  private static CollectionAO collectionAO;
  private static CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO;
  private static DataObjectAO dataObjectAO;

  @BeforeClass
  public static void setUp() {
    IRODSAccount account = new IRODSAccount(
            "v0214.nbi.ac.uk", 1247, "rods", "rods", "/tempZone/home/rods", "tempZone", "demoResc");
    try {
      irodsFileSystem = IRODSFileSystem.instance();
      Assert.assertNotNull(irodsFileSystem);
      IRODSAccessObjectFactory irodsAccessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
      Assert.assertNotNull(irodsAccessObjectFactory);
      zoneAO = irodsAccessObjectFactory.getZoneAO(account);
      Assert.assertNotNull(zoneAO);
      resourceAO = irodsAccessObjectFactory.getResourceAO(account);
      Assert.assertNotNull(resourceAO);
      collectionAO = irodsAccessObjectFactory.getCollectionAO(account);
      Assert.assertNotNull(collectionAO);
      collectionListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(account);
      Assert.assertNotNull(collectionListAndSearchAO);
      dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(account);
      Assert.assertNotNull(dataObjectAO);
    }
    catch (JargonException e) {
      log.error("Cannot connect to iRODS instance", e);
      e.printStackTrace();
    }
  }

  @Test
  public void testListZones() {
    log.info("LISTING ZONES");
    try {
      for (Zone z : zoneAO.listZones()) {
        log.info(z.toString());
      }
    }
    catch (JargonException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testListResources() {
    log.info("LISTING RESOURCES");
    try {
      for (Resource r : resourceAO.listResourcesInZone("tempZone")) {
        log.info(r.toString());
      }
    }
    catch (JargonException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testListCollections() {
    log.info("LISTING COLLECTIONS");
    try {
      Assert.assertNotNull(collectionAO);
      for (Collection c : collectionAO.findAll("/tempZone/home/rods")) {
        log.info(c.toString());
      }
    }
    catch (JargonException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testListFiles() {
    log.info("LISTING FILES");
    try {
      List<CollectionAndDataObjectListingEntry> entries = collectionListAndSearchAO.listDataObjectsAndCollectionsUnderPath("/tempZone/home/rods");
      for (CollectionAndDataObjectListingEntry entry : entries) {
        recurse(entry);
      }
    }
    catch (JargonException e) {
      e.printStackTrace();
    }
    catch (JargonQueryException e) {
      e.printStackTrace();
    }
  }

  private List<CollectionAndDataObjectListingEntry> recurse(CollectionAndDataObjectListingEntry e) throws JargonException, JargonQueryException {
    if (e != null) {
      if (e.isCollection()) {
        log.info("Recursing -> " +e.getParentPath()+"/"+e.getLastPathComponentForCollectionName());
        List<MetaDataAndDomainData> metas = collectionAO.findMetadataValuesForCollection(e.getPathOrName());
        for (MetaDataAndDomainData data : metas) {
          log.info(data.toString());
        }
        for (CollectionAndDataObjectListingEntry entry : collectionListAndSearchAO.listDataObjectsAndCollectionsUnderPath(e.getParentPath()+"/"+e.getLastPathComponentForCollectionName())) {
          recurse(entry);
        }
      }
      else if (e.isDataObject()) {
        log.info("Leaf -> " +e.getPathOrName() + " ("+e.getDataSize()+")");
        List<MetaDataAndDomainData> metas = dataObjectAO.findMetadataValuesForDataObject(e.getPathOrName());
        for (MetaDataAndDomainData data : metas) {
          log.info(data.toString());
        }
      }
    }
    return null;
  }

  @AfterClass
  public static void tearDown() {
    try {
      irodsFileSystem.close();
    }
    catch (JargonException e) {
      e.printStackTrace();
    }
    finally {
      irodsFileSystem.closeAndEatExceptions();
    }
  }
}
