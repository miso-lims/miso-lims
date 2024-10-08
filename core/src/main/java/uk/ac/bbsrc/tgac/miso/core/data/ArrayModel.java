package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ArrayModel implements Serializable, Aliasable, Deletable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @Column(name = "arrayModelId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id = UNSAVED_ID;
  private String alias;

  @Column(name = "arrayModelRows")
  private int rows;

  @Column(name = "arrayModelColumns")
  private int columns;

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

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getColumns() {
    return columns;
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Array Model";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

}
