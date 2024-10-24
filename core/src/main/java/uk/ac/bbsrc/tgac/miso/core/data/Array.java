package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.ArrayChangeLog;

@Entity
public class Array implements Serializable, Aliasable, ChangeLoggable, Deletable {

  private static final long serialVersionUID = 1L;

  private static final Pattern positionRegex = Pattern.compile("^R(\\d{2})C(\\d{2})$");

  private static final long UNSAVED_ID = 0L;

  @Id
  @Column(name = "arrayId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id = Array.UNSAVED_ID;

  private String alias;

  @ManyToOne
  @JoinColumn(name = "arrayModelId", nullable = false, updatable = false)
  private ArrayModel arrayModel;

  private String serialNumber;
  private String description;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @OneToMany(targetEntity = SampleImpl.class)
  @MapKeyColumn(name = "position", unique = true)
  @JoinTable(name = "ArrayPosition",
      joinColumns = {@JoinColumn(name = "arrayId", referencedColumnName = "arrayId")},
      inverseJoinColumns = {@JoinColumn(name = "sampleId", referencedColumnName = "sampleId")})
  @Fetch(FetchMode.SUBSELECT)
  private Map<String, Sample> samples = new HashMap<>();

  @OneToMany(targetEntity = ArrayChangeLog.class, mappedBy = "array", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public ArrayModel getArrayModel() {
    return arrayModel;
  }

  public void setArrayModel(ArrayModel arrayModel) {
    this.arrayModel = arrayModel;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public Date getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Map<String, Sample> getSamples() {
    return samples;
  }

  public void setSamples(Map<String, Sample> samples) {
    this.samples = samples;
  }

  public Sample getSample(String position) {
    validatePosition(position);
    return samples.get(position);
  }

  public void setSample(String position, Sample sample) {
    validatePosition(position);
    if (sample == null) {
      samples.remove(position);
    } else {
      samples.put(position, sample);
    }
  }

  public boolean isPositionValid(String position) {
    if (getArrayModel() == null) {
      return false;
    }
    Matcher m = positionRegex.matcher(position);
    if (!m.matches()) {
      return false;
    }
    int y = Integer.parseInt(m.group(1));
    int x = Integer.parseInt(m.group(2));
    return isInBounds(x, y);
  }

  private void validatePosition(String position) {
    if (getArrayModel() == null) {
      throw new IllegalStateException("ArrayModel not set. Cannot verify size of Array.");
    }
    Matcher m = positionRegex.matcher(position);
    if (!m.matches()) {
      throw new IllegalArgumentException("Invalid Array position String: " + position);
    }
    int y = Integer.parseInt(m.group(1));
    int x = Integer.parseInt(m.group(2));
    if (!isInBounds(x, y)) {
      throw new IndexOutOfBoundsException("Position " + position + " is outside of the Array bounds");
    }
  }

  private boolean isInBounds(int x, int y) {
    return x > 0 && x <= getArrayModel().getColumns() && y > 0 && y <= getArrayModel().getRows();
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    ArrayChangeLog change = new ArrayChangeLog();
    change.setArray(this);
    change.setSummary(summary);
    change.setColumnsChanged(columnsChanged);
    change.setUser(user);
    return change;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Array";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

}
