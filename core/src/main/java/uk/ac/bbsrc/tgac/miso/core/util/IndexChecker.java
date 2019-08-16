package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    List<List<Index>> indices = getIndexSequences(order);
    return getIndexSequencesWithTooFewMismatches(indices, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(PoolOrder order) {
    if (order == null) return Collections.emptySet();
    List<List<Index>> indices = getIndexSequences(order);
    return getIndexSequencesWithTooFewMismatches(indices, warningMismatches);
  }

  public Set<String> getDuplicateIndicesSequences(List<LibraryAliquot> aliquots) {
    if (aliquots == null) return Collections.emptySet();
    List<List<Index>> indices = getIndexSequences(aliquots);
    return getIndexSequencesWithTooFewMismatches(indices, errorMismatches);
  }

  public Set<String> getNearDuplicateIndicesSequences(List<LibraryAliquot> aliquots) {
    if (aliquots == null) return Collections.emptySet();
    List<List<Index>> indices = getIndexSequences(aliquots);
    return getIndexSequencesWithTooFewMismatches(indices, warningMismatches);
  }
  private static Set<String> getIndexSequencesWithTooFewMismatches(List<List<Index>> indices, int mismatchesThreshold) {
    Set<String> nearMatchSequences = new HashSet<>();
    if (indices.stream().flatMap(List::stream).allMatch(index -> hasFakeSequence(index)))
      return Collections.emptySet();
    for (int i = 0; i < indices.size(); i++) {
      String sequence1 = getCombinedIndexSequences(indices.get(i));
      if (sequence1.length() == 0) {
        continue;
      }
      for (int j = i + 1; j < indices.size(); j++) {
        String sequence2 = getCombinedIndexSequences(indices.get(j));
        if (sequence2.length() == 0 || !isCheckNecessary(indices.get(i), indices.get(j), mismatchesThreshold)) {
          continue;
        }
        if (Index.checkMismatches(sequence1, sequence2) <= mismatchesThreshold) {
          nearMatchSequences.add(sequence1);
          nearMatchSequences.add(sequence2);
        }
      }
    }
    return nearMatchSequences;
  }

  private static boolean hasFakeSequence(Index index) {
    return index == null ? false : index.getFamily().hasFakeSequence();
  }

  private static boolean isCheckNecessary(List<Index> indices1, List<Index> indices2, int minimumDistance) {
    return !(indices1.stream().anyMatch(index -> hasFakeSequence(index)) || indices2.stream().anyMatch(index -> hasFakeSequence(index))
        && (getCombinedIndexSequences(indices2).length() != getCombinedIndexSequences(indices2).length()));
  }

  private static String getCombinedIndexSequences(List<Index> indices) {
    return indices.stream()
        .sorted((i1, i2) -> Integer.compare(i1.getPosition(), i2.getPosition()))
        .map(Index::getSequence)
        .collect(Collectors.joining("-"));
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

  private static List<List<Index>> getIndexSequences(PoolOrder pool) {
    return pool.getOrderLibraryAliquots().stream().map(ola -> ola.getAliquot().getLibrary().getIndices())
        .collect(Collectors.toList());
  }

  private static List<List<Index>> getIndexSequences(List<LibraryAliquot> aliquots) {
    return aliquots.stream().map(la -> la.getLibrary().getIndices())
        .collect(Collectors.toList());
  }
}
