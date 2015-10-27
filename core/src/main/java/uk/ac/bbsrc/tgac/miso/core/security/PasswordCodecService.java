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

import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * A service class that encodes plaintext passwords into their hashed Base64-encoded counterparts using a specified Spring Security
 * PasswordEncoder
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PasswordCodecService {

  PasswordEncoder getEncoder();

  void setEncoder(PasswordEncoder encoder);

  /**
   * Encrypt a plaintext String using a PasswordEncoder strategy, with a null salt.
   * 
   * @param plaintext
   *          of type String
   * @return String the encrypted String of the given plaintext String
   */
  String encrypt(String plaintext);

  /**
   * Encrypt a plaintext String using a PasswordEncoder strategy, with a given salt.
   * 
   * @param plaintext
   *          of type String
   * @return String the encrypted String of the given plaintext String
   */
  String encrypt(String plaintext, byte[] salt);
}
