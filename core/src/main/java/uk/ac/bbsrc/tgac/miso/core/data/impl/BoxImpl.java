package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

@Entity
@Table(name = "Box")
public class BoxImpl extends AbstractBox {

  private static final long serialVersionUID = 1L;

  protected static final Logger log = LoggerFactory.getLogger(BoxImpl.class);

  // The contents of the Box
  @OneToMany(targetEntity = BoxableView.class, fetch = FetchType.LAZY)
  @MapKeyColumn(name = "position", unique = true)
  @JoinTable(name = "BoxPosition", joinColumns = { @JoinColumn(name = "boxId") }, inverseJoinColumns = {
      @JoinColumn(name = "targetType", referencedColumnName = "targetType"),
      @JoinColumn(name = "targetId", referencedColumnName = "targetId") })
  @Fetch(FetchMode.SUBSELECT)
  private Map<String, BoxableView> boxableViews = new HashMap<>();

  @Formula("(SELECT COUNT(bp.targetId) from BoxPosition bp WHERE bp.boxId = boxId)")
  private int tubeCountOnLoad;

  /**
   * Construct new Box with defaults, and an empty SecurityProfile
   */
  public BoxImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct new Box using Security Profile owned by a given User
   * 
   * @param User user
   */
  public BoxImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public boolean isFreePosition(String position) {
    validate(position);
    if (boxableViews.get(position) == null) return true;
    return false;
  }

  @Override
  public boolean isValidPosition(String position) {
    if (!position.matches("[A-Z][0-9][0-9]")) return false;
    if (BoxUtils.fromRowChar(position.charAt(0)) >= getSize().getRows()) return false;
    int col = BoxUtils.tryParseInt(position.substring(1, 3));
    if (col < 0 || col > getSize().getColumns()) return false; // columns are zero-indexed in database
    return true;
  }

  private void validate(String position) {
    if (!position.matches("[A-Z][0-9][0-9]"))
      throw new IllegalArgumentException("Position " + position + " does not match [A-Z][0-9][0-9]");
    if (BoxUtils.fromRowChar(position.charAt(0)) >= getSize().getRows())
      throw new IndexOutOfBoundsException("Row letter too large!" + position);
    int col = BoxUtils.tryParseInt(position.substring(1, 3));
    if (col <= 0 || col > getSize().getColumns()) throw new IndexOutOfBoundsException("Column value too large!");
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
    if (Hibernate.isInitialized(boxableViews)) {
      return boxableViews.size();
    } else {
      return tubeCountOnLoad;
    }
  }

  @Override
  public void setBoxables(Map<String, BoxableView> items) {
    this.boxableViews = items;
  }

  @Override
  public Map<String, BoxableView> getBoxables() {
    return boxableViews;
  }

  @Override
  public void setBoxable(String position, BoxableView item) {
    validate(position);

    // if already in this box, remove from previous position first
    if (item.getId().getTargetId() != 0L) {
      String oldPosition = null;
      for (Map.Entry<String, BoxableView> entry : boxableViews.entrySet()) {
        if (entry.getValue().getId().equals(item.getId())) {
          oldPosition = entry.getKey();
          break;
        }
      }
      if (oldPosition != null) {
        boxableViews.remove(oldPosition);
      }
    }
    boxableViews.put(position, item);
  }

  @Override
  public BoxableView getBoxable(String position) {
    validate(position);
    return boxableViews.get(position);
  }

  @Override
  public void removeBoxable(String position) {
    validate(position);
    boxableViews.remove(position);
  }

  @Override
  public void removeAllBoxables() {
    boxableViews.clear();
  }

  @Override
  public boolean isDeletable() {
    return true;
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

}
