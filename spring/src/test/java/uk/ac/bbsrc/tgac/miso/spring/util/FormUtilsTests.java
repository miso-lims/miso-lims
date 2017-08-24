/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

package uk.ac.bbsrc.tgac.miso.spring.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

public class FormUtilsTests {

  @Mock
  private LibraryService libraryService;
  @Mock
  private QualityControlService qcService;
  @Mock
  private SampleService sampleService;
  @Mock
  private SampleQcStore sampleQcStore;
  @Mock
  private NamingScheme namingScheme;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    mockSampleByAlias();
    mockLibraryTypeByDescriptionAndPlatform();
    mockLibrarySelectionTypeByName();
    mockLibraryStrategyTypeByName();
    mockSampleQcTypeByName();
    mockLibraryQcTypeByName();
    mockLibrariesBySampleId();
    mockNaming();
  }

  @Test
  public void testCreateSampleDeliveryForm() throws Exception {
    File testSampleDeliveryFile = null;
    try {
      testSampleDeliveryFile = File.createTempFile("test-sampleDeliveryForm", ".odt");
      FormUtils.createSampleDeliveryForm(generateSamples(), testSampleDeliveryFile, true);
    } finally {
      if (testSampleDeliveryFile != null && testSampleDeliveryFile.exists()) {
        testSampleDeliveryFile.delete();
      }
    }
  }

  @Test
  public void testImportSampleDeliveryForm() throws Exception {
    File testSampleDeliveryFile = null;
    try {
      testSampleDeliveryFile = File.createTempFile("test-sampleDeliveryForm", ".odt");
      List<Sample> generatedSamples = generateSamples();
      FormUtils.createSampleDeliveryForm(generatedSamples, testSampleDeliveryFile, true);
      List<Sample> samples = FormUtils.importSampleDeliveryForm(testSampleDeliveryFile);
      int numExpected = generateSamples().size();
      assertEquals(numExpected, samples.size());
    } finally {
      if (testSampleDeliveryFile != null && testSampleDeliveryFile.exists()) {
        testSampleDeliveryFile.delete();
      }
    }
  }

  @Test
  public void testImportBulkInputODS() throws Exception {
    File testSampleBulkInputOdsFile = null;
    try {
      testSampleBulkInputOdsFile = File.createTempFile("test-sampleBulkInputOds", ".ods");
      InputStream in = FormUtilsTests.class.getClassLoader().getResourceAsStream("test-bulk_input.ods");
      LimsUtils.writeFile(in, testSampleBulkInputOdsFile);
      User u = new UserImpl();
      u.setLoginName("testBulkImportUser");
      List<Sample> samples = FormUtils.importSampleInputSpreadsheet(testSampleBulkInputOdsFile, u, sampleService, libraryService,
          qcService, namingScheme, new MockFormTestIndexService());
      assertFalse(samples.isEmpty());
    } finally {
      if (testSampleBulkInputOdsFile != null && testSampleBulkInputOdsFile.exists()) {
        testSampleBulkInputOdsFile.delete();
      }
    }
  }

  @Test
  public void testImportBulkInputXLS() throws Exception {
    File testSampleBulkInputXlsFile = null;
    try {
      testSampleBulkInputXlsFile = File.createTempFile("test-sampleBulkInputXls", ".xlsx");
      InputStream in = FormUtilsTests.class.getClassLoader().getResourceAsStream("test-bulk_input.xlsx");
      LimsUtils.writeFile(in, testSampleBulkInputXlsFile);
      User u = new UserImpl();
      u.setLoginName("testBulkImportUser");
      List<Sample> samples = FormUtils.importSampleInputSpreadsheet(testSampleBulkInputXlsFile, u, sampleService, libraryService,
          qcService, namingScheme, new MockFormTestIndexService());
      assertFalse(samples.isEmpty());
    } finally {
      if (testSampleBulkInputXlsFile != null && testSampleBulkInputXlsFile.exists()) {
        testSampleBulkInputXlsFile.delete();
      }
    }
  }

  private List<Sample> generateSamples() {
    List<Sample> samples = new ArrayList<>();

    for (int i = 1; i < 6; i++) {
      Sample s = new SampleImpl();
      s.setId(i);
      s.setName("SAM" + i);
      s.setAlias("MI_S" + i + "_TestSample");
      s.setScientificName("F.bar");
      s.setIdentificationBarcode(s.getName() + "::" + s.getAlias());
      samples.add(s);
    }
    Collections.sort(samples);
    return samples;
  }

  private void mockSampleByAlias() throws Exception {
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

    Mockito.when(sampleService.getByAlias(Mockito.anyString())).thenReturn(Collections.singletonList(s));
  }

  public void mockLibraryTypeByDescriptionAndPlatform() throws Exception {
    LibraryType lt = new LibraryType();
    lt.setId(1L);
    lt.setDescription("Paired End");
    lt.setPlatformType(PlatformType.get("Illumina"));

    Mockito.when(libraryService.getLibraryTypeByDescriptionAndPlatform(Mockito.anyString(), Mockito.any(PlatformType.class)))
        .thenReturn(lt);
  }

  public void mockLibrarySelectionTypeByName() throws Exception {
    LibrarySelectionType lst = new LibrarySelectionType();
    lst.setId(3L);
    lst.setName("PCR");
    lst.setDescription("Source material was selected by designed primers");

    Mockito.when(libraryService.getLibrarySelectionTypeByName(Mockito.anyString())).thenReturn(lst);
  }

  public void mockLibraryStrategyTypeByName() throws Exception {
    LibraryStrategyType lst = new LibraryStrategyType();
    lst.setId(1L);
    lst.setName("WGS");
    lst.setDescription("Whole genome shotgun");

    Mockito.when(libraryService.getLibraryStrategyTypeByName(Mockito.anyString())).thenReturn(lst);
  }
  
  public void mockSampleQcTypeByName() throws Exception {
    QcType qt = new QcType();
    qt.setQcTypeId(1L);
    qt.setName("QuBit");
    qt.setDescription("Quantitation of DNA, RNA and protein, manufacturered by Invitrogen");
    qt.setUnits("ng/&#181;l");
    
    Mockito.when(qcService.getQcType(Mockito.eq(QcTarget.Sample), Mockito.anyString())).thenReturn(qt);
  }

  public void mockLibraryQcTypeByName() throws Exception {
    QcType qt = new QcType();
    qt.setQcTypeId(2L);
    qt.setName("Bioanalyzer");
    qt.setDescription("Chip-based capillary electrophoresis machine to analyse RNA, DNA, and protein, manufactured by Agilent");
    qt.setUnits("nM");
    
    Mockito.when(qcService.getQcType(Mockito.eq(QcTarget.Library), Mockito.anyString())).thenReturn(qt);
  }

  public void mockLibrariesBySampleId() throws Exception {
    List<Library> list = Collections.emptyList();
    Mockito.when(libraryService.listBySampleId(Mockito.anyLong())).thenReturn(list);
  }

  public void mockNaming() throws Exception {
    Mockito.when(namingScheme.generateLibraryAlias(Mockito.any(Library.class))).thenReturn("lib");
  }

}
