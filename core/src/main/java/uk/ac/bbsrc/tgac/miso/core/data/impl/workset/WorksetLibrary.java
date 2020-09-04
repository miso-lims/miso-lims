package uk.ac.bbsrc.tgac.miso.core.data.impl.workset;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibrary.WorksetLibraryId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Workset_Library")
@IdClass(WorksetLibraryId.class)
public class WorksetLibrary extends WorksetItem<Library> {

  public static class WorksetLibraryId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Workset workset;
    private Library item;

    public Workset getWorkset() {
      return workset;
    }

    public void setWorkset(Workset workset) {
      this.workset = workset;
    }

    public Library getItem() {
      return item;
    }

    public void setItem(Library item) {
      this.item = item;
    }

    @Override
    public int hashCode() {
      return Objects.hash(workset, item);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          WorksetLibraryId::getWorkset,
          WorksetLibraryId::getItem);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "worksetId")
  private Workset workset;

  @Id
  @ManyToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "libraryId")
  private Library item;

  @Override
  public Workset getWorkset() {
    return workset;
  }

  @Override
  public void setWorkset(Workset workset) {
    this.workset = workset;
  }

  @Override
  public Library getItem() {
    return item;
  }

  @Override
  public void setItem(Library item) {
    this.item = item;
  }

}
