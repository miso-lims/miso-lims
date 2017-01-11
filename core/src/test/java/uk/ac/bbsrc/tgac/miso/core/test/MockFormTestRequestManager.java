package uk.ac.bbsrc.tgac.miso.core.test;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoRequestManager;

/**
 * Mock request manager for form tests
 * 
 * @author Rob Davey
 * @date 26/09/12
 * @since 0.1.8
 */
public class MockFormTestRequestManager extends MisoRequestManager {
  @Override
  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) {
    LibraryType lt = new LibraryType();
    lt.setId(1L);
    lt.setDescription("Paired End");
    lt.setPlatformType(PlatformType.get("Illumina"));
    return lt;
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) {
    LibrarySelectionType lst = new LibrarySelectionType();
    lst.setId(3L);
    lst.setName("PCR");
    lst.setDescription("Source material was selected by designed primers");
    return lst;
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) {
    LibraryStrategyType lst = new LibraryStrategyType();
    lst.setId(1L);
    lst.setName("WGS");
    lst.setDescription("Whole genome shotgun");
    return lst;
  }

  @Override
  public Collection<Sample> listSamplesByAlias(String alias) {
    final Sample s = new SampleImpl();
    s.setId(1L);
    s.setName("SAM1");
    s.setAlias("RD_S1_MockSample");
    s.setAccession("");
    s.setDescription("Mock Sample 1");
    s.setScientificName("Homo sapiens");
    s.setTaxonIdentifier("9606");
    s.setIdentificationBarcode("SAM1::RD_S1_MockSample");
    s.setLocationBarcode("Freezer1");
    s.setSampleType("GENOMIC");
    s.setReceivedDate(new Date());

    s.setSecurityProfile(new SecurityProfile());

    Project p = new ProjectImpl();
    p.setProjectId(1L);
    p.setAlias("MockInputProject");
    s.setProject(p);

    return Collections.singletonList(s);
  }

  @Override
  public QcType getSampleQcTypeByName(String name) {
    QcType qt = new QcType();
    qt.setQcTypeId(1L);
    qt.setName("QuBit");
    qt.setDescription("Quantitation of DNA, RNA and protein, manufacturered by Invitrogen");
    qt.setUnits("ng/&#181;l");
    return qt;
  }

  @Override
  public QcType getLibraryQcTypeByName(String name) {
    QcType qt = new QcType();
    qt.setQcTypeId(2L);
    qt.setName("Bioanalyzer");
    qt.setDescription("Chip-based capillary electrophoresis machine to analyse RNA, DNA, and protein, manufactured by Agilent");
    qt.setUnits("nM");
    return qt;
  }

  @Override
  public Collection<Library> listAllLibrariesBySampleId(long sampleId) {
    return Collections.emptyList();
  }
}