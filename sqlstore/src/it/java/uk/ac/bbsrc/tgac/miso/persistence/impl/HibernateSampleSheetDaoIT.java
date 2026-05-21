package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

import java.util.Collections;
import java.util.List;

public class HibernateSampleSheetDaoIT extends AbstractHibernateSaveDaoTest<SampleSheet, HibernateSampleSheetDao> {

  public HibernateSampleSheetDaoIT() {
    super(SampleSheet.class, 3L, 3);
  }

  @Override
  public HibernateSampleSheetDao constructTestSubject() {
    HibernateSampleSheetDao sut = new HibernateSampleSheetDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public SampleSheet getCreateItem() {
    SampleSheet sheet = new SampleSheet();
    sheet.setName("New Sheet");
    sheet.setPlatformType(PlatformType.ILLUMINA);

    SampleSheetParameter parameter = new SampleSheetParameter();
    parameter.setName("Parameter 1");
    parameter.setType(SampleSheetParameter.ParameterType.TEXT);
    sheet.setParameters(Collections.singletonList(parameter));

    SampleSheetSection section = new SampleSheetSection();
    section.setName("Section 1");
    SampleSheetField field = new SampleSheetField();
    field.setName("Field 1");
    SampleSheetFieldSource source = new SampleSheetFieldSource();
    source.setSource(SampleSheetFieldCommonSource.POOL.name());
    field.setSources(Collections.singletonList(source));
    section.setFields(Collections.singletonList(field));
    sheet.setSections(Collections.singletonList(section));

    return sheet;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<SampleSheet, String> getUpdateParams() {
    return new UpdateParameters<>(1L, SampleSheet::getName, SampleSheet::setName, "New Name");
  }

  @Test
  public void testGetByName() throws Exception {
    testGetBy(HibernateSampleSheetDao::getByName, "Empty Ultima", SampleSheet::getName);
  }

  @Test
  public void testListByPlatform() throws Exception {
    List<SampleSheet> results = getTestSubject().listByPlatform(PlatformType.ILLUMINA);
    assertEquals(2, results.size());
  }
}
