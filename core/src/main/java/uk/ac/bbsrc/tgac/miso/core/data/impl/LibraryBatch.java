package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class LibraryBatch implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Pattern batchIdPattern =
      Pattern.compile("^(\\d{4}-\\d{2}-\\d{2})_u(\\d+)_s(\\d+)_k(\\d+)-(.+)$");

  private final String batchId;
  private final LocalDate date;
  private final long userId;
  private final long sopId;
  private final long kitId;
  private final String kitLot;

  public static String generateId(Library library) {
    if (library.getCreationDate() == null || library.getCreator() == null || library.getSop() == null
        || library.getKitDescriptor() == null
        || library.getKitLot() == null) {
      return null;
    }
    return LimsUtils.formatDate(library.getCreationDate())
        + "_u" + library.getCreator().getId()
        + "_s" + library.getSop().getId()
        + "_k" + library.getKitDescriptor().getId() + "-" + library.getKitLot();
  }

  public LibraryBatch(String batchId) {
    Matcher m = batchIdPattern.matcher(batchId);
    if (!m.matches()) {
      throw new IllegalArgumentException("Invalid batch ID");
    }
    this.batchId = batchId;
    this.date = LimsUtils.parseLocalDate(m.group(1));
    this.userId = Long.parseLong(m.group(2));
    this.sopId = Long.parseLong(m.group(3));
    this.kitId = Long.parseLong(m.group(4));
    this.kitLot = m.group(5);
  }

  public String getBatchId() {
    return batchId;
  }

  public LocalDate getDate() {
    return date;
  }

  public long getUserId() {
    return userId;
  }

  public long getSopId() {
    return sopId;
  }

  public long getKitId() {
    return kitId;
  }

  public String getKitLot() {
    return kitLot;
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, userId, sopId, kitId, kitLot);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        LibraryBatch::getDate,
        LibraryBatch::getUserId,
        LibraryBatch::getSopId,
        LibraryBatch::getKitId,
        LibraryBatch::getKitLot);
  }

}
