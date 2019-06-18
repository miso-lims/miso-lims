package uk.ac.bbsrc.tgac.miso.core.util;

public class Pluralizer {

  private Pluralizer() {
    throw new IllegalStateException("Static util class not intended for instantiation");
  }

  public static final String arrays(long count) {
    return pluralize(count, "array", "arrays");
  }

  public static final String attachments(long count) {
    return pluralize(count, "attachment", "attachments");
  }

  public static final String boxes(long count) {
    return pluralize(count, "box", "boxes");
  }

  public static final String freezers(long count) {
    return pluralize(count, "freezer", "freezers");
  }

  public static final String labs(long count) {
    return pluralize(count, "lab", "labs");
  }

  public static String libraries(long count) {
    return pluralize(count, "library", "libraries");
  }

  public static String libraryAliquots(long count) {
    return pluralize(count, "library aliquot", "library aliquots");
  }

  public static String libraryDesigns(long count) {
    return pluralize(count, "library design", "library designs");
  }

  public static String libraryTemplates(long count) {
    return pluralize(count, "library template", "library templates");
  }

  public static String orders(long count) {
    return pluralize(count, "order", "orders");
  }

  public static String partitions(long count) {
    return pluralize(count, "partition", "partitions");
  }

  public static String projects(long count) {
    return pluralize(count, "project", "projects");
  }

  public static String samples(long count) {
    return pluralize(count, "sample", "samples");
  }

  public static String stains(long count) {
    return pluralize(count, "stain", "stains");
  }

  public static String studies(long count) {
    return pluralize(count, "study", "studies");
  }

  public static String templates(long count) {
    return pluralize(count, "template", "templates");
  }

  private static String pluralize(long count, String singular, String plural) {
    return count == 1 ? singular : plural;
  }

}
