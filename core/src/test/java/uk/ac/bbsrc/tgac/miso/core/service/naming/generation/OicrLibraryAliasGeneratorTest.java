package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;

public class OicrLibraryAliasGeneratorTest {

  private OicrLibraryAliasGenerator sut;

  @Before
  public void setup() throws Exception {
    sut = new OicrLibraryAliasGenerator();
  }

  @Test
  public void test_alias_generation_from_sample() throws Exception {
    DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();
    Sample sample = dataObjectFactory.getSample();
    sample.setAlias("BART_1273_Br_P_nn_1-1_D_1");
    Library library = dataObjectFactory.getLibrary();
    library.setLibraryAdditionalInfo(new LibraryAdditionalInfoImpl());
    library.setSample(sample);
    LibraryType libraryType = new LibraryType();
    libraryType.setDescription("Paired End");
    library.setLibraryType(libraryType);
    LibraryStrategyType libraryStrategyType = new LibraryStrategyType();
    libraryStrategyType.setName("AMPLICON");
    library.setLibraryStrategyType(libraryStrategyType);
    LibraryDesignCode code = new LibraryDesignCode();
    code.setCode("WG");
    library.getLibraryAdditionalInfo().setLibraryDesignCode(code);

    assertThat(sut.generate(library), is("BART_1273_Br_P_PE_300_WG"));
  }

}
