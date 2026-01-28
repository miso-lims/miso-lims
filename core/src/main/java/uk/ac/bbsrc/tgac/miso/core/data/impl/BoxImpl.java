package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

@Entity
@Table(name = "Box")
public class BoxImpl extends AbstractBox {

  private static final long serialVersionUID = 1L;

  @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true)
  @MapKeyColumn(name = "position")
  private Map<String, BoxPosition> boxPositions = new HashMap<>();

  /**
   * Construct new Box with defaults, and an empty SecurityProfile
   */
  public BoxImpl() {}

  @Override
  public boolean isFreePosition(String position) {
    validate(position);
    return boxPositions.get(position) == null;
  }

  @Override
  public boolean isValidPosition(String position) {
    if (!position.matches("[A-Z][0-9][0-9]"))
      return false;
    if (BoxUtils.fromRowChar(position.charAt(0)) >= getSize().getRows())
      return false;
    int col = BoxUtils.tryParseInt(position.substring(1, 3));
    if (col < 0 || col > getSize().getColumns())
      return false; // columns are zero-indexed in database
    return true;
  }

  private void validate(String position) {
    if (!position.matches("[A-Z][0-9][0-9]"))
      throw new IllegalArgumentException("Position " + position + " does not match [A-Z][0-9][0-9]");
    if (BoxUtils.fromRowChar(position.charAt(0)) >= getSize().getRows())
      throw new IndexOutOfBoundsException("Row letter too large!" + position);
    int col = BoxUtils.tryParseInt(position.substring(1, 3));
    if (col <= 0 || col > getSize().getColumns())
      throw new IndexOutOfBoundsException("Column value too large!");
  }

  @Override
  public int getPositionCount() {
    return getSize().getColumns() * getSize().getRows();
  }

  @Override
  public int getFreeCount() {
    return getPositionCount() - getTubeCount();
  }

  @Override
  public int getTubeCount() {
    return boxPositions.size();
  }

  @Override
  public Map<String, BoxPosition> getBoxPositions() {
    if (boxPositions == null) {
      boxPositions = new HashMap<>();
    }
    return boxPositions;
  }

  @Override
  public void setBoxPositions(Map<String, BoxPosition> boxPositions) {
    this.boxPositions = boxPositions;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", getId())
        .append("name", getName())
        .append("alias", getAlias())
        .toString();
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    BoxChangeLog changeLog = new BoxChangeLog();
    changeLog.setBox(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitBox(this);
  }

}
