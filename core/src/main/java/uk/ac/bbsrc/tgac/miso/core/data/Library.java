/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

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
 * A Library is the first step in constructing sequenceable material from an initial {@link Sample}. A Library is then diluted down to a
 * {@link LibraryAliquot}, and put in a {@link Pool}, which is then sequenced.
 * <p/>
 * Libraries also have a target {@link InstrumentModel} and can be uniquely identified via {@link Index} objects for multiplexing purposes.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Library extends Attachable, Comparable<Library>, Deletable, HierarchyEntity, Locatable,
    QualityControllable<LibraryQC>, Serializable {

  /** Field UNSAVED_ID */
  public static final long UNSAVED_ID = 0L;
  /** Field PREFIX */
  public static final String PREFIX = "LIB";

  /**
   * Sets the name of this Library object.
   * 
   * @param name
   *          name.
   */
  public void setName(String name);

  /**
   * Returns the description of this Library object.
   * 
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Library object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description);

  /**
   * Returns the accession of this Library object.
   * 
   * @return String accession.
   */
  public String getAccession();

  /**
   * Sets the accession of this Library object.
   * 
   * @param accession
   *          accession.
   */
  public void setAccession(String accession);

  /**
   * Returns the sample of this Library object.
   * 
   * @return Sample sample.
   */
  public Sample getSample();

  /**
   * Sets the sample of this Library object.
   * 
   * @param sample
   *          sample.
   */
  public void setSample(Sample sample);

  /**
   * Sets the notes of this Library object.
   * 
   * @param notes
   *          notes.
   */
  public void setNotes(Collection<Note> notes);

  /**
   * Adds a Note to the Set of notes of this Library object.
   * 
   * @param note
   *          Note
   */
  public void addNote(Note note);

  /**
   * Returns the notes of this Library object.
   * 
   * @return Collection<Note> notes.
   */
  public Collection<Note> getNotes();

  /**
   * Registers that a LibraryAliquot has been carried out using this Library
   * 
   * @param libraryAliquot
   */
  public void addLibraryAliquot(LibraryAliquot libraryAliquot);

  /**
   * @return the libraryAliquots of this Library object.
   */
  public Collection<LibraryAliquot> getLibraryAliquots();

  /**
   * Returns the paired attribute of this Library object.
   * 
   * @return Boolean paired.
   */
  public Boolean getPaired();

  /**
   * Sets the paired attribute of this Library object, i.e. true is paired, false is single.
   * 
   * @param paired
   *          paired.
   */
  public void setPaired(Boolean paired);

  /**
   * Returns the libraryType of this Library object.
   * 
   * @return LibraryType libraryType.
   */
  public LibraryType getLibraryType();

  /**
   * Sets the libraryType of this Library object.
   * 
   * @param libraryType
   *          libraryType.
   */
  public void setLibraryType(LibraryType libraryType);

  /**
   * Returns the librarySelectionType of this Library object.
   * 
   * @return LibrarySelectionType librarySelectionType.
   */
  public LibrarySelectionType getLibrarySelectionType();

  /**
   * Sets the librarySelectionType of this Library object.
   * 
   * @param librarySelectionType
   *          LibrarySelectionType.
   */
  public void setLibrarySelectionType(LibrarySelectionType librarySelectionType);

  /**
   * Returns the libraryStrategyType of this Library object.
   * 
   * @return LibraryStrategyType libraryStrategyType.
   */
  public LibraryStrategyType getLibraryStrategyType();

  /**
   * Sets the libraryStrategyType of this Library object.
   * 
   * @param libraryStrategyType
   *          LibraryStrategyType.
   */
  public void setLibraryStrategyType(LibraryStrategyType libraryStrategyType);

  /**
   * Returns the indices for this Library object.
   */
  public Set<Index> getIndices();

  /**
   * Returns the platformType of this Library object.
   * 
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType();

  /**
   * Sets the platformType of this Library object.
   * 
   * @param PlatformType platformType.
   * 
   */
  public void setPlatformType(PlatformType platformType);

  /**
   * Sets the platformType of this Library object.
   * 
   * @param String platformType
   */
  public void setPlatformType(String platformName);

  public BigDecimal getConcentration();

  public void setConcentration(BigDecimal concentration);

  @Override
  public Collection<ChangeLog> getChangeLog();

  /**
   * Set the flag that this library is sufficiently bad that it is not worth sequencing.
   */
  public void setLowQuality(boolean lowquality);

  public boolean isLowQuality();

  public Integer getDnaSize();

  public void setDnaSize(Integer dnaSize);

  public void setBoxPosition(LibraryBoxPosition boxPosition);

  /**
   * @return the user-specified date that this Library was created
   */
  public Date getCreationDate();

  /**
   * Sets the user-specified date that this Library was created
   * 
   * @param creationDate
   */
  public void setCreationDate(Date creationDate);

  public KitDescriptor getKitDescriptor();

  public void setKitDescriptor(KitDescriptor prepKit);

  public String getKitLot();

  public void setKitLot(String kitLot);

  /**
   * Returns the concentration units of this Library object.
   * 
   * @return ConcentrationUnit concentrationUnits.
   */
  public ConcentrationUnit getConcentrationUnits();

  /**
   * Sets the concentrationUnits of this Library object.
   * 
   * @param concentrationUnits concentrationUnits.
   */
  public void setConcentrationUnits(ConcentrationUnit concentrationUnits);

  public BigDecimal getInitialVolume();

  public void setInitialVolume(BigDecimal initialVolume);

  public BigDecimal getNgUsed();

  public void setNgUsed(BigDecimal ngUsed);

  /**
   * Returns the volume units of this Library object.
   * 
   * @return VolumeUnit volumeUnits.
   */
  public VolumeUnit getVolumeUnits();

  /**
   * Sets the volumeUnits of this Library object.
   * 
   * @param volumeUnits volumeUnits.
   */
  public void setVolumeUnits(VolumeUnit volumeUnits);

  public LibrarySpikeIn getSpikeIn();

  public void setSpikeIn(LibrarySpikeIn spikeIn);

  public BigDecimal getSpikeInVolume();

  public void setSpikeInVolume(BigDecimal spikeInVolume);

  public DilutionFactor getSpikeInDilutionFactor();

  public void setSpikeInDilutionFactor(DilutionFactor dilutionFactor);

  @Override
  public Sample getParent();

  public boolean getUmis();

  public void setUmis(boolean umis);

  public Workstation getWorkstation();

  public void setWorkstation(Workstation workstation);

  public Instrument getThermalCycler();

  public void setThermalCycler(Instrument thermalCycler);

  /**
   * @return information to be used for creating a receipt transfer during library creation only. This field should
   *         otherwise be null
   */
  public TransferLibrary getCreationReceiptInfo();

  public void setCreationReceiptInfo(TransferLibrary creationReceiptInfo);

  public Sop getSop();

  public void setSop(Sop sop);

  public String getBatchId();

}
