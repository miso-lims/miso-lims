/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonManagedReference;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "TagBarcodeFamily")
public class TagBarcodeFamily implements Iterable<TagBarcode> {
  public static final TagBarcodeFamily NULL = new TagBarcodeFamily();

  static {
    NULL.setId(0L);
    NULL.setName("No barcode");
    List<TagBarcode> empty = Collections.emptyList();
    NULL.setBarcodes(empty);
  }

  private Boolean archived;
  @OneToMany(targetEntity = TagBarcode.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "tagFamilyId")
  @OrderBy("position, name")
  @JsonManagedReference
  private List<TagBarcode> barcodes;
  @Column(nullable = false)
  private String name;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlatformType platformType;
  @Id
  private Long tagFamilyId;

  public Boolean getArchived() {
    return archived;
  }

  public List<TagBarcode> getBarcodes() {
    return barcodes;
  }

  public Iterable<TagBarcode> getBarcodesForPosition(int position) {
    List<TagBarcode> selected = new ArrayList<>();
    for (TagBarcode barcode : this) {
      if (barcode.getPosition() == position) {
        selected.add(barcode);
      }
    }
    return selected;
  }

  public Long getId() {
    return tagFamilyId;
  }

  public int getMaximumNumber() {
    int max = 0;
    for (TagBarcode barcode : this) {
      if (barcode.getPosition() > max) {
        max = barcode.getPosition();
      }
    }
    return max;
  }

  public String getName() {
    return name;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  @Override
  public Iterator<TagBarcode> iterator() {
    return getBarcodes().iterator();
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public void setBarcodes(List<TagBarcode> barcodes) {
    this.barcodes = barcodes;
  }

  public void setId(Long id) {
    tagFamilyId = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlatformType(PlatformType platform) {
    this.platformType = platform;
  }

}
