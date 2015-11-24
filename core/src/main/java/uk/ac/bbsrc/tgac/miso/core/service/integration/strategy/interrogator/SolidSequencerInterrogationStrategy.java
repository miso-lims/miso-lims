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

package uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.interrogator;

import java.util.List;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.SequencerInterrogationStrategy;

/**
 * A concrete implementation of a SequencerInterrogationStrategy that can make queries and parse results, supported by a
 * MisoPerlDaemonInterrogationMechanism, to a SOLiD sequencer.
 * <p/>
 * Methods in this class are not usually called explicitly, but via a {@link SequencerInterrogator} that has wrapped up this strategy to a
 * SequencerReference.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class SolidSequencerInterrogationStrategy implements SequencerInterrogationStrategy {

  @Override
  public boolean isStrategyFor(SequencerReference sr) {
    return (sr.getPlatform().getPlatformType().equals(PlatformType.SOLID));
  }

  @Override
  public List<Status> listAllStatus(SequencerReference sr) throws InterrogationException {
    return null;
  }

  @Override
  public List<Status> listAllStatusBySequencerName(SequencerReference sr, String name) throws InterrogationException {
    return null;
  }

  @Override
  public List<String> listRunsByHealthType(SequencerReference sr, HealthType healthType) throws InterrogationException {
    return null;
  }

  @Override
  public List<String> listAllCompleteRuns(SequencerReference sr) throws InterrogationException {
    return null;
  }

  @Override
  public List<String> listAllIncompleteRuns(SequencerReference sr) throws InterrogationException {
    return null;
  }

  @Override
  public Status getRunStatus(SequencerReference sr, String runName) throws InterrogationException {
    return null;
  }

  @Override
  public JSONObject getRunInformation(SequencerReference sr, String runName) throws InterrogationException {
    return null;
  }
}
