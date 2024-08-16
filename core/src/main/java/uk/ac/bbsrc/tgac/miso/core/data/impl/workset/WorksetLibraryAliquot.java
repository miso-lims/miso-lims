package uk.ac.bbsrc.tgac.miso.core.data.impl.workset;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibraryAliquot.WorksetLibraryAliquotId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Workset_LibraryAliquot")
@IdClass(WorksetLibraryAliquotId.class)
public class WorksetLibraryAliquot extends WorksetItem<LibraryAliquot> {

  public static class WorksetLibraryAliquotId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Workset workset;
    private LibraryAliquot item;

    public Workset getWorkset() {
      return workset;
    }

    public void setWorkset(Workset workset) {
      this.workset = workset;
    }

    public LibraryAliquot getItem() {
      return item;
    }

    public void setItem(LibraryAliquot item) {
      this.item = item;
    }

    @Override
    public int hashCode() {
      return Objects.hash(workset, item);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          WorksetLibraryAliquotId::getWorkset,
          WorksetLibraryAliquotId::getItem);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "worksetId")
  private Workset workset;

  @Id
  @ManyToOne
  @JoinColumn(name = "aliquotId")
  private LibraryAliquot item;

  @Override
  public Workset getWorkset() {
    return workset;
  }

  @Override
  public void setWorkset(Workset workset) {
    this.workset = workset;
  }

  @Override
  public LibraryAliquot getItem() {
    return item;
  }

  @Override
  public void setItem(LibraryAliquot item) {
    this.item = item;
  }

}
