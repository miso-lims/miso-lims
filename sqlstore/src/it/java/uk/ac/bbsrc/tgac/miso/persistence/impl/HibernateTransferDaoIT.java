package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;

public class HibernateTransferDaoIT extends AbstractDAOTest {

  private HibernateTransferDao sut;

  @Before
  public void setup() {
    sut = new HibernateTransferDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws Exception {
    long id = 1L;
    Transfer transfer = sut.get(id);
    assertNotNull(transfer);
    assertEquals(id, transfer.getId());
  }

  @Test
  public void testList() throws Exception {
    List<Transfer> list = sut.list();
    assertNotNull(list);
    assertEquals(3, list.size());
  }

  @Test
  public void testListByProperties() throws Exception {
    Lab sender = (Lab) currentSession().get(LabImpl.class, 1L);
    Group recipient = (Group) currentSession().get(Group.class, 1L);
    Project project = (Project) currentSession().get(ProjectImpl.class, 1L);
    Date transferTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-07-07 12:00:00");

    List<Transfer> list = sut.listByProperties(sender, recipient, project, transferTime);
    assertNotNull(list);
    assertEquals(1, list.size());

    Transfer transfer = list.get(0);
    assertEquals(sender.getId(), transfer.getSenderLab().getId());
    assertEquals(recipient.getId(), transfer.getRecipientGroup().getId());
    assertEquals(project.getId(), transfer.getSampleTransfers().iterator().next().getItem().getProject().getId());
    assertEquals(transferTime, transfer.getTransferTime());
  }

  @Test
  public void testCreate() throws Exception {
    Transfer transfer = new Transfer();
    Lab senderLab = (Lab) currentSession().get(LabImpl.class, 1L);
    transfer.setSenderLab(senderLab);
    Group recipientGroup = (Group) currentSession().get(Group.class, 1L);
    transfer.setRecipientGroup(recipientGroup);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    transfer.setChangeDetails(user);
    transfer.setTransferTime(new Date());
    long savedId = sut.create(transfer);

    clearSession();

    Transfer saved = (Transfer) currentSession().get(Transfer.class, savedId);
    assertNotNull(saved);
    assertNotNull(saved.getSenderLab());
    assertEquals(senderLab.getAlias(), saved.getSenderLab().getAlias());
    assertNotNull(saved.getRecipientGroup());
    assertEquals(recipientGroup.getName(), saved.getRecipientGroup().getName());
  }

  @Test
  public void testUpdate() throws Exception {
    Transfer transfer = (Transfer) currentSession().get(Transfer.class, 1L);
    Lab lab = (Lab) currentSession().get(LabImpl.class, 4L);
    assertNotEquals(lab.getId(), transfer.getSenderLab().getId());
    transfer.setSenderLab(lab);
    sut.update(transfer);

    clearSession();

    Transfer saved = (Transfer) currentSession().get(Transfer.class, 1L);
    assertEquals(lab.getId(), saved.getSenderLab().getId());
  }

  @Test
  public void testCascadeUpdateItems() throws Exception {
    Transfer transfer = (Transfer) currentSession().get(Transfer.class, 1L);
    String qcNote = "bad stuff";
    for (TransferSample item : transfer.getSampleTransfers()) {
      assertNull(item.isReceived());
      assertNull(item.isQcPassed());
      assertNull(item.getQcNote());
      item.setReceived(true);
      item.setQcPassed(false);
      item.setQcNote(qcNote);
    }
    sut.update(transfer);

    clearSession();

    Transfer saved = (Transfer) currentSession().get(Transfer.class, 1L);
    for (TransferSample item : saved.getSampleTransfers()) {
      assertTrue(item.isReceived());
      assertFalse(item.isQcPassed());
      assertEquals(qcNote, item.getQcNote());
    }
  }

  @Test
  public void testUpdateRemoveItem() throws Exception {
    Transfer transfer = (Transfer) currentSession().get(Transfer.class, 1L);
    Predicate<TransferSample> predicate = item -> item.getItem().getId() == 1L;
    TransferSample item = transfer.getSampleTransfers().stream().filter(predicate).findFirst().orElse(null);
    assertNotNull(item);
    transfer.getSampleTransfers().remove(item);
    sut.deleteTransferItem(item);
    sut.update(transfer);

    clearSession();

    Transfer saved = (Transfer) currentSession().get(Transfer.class, 1L);
    assertFalse(saved.getSampleTransfers().stream().anyMatch(predicate));
  }

  @Test
  public void testUpdateAddItem() throws Exception {
    Transfer transfer = (Transfer) currentSession().get(Transfer.class, 1L);
    Predicate<TransferSample> predicate = item -> item.getItem().getId() == 4L;
    assertFalse(transfer.getSampleTransfers().stream().anyMatch(predicate));

    TransferSample newItem = new TransferSample();
    Sample sample = (Sample) currentSession().get(SampleImpl.class, 4L);
    newItem.setItem(sample);
    newItem.setTransfer(transfer);
    transfer.getSampleTransfers().add(newItem);
    sut.update(transfer);

    clearSession();

    Transfer saved = (Transfer) currentSession().get(Transfer.class, 1L);
    assertTrue(saved.getSampleTransfers().stream().anyMatch(predicate));
  }

}
