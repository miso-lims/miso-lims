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
public class PaneAjaxTag extends RequestContextAwareTag {

  private String target;

  @Override
  protected int doStartTagInternal() throws Exception {
    pageContext.getOut().append(String.format(
        "<div id='%1$s'></div><script type='text/javascript'>jQuery(document).ready(function () { PaneTarget.%2$s.createPane('%1$s');});</script>",
        getId(), target));
    return SKIP_BODY;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }
}
