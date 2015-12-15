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

package uk.ac.bbsrc.tgac.miso.webapp.util;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

/**
 * This class handles setting a variable as a request attribute and creating
 * a hidden input field.
 * @author MJones
 * @version Aug 31, 2010
 *
 */

/**
 * uk.ac.bbsrc.tgac.miso.webapp.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 09/07/12
 * @since 0.1.6
 */
public class SessionConversationIdTag extends AbstractHtmlElementTag {

  /**
   *
   */
  private static final long serialVersionUID = -421868972235483510L;
  private String attributeName;
  private boolean createHiddenInput = true;

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.tags.form.AbstractFormTag#writeTagContent(org.springframework.web.servlet.tags.form.TagWriter)
   */
  @Override
  protected int writeTagContent(TagWriter tagWriter) throws JspException {

    // first try to pull value from request attribute.
    String conversationId = (String) pageContext.getRequest().getAttribute(attributeName + "_cId");

    // if no value was found then try to pull value as request parameter.
    if (isStringBlankOrNull(conversationId)) {
      conversationId = pageContext.getRequest().getParameter(attributeName + "_cId");
    }

    // if a conversation Id was found then process it.
    if (!isStringBlankOrNull(conversationId)) {

      // set the request attribute.
      pageContext.getRequest().setAttribute("curr_" + attributeName + "_cId", conversationId);

      if (createHiddenInput) {
        // now create the hidden input field.
        tagWriter.startTag("input");
        tagWriter.writeAttribute("type", "hidden");
        tagWriter.writeAttribute("name", attributeName + "_cId");
        tagWriter.writeAttribute("value", conversationId);
        tagWriter.endTag();
      }
    }

    return EVAL_PAGE;
  }

  /**
   * @return the attributeName
   */
  public String getAttributeName() {
    return attributeName;
  }

  /**
   * @param attributeName
   *          the attributeName to set
   */
  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  /**
   * @return the createHiddenInput
   */
  public boolean isCreateHiddenInput() {
    return createHiddenInput;
  }

  /**
   * @param createHiddenInput
   *          the createHiddenInput to set
   */
  public void setCreateHiddenInput(boolean createHiddenInput) {
    this.createHiddenInput = createHiddenInput;
  }
}
