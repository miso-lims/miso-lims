package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.MetaValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

@Entity
@Table(name = "Box")
public class BoxImpl extends AbstractBox implements Serializable {

  private static final long serialVersionUID = 1L;

  protected static final Logger log = LoggerFactory.getLogger(BoxImpl.class);

  // The contents of the Box
  @ManyToAny(metaColumn = @Column(name = "targetType"))
  @MapKeyColumn(name = "position", unique = true)
  @MapKeyClass(String.class)
  @JoinTable(name = "BoxPosition", joinColumns = { @JoinColumn(name = "targetId") }, inverseJoinColumns = {
      @JoinColumn(name = "boxId") })
  @AnyMetaDef(idType = "long", metaType = "string", metaValues = {
      @MetaValue(targetEntity = LibraryImpl.class, value = "L"),
      @MetaValue(targetEntity = PoolImpl.class, value = "P"),
      @MetaValue(targetEntity = SampleImpl.class, value = "S")
  })
  private Map<String, Boxable> boxableItems = new HashMap<>();

  /*
   * Construct new Box with defaults, and an empty SecurityProfile
   */
  public BoxImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /*
   * Construct new Box using Security Profile owned by a given User
   * 
   * @param User user
   */
  public BoxImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public boolean isFreePosition(String position) {
    if (boxableItems.get(position) == null) return true;
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
    if (!position.matches("[A-Z][0-9][0-9]")) throw new IllegalArgumentException("Position must match [A-Z][0-9][0-9]");
    if (BoxUtils.fromRowChar(position.charAt(0)) >= getSize().getRows()) throw new IndexOutOfBoundsException("Row letter too large!");
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
    return boxableItems.size();
  }

  @Override
  public String getLabelText() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBoxables(Map<String, Boxable> items) {
    this.boxableItems = items;
  }

  @Override
  public Map<String, Boxable> getBoxables() {
    return boxableItems;
  }

  @Override
  public boolean boxableExists(Boxable boxable) {
    return boxableItems.values().contains(boxable);
  }

  @Override
  public void setBoxable(String position, Boxable item) {
    validate(position);
    boxableItems.put(position, item);
  }

  @Override
  public Boxable getBoxable(String position) {
    validate(position);
    return boxableItems.get(position);
  }

  @Override
  public void removeBoxable(String position) {
    validate(position);
    boxableItems.remove(position);
  }

  @Override
  public void removeAllBoxables() {
    Iterator<Boxable> i = boxableItems.values().iterator();
    while (i.hasNext()) {
      Boxable box = i.next();
      // box.setLocationBarcode(""); // TODO: GLT-219
      i.remove();
    }
  }

  @Override
  public Boxable[][] get2DArray() {
    Boxable[][] arr = new Boxable[getSize().getRows()][getSize().getColumns()];
    for (int i = 0; i < getSize().getRows(); i++) {
      for (int j = 0; j < getSize().getColumns(); j++) {
        arr[i][j] = boxableItems.get(BoxUtils.getPositionString(i, j));
      }
    }
    return arr;
  }

  @Override
  public boolean isDeletable() {
    return true;
  }
  
  @Override
  public String toString() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (IOException ex) {
      return "";
    }
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
