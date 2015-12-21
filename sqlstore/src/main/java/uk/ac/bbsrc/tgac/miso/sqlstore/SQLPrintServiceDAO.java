/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.CustomPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.DefaultPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;
import uk.ac.bbsrc.tgac.miso.core.store.PrintServiceStore;
import uk.ac.bbsrc.tgac.miso.core.util.PrintServiceUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 16-Apr-2012
 * @since 0.1.6
 */
public class SQLPrintServiceDAO implements PrintServiceStore {
  private static final String TABLE_NAME = "PrintService";

  public static final String PRINT_SERVICE_SELECT = "SELECT serviceId, serviceName, contextName, contextFields, enabled, printServiceFor, printSchema "
      + "FROM " + TABLE_NAME;

  public static final String PRINT_SERVICE_SELECT_BY_SERVICE_ID = PRINT_SERVICE_SELECT + " WHERE serviceId = ?";

  public static final String PRINT_SERVICE_SELECT_BY_SERVICE_NAME = PRINT_SERVICE_SELECT + " WHERE serviceName = ?";

  public static final String PRINT_SERVICES_SELECT_BY_CONTEXT_NAME = PRINT_SERVICE_SELECT + " WHERE contextName = ?";

  public static final String PRINT_SERVICES_SELECT_BY_CLASS = PRINT_SERVICE_SELECT + " WHERE printServiceFor = ?";

  public static final String PRINT_SERVICE_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET contextName=:contextName, contextFields=:contextFields, enabled=:enabled, printServiceFor=:printServiceFor, printSchema=:printSchema "
      + "WHERE serviceName=:serviceName";

  protected static final Logger log = LoggerFactory.getLogger(SQLPrintServiceDAO.class);
  private JdbcTemplate template;

  @Autowired
  private PrintManager<MisoPrintService, ?> printManager;

  public void setPrintManager(PrintManager<MisoPrintService, ?> printManager) {
    this.printManager = printManager;
  }

  @Autowired
  private MisoFilesManager misoFilesManager;

  public void setMisoFilesManager(MisoFilesManager misoFilesManager) {
    this.misoFilesManager = misoFilesManager;
  }

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public long save(MisoPrintService printService) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("serviceName", printService.getName());
    params.addValue("contextName", printService.getPrintContext().getName());
    params.addValue("enabled", printService.isEnabled());
    params.addValue("printServiceFor", printService.getPrintServiceFor().getName());
    params.addValue("printSchema", printService.getBarcodableSchema().getName());
    try {
      JSONObject contextFields = PrintServiceUtils.mapContextFieldsToJSON(printService.getPrintContext());
      String contextFieldJSON = contextFields.toString();
      params.addValue("contextFields", contextFieldJSON);
    } catch (IllegalAccessException e) {
      log.error("save print service", e);
    }

    if (printService.getServiceId() == -1) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("serviceId");
      Number newId = insert.executeAndReturnKey(params);
      printService.setServiceId(newId.longValue());
    } else {
      params.addValue("serviceId", printService.getServiceId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(PRINT_SERVICE_UPDATE, params);
    }
    return printService.getServiceId();
  }

  @Override
  public MisoPrintService get(long serviceId) throws IOException {
    List eResults = template.query(PRINT_SERVICE_SELECT_BY_SERVICE_ID, new Object[] { serviceId }, new MisoPrintServiceMapper());
    MisoPrintService e = eResults.size() > 0 ? (MisoPrintService) eResults.get(0) : null;
    return e;
  }

  @Override
  public MisoPrintService lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public MisoPrintService getByName(String serviceName) throws IOException {
    List eResults = template.query(PRINT_SERVICE_SELECT_BY_SERVICE_NAME, new Object[] { serviceName }, new MisoPrintServiceMapper());
    MisoPrintService e = eResults.size() > 0 ? (MisoPrintService) eResults.get(0) : null;
    return e;
  }

  @Override
  public Collection<MisoPrintService> listAll() throws IOException {
    return template.query(PRINT_SERVICE_SELECT, new MisoPrintServiceMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<MisoPrintService> listByContext(String contextName) throws IOException {
    return template.query(PRINT_SERVICES_SELECT_BY_CONTEXT_NAME, new Object[] { contextName }, new MisoPrintServiceMapper());
  }

  public class MisoPrintServiceMapper implements RowMapper<MisoPrintService> {
    @Override
    public MisoPrintService mapRow(ResultSet rs, int rowNum) throws SQLException {
      try {
        MisoPrintService printService;

        PrintContext pc = printManager.getPrintContext(rs.getString("contextName"));
        BarcodableSchema barcodableSchema = printManager.getBarcodableSchema(rs.getString("printSchema"));
        if (barcodableSchema != null) {
          barcodableSchema.getBarcodeLabelFactory().setFilesManager(misoFilesManager);
          barcodableSchema.getBarcodeLabelFactory().setSecurityManager(securityManager);
        }

        if ("net.sf.json.JSONObject".equals(rs.getString("printServiceFor"))) {
          printService = new CustomPrintService();
          printService.setBarcodableSchema(barcodableSchema);

          printService.setServiceId(rs.getLong("serviceId"));
          printService.setName(rs.getString("serviceName"));
          printService.setEnabled(rs.getBoolean("enabled"));
          printService.setPrintServiceFor(JSONObject.class);

          JSONObject contextFields = JSONObject.fromObject(rs.getString("contextFields"));
          PrintServiceUtils.mapJSONToContextFields(contextFields, pc);
        } else {
          printService = new DefaultPrintService();
          printService.setBarcodableSchema(barcodableSchema);

          printService.setServiceId(rs.getLong("serviceId"));
          printService.setName(rs.getString("serviceName"));
          printService.setEnabled(rs.getBoolean("enabled"));
          printService.setPrintServiceFor(Class.forName(rs.getString("printServiceFor")).asSubclass(Barcodable.class));

          JSONObject contextFields = JSONObject.fromObject(rs.getString("contextFields"));
          PrintServiceUtils.mapJSONToContextFields(contextFields, pc);
        }

        printService.setPrintContext(pc);

        return printService;
      } catch (ClassNotFoundException e) {
        log.error("print service row mapper", e);
      } catch (IllegalAccessException e) {
        log.error("print service row mapper", e);
      } catch (JSONException e) {
        log.error("print service row mapper", e);
      } catch (IOException e) {
        log.error("print service row mapper", e);
      }
      return null;
    }
  }
}
