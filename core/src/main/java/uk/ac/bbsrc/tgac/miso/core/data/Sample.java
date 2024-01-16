package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.SampleBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;

/**
 * A Sample contains information about the original material upon which a sequencing experiment is
 * to be based.
 * <p/>
 * Samples can be used in any number of sequencing {@link Experiment}s in the form of a
 * {@link Library} that is processed further into pooled {@link LibraryAliquot}s. Samples can be
 * described further by a scientific name which, when enabled, will be checked against the NCBI
 * Taxonomy database.
 * <p/>
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Sample extends Attachable, Comparable<Sample>, Deletable, HierarchyEntity, Locatable,
    QualityControllable<SampleQC>, Requisitionable {

  public static final String PLAIN_CATEGORY_NAME = "Plain";

  /**
   * Returns the accession of this Sample object.
   * 
   * @return String accession.
   */
  public String getAccession();

  /**
   * Sets the accession of this Sample object.
   * 
   * @param accession accession.
   */
  public void setAccession(String accession);

  /**
   * Sets the name of this Sample object.
   * 
   * @param name name.
   */
  public void setName(String name);

  /**
   * Returns the description of this Sample object.
   * 
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Sample object.
   * 
   * @param description description.
   */
  public void setDescription(String description);

  /**
   * Returns the scientificName of this Sample object. This should ideally match a taxon name of a
   * species in the NCBI Taxonomy database.
   * 
   * @return String scientificName.
   */
  public ScientificName getScientificName();

  /**
   * Sets the scientificName of this Sample object. This should ideally match a taxon name of a
   * species in the NCBI Taxonomy database.
   * 
   * @param scientificName scientificName.
   */
  public void setScientificName(ScientificName scientificName);

  /**
   * Returns the taxonIdentifier of this Sample object. This should ideally match a taxon ID of a
   * strain in the NCBI Taxonomy database.
   * 
   * @return String taxonIdentifier.
   */
  public String getTaxonIdentifier();

  /**
   * Sets the taxonIdentifier of this Sample object. This should ideally match a taxon ID of a strain
   * in the NCBI Taxonomy database.
   * 
   * @param taxonIdentifier taxonIdentifier.
   */
  public void setTaxonIdentifier(String taxonIdentifier);

  /**
   * Returns the project of this Sample object.
   * 
   * @return Project project.
   */
  public Project getProject();

  /**
   * Sets the project of this Sample object.
   * 
   * @param project project.
   */
  public void setProject(Project project);

  /**
   * Sets the notes of this Sample object.
   * 
   * @param notes notes.
   */
  public void setNotes(Collection<Note> notes);

  /**
   * Adds a Note to the Set of notes of this Sample object.
   * 
   * @param note Note.
   */
  public void addNote(Note note);

  /**
   * Returns the notes of this Sample object.
   * 
   * @return Collection<Note> notes.
   */
  public Collection<Note> getNotes();

  /**
   * Returns the change logs of this Sample object.
   * 
   * @return Collection<ChangeLog> change logs.
   */
  @Override
  public Collection<ChangeLog> getChangeLog();

  /**
   * Returns the sampleType of this Sample object.
   * 
   * @return String sampleType.
   */
  public String getSampleType();

  /**
   * Sets the sampleType of this Sample object.
   * 
   * @param string sampleType.
   */
  public void setSampleType(String string);

  /**
   * Registers a collection of QCs to this Sample object.
   * 
   * @param qcs qcs.
   */
  public void setQCs(Collection<SampleQC> qcs);

  public void setBoxPosition(SampleBoxPosition boxPosition);

  public BigDecimal getInitialVolume();

  public void setInitialVolume(BigDecimal initialVolume);

  /**
   * Returns the volume units of this Sample object.
   * 
   * @return VolumeUnit volumeUnits.
   */
  public VolumeUnit getVolumeUnits();

  /**
   * Sets the volumeUnits of this Sample object.
   * 
   * @param volumeUnits volumeUnits.
   */
  public void setVolumeUnits(VolumeUnit volumeUnits);

  /**
   * Returns the concentration units of this Sample object.
   * 
   * @return ConcentrationUnit concentrationUnits.
   */
  public ConcentrationUnit getConcentrationUnits();

  /**
   * Sets the concentrationUnits of this Sample object.
   * 
   * @param concentrationUnits concentrationUnits.
   */
  public void setConcentrationUnits(ConcentrationUnit concentrationUnits);

  /**
   * Returns the concentration of this Sample object.
   * 
   * @return concentration.
   */
  public BigDecimal getConcentration();

  /**
   * Sets the concentration of this Sample object.
   * 
   * @param concentration.
   */
  public void setConcentration(BigDecimal concentration);

  @Override
  public Sample getParent();

  public SequencingControlType getSequencingControlType();

  public void setSequencingControlType(SequencingControlType sequencingControlType);

  /**
   * @return information to be used for creating a receipt transfer during sample creation only. This
   *         field should otherwise be null
   */
  public TransferSample getCreationReceiptInfo();

  public void setCreationReceiptInfo(TransferSample creationReceiptInfo);

  public Sop getSop();

  public void setSop(Sop sop);

}
