package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import net.sf.ehcache.CacheManager;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

public class SQLBoxDAO implements BoxStore {
  public class BoxMapper extends CacheAwareRowMapper<Box> {
    public BoxMapper() {
      super(Box.class);
    }

    public BoxMapper(boolean lazy) {
      super(Box.class, lazy);
    }

    @Override
    public Box mapRow(ResultSet rs, int rowNum) throws SQLException {
      final Box box = dataObjectFactory.getBox();
      box.setId(rs.getLong("boxId"));
      box.setName(rs.getString("name"));
      box.setAlias(rs.getString("alias"));
      box.setDescription(rs.getString("description"));
      box.setIdentificationBarcode(rs.getString("identificationBarcode"));
      box.setLocationBarcode(rs.getString("locationBarcode"));

      try {
        box.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        box.setUse(getUseById(rs.getLong("boxUseId")));
        box.setSize(getSizeById(rs.getLong("boxSizeId")));
        box.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        box.getBoxables().clear();
        template.query(BOX_POSITIONS_SELECT, new RowCallbackHandler() {

          @Override
          public void processRow(ResultSet inner_rs) throws SQLException {
            long positionId = inner_rs.getLong("boxPositionId");
            int row = inner_rs.getInt("row");
            int column = inner_rs.getInt("column");
            Boxable item = libraryDAO.getByPositionId(positionId);
            if (item == null) {
              item = sampleDAO.getByPositionId(positionId);
            }
            if (item == null) {
              item = poolDAO.getByPositionId(positionId);
            }
            box.getBoxables().put(BoxUtils.getPositionString(row, column), item);
          }

        }, box.getId());
        box.getChangeLog().addAll(changeLogDAO.listAllById(TABLE_NAME, rs.getLong("boxId")));
      } catch (IOException e) {
        e.printStackTrace();
      }
      return box;
    }
  }

  public class BoxSizeMapper extends CacheAwareRowMapper<BoxSize> {

    public BoxSizeMapper() {
      super(BoxSize.class);
    }

    @Override
    public BoxSize mapRow(ResultSet rs, int rownum) throws SQLException {
      BoxSize size = new BoxSize();
      size.setId(rs.getLong("boxSizeId"));
      size.setRows(rs.getInt("rows"));
      size.setColumns(rs.getInt("columns"));
      size.setScannable(rs.getBoolean("scannable"));
      return size;
    }

  }

  public class BoxUseMapper extends CacheAwareRowMapper<BoxUse> {

    public BoxUseMapper() {
      super(BoxUse.class);
    }

    @Override
    public BoxUse mapRow(ResultSet rs, int rownum) throws SQLException {
      BoxUse use = new BoxUse();
      use.setId(rs.getLong("boxUseId"));
      use.setAlias(rs.getString("alias"));
      return use;
    }

  }

  public static final String BOX_POSITIONS_SELECT = "SELECT boxPositionId, `column`, `row` FROM BoxPosition WHERE boxId = ?";

  private static String TABLE_NAME = "Box";

  public static final String BOX_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE boxId=:boxId";

  public static final String BOX_SELECT = "SELECT boxId, boxUseId, boxSizeId, name, alias, description, identificationBarcode, locationBarcode, securityProfile_profileId, lastModifier FROM "
      + TABLE_NAME;

  public static final String BOX_SELECT_BY_ALIAS = BOX_SELECT + " WHERE alias = ?";

  public static final String BOX_SELECT_BY_ID = BOX_SELECT + " WHERE boxId = ?";

  public static final String BOX_SELECT_BY_ID_BARCODE = BOX_SELECT + " WHERE identificationBarcode = ?";

  public static final String BOX_SELECT_BY_SEARCH = BOX_SELECT
      + " WHERE identificationBarcode LIKE ? OR name LIKE ? OR alias LIKE ? OR description LIKE ?";

  public static final String BOX_SELECT_LIMIT = BOX_SELECT + " ORDER BY boxId DESC LIMIT ?";

  public static final String BOX_USE_SELECT = "SELECT boxUseId, alias FROM BoxUse";

  public static final String BOX_USE_ALIAS_SELECT = "SELECT alias " + "FROM BoxUse";

  public static final String BOX_USE_SELECT_BY_ID = BOX_USE_SELECT + " WHERE boxUseId = ?";

  public static final String BOX_SIZE_SELECT = "SELECT boxSizeId, rows, columns, scannable FROM BoxSize";

  public static final String BOX_SIZE_SELECT_BY_ID = BOX_SIZE_SELECT + " WHERE boxSizeId = ?";

