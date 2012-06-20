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

  public static String standardTubeBarcodeLabel(String name, String alias, String barcode) {
    StringBuilder sb = new StringBuilder();

    sb.append("m m").append("\n");
    sb.append("J").append("\n");
    sb.append("S l1;0,0,12,15,38").append("\n");
    sb.append("B 3,2,0,DATAMATRIX,0.21;").append(barcode).append("\n");
    sb.append("B 17,1,0,DATAMATRIX+RECT,0.25;").append(barcode).append("\n");
    sb.append("T 29,2,0,5,pt4;[DATE]").append("\n");

    //shorten alias to fit on label if too long
    if (alias.length() >= 17) {
      alias = alias.substring(0, 15) + "...";
    }

    sb.append("T 17,8,0,5,pt6;").append(alias).append("\n");
    sb.append("T 17,11,0,5,pt6;").append(name).append("\n");
    sb.append("A 1").append("\n");

    return sb.toString();
  }

  public static String minus80TubeBarcodeLabel(String name, String alias, String barcode) {
    StringBuilder sb = new StringBuilder();

    sb.append("m m").append("\n");
    sb.append("J").append("\n");
    sb.append("S l1;0,0,12,15,38").append("\n");
    sb.append("B 2,6,0,DATAMATRIX,0.21;").append(barcode).append("\n");
    sb.append("B 13,1,0,DATAMATRIX+RECT,0.25;").append(barcode).append("\n");
    sb.append("T 29,2,0,5,pt4;[DATE]").append("\n");

    //shorten alias to fit on label if too long
    if (alias.length() >= 20) {
      alias = alias.substring(0, 18) + "...";
    }

    sb.append("T 13,8,0,5,pt6;").append(alias).append("\n");
    sb.append("T 13,11,0,5,pt6;").append(name).append("\n");
    sb.append("A 1").append("\n");

    return sb.toString();
  }

  public static String plateBarcodeLabel(String name, String tagBarcode, String barcode) {
    StringBuilder sb = new StringBuilder();
    /*
    sb.append("m m").append("\n");
    sb.append("J").append("\n");
    sb.append("S l1;0,0,12,15,38").append("\n");
    sb.append("B 3,2,0,DATAMATRIX,0.2;").append(barcode).append("\n");
    sb.append("B 17,1,0,DATAMATRIX+RECT,0.25;").append(barcode).append("\n");
    sb.append("T 29,2,0,5,pt4;[DATE]").append("\n");

    //shorten alias to fit on label if too long
    if (tagBarcode.length() >= 17) {
      tagBarcode = tagBarcode.substring(0, 15) + "...";
    }

    sb.append("T 17,8,0,5,pt6;").append(tagBarcode).append("\n");
    sb.append("T 17,11,0,5,pt6;").append(name).append("\n");
    sb.append("A 1").append("\n");
    */
    return sb.toString();
  }
}
