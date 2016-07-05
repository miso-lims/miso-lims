package uk.ac.bbsrc.tgac.miso.notification.service;

public class AsInteger extends RunTransform<String, Integer> {

  @Override
  protected Integer convert(String input, IlluminaRunMessage output) throws Exception {
    try {
      int x = Integer.parseInt(input);
      return x;
    } catch (NumberFormatException e) {
      return null;
    }
  }

}
