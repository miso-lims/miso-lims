package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.MetaValue;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

@Entity
@Table(name = "DuplicateBarcodes")
public class DuplicateBarcodes {
  private long count;
  @Id
  private String identificationBarcode;
  @ManyToAny(metaColumn = @Column(name = "targetType"), fetch = FetchType.LAZY)
  @JoinTable(name = "DuplicateBarcodes_Items", joinColumns = { @JoinColumn(name = "identificationBarcode") }, inverseJoinColumns = {
      @JoinColumn(name = "targetId") })
  @AnyMetaDef(idType = "long", metaType = "string", metaValues = {
      @MetaValue(targetEntity = LibraryImpl.class, value = "Library"),
      @MetaValue(targetEntity = PoolImpl.class, value = "Pool"),
      @MetaValue(targetEntity = SampleImpl.class, value = "Sample"),
      @MetaValue(targetEntity = LibraryDilution.class, value = "Dilution"),
  })
  private Set<Boxable> items;

  public long getCount() {
    return count;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public Set<Boxable> getItems() {
    return items;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public void setItems(Set<Boxable> items) {
    this.items = items;
  }
}
