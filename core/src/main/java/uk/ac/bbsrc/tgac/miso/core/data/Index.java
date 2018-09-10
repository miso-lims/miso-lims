/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Indices represent adapter sequences that can be prepended to sequencable material in order to facilitate multiplexing.
 * 
 * @author Rob Davey
 * @date 10-May-2011
 * @since 0.0.3
 */
@Entity
@Table(name = "Indices")
public class Index implements Nameable, Serializable {

  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;

  public static void sort(final List<Index> indices) {
    Collections.sort(indices, (o1, o2) -> o1.getPosition() - o2.getPosition());
  }

  /**
   * Compares two sequences to check for duplicates or near matches. For sequences of different lengths, the additional characters
   * on the longer sequence are ignored - "AAAA" and "AAAACC" are considered duplicates. For every non-matching character, edit
   * distance increases by 1
   * 
   * @param sequence1
   * @param sequence2
   * @return edit distance between the two sequences with 0 meaning they are duplicates, 1 meaning they differ by one character, etc.
   */
  public static int checkEditDistance(String sequence1, String sequence2) {
    int minLength = Math.min(sequence1.length(), sequence2.length());
    int editDistance = minLength;
    for (int i = 0; i < minLength; i++) {
      if (sequence1.charAt(i) == sequence2.charAt(i)) {
        editDistance--;
      }
    }
    return editDistance;
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

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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
    if (getSequence() == null || getFamily().hasFakeSequence()) return getName();
    return getName() + " (" + getSequence() + ")";
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(9, 39)
        .append(family)
        .append(name)
        .append(sequence)
        .append(position)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Index other = (Index) obj;
    return new EqualsBuilder()
        .append(family, other.family)
        .append(name, other.name)
        .append(sequence, other.sequence)
        .append(position, other.position)
        .isEquals();
  }

}
