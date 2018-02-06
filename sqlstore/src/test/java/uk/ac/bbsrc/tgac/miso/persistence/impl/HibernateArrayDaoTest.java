package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateArrayDaoTest extends AbstractDAOTest {

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateArrayDao sut;

  @Before
  public void setup() {
    sut = new HibernateArrayDao();
    sut.setSessionFactory(sessionFactory);
    sut.setJdbcTemplate(template);
    sut.setDetailedSample(true);
  }

  @Test
  public void testSaveNew() throws Exception {
    Session session = sessionFactory.getCurrentSession();
    ArrayModel model = (ArrayModel) session.get(ArrayModel.class, 1L);
    User user = (User) session.get(UserImpl.class, 1L);
    Date now = new Date();

    Array a = new Array();
    a.setAlias("Test Array");
    a.setArrayModel(model);
    a.setDescription("Array for test");
    a.setSerialNumber("12345");

    a.setCreator(user);
    a.setCreationTime(now);
    a.setLastModifier(user);
    a.setLastModified(now);

    long savedId = sut.save(a);
    Array saved = sut.get(savedId);
    assertNotNull(saved);
    assertEquals(a.getAlias(), saved.getAlias());
  }

  @Test
  public void testSaveExisting() throws Exception {
    String description = "New description";
    String serialNum = "NEWSERIAL";

    Array a = sut.get(1L);
    assertNotEquals(description, a.getDescription());
    assertNotEquals(serialNum, a.getSerialNumber());

    a.setDescription(description);
    a.setSerialNumber(serialNum);

    long savedId = sut.save(a);
    Array saved = sut.get(savedId);
    assertNotNull(saved);
    assertEquals(description, saved.getDescription());
    assertEquals(serialNum, saved.getSerialNumber());
  }

  @Test
  public void testGet() throws Exception {
    Array arr = sut.get(1L);
    assertNotNull(arr);
    assertEquals(1L, arr.getId());
    assertEquals("Array_1", arr.getAlias());
    assertEquals("1234", arr.getSerialNumber());
    assertEquals("test array", arr.getDescription());
    assertNotNull(arr.getArrayModel());
    assertEquals("Test BeadChip", arr.getArrayModel().getAlias());
  }

  @Test
  public void testGetByAlias() throws Exception {
    Array arr = sut.getByAlias("Array_1");
    assertNotNull(arr);
  }

  @Test
  public void testGetBySerialNumber() throws Exception {
    Array arr = sut.getBySerialNumber("1234");
    assertNotNull(arr);
  }

  @Test
  public void testListAll() throws Exception {
    List<Array> arrays = sut.listAll();
    assertNotNull(arrays);
    assertEquals(1, arrays.size());
  }

  @Test
  public void testListBySampleId() throws Exception {
    List<Array> arrays = sut.listBySampleId(19L);
    assertNotNull(arrays);
    assertEquals(1, arrays.size());
  }

  @Test
  public void testSampleSearchByNamePlainSample() throws Exception {
    sut.setDetailedSample(false);
    List<Sample> results = sut.getArrayableSamplesBySearch("SAM15");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("SAM15", results.get(0).getName());
  }

  @Test
  public void testSampleSearchByNameDetailedSample() throws Exception {
    List<Sample> results = sut.getArrayableSamplesBySearch("SAM19");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("SAM19", results.get(0).getName());
  }

  @Test
  public void testSampleSearchByNameDetailedSampleNotAliquot() throws Exception {
    List<Sample> results = sut.getArrayableSamplesBySearch("SAM15");
    assertNotNull(results);
    assertEquals(0, results.size());
  }

  @Test
  public void testSampleSearchByBarcode() throws Exception {
    List<Sample> results = sut.getArrayableSamplesBySearch("SAM19::TEST_0001_ALIQUOT_1");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("SAM19::TEST_0001_ALIQUOT_1", results.get(0).getIdentificationBarcode());
  }

  @Test
  public void testSampleSearchByAlias() throws Exception {
    List<Sample> results = sut.getArrayableSamplesBySearch("TEST_0001_ALIQUOT_1");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("TEST_0001_ALIQUOT_1", results.get(0).getAlias());
  }

  @Test
  public void testSampleSearchNoResult() throws Exception {
    List<Sample> results = sut.getArrayableSamplesBySearch("SAM12398735692845");
    assertNotNull(results);
    assertEquals(0, results.size());
  }

  @Test
  public void testCount() throws Exception {
    assertEquals(1, sut.count());
  }

  @Test
  public void testGetArrayModel() throws Exception {
    ArrayModel model = sut.getArrayModel(1L);
    assertNotNull(model);
    assertEquals(1L, model.getId());
    assertEquals("Test BeadChip", model.getAlias());
    assertEquals(8, model.getRows());
    assertEquals(1, model.getColumns());
  }

  @Test
  public void testListArrayModels() throws Exception {
    List<ArrayModel> models = sut.listArrayModels();
    assertEquals(1, models.size());
  }

}
