/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import java.util.Collection;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.OptionTag;
import org.springframework.web.servlet.tags.form.OptionsTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import jakarta.servlet.jsp.JspException;

/**
 * Databinding-aware JSP tag that renders an HTML '{@code select}' element.
 *
 * <p>
 * Inner '{@code option}' tags can be rendered using one of the approaches supported by the
 * OptionWriter class.
 *
 * <p>
 * Also supports the use of nested {@link OptionTag OptionTags} or (typically one) nested
 * {@link OptionsTag}.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see OptionTag
 */
@SuppressWarnings("serial")
public class SelectTag extends AbstractHtmlInputElementTag {

  /**
   * The {@link javax.servlet.jsp.PageContext} attribute under which the bound value is exposed to
   * inner {@link OptionTag OptionTags}.
   */
  public static final String LIST_VALUE_PAGE_ATTRIBUTE = "uk.ac.bbsrc.tgac.miso.webapp.util.form.SelectTag.listValue";

  /**
   * The inner text of the extra '{@code option}' tag.
   */
  private String defaultLabel;

  /**
   * The '{@code value}' attribute of the extra '{@code option}' tag.
   */
  private String defaultValue;

  /**
   * The name of the property mapped to the inner text of the '{@code option}' tag.
   */
  private String itemLabel;

  /**
   * The {@link Iterable} of objects used to generate the inner '{@code option}' tags.
   */
  private Object items;

  /**
   * The name of the property mapped to the '{@code value}' attribute of the '{@code option}' tag.
   */
  private String itemValue;

  /**
   * Indicates whether or not the '{@code select}' tag allows multiple-selections.
   */
  private boolean multiple;

  /**
   * The value of the HTML '{@code size}' attribute rendered on the final '{@code select}' element.
   */
  private String size;

  /**
   * The {@link TagWriter} instance that the output is being written.
   * <p>
   * Only used in conjunction with nested {@link OptionTag OptionTags}.
   */
  private TagWriter tagWriter;

  /**
   * Closes any block tag that might have been opened when using nested {@link OptionTag options}.
   */
  @Override
  public int doEndTag() throws JspException {
    if (this.tagWriter != null) {
      this.tagWriter.endTag();
      writeHiddenTagIfNecessary(tagWriter);
    }
    return EVAL_PAGE;
  }

  /**
   * Clears the {@link TagWriter} that might have been left over when using nested {@link OptionTag
   * options}.
   */
  @Override
  public void doFinally() {
    super.doFinally();
    this.tagWriter = null;
    this.pageContext.removeAttribute(LIST_VALUE_PAGE_ATTRIBUTE);
  }

  public String getDefaultLabel() {
    return defaultLabel;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * Get the value of the '{@code itemLabel}' attribute.
   * <p>
   * May be a runtime expression.
   */
  protected String getItemLabel() {
    return this.itemLabel;
  }

  /**
   * Get the value of the '{@code items}' attribute.
   * <p>
   * May be a runtime expression.
   */
  protected Object getItems() {
    return this.items;
  }

  /**
   * Get the value of the '{@code itemValue}' attribute.
   * <p>
   * May be a runtime expression.
   */
  protected String getItemValue() {
    return this.itemValue;
  }

  /**
   * Get the value of the HTML '{@code multiple}' attribute rendered on the final '{@code select}'
   * element.
   */
  protected boolean getMultiple() {
    return this.multiple;
  }

  /**
   * Get the value of the '{@code size}' attribute.
   */
  protected String getSize() {
    return this.size;
  }

  public void setDefaultLabel(String defaultLabel) {
    this.defaultLabel = defaultLabel;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * Set the name of the property mapped to the label (inner text) of the '{@code option}' tag.
   * <p>
   * May be a runtime expression.
   */
  public void setItemLabel(String itemLabel) {
    this.itemLabel = itemLabel;
  }

  /**
   * Set the {@link Iterable} of objects used to generate the inner '{@code option}' tags.
   * 
   * @param items the items that comprise the options of this selection
   */
  public void setItems(Object items) {
    this.items = items;
  }

  /**
   * Set the name of the property mapped to the '{@code value}' attribute of the '{@code option}' tag.
   * <p>
   * Required when wishing to render '{@code option}' tags from an array or {@link Collection}.
   * <p>
   * May be a runtime expression.
   */
  public void setItemValue(String itemValue) {
    this.itemValue = itemValue;
  }

  /**
   * Set the value of the HTML '{@code multiple}' attribute rendered on the final '{@code select}'
   * element.
   */
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }

  /**
   * Set the value of the HTML '{@code size}' attribute rendered on the final '{@code select}'
   * element.
   */
  public void setSize(String size) {
    this.size = size;
  }

  /**
   * If using a multi-select, a hidden element is needed to make sure all items are correctly
   * unselected on the server-side in response to a {@code null} post.
   */
  private void writeHiddenTagIfNecessary(TagWriter tagWriter) throws JspException {
    if (getMultiple()) {
      tagWriter.startTag("input");
      tagWriter.writeAttribute("type", "hidden");
      String name = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + getName();
      tagWriter.writeAttribute("name", name);
      tagWriter.writeAttribute("value", processFieldValue(name, "1", "hidden"));
      tagWriter.endTag();
    }
  }

  /**
   * Renders the HTML '{@code select}' tag to the supplied {@link TagWriter}.
   * <p>
   * Renders nested '{@code option}' tags if the {@link #setItems items} property is set, otherwise
   * exposes the bound value for the nested {@link OptionTag OptionTags}.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected int writeTagContent(TagWriter tagWriter) throws JspException {
    tagWriter.startTag("select");
    writeDefaultAttributes(tagWriter);
    if (getMultiple()) {
      tagWriter.writeAttribute("multiple", "multiple");
    }
    tagWriter.writeOptionalAttributeValue("size", getDisplayString(evaluate("size", getSize())));

    if (defaultLabel != null && defaultValue != null) {
      tagWriter.startTag("option");
      tagWriter.writeAttribute("value", getDisplayString(evaluate("defaultValue", getDefaultValue())));

      tagWriter.appendValue(getDisplayString(evaluate("defaultLabel", getDefaultLabel())));
      tagWriter.endTag();
    }

    String valueProperty = ObjectUtils.getDisplayString(evaluate("itemValue", getItemValue()));
    String labelProperty = ObjectUtils.getDisplayString(evaluate("itemLabel", getItemLabel()));

    Object pathValue = getBindStatus().getActualValue();
    Object comparisonValue = pathValue == null ? null
        : PropertyAccessorFactory.forBeanPropertyAccess(pathValue).getPropertyValue(valueProperty);

    for (Object item : (Iterable<? extends Object>) evaluate("items", getItems())) {
      tagWriter.startTag("option");
      Object renderValue = PropertyAccessorFactory.forBeanPropertyAccess(item).getPropertyValue(valueProperty);
      Object renderLabel = PropertyAccessorFactory.forBeanPropertyAccess(item).getPropertyValue(labelProperty);

      // allows render values to handle some strange browser compat issues.
      tagWriter.writeAttribute("value", getDisplayString(renderValue));

      if (comparisonValue != null && comparisonValue.equals(renderValue)) {
        tagWriter.writeAttribute("selected", "selected");
      }
      tagWriter.appendValue(getDisplayString(renderLabel));
      tagWriter.endTag();
    }
    tagWriter.endTag(true);
    writeHiddenTagIfNecessary(tagWriter);
    return SKIP_BODY;
  }

}
