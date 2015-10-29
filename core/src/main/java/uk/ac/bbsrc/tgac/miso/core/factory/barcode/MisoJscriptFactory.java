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

package uk.ac.bbsrc.tgac.miso.core.factory.barcode;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uk.ac.bbsrc.tgac.miso.core.factory.barcode
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 04-May-2011
 * @since 0.0.3
 */
public class MisoJscriptFactory {
  protected static final Logger log = LoggerFactory.getLogger(MisoJscriptFactory.class);

  private static String unicodeify(String barcode) {
    log.info("ORIGINAL :: " + barcode);
    StringBuilder b = new StringBuilder();
    int count = 0;
    for (Character c : barcode.toCharArray()) {
      if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
        int codePoint = Character.codePointAt(barcode, count);
        b.append("[U:$").append(String.format("%04x", codePoint).toUpperCase()).append("]");
      } else {
        b.append(c);
      }
      count++;
    }
    log.info("UNICODED :: " + b.toString());
    return b.toString();
  }

  public static String standardTubeBarcodeLabel(String name, String alias, String barcode) {
    StringBuilder sb = new StringBuilder();

    try {
      barcode = new String(Base64.encodeBase64(barcode.getBytes("UTF-8")));

      sb.append("m m").append("\n");
      sb.append("J").append("\n");
      sb.append("S l1;0,0,12,15,38").append("\n");
      sb.append("B 3,2,0,DATAMATRIX,0.21;").append(barcode).append("\n");
      sb.append("B 17,1,0,DATAMATRIX+RECT,0.25;").append(barcode).append("\n");
      sb.append("T 29,2,0,5,pt4;[DATE]").append("\n");

      // shorten alias to fit on label if too long
      if (alias.length() >= 17) {
        alias = alias.substring(0, 15) + "...";
      }

      sb.append("T 17,8,0,5,pt6;").append(unicodeify(alias)).append("\n");
      sb.append("T 17,11,0,5,pt6;").append(unicodeify(name)).append("\n");
      sb.append("A 1").append("\n");
    } catch (UnsupportedEncodingException e) {
      log.error("standard tube barcode label", e);
    }
    return sb.toString();
  }

  public static String minus80TubeBarcodeLabel(String name, String alias, String barcode) {
    StringBuilder sb = new StringBuilder();

    try {
      barcode = new String(Base64.encodeBase64(barcode.getBytes("UTF-8")));

      sb.append("m m").append("\n");
      sb.append("J").append("\n");
      sb.append("S l1;0,0,12,15,38").append("\n");
      sb.append("B 2,6,0,DATAMATRIX,0.21;").append(barcode).append("\n");
      sb.append("B 13,1,0,DATAMATRIX+RECT,0.25;").append(barcode).append("\n");
      sb.append("T 29,2,0,5,pt4;[DATE]").append("\n");

      // shorten alias to fit on label if too long
      if (alias.length() >= 20) {
        alias = alias.substring(0, 18) + "...";
      }

      sb.append("T 13,8,0,5,pt6;").append(unicodeify(alias)).append("\n");
      sb.append("T 13,11,0,5,pt6;").append(unicodeify(name)).append("\n");
      sb.append("A 1").append("\n");
    } catch (UnsupportedEncodingException e) {
      log.error("-80 tube barcode label", e);
    }

    return sb.toString();
  }

  public static String plateBarcodeLabel(String name, String tagBarcode, String barcode) {
    StringBuilder sb = new StringBuilder();
    return sb.toString();
  }
}
