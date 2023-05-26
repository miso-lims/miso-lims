package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class OicrLibraryAliquotAliasGeneratorTest {

  @Mock
  private SiblingNumberGenerator siblingNumberGenerator;

  @InjectMocks
  private OicrLibraryAliquotAliasGenerator sut;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGenerateIlluminaLibraryAlias() throws Exception {
    DetailedSample sam = new DetailedSampleImpl();
    sam.setAlias("BART_1273_Br_P_nn_1-1_D_1");
    DetailedLibrary lib = new DetailedLibraryImpl();
    lib.setSample(sam);
    lib.setPlatformType(PlatformType.ILLUMINA);
    LibraryType libType = new LibraryType();
    libType.setAbbreviation("PE");
    lib.setLibraryType(libType);
    DetailedLibraryAliquot aliquot = new DetailedLibraryAliquot();
    aliquot.setLibrary(lib);
    LibraryDesignCode code = new LibraryDesignCode();
    code.setCode("WG");
    aliquot.setLibraryDesignCode(code);
    aliquot.setDnaSize(300);
    assertEquals("BART_1273_Br_P_PE_300_WG", sut.generate(aliquot));
  }

  @Test
  public void testGeneratePacBioLibraryAlias() throws Exception {
    DetailedSample sam = new DetailedSampleImpl();
    sam.setAlias("PROJ_1234_Pa_P_nn_1-1_D_8");
    DetailedLibrary lib = new DetailedLibraryImpl();
    lib.setSample(sam);
    lib.setPlatformType(PlatformType.PACBIO);
    DetailedLibraryAliquot aliquot = new DetailedLibraryAliquot();
    aliquot.setLibrary(lib);
    aliquot.setCreationDate(LimsUtils.parseLocalDate("2017-09-13"));
    Mockito.when(siblingNumberGenerator.getNextSiblingNumber(Mockito.any(), Mockito.any())).thenReturn(2);
    assertEquals("PROJ_1234_20170913_2", sut.generate(aliquot));
  }

  @Test
  public void testGenerateOxfordNanoporeAlias() throws Exception {
    DetailedSample sample = new DetailedSampleImpl();
    sample.setAlias("LALA_1234567_Ly_R_nn_1-1_D_1");
    DetailedLibrary library = new DetailedLibraryImpl();
    library.setSample(sample);
    library.setPlatformType(PlatformType.OXFORDNANOPORE);
    LibraryType libraryType = new LibraryType();
    libraryType.setAbbreviation("RPD");
    library.setLibraryType(libraryType);
    DetailedLibraryAliquot aliquot = new DetailedLibraryAliquot();
    aliquot.setLibrary(library);
    aliquot.setDnaSize(300);
    LibraryDesignCode code = new LibraryDesignCode();
    code.setCode("WG");
    aliquot.setLibraryDesignCode(code);

    Mockito.when(siblingNumberGenerator.getNextSiblingNumber(Mockito.any(), Mockito.any())).thenReturn(5);

    assertEquals("LALA_1234567_Ly_R_RPD_WG_5", sut.generate(aliquot));
  }

}
