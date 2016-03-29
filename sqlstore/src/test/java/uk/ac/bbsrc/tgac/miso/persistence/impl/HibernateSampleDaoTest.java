package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultSampleNamingScheme;

public class HibernateSampleDaoTest {

  private HibernateSampleDao sut;

  @Before
  public void setUp() throws Exception {
    sut = new HibernateSampleDao();
    sut.setNamingScheme(new DefaultSampleNamingScheme());
  }

  @Test
  public void parentNameNotModifiedWhenParentNullTest() throws Exception {
    Sample sample = new SampleImpl();
    sut.updateParentSampleNameIfRequired(sample);
    assertNull("Null parent will remain null. No need to udate sample name.", sample.getParent());
  }

  @Test
  public void parentNameNotModifiedWhenParentNameNotTemporaryTest() throws Exception {
    Sample sample = new SampleImpl();
    Sample parent = new SampleImpl();
    sample.setParent(parent);
    String nonTemporaryName = "RealSampleName";
    parent.setName(nonTemporaryName);
    sut.updateParentSampleNameIfRequired(sample);
    assertThat("Parent has a non-temporary name. No need to udate sample name.", sample.getParent().getName(), is(nonTemporaryName));
  }

  @Test
  public void parentNameNotModifiedWhenParentIdNotSetTest() throws Exception {
    Sample sample = new SampleImpl();
    Sample parent = new SampleImpl();
    sample.setParent(parent);
    String temporaryName = HibernateSampleDao.generateTemporaryName();
    parent.setName(temporaryName);
    parent.setId(Sample.UNSAVED_ID);
    sut.updateParentSampleNameIfRequired(sample);
    assertThat("Parent has not been assigned an id. No need to udate sample name.", sample.getParent().getName(), is(temporaryName));
  }

  @Test
  public void parentNameModifiedTest() throws Exception {
    Sample sample = new SampleImpl();
    Sample parent = new SampleImpl();
    sample.setParent(parent);
    String temporaryName = HibernateSampleDao.generateTemporaryName();
    parent.setName(temporaryName);
    parent.setId(42);
    sut.updateParentSampleNameIfRequired(sample);
    assertThat("Parent has temporary name and an id. Update sample name.", sample.getParent().getName(), is("SAM42"));
  }

  @Test
  public void temporarySampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName(HibernateSampleDao.generateTemporaryName());
    assertTrue("Temporary sample names must return true.", HibernateSampleDao.hasTemporaryName(sample));
  }

  @Test
  public void notTemporarySampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName("RealSampleName");
    assertFalse("Real sample names must return false.", HibernateSampleDao.hasTemporaryName(sample));
  }

  @Test
  public void nullSampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName(null);
    assertFalse("Non-temporary sample names must return false.", HibernateSampleDao.hasTemporaryName(sample));
  }

  @Test
  public void nullSampleObjectNameTest() throws Exception {
    Sample sample = null;
    assertFalse("A null sample object does not contain a temporary name so must return false.",
        HibernateSampleDao.hasTemporaryName(sample));
  }

}
