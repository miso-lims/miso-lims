package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Indices represent adapter sequences that can be prepended to sequencable material in order to
 * facilitate multiplexing.
 * 
 * @author Rob Davey
 * @date 10-May-2011
 * @since 0.0.3
 */
@Entity
@Table(name = "Indices")
public class Index implements Deletable, Nameable, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Long UNSAVED_ID = 0L;

  public static void sort(final List<Index> indices) {
    Collections.sort(indices, (o1, o2) -> o1.getPosition() - o2.getPosition());
  }

  /**
   * Compares two sequences to check for duplicates or near matches. For sequences of different
   * lengths, the additional characters on the longer sequence are ignored - "AAAA" and "AAAACC" are
   * considered duplicates. For every non-matching character, edit distance increases by 1
   * 
   * @param sequence1
   * @param sequence2
   * @return mismatches between the two sequences with 0 meaning they are duplicates, 1 meaning they
   *         differ by one character, etc.
   */
  public static int checkMismatches(String sequence1, String sequence2) {
    int minLength = Math.min(sequence1.length(), sequence2.length());
    int maxLength = Math.max(sequence1.length(), sequence2.length());
    if (minLength == 0 && maxLength > 0) {
      return maxLength;
    }
    int mismatches = minLength;
    for (int i = 0; i < minLength; i++) {
      if (sequence1.charAt(i) == sequence2.charAt(i)) {
        mismatches--;
      }
    }
    return mismatches;
  }

  @ManyToOne
  @JoinColumn(name = "indexFamilyId", nullable = false)
  private IndexFamily family;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private int position;
  @Column(nullable = false)
  private String sequence;

  private String realSequences;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long indexId = UNSAVED_ID;

  public IndexFamily getFamily() {
    return family;
  }

  @Override
  public long getId() {
    return indexId;
  }

  @Override
  public String getName() {
    return name;
  }

  public int getPosition() {
    return position;
  }

  public String getSequence() {
    return sequence;
  }

  public void setFamily(IndexFamily family) {
    this.family = family;
  }

  @Override
  public void setId(long id) {
    this.indexId = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void setSequence(String sequence) {
    this.sequence = sequence;
  }

  public String getLabel() {
    if (getSequence() == null || getFamily().hasFakeSequence())
      return getName();
    return getName() + " (" + getSequence() + ")";
  }

  @Override
  public int hashCode() {
    return LimsUtils.hashCodeByIdFirst(this, family, name, sequence, position);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equalsByIdFirst(this, obj,
        Index::getFamily,
        Index::getName,
        Index::getSequence,
        Index::getPosition);
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public Set<String> getRealSequences() {
    if (realSequences == null) {
      return Collections.emptySet();
    } else {
      return Arrays.stream(realSequences.split(",")).collect(Collectors.toUnmodifiableSet());
    }
  }

  public void setRealSequences(String realSequences) {
    if (LimsUtils.isStringEmptyOrNull(realSequences)) {
      this.realSequences = null;
    } else {
      this.realSequences = Arrays.stream(realSequences.split(",")).sorted().collect(Collectors.joining(","));
    }
  }

  public void setRealSequences(Set<String> realSequences) {
    if (realSequences == null || realSequences.isEmpty()) {
      this.realSequences = null;
    } else {
      this.realSequences = realSequences.stream().sorted().collect(Collectors.joining(","));
    }
  }

  @Override
  public String getDeleteType() {
    return "Index";
  }

  @Override
  public String getDeleteDescription() {
    return getFamily().getName() + " - " + getName();
  }

}
