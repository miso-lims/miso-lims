package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;
import uk.ac.bbsrc.tgac.miso.core.data.type.DilutionFactor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * A Library is the first step in constructing sequenceable material from an initial {@link Sample}.
 * A Library is then diluted down to a {@link LibraryAliquot}, and put in a {@link Pool}, which is
 * then sequenced.
 * <p/>
 * Libraries also have a target {@link InstrumentModel} and can be uniquely identified via
 * {@link Index} objects for multiplexing purposes.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Library extends Attachable, Comparable<Library>, Deletable, HierarchyEntity, IndexedLibrary, Locatable,
    QualityControllable<LibraryQC>, Requisitionable {

  public static final String PREFIX = "LIB";

  /**
   * Sets the name of this Library object.
   * 
   * @param name name.
   */
  void setName(String name);

  /**
   * Returns the description of this Library object.
   * 
   * @return String description.
   */
  String getDescription();

  /**
   * Sets the description of this Library object.
   * 
   * @param description description.
   */
  void setDescription(String description);

  /**
   * Returns the accession of this Library object.
   * 
   * @return String accession.
   */
  String getAccession();

  /**
   * Sets the accession of this Library object.
   * 
   * @param accession accession.
   */
  void setAccession(String accession);

  /**
   * Returns the sample of this Library object.
   * 
   * @return Sample sample.
   */
  Sample getSample();

  /**
   * Sets the sample of this Library object.
   * 
   * @param sample sample.
   */
  void setSample(Sample sample);

  /**
   * Sets the notes of this Library object.
   * 
   * @param notes notes.
   */
  void setNotes(Collection<Note> notes);

  /**
   * Adds a Note to the Set of notes of this Library object.
   * 
   * @param note Note
   */
  void addNote(Note note);

  /**
   * Returns the notes of this Library object.
   * 
   * @return Collection<Note> notes.
   */
  Collection<Note> getNotes();

  /**
   * Registers that a LibraryAliquot has been carried out using this Library
   * 
   * @param libraryAliquot
   */
  void addLibraryAliquot(LibraryAliquot libraryAliquot);

  /**
   * @return the libraryAliquots of this Library object.
   */
  Collection<LibraryAliquot> getLibraryAliquots();

  /**
   * Returns the paired attribute of this Library object.
   * 
   * @return Boolean paired.
   */
  Boolean getPaired();

  /**
   * Sets the paired attribute of this Library object, i.e. true is paired, false is single.
   * 
   * @param paired paired.
   */
  void setPaired(Boolean paired);

  /**
   * Returns the libraryType of this Library object.
   * 
   * @return LibraryType libraryType.
   */
  LibraryType getLibraryType();

  /**
   * Sets the libraryType of this Library object.
   * 
   * @param libraryType libraryType.
   */
  void setLibraryType(LibraryType libraryType);

  /**
   * Returns the librarySelectionType of this Library object.
   * 
   * @return LibrarySelectionType librarySelectionType.
   */
  LibrarySelectionType getLibrarySelectionType();

  /**
   * Sets the librarySelectionType of this Library object.
   * 
   * @param librarySelectionType LibrarySelectionType.
   */
  void setLibrarySelectionType(LibrarySelectionType librarySelectionType);

  /**
   * Returns the libraryStrategyType of this Library object.
   * 
   * @return LibraryStrategyType libraryStrategyType.
   */
  LibraryStrategyType getLibraryStrategyType();

  /**
   * Sets the libraryStrategyType of this Library object.
   * 
   * @param libraryStrategyType LibraryStrategyType.
   */
  void setLibraryStrategyType(LibraryStrategyType libraryStrategyType);

  /**
   * Returns the platformType of this Library object.
   * 
   * @return PlatformType platformType.
   */
  PlatformType getPlatformType();

  /**
   * Sets the platformType of this Library object.
   * 
   * @param PlatformType platformType.
   * 
   */
  void setPlatformType(PlatformType platformType);

  /**
   * Sets the platformType of this Library object.
   * 
   * @param String platformType
   */
  void setPlatformType(String platformName);

  BigDecimal getConcentration();

  void setConcentration(BigDecimal concentration);

  @Override
  Collection<ChangeLog> getChangeLog();

  /**
   * Set the flag that this library is sufficiently bad that it is not worth sequencing.
   */
  void setLowQuality(boolean lowquality);

  boolean isLowQuality();

  Integer getDnaSize();

  void setDnaSize(Integer dnaSize);

  void setBoxPosition(LibraryBoxPosition boxPosition);

  /**
   * @return the user-specified date that this Library was created
   */
  LocalDate getCreationDate();

  /**
   * Sets the user-specified date that this Library was created
   * 
   * @param creationDate
   */
  void setCreationDate(LocalDate creationDate);

  KitDescriptor getKitDescriptor();

  void setKitDescriptor(KitDescriptor prepKit);

  String getKitLot();

  void setKitLot(String kitLot);

  /**
   * Returns the concentration units of this Library object.
   * 
   * @return ConcentrationUnit concentrationUnits.
   */
  ConcentrationUnit getConcentrationUnits();

  /**
   * Sets the concentrationUnits of this Library object.
   * 
   * @param concentrationUnits concentrationUnits.
   */
  void setConcentrationUnits(ConcentrationUnit concentrationUnits);

  BigDecimal getInitialVolume();

  void setInitialVolume(BigDecimal initialVolume);

  BigDecimal getNgUsed();

  void setNgUsed(BigDecimal ngUsed);

  /**
   * Returns the volume units of this Library object.
   * 
   * @return VolumeUnit volumeUnits.
   */
  VolumeUnit getVolumeUnits();

  /**
   * Sets the volumeUnits of this Library object.
   * 
   * @param volumeUnits volumeUnits.
   */
  void setVolumeUnits(VolumeUnit volumeUnits);

  LibrarySpikeIn getSpikeIn();

  void setSpikeIn(LibrarySpikeIn spikeIn);

  BigDecimal getSpikeInVolume();

  void setSpikeInVolume(BigDecimal spikeInVolume);

  DilutionFactor getSpikeInDilutionFactor();

  void setSpikeInDilutionFactor(DilutionFactor dilutionFactor);

  @Override
  Sample getParent();

  boolean getUmis();

  void setUmis(boolean umis);

  Workstation getWorkstation();

  void setWorkstation(Workstation workstation);

  Instrument getThermalCycler();

  void setThermalCycler(Instrument thermalCycler);

  /**
   * @return information to be used for creating a receipt transfer during library creation only. This
   *         field should otherwise be null
   */
  TransferLibrary getCreationReceiptInfo();

  void setCreationReceiptInfo(TransferLibrary creationReceiptInfo);

  Sop getSop();

  void setSop(Sop sop);

  String getBatchId();

}
