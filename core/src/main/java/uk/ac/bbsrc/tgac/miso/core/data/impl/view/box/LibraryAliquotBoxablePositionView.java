package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "LibraryAliquotBoxPosition")
public class LibraryAliquotBoxablePositionView extends BoxablePositionView {

  private static final long serialVersionUID = 1L;

  @Id
  private long aliquotId;

  @Override
  public long getId() {
    return aliquotId;
  }

  @Override
  public void setId(long id) {
    this.aliquotId = id;
  }

}
