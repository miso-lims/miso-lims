package uk.ac.bbsrc.tgac.miso.migration.source;

public class MigrationException extends Exception {

  private static final long serialVersionUID = 1L;

  public MigrationException(String message) {
    super(message);
  }

  public MigrationException(Throwable cause) {
    super(cause);
  }

  public MigrationException(String message, Throwable cause) {
    super(message, cause);
  }

  public MigrationException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
