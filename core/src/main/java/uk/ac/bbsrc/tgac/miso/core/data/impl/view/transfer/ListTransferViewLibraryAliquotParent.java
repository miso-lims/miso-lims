package uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "LibraryAliquot")
@Immutable
public class ListTransferViewLibraryAliquotParent implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "aliquotId")
  private long id;

  @OneToOne
  @JoinColumn(name = "libraryId")
  private ListTransferViewLibraryParent library;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ListTransferViewLibraryParent getLibrary() {
    return library;
  }

  public void setLibrary(ListTransferViewLibraryParent library) {
    this.library = library;
  }

}
