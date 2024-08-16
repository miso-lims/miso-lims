package uk.ac.bbsrc.tgac.miso.webapp.util;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;

import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import jakarta.servlet.jsp.JspException;

/**
 * This class handles setting a variable as a request attribute and creating a hidden input field.
 * 
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
   * @see
   * org.springframework.web.servlet.tags.form.AbstractFormTag#writeTagContent(org.springframework.web
   * .servlet.tags.form.TagWriter)
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
   * @param attributeName the attributeName to set
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
   * @param createHiddenInput the createHiddenInput to set
   */
  public void setCreateHiddenInput(boolean createHiddenInput) {
    this.createHiddenInput = createHiddenInput;
  }
}
