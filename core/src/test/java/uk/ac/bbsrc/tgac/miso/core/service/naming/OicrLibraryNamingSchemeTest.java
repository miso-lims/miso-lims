package uk.ac.bbsrc.tgac.miso.core.service.naming;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;

public class OicrLibraryNamingSchemeTest {

  private MisoNamingScheme<Library> sut;

  @Before
  public void setUp() throws Exception {
    sut = new OicrLibraryNamingScheme();
  }

  @Test
  public void test_alias_01() throws Exception {
    assertThat(sut.validateField("alias", "BART_1273_Br_P_PE_300_WG"), is(true));
  }

  @Test
  public void test_alias_02() throws Exception {
    assertThat(sut.validateField("alias", "AOE_1273_Br_P_PE_1K_WG"), is(true));
  }

  @Test
  public void test_alias_03() throws Exception {
    assertThat(sut.validateField("alias", "MOUSE_123_Br_R_SE_1K_WG"), is(true));
  }

  @Test
  public void test_alias_04() throws Exception {
    assertThat(sut.validateField("alias", "MOUSE_1C3_Br_R_SE_1K_WG"), is(true));
  }

  @Test
  public void test_alias_05() throws Exception {
    assertThat(sut.validateField("alias", "MOUSE_1R45_Br_R_SE_1K_WG"), is(true));
  }

  @Test
  public void test_alias_generation_from_sample() throws Exception {
    DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();
    Sample sample = dataObjectFactory.getSample();
    sample.setAlias("BART_1273_Br_P_nn_1-1_D");
    Library library = dataObjectFactory.getLibrary();
    library.setSample(sample);
    LibraryType libraryType = new LibraryType();
    libraryType.setDescription("Paired End");
    library.setLibraryType(libraryType);
    LibraryStrategyType libraryStrategyType = new LibraryStrategyType();
    libraryStrategyType.setName("AMPLICON");
    library.setLibraryStrategyType(libraryStrategyType);

    assertThat(sut.generateNameFor("alias", library), is("BART_1273_Br_P_PE_???_??"));
  }
}
