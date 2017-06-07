package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class OicrLibraryAliasGeneratorTest {

  private OicrLibraryAliasGenerator sut;

  @Before
  public void setup() throws Exception {
    sut = new OicrLibraryAliasGenerator();
  }

  @Test
  public void testGenerateIlluminaLibraryAlias() throws Exception {
    DetailedLibrary library = new DetailedLibraryImpl();
    library.setPlatformType(PlatformType.ILLUMINA);

    DetailedSample sample = new DetailedSampleImpl();
    sample.setAlias("BART_1273_Br_P_nn_1-1_D_1");
    library.setSample(sample);

    LibraryType libraryType = new LibraryType();
    libraryType.setAbbreviation("PE");
    library.setLibraryType(libraryType);

    library.setDnaSize(300);

    LibraryDesignCode code = new LibraryDesignCode();
    code.setCode("WG");
    library.setLibraryDesignCode(code);

    assertEquals("BART_1273_Br_P_PE_300_WG", sut.generate(library));
  }

  @Test
  public void testGeneratePacBioLibraryAlias() throws Exception {
    DetailedLibrary library = new DetailedLibraryImpl();
    library.setPlatformType(PlatformType.PACBIO);

    DetailedSample parent = new DetailedSampleImpl();
    Project proj = new ProjectImpl();
    proj.setShortName("PROJ");
    parent.setProject(proj);
    SampleTissue grandParent = new SampleTissueImpl();
    grandParent.setTimesReceived(2);
    parent.setParent(grandParent);
    library.setSample(parent);

    library.setInitialConcentration(150D);

    assertEquals("PROJ_2_150pM", sut.generate(library));
  }

}
