package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateAttachableDaoIT extends AbstractDAOTest {

  private HibernateAttachableDao sut;

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setup() {
    sut = new HibernateAttachableDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGetManagedAttachable() throws Exception {
    Sample sample = (Sample) currentSession().get(SampleImpl.class, 1L);
    Attachable attachable = sut.getManaged(sample);
    assertTrue(attachable instanceof Sample);
    assertEquals(sample.getId(), attachable.getId());
  }

  @Test
  public void testSaveAttachable() throws Exception {
    long sampleId = 1L;
    long newAttachmentId = 2L;
    Sample sample = (Sample) currentSession().get(SampleImpl.class, sampleId);
    assertFalse(sample.getAttachments().stream().anyMatch(x -> x.getId() == newAttachmentId));
    FileAttachment attachment = (FileAttachment) currentSession().get(FileAttachment.class, newAttachmentId);

    sample.getAttachments().add(attachment);
    sut.save(sample);

    clearSession();

    Sample saved = (Sample) currentSession().get(SampleImpl.class, sampleId);
    assertTrue(saved.getAttachments().stream().anyMatch(x -> x.getId() == newAttachmentId));
  }

  @Test
  public void testGetAttachment() throws Exception {
    long attachmentId = 2L;
    FileAttachment attachment = sut.getAttachment(attachmentId);
    assertNotNull(attachment);
    assertEquals(attachmentId, attachment.getId());
  }

  @Test
  public void testGetUsage() throws Exception {
    FileAttachment attachment1 = (FileAttachment) currentSession().get(FileAttachment.class, 1L);
    assertEquals(1L, sut.getUsage(attachment1));
    FileAttachment attachment3 = (FileAttachment) currentSession().get(FileAttachment.class, 3L);
    assertEquals(0L, sut.getUsage(attachment3));
  }

  @Test
  public void testBadGetUsage() throws Exception {
    FileAttachment fake = new FileAttachment();
    fake.setId(1000000);

    exception.expect(IllegalArgumentException.class);
    sut.getUsage(fake);
  }

  @Test
  public void testDeleteAttachment() throws Exception {
    long attachmentId = 3L;
    FileAttachment attachment = (FileAttachment) currentSession().get(FileAttachment.class, attachmentId);
    assertNotNull(attachment);
    sut.delete(attachment);

    clearSession();

    FileAttachment after = (FileAttachment) currentSession().get(FileAttachment.class, attachmentId);
    assertNull(after);
  }

  @Test
  public void testSaveAttachment() throws Exception {
    FileAttachment attachment = new FileAttachment();
    attachment.setFilename("Test");
    attachment.setPath("/path/to/file");
    attachment.setCreator((User) currentSession().get(UserImpl.class, 1L));
    attachment.setCreationTime(new Date());
    assertEquals(0L, attachment.getId());
    sut.save(attachment);
    assertNotEquals(0L, attachment.getId());

    clearSession();

    FileAttachment saved = (FileAttachment) currentSession().get(FileAttachment.class, attachment.getId());
    assertNotNull(saved);
  }

}
