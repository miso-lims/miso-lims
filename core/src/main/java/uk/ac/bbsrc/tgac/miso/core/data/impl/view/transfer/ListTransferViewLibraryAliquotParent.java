package uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
