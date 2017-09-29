package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;

@Entity
@Table(appliesTo = "StudyChangeLog", indexes = {
    @Index(name = "StudyChangeLog_studyId_changeTime", columnNames = { "studyId", "changeTime" }) })
public class StudyChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long studyChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = StudyImpl.class)
  @JoinColumn(name = "studyId", nullable = false, updatable = false)
  private Study study;

  @Override
  public Long getId() {
    return study.getId();
  }

  @Override
  public void setId(Long id) {
    study.setId(id);
  }

  public Long getStudyChangeLogId() {
    return studyChangeLogId;
  }

  public Study getStudy() {
    return study;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

}
