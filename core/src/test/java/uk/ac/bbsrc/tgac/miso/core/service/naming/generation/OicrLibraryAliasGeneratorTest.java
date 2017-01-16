package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;

public class OicrLibraryAliasGeneratorTest {

  private OicrLibraryAliasGenerator sut;

  @Before
  public void setup() throws Exception {
    sut = new OicrLibraryAliasGenerator();
  }

  @Test
  public void test_alias_generation_from_sample() throws Exception {
    DetailedSample sample = new DetailedSampleImpl();
    sample.setAlias("BART_1273_Br_P_nn_1-1_D_1");
    DetailedLibrary library = new DetailedLibraryImpl();
    library.setSample(sample);
    LibraryType libraryType = new LibraryType();
    libraryType.setDescription("Paired End");
    library.setLibraryType(libraryType);
    LibraryStrategyType libraryStrategyType = new LibraryStrategyType();
    libraryStrategyType.setName("AMPLICON");
    library.setLibraryStrategyType(libraryStrategyType);
    LibraryDesignCode code = new LibraryDesignCode();
    code.setCode("WG");
    library.setLibraryDesignCode(code);

    assertThat(sut.generate(library), is("BART_1273_Br_P_PE_300_WG"));
  }

}
