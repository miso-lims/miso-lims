package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.sql.JoinType;

public class AliasDescriptor {

  private final String associationPath;

  private final String alias;

  private final JoinType joinType;

  private static String getLastPart(String associationPath) {
    String[] parts = associationPath.split("\\.");
    return parts[parts.length - 1];
  }

  public AliasDescriptor(String associationPath) {
    this(associationPath, JoinType.INNER_JOIN);
  }

  public AliasDescriptor(String associationPath, JoinType joinType) {
    this.associationPath = associationPath;
    this.alias = getLastPart(associationPath);
    this.joinType = joinType;
  }

  public AliasDescriptor(String associationPath, String alias) {
    this(associationPath, alias, JoinType.INNER_JOIN);
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
