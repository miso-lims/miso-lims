package uk.ac.bbsrc.tgac.miso.core.service;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcodeFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface TagBarcodeService {
  public TagBarcodeFamily getTagBarcodeFamilyByName(String name);

  public Collection<TagBarcodeFamily> getTagBarcodeFamilies();

  public Collection<TagBarcodeFamily> getTagBarcodeFamiliesByPlatform(PlatformType platformType);

  public TagBarcode getTagBarcodeById(long id);

  public Collection<TagBarcode> listAllTagBarcodes(PlatformType platformType);
}
