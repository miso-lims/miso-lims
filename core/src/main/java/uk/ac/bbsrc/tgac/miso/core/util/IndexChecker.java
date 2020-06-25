package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolViewElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;

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

  public String getErrorMismatchesMessage() {
    return errorMismatchesMessage;
  }

  public int getWarningMismatches() {
    return warningMismatches;
  }

  public String getWarningMismatchesMessage() {
    return warningMismatchesMessage;
  }

  public Set<String> getDuplicateIndicesSequencesFromList(List<List<Index>> indices){
    if(indices == null) return Collections.emptySet();
    return getIndexSequencesWithTooFewMismatches(indices, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequencesFromList(List<List<Index>> indices){
    if(indices == null) return Collections.emptySet();
    return getIndexSequencesWithTooFewMismatches(indices, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(Pool pool) {
    if (pool == null) return Collections.emptySet();
    List<List<Index>> indices = getIndexSequences(pool);
    return getIndexSequencesWithTooFewMismatches(indices, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(Pool pool) {
    if (pool == null) return Collections.emptySet();
    List<List<Index>> indices = getIndexSequences(pool);
    return getIndexSequencesWithTooFewMismatches(indices, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(ListPoolView pool) {
    if (pool == null) return Collections.emptySet();
    List<List<Index>> indices = getIndexSequences(pool);
    return getIndexSequencesWithTooFewMismatches(indices, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(ListPoolView pool) {
    if (pool == null) return Collections.emptySet();
    List<List<Index>> indices = getIndexSequences(pool);
    return getIndexSequencesWithTooFewMismatches(indices, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(PoolOrder order) {
    if (order == null) return Collections.emptySet();
    List<Set<Index>> indices = getIndexSequences(order);
    return getIndexSequencesWithTooFewMismatches(indices, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(PoolOrder order) {
    if (order == null) return Collections.emptySet();
    List<Set<Index>> indices = getIndexSequences(order);
    return getIndexSequencesWithTooFewMismatches(indices, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(List<LibraryAliquot> aliquots) {
    if (aliquots == null) return Collections.emptySet();
    List<Set<Index>> indices = getIndexSequences(aliquots);
    return getIndexSequencesWithTooFewMismatches(indices, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(List<LibraryAliquot> aliquots) {
    if (aliquots == null) return Collections.emptySet();
    List<Set<Index>> indices = getIndexSequences(aliquots);
    return getIndexSequencesWithTooFewMismatches(indices, warningMismatches);
  }

  private static Set<String> getIndexSequencesWithTooFewMismatches(List<? extends Collection<Index>> indices, int mismatchesThreshold) {
    Set<String> nearMatchSequences = new HashSet<>();
    // Real sequence → name the front end expects
    Map<String, String> knownSequences = new HashMap<>();
    for (Collection<Index> indexGroup : indices) {
      String name = indexGroup.stream().sorted(Comparator.comparingInt(Index::getPosition)).map(Index::getSequence)
          .collect(Collectors.joining("-"));
      for (String sequence : getCombinedIndexSequences(indexGroup)) {
        if (knownSequences.containsKey(sequence)) {
          nearMatchSequences.add(name);
          nearMatchSequences.add(knownSequences.get(sequence));
        } else {
          for (Map.Entry<String, String> otherSequence : knownSequences.entrySet()) {
            if (Index.checkMismatches(sequence, otherSequence.getKey()) <= mismatchesThreshold) {
              nearMatchSequences.add(name);
              nearMatchSequences.add(otherSequence.getValue());
            }
          }
        }
        knownSequences.put(sequence, name);
      }
    }
    return nearMatchSequences;
  }

  private static List<String> getCombinedIndexSequences(Collection<Index> indices) {
    if (indices.isEmpty()) {
      return Collections.singletonList("");
    }
    List<String> currentSequences = null;
    Set<Integer> positions = indices.stream().map(Index::getPosition).collect(Collectors.toCollection(TreeSet::new));
    for (int position : positions) {
      List<String> suffixes = indices.stream().filter(i -> i.getPosition() == position)
          .flatMap(i -> i.getFamily().hasFakeSequence() ? i.getRealSequences().stream() : Stream.of(i.getSequence()))
          .collect(Collectors.toList());
      if (currentSequences == null) {
        currentSequences = suffixes;
      } else {
        currentSequences = currentSequences.stream().flatMap(prefix -> suffixes.stream().map(suffix -> prefix + "-" + suffix))
            .collect(Collectors.toList());
      }
    }
    return currentSequences;
  }

  private static List<List<Index>> getIndexSequences(Pool pool) {
    return pool.getPoolContents().stream().map(PoolElement::getPoolableElementView)
        .map(PoolableElementView::getIndices)
        .collect(Collectors.toList());
  }

  private static List<List<Index>> getIndexSequences(ListPoolView pool) {
    return pool.getElements().stream()
        .map(ListPoolViewElement::getIndices)
        .collect(Collectors.toList());
  }

  private static List<Set<Index>> getIndexSequences(PoolOrder pool) {
    return pool.getOrderLibraryAliquots().stream().map(ola -> ola.getAliquot().getLibrary().getIndices())
        .collect(Collectors.toList());
  }

  private static List<Set<Index>> getIndexSequences(List<LibraryAliquot> aliquots) {
    return aliquots.stream().map(la -> la.getLibrary().getIndices())
        .collect(Collectors.toList());
  }
}
