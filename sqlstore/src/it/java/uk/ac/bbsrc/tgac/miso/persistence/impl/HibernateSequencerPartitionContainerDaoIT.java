package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;

public class HibernateSequencerPartitionContainerDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private SecurityStore securityDao;

  @InjectMocks
  private HibernateSequencerPartitionContainerDao dao;

  private final User emptyUser = new UserImpl();

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);

    emptyUser.setId(1L);
    when(securityDao.getUserById(ArgumentMatchers.anyLong())).thenReturn(emptyUser);
  }

  @Test
  public void testListByBarcodeC075RACXX() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listSequencerPartitionContainersByBarcode("C075RACXX");
    assertEquals(1, spcs.size());
  }

  @Test
  public void testListByBarcodeNone() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listSequencerPartitionContainersByBarcode("A0AAAAAXX");
    assertEquals(0, spcs.size());
  }

  @Test
  public void testListByBarcodeEmpty() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listSequencerPartitionContainersByBarcode("A0AAAAAXX");
    assertEquals(0, spcs.size());
  }

  @Test
  public void testListByRunId() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listAllSequencerPartitionContainersByRunId(1L);
    assertEquals(1, spcs.size());
  }

  @Test
  public void testListByRunIdNone() throws IOException {
    List<SequencerPartitionContainer> spcs = dao.listAllSequencerPartitionContainersByRunId(9999L);
    assertEquals(0, spcs.size());
  }

  @Test
  public void testGet() throws IOException {
    SequencerPartitionContainer spc = dao.get(1L);
    assertNonLazyThings(spc);
  }

  @Test
  public void testSaveEdit() throws IOException {
    SequencerPartitionContainer spc = dao.get(4L);

    SequencingContainerModel model =
        (SequencingContainerModel) sessionFactory.getCurrentSession().get(SequencingContainerModel.class, 1L);
    spc.setModel(model);
    spc.setLastModifier(emptyUser);
    Run run = Mockito.mock(Run.class);
    Mockito.when(run.getId()).thenReturn(1L);
    spc.setIdentificationBarcode("ABCDEFXX");

    dao.update(spc);
    assertEquals(4L, spc.getId());
    SequencerPartitionContainer savedSPC = dao.get(4L);
    assertEquals(spc.getId(), savedSPC.getId());
    assertEquals("ABCDEFXX", savedSPC.getIdentificationBarcode());
  }

  @Test
  public void testCreateNull() throws IOException {
    exception.expect(IllegalArgumentException.class);
    dao.create(null);
  }

  @Test
  public void testUpdateNull() throws IOException {
    exception.expect(IllegalArgumentException.class);
    dao.update(null);
  }

  @Test
  public void testSaveNew() throws IOException {
    SequencerPartitionContainer newSPC = makeSPC("ABCDEFXX");

    assertEquals(SequencerPartitionContainerImpl.UNSAVED_ID, newSPC.getId());
    dao.create(newSPC);
    assertNotEquals(SequencerPartitionContainerImpl.UNSAVED_ID, newSPC.getId());

    SequencerPartitionContainer savedSPC = dao.get(newSPC.getId());
    assertEquals(newSPC.getIdentificationBarcode(), savedSPC.getIdentificationBarcode());
  }

  private SequencerPartitionContainer makeSPC(String identificationBarcode) throws IOException {
    SequencerPartitionContainer pc = new SequencerPartitionContainerImpl();
    Date now = new Date();
    pc.setIdentificationBarcode(identificationBarcode);
    SequencingContainerModel model =
        (SequencingContainerModel) sessionFactory.getCurrentSession().get(SequencingContainerModel.class, 1L);
    pc.setModel(model);
    pc.setCreationTime(now);
    pc.setCreator(emptyUser);
    pc.setLastModified(now);
    pc.setLastModifier(emptyUser);
    return pc;
  }

  private void assertNonLazyThings(SequencerPartitionContainer spc) {
    assertNotNull(spc);
    assertFalse(spc.getPartitions().isEmpty());
  }

  @Test
  public void testGetModel() throws Exception {
    assertNotNull(dao.get(1L));
  }

  @Test
  public void testGetPartitionIdByRunIdAndPartitionNumber() throws Exception {
    assertEquals(Long.valueOf(11L), dao.getPartitionIdByRunIdAndPartitionNumber(2L, 3));
  }

}
