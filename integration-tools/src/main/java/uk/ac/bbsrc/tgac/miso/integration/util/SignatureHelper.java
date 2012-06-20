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

package uk.ac.bbsrc.tgac.miso.integration.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.context
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 09/02/12
 * @since 0.1.6
 */

public class SignatureHelper {
  protected static final Logger log = LoggerFactory.getLogger(SignatureHelper.class);

  public static final String API_KEY = "RS00001";
  public static final String PUBLIC_KEY = "MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZp;RV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGARBu0g4MdHVhU6NoSXMKDBFSX9KfkTwIOXM6GY3DhAWsQhejkAkxp8c0IpkKn+i+PQNM/2pntXLWxDGHQGhfJIwvP041SrRTCXtx8SJ59ima8Z6/my7N72pPvbeDcPjlshtp/oa6eHh9M4J18W5hI4HD6I6f4qnppP1rRYaZolhw=";
  public static final String PRIVATE_KEY = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUOCrAiHXm+FJBM7QHMhBxanPAn3k=";

  public static final String APIKEY_HEADER = "apikey";
  public static final String TIMESTAMP_HEADER = "timestamp";
  public static final String SIGNATURE_HEADER = "signature";

  public static final String URL_X_HEADER = "x-url";
  public static final String APIKEY_X_HEADER = "x-apikey";
  public static final String TIMESTAMP_X_HEADER = "x-timestamp";
  public static final String SIGNATURE_X_HEADER = "x-signature";

  public static final List<String> SIGNATURE_KEYWORDS = Arrays.asList(APIKEY_HEADER,
                                                                      TIMESTAMP_HEADER,
                                                                      URL_X_HEADER,
                                                                      APIKEY_X_HEADER,
                                                                      TIMESTAMP_X_HEADER,
                                                                      SIGNATURE_X_HEADER);

  private static final String ALGORITHM = "DSA";

  public static String getPublicKey(String apiKey) {
    if (apiKey.equals(SignatureHelper.API_KEY)) {
      return SignatureHelper.PUBLIC_KEY;
    }
    return null;
  }

  public static String createSignature(Map<String, List<String>> headers, String url, String privateKey) throws Exception {
    TreeMap<String, String> sortedHeaders = new TreeMap<String, String>();
    for (String key : headers.keySet()) {
      if (SIGNATURE_KEYWORDS.contains(key)) {
        sortedHeaders.put(key, headers.get(key).get(0));
      }
    }

    String sortedUrl = createSortedUrl(url, sortedHeaders);

    log.debug("CREATING SIGNATURE: " + sortedUrl);

    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    byte[] privateKeyBytes = Base64.decodeBase64(privateKey);
    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

    Signature sig = Signature.getInstance(ALGORITHM);
    sig.initSign(keyFactory.generatePrivate(privateKeySpec));
    sig.update(sortedUrl.getBytes());

    return Base64.encodeBase64URLSafeString(sig.sign());
  }

  private static PublicKey decodePublicKey(String publicKey) throws Exception {
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    byte[] publicKeyBytes = Base64.decodeBase64(publicKey);
    EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
    return keyFactory.generatePublic(publicKeySpec);
  }

  public static boolean validateSignature(String url, String signatureString, String apiKey) throws InvalidKeyException, Exception {
    if (url == null || signatureString == null || apiKey == null) {
      throw new InvalidKeyException("Cannot verify signature when url, signature or api key are null!");
    }
    log.debug("VALIDATING: " + url + " :: " + signatureString + " :: " + apiKey);

    String publicKey = SignatureHelper.getPublicKey(apiKey);
    if (publicKey == null) return false;

    Signature signature = Signature.getInstance(ALGORITHM);
    signature.initVerify(decodePublicKey(publicKey));
    signature.update(url.getBytes());
    try {
      return signature.verify(Base64.decodeBase64(signatureString));
    }
    catch (SignatureException e) {
      log.info("FAILED TO VERIFY SIGNATURE: " + signature.toString());
      return false;
    }
  }

  public static String createSortedUrl(HttpServletRequest request) {
// use a TreeMap to sort the headers and parameters
    TreeMap<String, String> headersAndParams = new TreeMap<String, String>();

// load header values we care about
    Enumeration e = request.getHeaderNames();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      log.debug("FOUND HEADER: " + key);
      if (SIGNATURE_KEYWORDS.contains(key)) {
        headersAndParams.put(key, request.getHeader(key));
      }
    }

// load parameters
    for (Object key : request.getParameterMap().keySet()) {
      log.debug("FOUND PARAMETER: " + (String)key);
      String[] o = (String[]) request.getParameterMap().get(key);
      headersAndParams.put((String) key, o[0]);
    }

    return createSortedUrl(
            request.getContextPath() + request.getServletPath() + request.getPathInfo(),
            headersAndParams);

  }

  public static String createSortedUrl(String url, TreeMap<String, String> headersAndParams) {
// build the url with headers and parms sorted
    String params = "";
    for (String key : headersAndParams.keySet()) {
      if (params.length() > 0) {
        params += "@";
      }
      params += key + "=" + headersAndParams.get(key).toString();
    }
    if (!url.endsWith("?")) url += "?";

    log.debug("COMPLETE URL: " + url + params);

    return url + params;
  }
}
