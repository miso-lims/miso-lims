package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.core.store.TissueTypeDao;

public class OicrLibraryAliasValidatorTest {

  @Mock
  private TissueOriginDao tissueOriginDao;
  @Mock
  private TissueTypeDao tissueTypeDao;
  @Mock
  private LibraryStore libraryStore;
  @Mock
  private LibraryDesignCodeDao libraryDesignCodeDao;

  @InjectMocks
  private OicrLibraryAliasValidator sut;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.when(tissueOriginDao.getTissueOrigin()).thenReturn(makeTissueOrigins());
    Mockito.when(tissueTypeDao.getTissueType()).thenReturn(makeTissueTypes());
    Mockito.when(libraryStore.listLibraryTypesByPlatform(PlatformType.ILLUMINA)).thenReturn(makeIlluminaLibTypes());
    Mockito.when(libraryStore.listLibraryTypesByPlatform(PlatformType.OXFORDNANOPORE)).thenReturn(makeOxfordNanoporeLibTypes());
    Mockito.when(libraryDesignCodeDao.getLibraryDesignCodes()).thenReturn(makeDesignCodes());
  }

  @Test
  public void test_alias_01() throws Exception {
    assertThat(sut.validate("BART_1273_Br_P_PE_300_WG").isValid(), is(true));
  }

  @Test
  public void test_alias_02() throws Exception {
    assertThat(sut.validate("AOE_1234567_Br_P_PE_1K_WG").isValid(), is(true));
  }

  @Test
  public void test_alias_03() throws Exception {
    assertThat(sut.validate("MOUSE_123_Br_R_SE_1K_WG").isValid(), is(true));
  }

  @Test
  public void test_alias_04() throws Exception {
    assertThat(sut.validate("MOUSE_1C3_Br_R_SE_1K_WG").isValid(), is(true));
  }

  @Test
  public void test_alias_05() throws Exception {
    assertThat(sut.validate("MOUSE_1R45_Br_R_SE_1K_WG").isValid(), is(true));
  }

  @Test
  public void testPacBioPattern() throws Exception {
    assertTrue(sut.validate("PROJ_1234_20170913_1").isValid());
    assertTrue(sut.validate("A123Z_0001_19991231_32").isValid());
  }

  @Test
  public void testOxfordNanoporePattern() throws Exception {
    assertTrue(sut.validate("LALA_1010_Ly_R_LIG_WG_1").isValid());
    assertTrue(sut.validate("ABCD_1234_Pa_P_1D2_MR_123").isValid());
  }

  private static List<TissueOrigin> makeTissueOrigins() {
    return makeList(new String[] { "Br", "Ly", "Pa" }, alias -> {
      TissueOrigin o = new TissueOriginImpl();
      o.setAlias(alias);
      return o;
    });
  }

  private static List<TissueType> makeTissueTypes() {
    return makeList(new String[] { "P", "R" }, alias -> {
      TissueType tt = new TissueTypeImpl();
      tt.setAlias(alias);
      return tt;
    });
  }

  private static List<LibraryType> makeIlluminaLibTypes() {
    return makeList(new String[] { "PE", "SE" }, alias -> {
      LibraryType lt = new LibraryType();
      lt.setAbbreviation(alias);
      return lt;
    });
  }

  private static List<LibraryType> makeOxfordNanoporeLibTypes() {
    return makeList(new String[] { "LIG", "1D2" }, alias -> {
      LibraryType lt = new LibraryType();
      lt.setAbbreviation(alias);
      return lt;
    });
  }

  private static List<LibraryDesignCode> makeDesignCodes() {
    return makeList(new String[] { "MR", "WG" }, alias -> {
      LibraryDesignCode ldc = new LibraryDesignCode();
      ldc.setCode(alias);
      return ldc;
    });
  }

  private static <T> List<T> makeList(String[] aliases, Function<String, T> constructor) {
    return Arrays.stream(aliases)
        .map(alias -> constructor.apply(alias))
        .collect(Collectors.toList());
  }

}
