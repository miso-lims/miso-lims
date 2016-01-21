package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

/**
 * A user defined hibernate type to store an Enum attribute in the database. Normally the ordinal (bad) or the string name is stored. If we
 * upgrade the versions of Hibernate and Spring we can accomplish the same thing with the @Convert and @Converter annotations.
 *
 */
public class ProgressTypeUserType implements UserType {

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    return x.equals(y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    assert (x != null);
    return x.hashCode();
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Class returnedClass() {
    return ProgressType.class;
  }

  @Override
  public int[] sqlTypes() {
    return new int[] { Types.VARCHAR };
  }

  @Override
  public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor arg2, Object arg3)
      throws HibernateException, SQLException {
    final String value = resultSet.getString(names[0]);
    return ProgressType.get(value); // Return Enum associated with Enum's 'key' value.
  }

  @Override
  public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session)
      throws HibernateException, SQLException {
    statement.setString(index, ((ProgressType) value).getKey()); // Store Enum's 'key' value (a String) in the database.
  }

}
