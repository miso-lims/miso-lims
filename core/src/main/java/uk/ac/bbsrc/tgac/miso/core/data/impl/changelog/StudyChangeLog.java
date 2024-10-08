package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;

@Entity
@Table(name = "StudyChangeLog", indexes = {
    @Index(name = "StudyChangeLog_studyId_changeTime", columnList = "studyId, changeTime")})
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
