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
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractPrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MisoPrintJob;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.store.PrintJobStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 01-Jul-2011
 * @since 0.0.3
 */
public class SQLPrintJobDAO implements PrintJobStore {
  private static final String TABLE_NAME = "PrintJob";

  public static final String PRINT_JOB_SELECT = "SELECT jobId, printServiceName, printDate, jobCreator_userId, printedElements, status "
      + "FROM " + TABLE_NAME;

  public static final String PRINT_JOB_SELECT_BY_ID = PRINT_JOB_SELECT + " WHERE jobId = ?";

  public static final String PRINT_JOB_SELECT_BY_SERVICE_NAME = PRINT_JOB_SELECT + " WHERE printServiceName = ?";

  public static final String PRINT_JOB_SELECT_BY_USER = PRINT_JOB_SELECT + " WHERE jobCreator_userId = ?";

  public static final String PRINT_JOB_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET printServiceName=:printServiceName, printDate=:printDate, jobCreator_userId=:jobCreator_userId, printedElements=:printedElements, status=:status "
      + "WHERE jobId=:jobId";

  protected static final Logger log = LoggerFactory.getLogger(SQLPrintJobDAO.class);
  private JdbcTemplate template;

  @Autowired
  private PrintManager<MisoPrintService, ?> printManager;
  private SecurityManager securityManager;

  public void setPrintManager(PrintManager printManager) {
    this.printManager = printManager;
  }

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
  public long save(PrintJob printJob) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("printServiceName", printJob.getPrintService().getName());
    params.addValue("printDate", printJob.getPrintDate());
    params.addValue("jobCreator_userId", printJob.getPrintUser().getUserId());
    params.addValue("status", printJob.getStatus());

    Blob barcodeBlob = null;
    try {
      if (printJob.getQueuedElements() != null) {
        byte[] rbytes = LimsUtils.objectToByteArray(printJob.getQueuedElements());
        barcodeBlob = new SerialBlob(rbytes);
        params.addValue("printedElements", barcodeBlob);
      } else {
        params.addValue("printedElements", null);
      }
    } catch (SerialException e) {
      log.error("print job save", e);
    } catch (SQLException e) {
      log.error("print job save", e);
    }

    if (printJob.getJobId() == AbstractPrintJob.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("jobId");
      Number newId = insert.executeAndReturnKey(params);
      printJob.setJobId(newId.longValue());
    } else {
      params.addValue("jobId", printJob.getJobId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(PRINT_JOB_UPDATE, params);
    }
    return printJob.getJobId();
  }

  @Override
  public PrintJob get(long jobId) throws IOException {
    List eResults = template.query(PRINT_JOB_SELECT_BY_ID, new Object[] { jobId }, new PrintJobMapper());
    PrintJob e = eResults.size() > 0 ? (PrintJob) eResults.get(0) : null;
    return e;
  }

  @Override
  public PrintJob lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<PrintJob> listAll() throws IOException {
    return template.query(PRINT_JOB_SELECT, new PrintJobMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<PrintJob> listByUser(User user) throws IOException {
    return template.query(PRINT_JOB_SELECT_BY_USER, new Object[] { user.getUserId() }, new PrintJobMapper());
  }

  @Override
  public List<PrintJob> listByPrintService(MisoPrintService service) throws IOException {
    return template.query(PRINT_JOB_SELECT_BY_SERVICE_NAME, new Object[] { service.getName() }, new PrintJobMapper());
  }

  public class PrintJobMapper implements RowMapper<PrintJob> {
    @Override
    public PrintJob mapRow(ResultSet rs, int rowNum) throws SQLException {
      try {
        MisoPrintJob printJob = new MisoPrintJob();
        printJob.setJobId(rs.getLong("jobId"));
        printJob.setPrintDate(rs.getDate("printDate"));
        printJob.setPrintService(printManager.getPrintService(rs.getString("printServiceName")));
        printJob.setPrintUser(securityManager.getUserById(rs.getLong("jobCreator_userId")));
        Blob barcodeBlob = rs.getBlob("printedElements");
        if (barcodeBlob != null) {
          if (barcodeBlob.length() > 0) {
            byte[] rbytes = barcodeBlob.getBytes(1, (int) barcodeBlob.length());
            printJob.setQueuedElements((Queue<?>) LimsUtils.byteArrayToObject(rbytes));
          }
        }
        printJob.setStatus(rs.getString("status"));
        return printJob;
      } catch (IOException e) {
        log.error("print job row mapper", e);
      } catch (ClassNotFoundException e) {
        log.error("print job row mapper", e);
      }
      return null;
    }
  }
}
