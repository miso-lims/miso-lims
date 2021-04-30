package uk.ac.bbsrc.tgac.miso.core.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.IndexedLibrary;

public class IndexCheckerTest {

  private static class TestLibrary implements IndexedLibrary {

    private final Index index1;
    private final Index index2;

    public TestLibrary(Index index1, Index index2) {
      this.index1 = index1;
      this.index2 = index2;
    }

    @Override
    public Index getIndex1() {
      return index1;
    }

    @Override
    public void setIndex1(Index index1) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Index getIndex2() {
      return index2;
    }

    @Override
    public void setIndex2(Index index2) {
      throw new UnsupportedOperationException();
    }

  }

  private static final String UNIQUE_1 = "AAAAAA";
  private static final String UNIQUE_2 = "BBBBBB";
  private static final String UNIQUE_3 = "CCCCCC";
  private static final String UNIQUE_4 = "DDDDDD";
  private static final String WARN_1 = "EEEEEE";
  private static final String WARN_2 = "EEEEFF";
  private static final String ERROR_1 = "GGGGGG";
  private static final String ERROR_2 = "GGGGGE";

  private IndexChecker sut;

  @Before
  public void setup() {
    sut = new IndexChecker();
    sut.setErrorMismatches(1);
    sut.setWarningMismatches(2);
  }

  @Test
  public void testNoMatches() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1), null),
        new TestLibrary(makeIndex(UNIQUE_2), null),
        new TestLibrary(makeIndex(UNIQUE_3), null),
        new TestLibrary(makeIndex(UNIQUE_4), null));
    assertEquals(0, sut.getDuplicateIndicesSequences(libraries).size());
    assertEquals(0, sut.getNearDuplicateIndicesSequences(libraries).size());
  }

  @Test
  public void testNoMatchesDualIndex() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(UNIQUE_1)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(UNIQUE_2)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(UNIQUE_3)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(UNIQUE_4)),
        new TestLibrary(makeIndex(UNIQUE_2), makeIndex(UNIQUE_1)),
        new TestLibrary(makeIndex(UNIQUE_3), makeIndex(UNIQUE_1)),
        new TestLibrary(makeIndex(UNIQUE_4), makeIndex(UNIQUE_1)));
    assertEquals(0, sut.getDuplicateIndicesSequences(libraries).size());
    assertEquals(0, sut.getNearDuplicateIndicesSequences(libraries).size());
  }

  @Test
  public void testNoMatchesMultisequence() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1, String.join(",", UNIQUE_1, UNIQUE_2)), null),
        new TestLibrary(makeIndex(UNIQUE_3, String.join(",", UNIQUE_3, UNIQUE_4)), null));
    assertEquals(0, sut.getDuplicateIndicesSequences(libraries).size());
    assertEquals(0, sut.getNearDuplicateIndicesSequences(libraries).size());
  }

  @Test
  public void testWarn() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1), null),
        new TestLibrary(makeIndex(UNIQUE_2), null),
        new TestLibrary(makeIndex(WARN_1), null),
        new TestLibrary(makeIndex(WARN_2), null));
    assertEquals(0, sut.getDuplicateIndicesSequences(libraries).size());
    Set<String> warnings = sut.getNearDuplicateIndicesSequences(libraries);
    assertEquals(2, warnings.size());
    assertTrue(warnings.contains(WARN_1));
    assertTrue(warnings.contains(WARN_2));
  }

  @Test
  public void testWarnDualIndex() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(UNIQUE_1)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(UNIQUE_2)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(WARN_1)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(WARN_2)));
    assertEquals(0, sut.getDuplicateIndicesSequences(libraries).size());
    Set<String> warnings = sut.getNearDuplicateIndicesSequences(libraries);
    assertEquals(2, warnings.size());
    assertTrue(warnings.contains(String.join("-", UNIQUE_1, WARN_1)));
    assertTrue(warnings.contains(String.join("-", UNIQUE_1, WARN_2)));
  }

  @Test
  public void testWarnMultisequence() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1, String.join(",", UNIQUE_1, UNIQUE_2)), null),
        new TestLibrary(makeIndex(WARN_1, String.join(",", UNIQUE_3, WARN_1)), null),
        new TestLibrary(makeIndex(WARN_2, String.join(",", WARN_2, UNIQUE_4)), null));
    assertEquals(0, sut.getDuplicateIndicesSequences(libraries).size());
    Set<String> warnings = sut.getNearDuplicateIndicesSequences(libraries);
    assertEquals(2, warnings.size());
    assertTrue(warnings.contains(WARN_1));
    assertTrue(warnings.contains(WARN_2));
  }

  @Test
  public void testError() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1), null),
        new TestLibrary(makeIndex(UNIQUE_2), null),
        new TestLibrary(makeIndex(ERROR_1), null),
        new TestLibrary(makeIndex(ERROR_2), null));
    Set<String> errors = sut.getDuplicateIndicesSequences(libraries);
    assertEquals(2, errors.size());
    assertTrue(errors.contains(ERROR_1));
    assertTrue(errors.contains(ERROR_2));
    Set<String> warnings = sut.getNearDuplicateIndicesSequences(libraries);
    assertEquals(2, warnings.size());
    assertTrue(warnings.contains(ERROR_1));
    assertTrue(warnings.contains(ERROR_2));
  }

  @Test
  public void testErrorDualIndex() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(UNIQUE_1)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(UNIQUE_2)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(ERROR_1)),
        new TestLibrary(makeIndex(UNIQUE_1), makeIndex(ERROR_2)));
    Set<String> errors = sut.getDuplicateIndicesSequences(libraries);
    assertEquals(2, errors.size());
    assertTrue(errors.contains(String.join("-", UNIQUE_1, ERROR_1)));
    assertTrue(errors.contains(String.join("-", UNIQUE_1, ERROR_2)));
    Set<String> warnings = sut.getNearDuplicateIndicesSequences(libraries);
    assertEquals(2, warnings.size());
    assertTrue(warnings.contains(String.join("-", UNIQUE_1, ERROR_1)));
    assertTrue(warnings.contains(String.join("-", UNIQUE_1, ERROR_2)));
  }

  @Test
  public void testErrorMultisequence() throws Exception {
    List<TestLibrary> libraries = Arrays.asList(
        new TestLibrary(makeIndex(UNIQUE_1, String.join(",", UNIQUE_1, UNIQUE_2)), null),
        new TestLibrary(makeIndex(ERROR_1, String.join(",", UNIQUE_3, ERROR_1)), null),
        new TestLibrary(makeIndex(ERROR_2, String.join(",", ERROR_2, UNIQUE_4)), null));
    Set<String> errors = sut.getDuplicateIndicesSequences(libraries);
    assertEquals(2, errors.size());
    assertTrue(errors.contains(ERROR_1));
    assertTrue(errors.contains(ERROR_2));
    Set<String> warnings = sut.getNearDuplicateIndicesSequences(libraries);
    assertEquals(2, warnings.size());
    assertTrue(warnings.contains(ERROR_1));
    assertTrue(warnings.contains(ERROR_2));
  }

  public Index makeIndex(String sequence) {
    return makeIndex(sequence, null);
  }

  public Index makeIndex(String fakeSequence, String realSequences) {
    Index index = new Index();
    IndexFamily family = new IndexFamily();
    family.setFake(realSequences != null);
    index.setFamily(family);
    index.setName(fakeSequence + " name");
    index.setSequence(fakeSequence);
    index.setRealSequences(realSequences);
    return index;
  }

}
