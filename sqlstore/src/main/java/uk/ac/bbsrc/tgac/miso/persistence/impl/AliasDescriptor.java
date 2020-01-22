package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.sql.JoinType;

public class AliasDescriptor {

  private final String associationPath;

  private final String alias;

  private final JoinType joinType;

  public AliasDescriptor(String alias) {
    this.associationPath = alias;
    this.alias = alias;
    this.joinType = JoinType.INNER_JOIN;
  }

  public AliasDescriptor(String alias, JoinType joinType) {
    this.associationPath = alias;
    this.alias = alias;
    this.joinType = joinType;
  }

  public AliasDescriptor(String associationPath, String alias) {
    this.associationPath = associationPath;
    this.alias = alias;
    this.joinType = JoinType.INNER_JOIN;
  }

  public AliasDescriptor(String associationPath, String alias, JoinType joinType) {
    this.associationPath = associationPath;
    this.alias = alias;
    this.joinType = joinType;
  }

  public String getAssociationPath() {
    return associationPath;
  }

  public String getAlias() {
    return alias;
  }

  public JoinType getJoinType() {
    return joinType;
  }

}
