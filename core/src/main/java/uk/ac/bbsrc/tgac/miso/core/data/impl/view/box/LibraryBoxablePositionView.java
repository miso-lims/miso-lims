package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "LibraryBoxPosition")
public class LibraryBoxablePositionView extends BoxablePositionView {

  private static final long serialVersionUID = 1L;

  @Id
  private long libraryId;

  @Override
  public long getId() {
    return libraryId;
  }

  @Override
  public void setId(long id) {
    this.libraryId = id;
  }

}
