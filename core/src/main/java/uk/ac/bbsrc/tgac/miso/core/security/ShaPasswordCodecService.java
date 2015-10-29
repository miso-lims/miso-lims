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

import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
 * A service class that encodes plaintext passwords into their hashed Base64-encoded counterparts using a specified Spring Security
 * PasswordEncoder
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public final class ShaPasswordCodecService implements PasswordCodecService {
  /** Field log */
  protected static final Logger log = LoggerFactory.getLogger(ShaPasswordCodecService.class);

  /** Field instance */
  private static ShaPasswordCodecService instance;
  private PasswordEncoder encoder;

  @Override
  public PasswordEncoder getEncoder() {
    return encoder;
  }

  @Override
  public void setEncoder(PasswordEncoder encoder) {
    this.encoder = encoder;
  }

  /**
   * Constructor PasswordCodecService creates a new PasswordCodecService instance with a SHA password encoder as default
   */
  private ShaPasswordCodecService() {
    encoder = new ShaPasswordEncoder();
  }

  /**
   * Encrypt a plaintext String using a PasswordEncoder strategy, with a null salt.
   * 
   * @param plaintext
   *          of type String
   * @return String the encrypted String of the given plaintext String
   */
  @Override
  public synchronized String encrypt(String plaintext) {
    return encrypt(plaintext, null);
  }

  /**
   * Encrypt a plaintext String using a PasswordEncoder strategy, with a given salt.
   * 
   * @param plaintext
   *          of type String
   * @return String the encrypted String of the given plaintext String
   */
  @Override
  public synchronized String encrypt(String plaintext, byte[] salt) {
    return encoder.encodePassword(plaintext, salt);
  }

  /**
   * Encrypt a plaintext String using a hmac_sha1 salt
   * 
   * @param key
   *          of type String
   * @param plaintext
   *          of type String
   * @return String the encrypted String of the given plaintext String
   * @throws java.security.SignatureException
   *           when the HMAC is unable to be generated
   */
  public synchronized String encryptHMACSHA1(String key, String plaintext) throws SignatureException {
    String result;
    try {
      // get an hmac_sha1 key from the raw key bytes
      SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");

      // get an hmac_sha1 Mac instance and initialize with the signing key
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(signingKey);

      // compute the hmac on input data bytes
      byte[] rawHmac = mac.doFinal(plaintext.getBytes());

      // base64-encode the hmac
      result = new Base64().encodeToString(rawHmac);
    } catch (Exception e) {
      log.error("failed to generate HMAC", e);
      throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
    }
    return result;
  }

  /**
   * Returns a singleton (as far as singletons are actually singletons!) instance of a PasswordCodecService object.
   * 
   * @return PasswordCodecService instance.
   */
  public static synchronized ShaPasswordCodecService getInstance() {
    if (instance == null) {
      instance = new ShaPasswordCodecService();
    }
    return instance;
  }
}
