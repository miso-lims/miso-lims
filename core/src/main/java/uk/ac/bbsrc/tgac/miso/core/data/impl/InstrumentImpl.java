/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractInstrument;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;

@Entity
@Table(name = "Instrument")
public class InstrumentImpl extends AbstractInstrument implements Serializable {

  private static final long serialVersionUID = 1L;

  public InstrumentImpl(String name, String ip, Platform platform) {
    setName(name);
    setIpAddress(ip);
    setPlatform(platform);
  }

  /**
   * Exists for Hibernate purposes
   * 
   * @throws IOException
   */
  public InstrumentImpl() {
    setPlatform(null);
    setName(null);
    setIpAddress(null);
  }
}
