package uk.ac.bbsrc.tgac.miso.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcodeFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.TagBarcodeService;
import uk.ac.bbsrc.tgac.miso.core.store.TagBarcodeStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTagBarcodeService implements TagBarcodeService {
  @Autowired
  private TagBarcodeStore tagBarcodeStore;

  public DefaultTagBarcodeService() {
    System.out.println(this.hashCode());
  }

  @Override
  public TagBarcodeFamily getTagBarcodeFamilyByName(String name) {
    return tagBarcodeStore.getTagBarcodeFamilyByName(name);
  }

  @Override
  public Collection<TagBarcodeFamily> getTagBarcodeFamilies() {
    return tagBarcodeStore.getTagBarcodeFamilies();
  }

  @Override
  public Collection<TagBarcodeFamily> getTagBarcodeFamiliesByPlatform(PlatformType platformType) {
    return tagBarcodeStore.getTagBarcodeFamiliesByPlatform(platformType);
  }

  @Override
  public TagBarcode getTagBarcodeById(long id) {
    return tagBarcodeStore.getTagBarcodeById(id);
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodes(PlatformType platformType) {
    return tagBarcodeStore.listAllTagBarcodes(platformType);
  }

}
