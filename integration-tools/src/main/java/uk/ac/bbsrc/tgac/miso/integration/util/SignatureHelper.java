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
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public static final String PUBLIC_KEY = "MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZp;RV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGARBu0g4MdHVhU6NoSXMKDBFSX9KfkTwIOXM6GY3DhAWsQhejkAkxp8c0IpkKn+i+PQNM/2pntXLWxDGHQGhfJIwvP041SrRTCXtx8SJ59ima8Z6/my7N72pPvbeDcPjlshtp/oa6eHh9M4J18W5hI4HD6I6f4qnppP1rRYaZolhw=";
  public static final String PRIVATE_KEY = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUOCrAiHXm+FJBM7QHMhBxanPAn3k=";

  public static final String USER_HEADER = "x-user";
  public static final String TIMESTAMP_HEADER = "x-timestamp";
  public static final String SIGNATURE_HEADER = "x-signature";
  public static final String URL_X_HEADER = "x-url";

  public static final List<String> SIGNATURE_KEYWORDS = Arrays.asList(USER_HEADER, TIMESTAMP_HEADER, URL_X_HEADER);
  private static final String DSA_ALGORITHM = "DSA";
  private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

  public static String createSignature(Map<String, List<String>> headersAndParams, String url, String privateKey) throws Exception {
    TreeMap<String, String> sortedHeaders = new TreeMap<String, String>();
    for (String key : headersAndParams.keySet()) {
      if (SIGNATURE_KEYWORDS.contains(key)) {
        sortedHeaders.put(key, headersAndParams.get(key).get(0));
      }
    }

    String sortedUrl = createSortedUrl(url, sortedHeaders);

    log.debug("CREATING SIGNATURE FROM SUPPLIED HEADERS: " + sortedUrl + " :: " + privateKey);
    return calculateHMAC(sortedUrl, privateKey);
  }

  public static String createSignatureFromRequest(HttpServletRequest request, String privateKey) throws Exception {
    String sortedUrl = createSortedUrl(request);

    log.debug("CREATING SIGNATURE FROM REQUEST: " + sortedUrl + " :: " + privateKey);
    return calculateHMAC(sortedUrl, privateKey);
  }

  public static String createSortedUrl(String url, TreeMap<String, String> headersAndParams) {
    // build the url with headers and params sorted
    String params = "";
    for (String key : headersAndParams.keySet()) {
      if (params.length() > 0) {
        params += "@";
      }
      params += key + "=" + headersAndParams.get(key).toString();
    }
    if (!url.endsWith("?") && !"".equals(params)) url += "?";

    log.debug("COMPLETE URL: " + url + params);

    return url + params;
  }

  public static String createSortedUrl(HttpServletRequest request) {
    // use a TreeMap to sort the headers and parameters
    TreeMap<String, String> headersAndParams = new TreeMap<String, String>();

    // load header values we care about
    Enumeration e = request.getHeaderNames();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      if (SIGNATURE_KEYWORDS.contains(key)) {
        log.debug("FOUND HEADER: " + key);
        headersAndParams.put(key, request.getHeader(key));
      }
    }

    return createSortedUrl(request.getContextPath() + request.getServletPath() + request.getPathInfo(), headersAndParams);
  }

  public static String calculateHMAC(String data, String key) throws java.security.SignatureException {
    String result;
    try {
      // get an hmac_sha1 key from the raw key bytes
      SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

      // get an hmac_sha1 Mac instance and initialize with the signing key
      Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
      mac.init(signingKey);

      // compute the hmac on input data bytes
      byte[] rawHmac = mac.doFinal(data.getBytes());

      // base64-encode the hmac
      result = Base64.encodeBase64URLSafeString(rawHmac);
    } catch (Exception e) {
      log.error("failed to generate HMAC", e);
      throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
    }
    return result;
  }

  @Deprecated
  private static PublicKey decodePublicKey(String publicKey) throws Exception {
    KeyFactory keyFactory = KeyFactory.getInstance(DSA_ALGORITHM);
    byte[] publicKeyBytes = Base64.decodeBase64(publicKey);
    EncodedKeySpec publicKeySpec = new PKCS8EncodedKeySpec(publicKeyBytes);
    return keyFactory.generatePublic(publicKeySpec);
  }

  @Deprecated
  public static boolean validateSignature(String url, String signatureString, String apiKey) throws InvalidKeyException, Exception {
    if (url == null || signatureString == null || apiKey == null) {
      throw new InvalidKeyException("Cannot verify signature when url, signature or api key are null!");
    }
    log.debug("VALIDATING: " + url + " :: " + signatureString + " :: " + apiKey);

    Signature signature = Signature.getInstance(HMAC_SHA1_ALGORITHM);
    signature.initVerify(decodePublicKey(apiKey));
    signature.update(url.getBytes());
    try {
      return signature.verify(Base64.decodeBase64(signatureString));
    } catch (SignatureException e) {
      log.error("FAILED TO VERIFY SIGNATURE: " + signature.toString(), e);
      return false;
    }
  }

  public static boolean validateSignature(HttpServletRequest request, String publicKey, String signature)
      throws InvalidKeyException, Exception {
    if (request == null || signature == null || publicKey == null) {
      throw new InvalidKeyException("Cannot verify signature when request or signature are null!");
    }

    log.debug("VERIFYING HMAC: " + signature + " vs " + createSignatureFromRequest(request, publicKey));

    return signature.equals(createSignatureFromRequest(request, publicKey));
  }

  public static String generatePrivateUserKey(byte[] data) throws NoSuchAlgorithmException {
    SecretKeySpec signingKey = new SecretKeySpec(data, DSA_ALGORITHM);
    return Base64.encodeBase64URLSafeString(signingKey.getEncoded());
  }
}
