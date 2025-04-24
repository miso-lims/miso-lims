package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.IndexedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolViewElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;

@Component
public class IndexChecker {

  @Value("${miso.pools.error.index.mismatches:1}")
  private int errorMismatches;
  @Value("${miso.pools.error.index.mismatches.message:DUPLICATE INDICES}")
  private String errorMismatchesMessage;
  @Value("${miso.pools.warning.index.mismatches:2}")
  private int warningMismatches;
  @Value("${miso.pools.warning.index.mismatches.message:Near-Duplicate Indices}")
  private String warningMismatchesMessage;

  public int getErrorMismatches() {
    return errorMismatches;
  }

  public void setErrorMismatches(int errorMismatches) {
    this.errorMismatches = errorMismatches;
  }

  public String getErrorMismatchesMessage() {
    return errorMismatchesMessage;
  }

  public void setErrorMismatchesMessage(String errorMismatchesMessage) {
    this.errorMismatchesMessage = errorMismatchesMessage;
  }

  public int getWarningMismatches() {
    return warningMismatches;
  }

  public void setWarningMismatches(int warningMismatches) {
    this.warningMismatches = warningMismatches;
  }

  public String getWarningMismatchesMessage() {
    return warningMismatchesMessage;
  }

  public void setWarningMismatchesMessage(String warningMismatchesMessage) {
    this.warningMismatchesMessage = warningMismatchesMessage;
  }

  public Set<String> getDuplicateIndicesSequences(Pool pool) {
    if (pool == null)
      return Collections.emptySet();
    Stream<ParentLibrary> libraries = pool.getPoolContents().stream()
        .map(element -> element.getAliquot().getParentLibrary());
    return getIndexSequencesWithTooFewMismatches(libraries, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(Pool pool) {
    if (pool == null)
      return Collections.emptySet();
    Stream<ParentLibrary> libraries = pool.getPoolContents().stream()
        .map(element -> element.getAliquot().getParentLibrary());
    return getIndexSequencesWithTooFewMismatches(libraries, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(ListPoolView pool) {
    if (pool == null)
      return Collections.emptySet();
    Stream<ListPoolViewElement> libraries = pool.getElements().stream();
    return getIndexSequencesWithTooFewMismatches(libraries, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(ListPoolView pool) {
    if (pool == null)
      return Collections.emptySet();
    Stream<ListPoolViewElement> libraries = pool.getElements().stream();
    return getIndexSequencesWithTooFewMismatches(libraries, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(PoolOrder order) {
    if (order == null)
      return Collections.emptySet();
    Stream<Library> libraries = order.getOrderLibraryAliquots().stream()
        .map(orderAliquot -> orderAliquot.getAliquot().getLibrary());
    return getIndexSequencesWithTooFewMismatches(libraries, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(PoolOrder order) {
    if (order == null)
      return Collections.emptySet();
    Stream<Library> libraries = order.getOrderLibraryAliquots().stream()
        .map(orderAliquot -> orderAliquot.getAliquot().getLibrary());
    return getIndexSequencesWithTooFewMismatches(libraries, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(Collection<? extends IndexedLibrary> libraries) {
    if (libraries == null)
      return Collections.emptySet();
    return getIndexSequencesWithTooFewMismatches(libraries.stream(), errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(Collection<? extends IndexedLibrary> libraries) {
    if (libraries == null)
      return Collections.emptySet();
    return getIndexSequencesWithTooFewMismatches(libraries.stream(), warningMismatches);
  }

  private static Set<String> getIndexSequencesWithTooFewMismatches(Stream<? extends IndexedLibrary> libraries,
      int mismatchesThreshold) {
    Set<String> nearMatchSequences = new HashSet<>();
    // Real sequence → name the front end expects
    Map<String, String> knownSequences = new HashMap<>();
    libraries.forEach(library -> {
      String name = getIndicesString(library);
      for (String sequence : getCombinedIndexSequences(library.getIndex1(), library.getIndex2())) {
        if (knownSequences.containsKey(sequence)) {
          nearMatchSequences.add(name);
          nearMatchSequences.add(knownSequences.get(sequence));
        } else {
          for (Map.Entry<String, String> otherSequence : knownSequences.entrySet()) {
            if (LibraryIndex.checkMismatches(sequence, otherSequence.getKey()) <= mismatchesThreshold) {
              nearMatchSequences.add(name);
              nearMatchSequences.add(otherSequence.getValue());
            }
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
