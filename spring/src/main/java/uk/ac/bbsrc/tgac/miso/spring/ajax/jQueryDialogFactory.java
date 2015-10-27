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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class jQueryDialogFactory {
  public static String okDialog(String title, String message) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div id='dialog-message' title='" + title + "'>");
    sb.append("<p><span class='ui-icon ui-icon-circle-check' style='float:left; margin:0 7px 50px 0;'></span>");
    sb.append(message);
    sb.append("</p></div>");
    return sb.toString();
  }

  public static String errorDialog(String title, String message) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div id='dialog-message' title='" + title + "'>");
    sb.append("<p><span class='ui-icon ui-icon-alert' style='float:left; margin:0 7px 50px 0;'></span>");
    sb.append(message);
    sb.append("</p></div>");
    return sb.toString();
  }
}
