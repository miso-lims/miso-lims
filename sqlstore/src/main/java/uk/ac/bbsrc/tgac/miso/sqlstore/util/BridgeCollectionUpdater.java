package uk.ac.bbsrc.tgac.miso.sqlstore.util;

import java.io.IOException;
import java.util.Collections;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Maintains a bridge table in SQLStore.
 * 
 * This clears the table and repopulates it from a collection.
 */
public abstract class BridgeCollectionUpdater<T> {
  private final String tableName;
  private final String parentColumn;
  private final String childColumn;
  private final String positionColumn;

  public BridgeCollectionUpdater(String tableName, String parentColumn, String childColumn) {
    this(tableName, parentColumn, childColumn, null);
  }

  public BridgeCollectionUpdater(String tableName, String parentColumn, String childColumn, String positionColumn) {
    super();
    this.tableName = tableName;
    this.parentColumn = parentColumn;
    this.childColumn = childColumn;
    this.positionColumn = positionColumn;
  }

  protected abstract Object getId(T item);

  protected Object getPosition(T item) {
    throw new UnsupportedOperationException();
  }

  public final void saveAll(JdbcTemplate template, long targetId, Iterable<? extends T> collection) throws IOException {
    template.update("DELETE FROM " + tableName + " WHERE " + parentColumn + " = ?", targetId);
    SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(tableName);

    if (collection == null) {
      return;
    }

    for (T item : collection) {
      if (item == null) continue;
      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue(parentColumn, targetId);
      Object id = getId(item);
      if (id == null) continue;
      params.addValue(childColumn, id);
      if (positionColumn != null) {
        params.addValue(positionColumn, getPosition(item));
      }
      insert.execute(params);
    }
  }

  public final void saveOne(JdbcTemplate template, long targetId, T item) throws IOException {
    saveAll(template, targetId, Collections.singleton(item));
  }
}
