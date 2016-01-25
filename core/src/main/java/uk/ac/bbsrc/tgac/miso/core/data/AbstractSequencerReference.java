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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to provide basic methods to encapsulate a reference to a physical machine attached to a sequencer
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class AbstractSequencerReference implements SequencerReference {
  protected static final Logger log = LoggerFactory.getLogger(AbstractSequencerReference.class);

  public static final Long UNSAVED_ID = 0L;

  private long id = AbstractSequencerReference.UNSAVED_ID;
  private String name;
  private Platform platform;
  private Boolean available;
  private InetAddress ip;

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  @Override
  public Platform getPlatform() {
    return this.platform;
  }

  @Override
  public void setAvailable(Boolean available) {
    this.available = available;
  }

  @Override
  public Boolean getAvailable() {
    return this.available;
  }

  @Override
  public void checkAvailability(int timeout) throws IOException {
    this.available = getIpAddress().isReachable(timeout);
  }

  @Override
  public void setIpAddress(InetAddress ip) {
    this.ip = ip;
  }

  @Override
  public InetAddress getIpAddress() {
    return this.ip;
  }

  @Override
  public String getFQDN() {
    return getIpAddress().getCanonicalHostName();
  }

  @Override
  public boolean isDeletable() {
    return getId() != AbstractSequencerReference.UNSAVED_ID;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getFQDN());
    sb.append(" : ");
    sb.append(getAvailable());
    return sb.toString();
  }
}
