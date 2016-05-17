package uk.ac.bbsrc.tgac.miso.core.test;

import java.util.Collection;
import java.util.Collections;

import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcodeFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.TagBarcodeService;

public class MockFormTestTagBarcodeStrategyService implements TagBarcodeService {
  private static TagBarcodeFamily TRUSEQ = new TagBarcodeFamily();

  static {
    TRUSEQ.setName("TruSeq Single Index");
    TRUSEQ.setPlatformType(PlatformType.ILLUMINA);
    TagBarcode barcode = new TagBarcode();
    barcode.setId(1);
    barcode.setName("Index 1");
    barcode.setPosition(1);
    barcode.setSequence("AAAAAA");
    barcode.setFamily(TRUSEQ);
    TRUSEQ.setBarcodes(Collections.singletonList(barcode));
  }

  @Override
  public TagBarcodeFamily getTagBarcodeFamilyByName(String strategyName) {
    return strategyName.equals(TRUSEQ.getName()) ? TRUSEQ : null;
  }

  @Override
  public Collection<TagBarcodeFamily> getTagBarcodeFamilies() {
    return Collections.singleton(TRUSEQ);
  }

  @Override
  public Collection<TagBarcodeFamily> getTagBarcodeFamiliesByPlatform(PlatformType platformType) {
    if (platformType == TRUSEQ.getPlatformType()) {
      return Collections.singleton(TRUSEQ);
    } else {
      return Collections.emptySet();
    }
  }

  @Override
  public TagBarcode getTagBarcodeById(long id) {
    return null;
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodes(PlatformType platformType) {
    if (platformType == TRUSEQ.getPlatformType()) {
      return TRUSEQ.getBarcodes();
    } else {
      return Collections.emptySet();
    }
  }
}
