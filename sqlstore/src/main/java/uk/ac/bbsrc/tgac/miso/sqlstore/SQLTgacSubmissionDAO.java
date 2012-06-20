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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubmissionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.*;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLTgacSubmissionDAO implements Store<SubmissionImpl> {

  public static final String SUBMISSION_SELECT =
          "SELECT submissionId, creationDate, submittedDate, name, alias, title, description, accession, verified, completed " +
          "FROM Submission";

  public static final String SUBMISSION_SELECT_BY_ID =
          SUBMISSION_SELECT + " WHERE submissionId = ?";

  public static final String SUBMISSION_UPDATE =
          "UPDATE Submission " +
          "SET creationDate=:creationDate, submittedDate=:submittedDate, name=:name, alias=:alias, title=:title, " +
          "description=:description, accession=:accession, verified=:verified, completed=:completed " +
          "WHERE submissionId=:submissionId";

  public static final String SUBMISSION_ELEMENTS_DELETE =
          "DELETE sexp, ssam, sstu, ssch, ssla FROM Submission s " +
          "LEFT JOIN Submission_Experiment AS sexp ON s.submissionId = sexp.submission_submissionId " +
          "LEFT JOIN Submission_Sample AS ssam ON s.submissionId = ssam.submission_submissionId " +
          "LEFT JOIN Submission_Study AS sstu ON s.submissionId = sstu.submission_submissionId " +
          "LEFT JOIN Submission_Partition AS ssla ON s.submissionId = ssla.submission_submissionId " +
          "WHERE s.submissionId=:submissionId";

  protected static final Logger log = LoggerFactory.getLogger(SQLTgacSubmissionDAO.class);

  private JdbcTemplate template;
  private ExperimentStore experimentDAO;
  private PartitionStore partitionDAO;
  private RunStore runDAO;
  private StudyStore studyDAO;
  private SampleStore sampleDAO;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setExperimentDAO(ExperimentStore experimentDAO) {
    this.experimentDAO = experimentDAO;
  }

  public void setRunDAO(RunStore runDAO) {
    this.runDAO = runDAO;
  }

  public void setStudyDAO(StudyStore studyDAO) {
    this.studyDAO = studyDAO;
  }

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  public void setPartitionDAO(PartitionStore partitionDAO) {
    this.partitionDAO = partitionDAO;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Transactional(readOnly = false, rollbackFor = IOException.class)
  public long save(SubmissionImpl submission) throws IOException {
    SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
            .withTableName("Submission");

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", submission.getAlias())
            .addValue("accession", submission.getAccession())
            .addValue("description", submission.getDescription())
            .addValue("title", submission.getTitle())
            .addValue("creationDate", submission.getCreationDate())
            .addValue("submittedDate", submission.getSubmissionDate())
            .addValue("verified", submission.isVerified())
            .addValue("completed", submission.isCompleted());

    //if a submission already exists then delete all the old rows first, and repopulate.
    //easier than trying to work out which rows need to be updated and which don't
    if(submission.getSubmissionId() != Submission.UNSAVED_ID) {
      MapSqlParameterSource delparams = new MapSqlParameterSource();
      delparams.addValue("submissionId", submission.getSubmissionId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      log.debug("Deleting Submission elements for " + submission.getSubmissionId());
      namedTemplate.update(SUBMISSION_ELEMENTS_DELETE, delparams);

      params.addValue("submissionId", submission.getSubmissionId())
              .addValue("name", submission.getName());

      namedTemplate.update(SUBMISSION_UPDATE, params);
    }
    else {
      insert.usingGeneratedKeyColumns("submissionId");
      String name = "SUB" + DbUtils.getAutoIncrement(template, "Submission");
      params.addValue("creationDate", new Date());
      params.addValue("name", name);
      Number newId = insert.executeAndReturnKey(params);
      submission.setSubmissionId(newId.longValue());
      submission.setName(name);
    }

    if (submission.getSubmissionElements() != null) {
      Collection<Submittable<Document>> docs = submission.getSubmissionElements();
      for (Submittable s : docs) {
        String tableName = "Submission_";
        String priKey = null;
        Long priValue = null;

        if (s instanceof Sample) {
          tableName += "Sample";
          priKey = "samples_sampleId";
          priValue = ((Sample)s).getSampleId();
        }
        else if (s instanceof Study) {
          tableName += "Study";
          priKey = "studies_studyId";
          priValue = ((Study)s).getStudyId();
        }
        else if (s instanceof Experiment) {
          tableName += "Experiment";
          priKey = "experiments_experimentId";
          priValue = ((Experiment)s).getExperimentId();
        }
        else if (s instanceof SequencerPoolPartition) {
          SequencerPoolPartition l = (SequencerPoolPartition)s;
          tableName += "Partition_Dilution";
          priKey = "partitions_partitionId";
          priValue = l.getId();

          if (l.getPool() != null) {
            Collection<Experiment> exps = l.getPool().getExperiments();
            for (Experiment experiment : exps) {
              SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template)
                  .withTableName("Submission_Experiment");
              try {
                MapSqlParameterSource poParams = new MapSqlParameterSource();
                poParams.addValue("submission_submissionId", submission.getSubmissionId())
                        .addValue("experiments_experimentId", experiment.getExperimentId());
                pInsert.execute(poParams);
              }
              catch (DuplicateKeyException dke) {
                log.warn("This Submission_Experiment combination already exists - not inserting: " + dke.getMessage());
              }

              Study study = experiment.getStudy();
              SimpleJdbcInsert sInsert = new SimpleJdbcInsert(template)
                  .withTableName("Submission_Study");
              try {
                MapSqlParameterSource poParams = new MapSqlParameterSource();
                poParams.addValue("submission_submissionId", submission.getSubmissionId())
                        .addValue("studies_studyId", study.getStudyId());
                sInsert.execute(poParams);
              }
              catch (DuplicateKeyException dke) {
                log.warn("This Submission_Study combination already exists - not inserting: " + dke.getMessage());
              }
            }

            Collection<? extends Dilution> dils = l.getPool().getDilutions();
            for (Dilution dil : dils) {
              Sample sample = dil.getLibrary().getSample();
              SimpleJdbcInsert sInsert = new SimpleJdbcInsert(template)
                  .withTableName("Submission_Sample");
              try {
                MapSqlParameterSource poParams = new MapSqlParameterSource();
                poParams.addValue("submission_submissionId", submission.getSubmissionId())
                        .addValue("samples_sampleId", sample.getSampleId());
                sInsert.execute(poParams);
              }
              catch (DuplicateKeyException dke) {
                log.warn("This Submission_Sample combination already exists - not inserting: " + dke.getMessage());
              }
            }
          }
        }

        if (priKey != null && priValue != null) {
          SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template)
                  .withTableName(tableName);
          try {
            MapSqlParameterSource poParams = new MapSqlParameterSource();
            poParams.addValue("submission_submissionId", submission.getSubmissionId())
                    .addValue(priKey, priValue);
            pInsert.execute(poParams);
          }
          catch (DuplicateKeyException dke) {
            log.warn("This "+tableName+" combination already exists - not inserting: " + dke.getMessage());
          }
        }
        else {
          throw new IOException("Null parameter key/value detected. Cannot insert.");
        }
      }
    }
    else {
      throw new IOException("No defined Submittable elements available");  
    }

    return submission.getSubmissionId();
  }

  public SubmissionImpl get(long id) throws IOException {
    List eResults = template.query(SUBMISSION_SELECT_BY_ID, new Object[]{id}, new TgacSubmissionMapper());
    SubmissionImpl e = eResults.size() > 0 ? (SubmissionImpl) eResults.get(0) : null;
    return e;
  }

  public Collection<SubmissionImpl> listAll() throws IOException {
    return template.query(SUBMISSION_SELECT, new TgacSubmissionMapper());
  }

  public class TgacSubmissionMapper implements RowMapper<SubmissionImpl> {
    public SubmissionImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
      SubmissionImpl t = (SubmissionImpl)dataObjectFactory.getSubmission();
      t.setSubmissionId(rs.getLong("submissionId"));
      t.setAccession(rs.getString("accession"));
      t.setAlias(rs.getString("alias"));
      t.setCreationDate(rs.getDate("creationDate"));
      t.setDescription(rs.getString("description"));
      t.setName(rs.getString("name"));
      t.setSubmissionDate(rs.getDate("submittedDate"));
      t.setTitle(rs.getString("title"));
      t.setVerified(rs.getBoolean("verified"));
      t.setCompleted(rs.getBoolean("completed"));

      try {
        //process submittables
        for (Study study : studyDAO.listBySubmissionId(rs.getLong("submissionId"))) {
          t.addSubmissionElement(study);
          log.debug(t.getName() + ": added " + study.getName());
        }

        for (Sample sample : sampleDAO.listBySubmissionId(rs.getLong("submissionId"))) {
          t.addSubmissionElement(sample);
          log.debug(t.getName() + ": added " + sample.getName());
        }

        for (Experiment experiment : experimentDAO.listBySubmissionId(rs.getLong("submissionId"))) {
          t.addSubmissionElement(experiment);
          log.debug(t.getName() + ": added " + experiment.getName());
        }

        for (SequencerPoolPartition partition : partitionDAO.listBySubmissionId(rs.getLong("submissionId"))) {
          List<Run> runs = new ArrayList<Run>(runDAO.listBySequencerPartitionContainerId(partition.getSequencerPartitionContainer().getContainerId()));
          if (runs.size() == 1) {
            partition.getSequencerPartitionContainer().setRun(runs.get(0));
          }
          t.addSubmissionElement(partition);
        }
      }
      catch (IOException ie) {
        log.warn("Cannot map submission: " + ie.getMessage());
        ie.printStackTrace();
      }

      return t;
    }
  }
}