/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

@SuppressWarnings("serial")
public class AjaxListSectionTag extends RequestContextAwareTag {
  private String config;

  private String name;

  private String target;

  @Override
  protected int doStartTagInternal() throws Exception {
    pageContext.getOut().append(String.format(
        "<br/><h1>%2$s</h1><table id='%1$s' class='display no-border ui-widget-content'></table><script type='text/javascript'>jQuery(document).ready(function () { ListUtils.createTable('%1$s', ListTarget.%3$s, null, %4$s);});</script>",
        getId(), name, target, config));
    return SKIP_BODY;
  }

  public String getConfig() {
    return config;
  }

  public String getName() {
    return name;
  }

  public String getTarget() {
    return target;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTarget(String target) {
    this.target = target;
  }
}
