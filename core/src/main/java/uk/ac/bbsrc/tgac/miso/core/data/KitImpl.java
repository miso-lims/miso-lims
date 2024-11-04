package uk.ac.bbsrc.tgac.miso.core.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import com.eaglegenomics.simlims.core.Note;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

/**
 * Skeleton implementation of a Kit
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Kit")
public class KitImpl implements Kit {

  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long kitId = KitImpl.UNSAVED_ID;
  private String identificationBarcode;
  private String locationBarcode;

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Kit_Note", joinColumns = {
      @JoinColumn(name = "kit_kitId")},
      inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId")})
  private Collection<Note> notes = new HashSet<>();

  private String lotNumber;

  private LocalDate kitDate;

  @ManyToOne(targetEntity = KitDescriptor.class)
  @JoinColumn(name = "kitDescriptorId", nullable = false)
  private KitDescriptor kitDescriptor;

  @Override
  public long getId() {
    return kitId;
  }

  @Override
  public void setId(long id) {
    this.kitId = id;
  }

  @Override
  public String getLotNumber() {
    return lotNumber;
  }

  @Override
  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }

  @Override
  public LocalDate getKitDate() {
    return kitDate;
  }

  @Override
  public void setKitDate(LocalDate kitDate) {
    this.kitDate = kitDate;
  }

  @Override
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  @Override
  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

  @Override
  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  @CoverageIgnore
  @Override
  public void addNote(Note note) {
    this.notes.add(note);
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  @CoverageIgnore
  @Override
  public String getName() {
    return getKitDescriptor().getName();
  }

  @Override
  @CoverageIgnore
  public String getLabelText() {
    return getLotNumber();
  }

  @CoverageIgnore
  @Override
  public int compareTo(Kit t) {
    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  @CoverageIgnore
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getLotNumber());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    sb.append(" : ");
    sb.append(getKitDate());
    return sb.toString();
  }

  @Override
  public LocalDate getBarcodeDate() {
    return getKitDate();
  }


  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitKit(this);
  }
}
