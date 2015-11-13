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

package uk.ac.bbsrc.tgac.miso.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * A service class that passes plaintext passwords through the system so that they can then be encoded by a downstream service, such as
 * OpenLDAP
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public final class PassthroughPasswordCodecService implements PasswordCodecService {
  /** Field log */
  protected static final Logger log = LoggerFactory.getLogger(PassthroughPasswordCodecService.class);

  /** Field instance */
  private static PassthroughPasswordCodecService instance;
  private PasswordEncoder encoder;

  @Override
  public PasswordEncoder getEncoder() {
    return encoder;
  }

  /**
   * Set the encoder. This will NOT actually encrypt, but is used for validation of existing passwords
   */
  @Override
  public void setEncoder(PasswordEncoder encoder) {
    this.encoder = encoder;
  }

  /**
   * This method does nothing, i.e. simply passes the password through and does no encryption
   * 
   * @param plaintext
   *          of type String
   * @return String the plaintext String
   */
  @Override
  public synchronized String encrypt(String plaintext) {
    return plaintext;
  }

  /**
   * This method does nothing, i.e. simply passes the password through and does no encryption
   * 
   * @param plaintext
   *          of type String
   * @return String the plaintext String
   */
  @Override
  public synchronized String encrypt(String plaintext, byte[] salt) {
    return plaintext;
  }
}
