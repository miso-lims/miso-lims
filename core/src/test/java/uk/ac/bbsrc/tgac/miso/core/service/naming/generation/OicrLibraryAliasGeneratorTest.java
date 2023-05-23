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
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class OicrLibraryAliasGeneratorTest {

  @Mock
  private SiblingNumberGenerator siblingNumberGenerator;

  @InjectMocks
  private OicrLibraryAliasGenerator sut;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
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
    library.setCreationDate(LimsUtils.parseLocalDate("2017-09-13"));
    DetailedSample parent = new DetailedSampleImpl();
    parent.setAlias("PROJ_1234_Pa_P_nn_1-1_D_8");
    library.setSample(parent);

    Mockito.when(siblingNumberGenerator.getNextSiblingNumber(Mockito.any(), Mockito.any())).thenReturn(2);

    assertEquals("PROJ_1234_20170913_2", sut.generate(library));
  }

  @Test
  public void testGenerateOxfordNanoporeAlias() throws Exception {
    DetailedLibrary library = new DetailedLibraryImpl();
    library.setPlatformType(PlatformType.OXFORDNANOPORE);

    DetailedSample sample = new DetailedSampleImpl();
    sample.setAlias("LALA_1234567_Ly_R_nn_1-1_D_1");
    library.setSample(sample);

    LibraryType libraryType = new LibraryType();
    libraryType.setAbbreviation("RPD");
    library.setLibraryType(libraryType);

    library.setDnaSize(300);

    LibraryDesignCode code = new LibraryDesignCode();
    code.setCode("WG");
    library.setLibraryDesignCode(code);

    Mockito.when(siblingNumberGenerator.getNextSiblingNumber(Mockito.any(), Mockito.any())).thenReturn(5);

    assertEquals("LALA_1234567_Ly_R_RPD_WG_5", sut.generate(library));
  }

}
