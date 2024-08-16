package uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition;

@Entity
@Immutable
@Table(name = "LibraryBoxPosition")
@Synchronize("Library")
public class LibraryBoxPosition extends AbstractBoxPosition {

  private static final long serialVersionUID = 1L;

  @Id
  private Long libraryId;

  @Override
  public long getItemId() {
    return libraryId;
  }

  @Override
  public void setItemId(long id) {
    this.libraryId = id;
  }
}
