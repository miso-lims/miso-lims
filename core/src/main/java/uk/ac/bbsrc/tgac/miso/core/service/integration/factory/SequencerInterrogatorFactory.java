/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.service.integration.factory;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.interrogator.IlluminaSequencerInterrogationStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.interrogator.LS454SequencerInterrogationStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.interrogator.SequencerInterrogator;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.interrogator.SolidSequencerInterrogationStrategy;

/**
 * A factory to build SequencerInterrogators
 * 
 * @author Rob Davey
 * @date 06-Sep-2010
 * @since 0.0.2
 */
public class SequencerInterrogatorFactory {

  /**
   * Builds a SequencerInterrogator object from a given SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @return SequencerInterrogator
   * @throws InterrogationException
   *           when an unsupported PlatformType is specified
   */
  public static SequencerInterrogator getSequencerInterrogator(SequencerReference sr) throws InterrogationException {
    if (sr.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
      return new SequencerInterrogator(new IlluminaSequencerInterrogationStrategy(), sr);
    } else if (sr.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
      return new SequencerInterrogator(new LS454SequencerInterrogationStrategy(), sr);
    } else if (sr.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
      return new SequencerInterrogator(new SolidSequencerInterrogationStrategy(), sr);
    } else if (sr.getPlatform().getPlatformType().equals(PlatformType.IONTORRENT)) {
      throw new InterrogationException("Unsupported PlatformType");
    } else if (sr.getPlatform().getPlatformType().equals(PlatformType.PACBIO)) {
      throw new InterrogationException("Unsupported PlatformType");
    } else {
      throw new InterrogationException("Unrecognised PlatformType");
    }
  }
}
