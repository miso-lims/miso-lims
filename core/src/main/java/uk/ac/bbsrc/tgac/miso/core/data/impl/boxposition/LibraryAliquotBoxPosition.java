package uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition;

@Entity
@Immutable
@Table(name = "LibraryAliquotBoxPosition")
@Synchronize("LibraryAliquot")
public class LibraryAliquotBoxPosition extends AbstractBoxPosition {

  private static final long serialVersionUID = 1L;

  @Id
  private Long aliquotId;

  @Override
  public long getItemId() {
    return aliquotId;
  }

  @Override
  public void setItemId(long id) {
    this.aliquotId = aliquotId;
  }
}
