package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.core.data.IndexedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolViewElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class IndexChecker {

  private String errorMismatchesMessage;
  private String warningMismatchesMessage;
  private int defaultErrorMismatches;
  private int defaultWarningMismatches;
  private Map<PlatformType, Integer> errorMismatchesByPlatform;
  private Map<PlatformType, Integer> warningMismatchesByPlatform;

  public String getErrorMismatchesMessage() {
    return errorMismatchesMessage;
  }

  public void setErrorMismatchesMessage(String errorMismatchesMessage) {
    this.errorMismatchesMessage = errorMismatchesMessage;
  }

  public String getWarningMismatchesMessage() {
    return warningMismatchesMessage;
  }

  public void setWarningMismatchesMessage(String warningMismatchesMessage) {
    this.warningMismatchesMessage = warningMismatchesMessage;
  }

  public int getDefaultErrorMismatches() {
    return defaultErrorMismatches;
  }

  public void setDefaultErrorMismatches(int errorMismatches) {
    this.defaultErrorMismatches = errorMismatches;
  }

  public int getErrorMismatches(PlatformType platform) {
    if (errorMismatchesByPlatform != null && errorMismatchesByPlatform.containsKey(platform)) {
      return errorMismatchesByPlatform.get(platform);
    }
    return defaultErrorMismatches;
  }

  public void setErrorMismatches(PlatformType platform, int errorMismatches) {
    if (errorMismatchesByPlatform == null) {
      errorMismatchesByPlatform = new HashMap<>();
    }
    errorMismatchesByPlatform.put(platform, errorMismatches);
  }

  public int getDefaultWarningMismatches() {
    return defaultWarningMismatches;
  }

  public void setDefaultWarningMismatches(int warningMismatches) {
    this.defaultWarningMismatches = warningMismatches;
  }

  public int getWarningMismatches(PlatformType platform) {
    if (warningMismatchesByPlatform != null && warningMismatchesByPlatform.containsKey(platform)) {
      return warningMismatchesByPlatform.get(platform);
    }
    return defaultWarningMismatches;
  }

  public void setWarningMismatches(PlatformType platform, int warningMismatches) {
    if (warningMismatchesByPlatform == null) {
      warningMismatchesByPlatform = new HashMap<>();
    }
    warningMismatchesByPlatform.put(platform, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(Pool pool) {
    if (pool == null || pool.getPoolContents() == null || pool.getPoolContents().isEmpty()) {
      return Collections.emptySet();
    }
    Stream<ParentLibrary> libraries = pool.getPoolContents().stream()
        .map(element -> element.getAliquot().getParentLibrary());
    int mismatches = getErrorMismatches(pool.getPlatformType());
    return getIndexSequencesWithTooFewMismatches(libraries, mismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(Pool pool) {
    if (pool == null || pool.getPoolContents() == null || pool.getPoolContents().isEmpty()) {
      return Collections.emptySet();
    }
    Stream<ParentLibrary> libraries = pool.getPoolContents().stream()
        .map(element -> element.getAliquot().getParentLibrary());
    int mismatches = getWarningMismatches(pool.getPlatformType());
    return getIndexSequencesWithTooFewMismatches(libraries, mismatches);
  }

  public Set<String> getDuplicateIndicesSequences(ListPoolView pool) {
    if (pool == null || pool.getElements() == null || pool.getElements().isEmpty()) {
      return Collections.emptySet();
    }
    Stream<ListPoolViewElement> libraries = pool.getElements().stream();
    int mismatches = getErrorMismatches(pool.getPlatformType());
    return getIndexSequencesWithTooFewMismatches(libraries, mismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(ListPoolView pool) {
    if (pool == null || pool.getElements() == null | pool.getElements().isEmpty()) {
      return Collections.emptySet();
    }
    Stream<ListPoolViewElement> libraries = pool.getElements().stream();
    int mismatches = getWarningMismatches(pool.getPlatformType());
    return getIndexSequencesWithTooFewMismatches(libraries, mismatches);
  }

  public Set<String> getDuplicateIndicesSequences(PoolOrder order) {
    if (order == null || order.getOrderLibraryAliquots() == null || order.getOrderLibraryAliquots().isEmpty()) {
      return Collections.emptySet();
    }
    Stream<Library> libraries = order.getOrderLibraryAliquots().stream()
        .map(orderAliquot -> orderAliquot.getAliquot().getLibrary());
    int mismatches = getErrorMismatches(getPlatformType(order));
    return getIndexSequencesWithTooFewMismatches(libraries, mismatches);
  }

  private static PlatformType getPlatformType(PoolOrder order) {
    return order.getOrderLibraryAliquots().iterator().next().getAliquot().getLibrary().getPlatformType();
  }

  public Set<String> getNearDuplicateIndicesSequences(PoolOrder order) {
    if (order == null || order.getOrderLibraryAliquots() == null || order.getOrderLibraryAliquots().isEmpty()) {
      return Collections.emptySet();
    }
    Stream<Library> libraries = order.getOrderLibraryAliquots().stream()
        .map(orderAliquot -> orderAliquot.getAliquot().getLibrary());
    int mismatches = getWarningMismatches(getPlatformType(order));
    return getIndexSequencesWithTooFewMismatches(libraries, mismatches);
  }

  public Set<String> getDuplicateIndicesSequences(Collection<? extends IndexedLibrary> libraries) {
    if (libraries == null || libraries.isEmpty()) {
      return Collections.emptySet();
    }
    int mismatches = getErrorMismatches(libraries.iterator().next().getPlatformType());
    return getIndexSequencesWithTooFewMismatches(libraries.stream(), mismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(Collection<? extends IndexedLibrary> libraries) {
    if (libraries == null || libraries.isEmpty()) {
      return Collections.emptySet();
    }
    int mismatches = getWarningMismatches(libraries.iterator().next().getPlatformType());
    return getIndexSequencesWithTooFewMismatches(libraries.stream(), mismatches);
  }

  private static Set<String> getIndexSequencesWithTooFewMismatches(Stream<? extends IndexedLibrary> libraries,
      int mismatchesThreshold) {
    Set<String> nearMatchSequences = new HashSet<>();
    // Real sequence → name the front end expects
    Map<String, String> knownSequences = new HashMap<>();
    libraries.forEach(library -> {
      String name = getIndicesString(library);
      for (String sequence : getCombinedIndexSequences(library.getIndex1(), library.getIndex2())) {
        for (Map.Entry<String, String> otherSequence : knownSequences.entrySet()) {
          if (LibraryIndex.checkMismatches(sequence, otherSequence.getKey()) <= mismatchesThreshold) {
            nearMatchSequences.add(name);
            nearMatchSequences.add(otherSequence.getValue());
          }
        }
        knownSequences.put(sequence, name);
      }
    });
    return nearMatchSequences;
  }

  private static String getIndicesString(IndexedLibrary library) {
    if (library.getIndex1() != null) {
      if (library.getIndex2() != null) {
        return library.getIndex1().getSequence() + "-" + library.getIndex2().getSequence();
      } else {
        return library.getIndex1().getSequence();
      }
    } else {
      return "";
    }
  }

  private static Set<String> getCombinedIndexSequences(LibraryIndex index1, LibraryIndex index2) {
    if (index1 == null) {
      return Collections.singleton("");
    } else {
      Set<String> index1Sequences = getSequences(index1);
      if (index2 == null) {
        return index1Sequences;
      } else {
        Set<String> index2Sequences = getSequences(index2);
        return index1Sequences.stream()
            .flatMap(sequence1 -> index2Sequences.stream()
                .map(sequence2 -> sequence1 + "-" + sequence2))
            .collect(Collectors.toSet());
      }
    }
  }

  private static Set<String> getSequences(LibraryIndex index) {
    if (index.getFamily().hasFakeSequence()) {
      return index.getRealSequences();
    } else {
      return Collections.singleton(index.getSequence());
    }
  }
}
