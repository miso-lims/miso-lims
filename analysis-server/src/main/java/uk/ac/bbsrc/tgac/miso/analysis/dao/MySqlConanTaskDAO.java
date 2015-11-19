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

package uk.ac.bbsrc.tgac.miso.analysis.dao;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import uk.ac.bbsrc.tgac.miso.analysis.parameter.Optionable;
import uk.ac.bbsrc.tgac.miso.analysis.parameter.Transientable;
import uk.ac.ebi.fgpt.conan.dao.DatabaseConanTaskDAO;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;
import uk.ac.ebi.fgpt.conan.model.ConanPipeline;
import uk.ac.ebi.fgpt.conan.model.ConanProcessRun;
import uk.ac.ebi.fgpt.conan.model.ConanTask;


/**
 * uk.ac.bbsrc.tgac.miso.analysis
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 07/11/11
 * @since 0.1.3
 */
public class MySqlConanTaskDAO extends DatabaseConanTaskDAO {
  protected static final Logger log = LoggerFactory.getLogger(MySqlConanTaskDAO.class);

  private long getAutoIncrement(String tableName) throws IOException {
    final String q = "SHOW TABLE STATUS LIKE '" + tableName + "'";
    Map<String, Object> rs = getJdbcTemplate().queryForMap(q);
    Object ai = rs.get("Auto_increment");
    if (ai != null) {
      return new Long(ai.toString());
    } else {
      throw new IOException("Cannot resolve Auto_increment value from DBMS metadata tables");
    }
  }

  private Timestamp javaDateToSQLDate(Date date) {
    if (date == null) {
      return null;
    }
    return new Timestamp(date.getTime());
  }

  @Override
  public <P extends ConanPipeline> ConanTask<P> saveTask(ConanTask<P> conanTask) {
    Assert.notNull(getJdbcTemplate(), getClass().getSimpleName() + " must have a valid JdbcTemplate set");
    if (conanTask.getSubmitter().getId() == null) {
      getUserDAO().saveUser(conanTask.getSubmitter());
    }

    int currentExecutedIndex = conanTask.getPipeline().getProcesses().indexOf(conanTask.getLastProcess());
    if (currentExecutedIndex == -1) {
      currentExecutedIndex = 0;
    }
    int firstExecutedIndex = conanTask.getPipeline().getProcesses().indexOf(conanTask.getFirstProcess());
    if (firstExecutedIndex == -1) {
      firstExecutedIndex = 0;
    }
    try {
      if (conanTask.getId() == null) {
        int taskID = (int) getAutoIncrement("CONAN_TASKS");
        getJdbcTemplate().update(TASK_INSERT, taskID, conanTask.getName(), javaDateToSQLDate(conanTask.getStartDate()),
            javaDateToSQLDate(conanTask.getCompletionDate()), conanTask.getSubmitter().getId(), conanTask.getPipeline().getName(),
            conanTask.getPriority().toString(), firstExecutedIndex, conanTask.getCurrentState().toString(), conanTask.getStatusMessage(),
            currentExecutedIndex, javaDateToSQLDate(conanTask.getCreationDate()));
        conanTask.setId(Integer.toString(taskID));
        // save parameters
        Map<ConanParameter, String> params = conanTask.getParameterValues();
        for (ConanParameter conanParameter : params.keySet()) {
          // don't save transient (non-required, non-persistent, e.g. temporary) parameters
          if (!(conanParameter instanceof Transientable) || !((Transientable) conanParameter).isTransient()) {
            if (!(conanParameter instanceof Optionable)
                || ((Optionable) conanParameter).isOptional() && params.get(conanParameter) != null) {
              getLog().info("Adding parameter to DB: " + conanParameter.getName());
              getJdbcTemplate().update(PARAMETER_INSERT, conanParameter.getName(), params.get(conanParameter), taskID);
            }
          }
        }
      } else {
        getJdbcTemplate().update(TASK_UPDATE, conanTask.getName(), javaDateToSQLDate(conanTask.getStartDate()),
            javaDateToSQLDate(conanTask.getCompletionDate()), conanTask.getSubmitter().getId(), conanTask.getPipeline().getName(),
            conanTask.getPriority().toString(), firstExecutedIndex, conanTask.getCurrentState().toString(), conanTask.getStatusMessage(),
            currentExecutedIndex, javaDateToSQLDate(conanTask.getCreationDate()), conanTask.getId());
        // delete and save parameters
        getJdbcTemplate().update(PARAMETER_DELETE, conanTask.getId());
        Map<ConanParameter, String> params = conanTask.getParameterValues();
        for (ConanParameter conanParameter : params.keySet()) {
          // don't save transient (non-required, non-persistent, e.g. temporary) parameters
          if (!(conanParameter instanceof Transientable) || !((Transientable) conanParameter).isTransient()) {
            if (!(conanParameter instanceof Optionable)
                || ((Optionable) conanParameter).isOptional() && params.get(conanParameter) != null) {
              getLog().info("Adding parameter to DB: " + conanParameter.getName());
              getJdbcTemplate().update(PARAMETER_INSERT, conanParameter.getName(), params.get(conanParameter), conanTask.getId());
            }
          }
        }
      }
    } catch (IOException e) {
      log.error("save task", e);
    }
    return conanTask;
  }

  @Override
  public <P extends ConanPipeline> ConanTask<P> saveProcessRun(String conanTaskID, ConanProcessRun conanProcessRun)
      throws IllegalArgumentException {
    try {
      Assert.notNull(getJdbcTemplate(), getClass().getSimpleName() + " must have a valid JdbcTemplate set");

      ConanTask taskDB = getTask(conanTaskID);
      if (conanProcessRun.getUser().getId() == null) {
        getUserDAO().saveUser(conanProcessRun.getUser());
      }

      if (conanProcessRun.getId() == null) {
        int processRunID = (int) getAutoIncrement("CONAN_PROCESSES");
        getJdbcTemplate().update(PROCESS_INSERT, processRunID, conanProcessRun.getProcessName(),
            javaDateToSQLDate(conanProcessRun.getStartDate()), javaDateToSQLDate(conanProcessRun.getEndDate()),
            conanProcessRun.getUser().getId(), conanProcessRun.getExitValue(), conanTaskID);

        conanProcessRun.setId(Integer.toString(processRunID));
      }
    } catch (IOException e) {
      log.error("save process run", e);
    }
    return super.saveProcessRun(conanTaskID, conanProcessRun);
  }
}
