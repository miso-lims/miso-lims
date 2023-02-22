package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;
import com.google.common.annotations.VisibleForTesting;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateStorageLocationDaoIT extends AbstractDAOTest {

  private HibernateStorageLocationDao sut;

  @Before
  public void setup() {
    sut = new HibernateStorageLocationDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws Exception {
    long freezerId = 3L;
    StorageLocation freezer = sut.get(freezerId);
    assertNotNull(freezer);
    assertEquals(freezerId, freezer.getId());
  }

  @Test
  public void testGetByBarcode() throws Exception {
    String barcode = "room1barcode";
    StorageLocation room = sut.getByBarcode(barcode);
    assertNotNull(room);
    assertEquals(barcode, room.getIdentificationBarcode());
  }

  @Test
  public void testGetbyServiceRecord() throws Exception {
    ServiceRecord record = (ServiceRecord) currentSession.get(ServiceRecord.class, 4L);
    StorageLocation storageLocation = dao.getByServiceRecord(record);
    assertNotNull(storageLocation);
    assertEquals(1L, storageLocation.getId());
  }

  @Test
  public void testListRooms() throws Exception {
    List<StorageLocation> rooms = sut.listRooms();
    assertEquals(2, rooms.size());
    for (StorageLocation room : rooms) {
      assertEquals(LocationUnit.ROOM, room.getLocationUnit());
    }
  }

  @Test
  public void testListFreezers() throws Exception {
    List<StorageLocation> freezers = sut.listFreezers();
    assertEquals(2, freezers.size());
    for (StorageLocation freezer : freezers) {
      assertEquals(LocationUnit.FREEZER, freezer.getLocationUnit());
    }
  }

  @Test
  public void testCreate() throws Exception {
    String alias = "Test Room";
    StorageLocation room = new StorageLocation();
    room.setAlias(alias);
    room.setLocationUnit(LocationUnit.ROOM);
    room.setChangeDetails((User) currentSession().get(UserImpl.class, 1L));
    long savedId = sut.save(room);

    clearSession();

    StorageLocation saved = (StorageLocation) currentSession().get(StorageLocation.class, savedId);
    assertNotNull(saved);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws Exception {
    long locationId = 2L;
    String newAlias = "Changed";
    StorageLocation original = (StorageLocation) currentSession().get(StorageLocation.class, locationId);
    assertNotEquals(newAlias, original.getAlias());
    original.setAlias(newAlias);
    sut.save(original);

    clearSession();

    StorageLocation saved = (StorageLocation) currentSession().get(StorageLocation.class, locationId);
    assertEquals(newAlias, saved.getAlias());
  }

}
