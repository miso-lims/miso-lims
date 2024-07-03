package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface SequencingContainerModelStore extends BulkSaveDao<SequencingContainerModel> {

  /**
   * Attempt to find a SequencingContainerModel matching the supplied parameters
   * 
   * @param platform the platform to find a model for (required)
   * @param search the alias or identificationBarcode of the model to search for. If null, will search
   *        for a "fallback" model instead
   * @param partitionCount the number of partitions that the model must have (required)
   * @return an appropriate model if one is found; null otherwise
   */
  SequencingContainerModel find(InstrumentModel platform, String search, int partitionCount);

  /**
   * Attempt to find a SequencingContainerModel matching the supplied parameters
   * 
   * @param platform the platform to find a model for
   * @param search the alias or identificationBarcode of the model to search for. Aliases match
   *        case-sensitive from the start (e.g. searching 'mod' will find alias 'model'), and
   *        identificationBarcodes match exact search only
   * @return a list containing any matching models. This method will not return a fallback model
   *         unless its alias or identificationBarcode matches the search
   */
  List<SequencingContainerModel> find(PlatformType platform, String search) throws IOException;

  SequencingContainerModel getByPlatformAndAlias(PlatformType platform, String alias) throws IOException;

  SequencingContainerModel getByPlatformAndBarcode(PlatformType platform, String identificationBarcode)
      throws IOException;

  long getUsage(SequencingContainerModel model) throws IOException;

  long getUsage(SequencingContainerModel containerModel, InstrumentModel instrumentModel) throws IOException;

}
