package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;

/**
 * Defines a DAO interface for storing KitComponentDescriptors
 *
 * @author Michal Zak
 * @since 0.0.2
 */
public interface KitComponentDescriptorStore extends Store<KitComponentDescriptor> {

  @Deprecated
  KitComponentDescriptor getKitComponentDescriptorById(long kitComponentDescriptorId) throws IOException;

  KitComponentDescriptor getKitComponentDescriptorByReferenceNumber(String referenceNumber) throws IOException;

  List<KitComponentDescriptor> listKitComponentDescriptorsByKitDescriptorId(long kitDescriptorId) throws IOException;
  // String getKitFullNameByKitComponentDescriptorId(long kitComponentDescriptorId) throws IOException;
}