  public static final String BOX_UPDATE = "UPDATE " + TABLE_NAME
      + " SET boxUseId=:boxUseId, boxSizeId=:boxSizeId, name=:name, alias=:alias, description=:description, securityProfile_profileId=:securityProfile_profileId, "
      + "identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode, lastModifier=:lastModifier "
      + "WHERE boxId=:boxId";

  protected static final Logger log = LoggerFactory.getLogger(SQLBoxDAO.class);

  private static final String BOX_DELETE_CONTENTS = "DELETE FROM BoxPosition WHERE boxId = ?";

  private static final String BOX_INSERT_CONTENTS = "REPLACE INTO BoxPosition (boxId, `row`, `column`, boxPositionId) VALUES (?, ?, ?, ?)";

  @Autowired
  private DataObjectFactory dataObjectFactory;
  @Autowired
  private MisoNamingScheme<Box> namingScheme;
  private Store<SecurityProfile> securityProfileDAO;
  private SecurityStore securityDAO;
  private SampleStore sampleDAO;
  private LibraryStore libraryDAO;
  private PoolStore poolDAO;
  private JdbcTemplate template;
  private boolean autoGenerateIdentificationBarcodes;
  private ChangeLogStore changeLogDAO;

  @Autowired
  private CacheManager cacheManager;

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public Box get(long boxId) throws IOException {
    List<Box> results = template.query(BOX_SELECT_BY_ID, new Object[] { boxId }, new BoxMapper());
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    List<Box> results = template.query(BOX_SELECT_BY_ALIAS, new Object[] { alias }, new BoxMapper(true));
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public Box getByBarcode(String barcode) throws IOException {
    List<Box> results = template.query(BOX_SELECT_BY_ID_BARCODE, new Object[] { barcode }, new BoxMapper(true));
    return results.size() > 0 ? results.get(0) : null;
  }

  public void setAutoGenerateIdentificationBarcodes(boolean autoGenerateIdentificationBarcodes) {
    this.autoGenerateIdentificationBarcodes = autoGenerateIdentificationBarcodes;
  }

  public boolean getAutoGenerateIdentificationBarcodes() {
    return autoGenerateIdentificationBarcodes;
  }

  public void autoGenerateIdBarcode(Box box) {
    String barcode = box.getName() + "::" + box.getAlias();
    box.setIdentificationBarcode(barcode);
  } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user

  public ChangeLogStore getChangeLogDAO() {
    return changeLogDAO;
  }

  public DataObjectFactory getDataObjectFactory() {
    return dataObjectFactory;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public LibraryStore getLibraryDAO() {
    return libraryDAO;
  }

  public PoolStore getPoolDAO() {
    return poolDAO;
  }

  public SampleStore getSampleDAO() {
    return sampleDAO;
  }

  public SecurityStore getSecurityDAO() {
    return securityDAO;
  }

  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  @Override
  public MisoNamingScheme<Box> getNamingScheme() {
    return namingScheme;
  }

  public void setChangeLogDAO(ChangeLogStore changeLogDAO) {
    this.changeLogDAO = changeLogDAO;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
  }

  public void setPoolDAO(PoolStore poolDAO) {
    this.poolDAO = poolDAO;
  }

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  public void setSecurityDAO(SecurityStore securityDAO) {
    this.securityDAO = securityDAO;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Box> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Override
  public BoxSize getSizeById(long id) throws IOException {
    List<BoxSize> results = template.query(BOX_SIZE_SELECT_BY_ID, new Object[] { id }, new BoxSizeMapper());
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public BoxUse getUseById(long id) throws IOException {
    List<BoxUse> results = template.query(BOX_USE_SELECT_BY_ID, new Object[] { id }, new BoxUseMapper());
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public Box lazyGet(long boxId) throws IOException {
    List<Box> results = template.query(BOX_SELECT_BY_ID, new Object[] { boxId }, new BoxMapper(true));
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public Collection<Box> listAll() throws IOException {
    return template.query(BOX_SELECT, new BoxMapper());
  }

  @Override
  public Collection<BoxSize> listAllBoxSizes() throws IOException {
    return template.query(BOX_SIZE_SELECT, new Object[0], new BoxSizeMapper());
  }

  @Override
  public Collection<BoxUse> listAllBoxUses() throws IOException {
    return template.query(BOX_USE_SELECT, new Object[0], new BoxUseMapper());
  }

  @Override
  public List<String> listAllBoxUsesStrings() throws IOException {
    return template.queryForList(BOX_USE_ALIAS_SELECT, String.class);
  }

  @Override
  public Collection<Box> listByAlias(String alias) throws IOException {
    return template.query(BOX_SELECT_BY_ALIAS, new Object[] { alias }, new BoxMapper(true));
  }

  @Override
  public Collection<Box> listBySearch(String query) throws IOException {
    String squery = "%" + query + "%";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("search", squery);
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(BOX_SELECT_BY_SEARCH, params, new BoxMapper(true));
  }

  @Override
  public Collection<Box> listWithLimit(long limit) throws IOException {
    return template.query(BOX_SELECT_LIMIT, new Object[] { limit }, new BoxMapper(true));
  }

  @Override
  public boolean remove(Box box) throws IOException {
    template.update(BOX_DELETE_CONTENTS, box.getId());
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.update(BOX_DELETE, new MapSqlParameterSource().addValue("boxId", box.getId())) == 1;
  }

  @Override
  public void emptySingleTube(Box box, String position) throws IOException {
    String barcode = box.getBoxable(position).getIdentificationBarcode();
    Sample sample = sampleDAO.getByBarcode(barcode);
    Library library = libraryDAO.getByBarcode(barcode);
    Pool<? extends Poolable> pool = poolDAO.getByBarcode(barcode);

    if ((sample == null ? 0 : 1) + (library == null ? 0 : 1) + (pool == null ? 0 : 1) > 1) {
      String errorMessage = "";
      if (sample != null && library != null)
        errorMessage = "Duplicate barcodes found for both sample " + sample.getName() + " and library " + library.getName();
      if (sample != null && pool != null)
        errorMessage = "Duplicate barcodes found for both sample " + sample.getName() + " and pool " + pool.getName();
      if (library != null && pool != null)
        errorMessage = "Duplicate barcodes found for both library " + library.getName() + " and pool " + pool.getName();
      throw new DuplicateKeyException(errorMessage);
    } else if (sample == null && library == null && pool == null) {
      throw new IOException("Could not find a sample or library or pool with barcode " + barcode);
    } else {
      // save before emptying item to trigger cache update for item
      try {
        box.removeBoxable(position);
        save(box);
      } catch (IOException e) {
        log.debug("Error saving box", e);
        throw new IOException(e.getMessage());
      }

      if (library != null) {
        library.setEmpty(true);
        libraryDAO.save(library);
      } else if (sample != null) {
        sample.setEmpty(true);
        sampleDAO.save(sample);
      } else if (pool != null) {
        pool.setEmpty(true);
        poolDAO.save(pool);
      }
    }
  }

  @Override
  public void emptyAllTubes(Box box) throws IOException {
    List<String> boxableBarcodes = new ArrayList<String>();
    for (Boxable boxable : box.getBoxables().values()) {
      boxableBarcodes.add(boxable.getIdentificationBarcode());
    }
    try {
      box.removeAllBoxables();
      save(box);
    } catch (IOException e) {
      log.debug("Error emptying box", e);
      throw new IOException("Error emptying box: " + e.getMessage());
    }

    for (String barcode : boxableBarcodes) {
      Sample sample = sampleDAO.getByBarcode(barcode);
      if (sample != null) {
        sample.setEmpty(true);
        sampleDAO.save(sample);
        continue;
      }

      Library library = libraryDAO.getByBarcode(barcode);
      if (library != null) {
        library.setEmpty(true);
        libraryDAO.save(library);
        continue;
      }

      Pool<? extends Poolable> pool = poolDAO.getByBarcode(barcode);
      if (pool != null) {
        pool.setEmpty(true);
        poolDAO.save(pool);
        continue;
      }

      throw new IOException("Could not find a sample or library with barcode " + barcode);
    }
  }

  @Override
  public long save(Box box) throws IOException {
    Long securityProfileId = box.getSecurityProfile().getProfileId();
    if (securityProfileId == SecurityProfile.UNSAVED_ID) {
      securityProfileId = getSecurityProfileDAO().save(box.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("boxSizeId", box.getSize().getId());
    params.addValue("boxUseId", box.getUse().getId());
    params.addValue("alias", box.getAlias());
    params.addValue("description", box.getDescription());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("locationBarcode", box.getLocationBarcode());
    params.addValue("lastModifier", box.getLastModifier().getUserId());

    if (box.getId() == AbstractBox.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("boxId");
      try {
        box.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", box);
        box.setName(name);

        if (namingScheme.validateField("name", box.getName())) {
          params.addValue("name", name);

          if (autoGenerateIdentificationBarcodes) {
            autoGenerateIdBarcode(box);
          }
          params.addValue("identificationBarcode", box.getIdentificationBarcode());

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != box.getId()) {
            log.error("Expected Box ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(BOX_DELETE, new MapSqlParameterSource().addValue("boxId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Box ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save Box - invalid field:" + box.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Box - issue with naming scheme", e);
      }

    } else {
      try {
        if (namingScheme.validateField("name", box.getName())) {
          if (autoGenerateIdentificationBarcodes) {
            autoGenerateIdBarcode(box);
          } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user

          params.addValue("boxId", box.getId());
          params.addValue("name", box.getName());
          params.addValue("identificationBarcode", box.getIdentificationBarcode());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(BOX_UPDATE, params);
        } else {
          throw new IOException("Cannot save Box - invalid field:" + box.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Box - issue with naming scheme", e);
      }
    }

    final Map<Long, String> oldLocations = new HashMap<>();
    Map<Long, String> newLocations = new HashMap<>();
    template.query("SELECT * FROM BoxPosition WHERE boxId = ?", new Object[] { box.getId() }, new RowCallbackHandler() {

      @Override
      public void processRow(ResultSet rs) throws SQLException {
        oldLocations.put(rs.getLong("boxPositionId"), BoxUtils.getPositionString(rs.getInt("row"), rs.getInt("column")));
      }
    });

    template.update(BOX_DELETE_CONTENTS, box.getId());
    for (Entry<String, Boxable> entry : box.getBoxables().entrySet()) {
      if (entry.getValue() == null) continue;
      int row = BoxUtils.fromRowChar(entry.getKey().charAt(0));
      int column = Integer.parseInt(entry.getKey().substring(1)) - 1;
      template.update(BOX_INSERT_CONTENTS, box.getId(), row, column, entry.getValue().getBoxPositionId());
      newLocations.put(entry.getValue().getBoxPositionId(), entry.getKey());
    }

    Set<Long> commonIds = new HashSet<>(oldLocations.keySet());
    commonIds.retainAll(newLocations.keySet());

    Set<Long> addedIds = new HashSet<>(newLocations.keySet());
    addedIds.removeAll(commonIds);
    Set<Long> removedIds = new HashSet<>(oldLocations.keySet());
    removedIds.removeAll(commonIds);
    Set<Long> movedIds = new HashSet<>();
    for (Long id : commonIds) {
      if (!oldLocations.get(id).equals(newLocations.get(id))) {
        movedIds.add(id);
      }
    }

    if (!addedIds.isEmpty() || !removedIds.isEmpty() || !movedIds.isEmpty()) {
      String message = box.getLastModifier().getLoginName() + generateChangeLog("added", addedIds)
          + generateChangeLog("removed", removedIds) + generateChangeLog("moved", movedIds);
      template.update("INSERT INTO BoxChangeLog (boxId, columnsChanged, userId, message) VALUES (?, '', ?, ?)", box.getId(),
          box.getLastModifier().getUserId(), message);
    }
    for (Class<?> clazz : new Class<?>[]{Library.class, Pool.class, Sample.class}) {
      // remove all caching for Libraries, Pools and Samples to ensure correct position is displayed
      DbUtils.lookupCache(getCacheManager(), clazz, false).removeAll();
      DbUtils.lookupCache(getCacheManager(), clazz, true).removeAll();
    }

    return box.getId();
  }

  private String generateChangeLog(String verb, Set<Long> ids) {
    if (ids.isEmpty()) return "";

    StringBuilder query = new StringBuilder();
    query.append(
        "SELECT CONCAT(name, '::', alias) AS friendly FROM (SELECT boxPositionId, name, alias FROM Library UNION SELECT boxPositionId, name, alias FROM Pool UNION SELECT boxPositionId, name, alias FROM Sample) AS Boxable WHERE boxpositionId IN (");
    boolean first = true;
    for (long id : ids) {
      if (first) {
        first = false;
      } else {
        query.append(", ");
      }
      query.append(id);
    }
    query.append(") ORDER BY friendly");

    final StringBuilder message = new StringBuilder();
    message.append(" ");
    message.append(verb);
    message.append(":");

    template.query(query.toString(), new RowCallbackHandler() {

      @Override
      public void processRow(ResultSet rs) throws SQLException {
        message.append(" ");
        message.append(rs.getString("friendly"));
      }

    });

    return message.toString();
  }

  public CacheManager getCacheManager() {
    return cacheManager;
  }

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }
}
