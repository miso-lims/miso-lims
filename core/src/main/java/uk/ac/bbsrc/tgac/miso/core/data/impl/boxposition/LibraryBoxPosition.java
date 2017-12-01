package uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition;

@Entity
@Immutable
@Table(name = "LibraryBoxPosition")
@Synchronize("Library")
public class LibraryBoxPosition extends AbstractBoxPosition {

  private static final long serialVersionUID = 1L;

  @Id
  private Long libraryId;

  public Long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

}
