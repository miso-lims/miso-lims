package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Submission")
public class Submission implements Comparable<Submission>, Deletable, Serializable {

  private static final long UNSAVED_ID = 0L;

  private static final long serialVersionUID = 1L;

  private String accession;
  private String alias;
  private boolean completed;
  private LocalDate creationDate;
  private String description;

  @ManyToMany(targetEntity = Experiment.class)
  @JoinTable(name = "Submission_Experiment", joinColumns = {
      @JoinColumn(name = "submission_submissionId")},
      inverseJoinColumns = {
          @JoinColumn(name = "experiments_experimentId")})
  private Set<Experiment> experiments = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long submissionId = UNSAVED_ID;

  private LocalDate submittedDate;

  private String title;
  private boolean verified;

  @Override
  public int compareTo(Submission t) {
    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  public String getAccession() {
    return accession;
  }

  public String getAlias() {
    return alias;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public String getDescription() {
    return description;
  }

  public Set<Experiment> getExperiments() {
    return experiments;
  }

  @Override
  public long getId() {
    return submissionId;
  }

  public LocalDate getSubmissionDate() {
    return submittedDate;
  }

  public String getTitle() {
    return title;
  }

  public boolean isCompleted() {
    return completed;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public void setExperiments(Set<Experiment> experiments) {
    this.experiments = experiments;
  }

  @Override
  public void setId(long id) {
    this.submissionId = id;
  }

  public void setSubmissionDate(LocalDate submissionDate) {
    this.submittedDate = submissionDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Submission";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }
}
