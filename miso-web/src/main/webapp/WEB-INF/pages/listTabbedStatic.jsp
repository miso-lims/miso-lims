<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <h1 id="tableTitle"></h1>
    <div id="headerMessages"></div>
    ${note}
    <div id="tabs">
      <ul>
        <c:forEach items="${tabs}" var="tab" varStatus="c">
          <li><a href="#tab-${c.count}"><span>${tab.key}</span></a></li>
        </c:forEach>
      </ul>
      <c:forEach items="${tabs}" var="tab" varStatus="c">
        <div id="tab-${c.count}">
          <h1>${tab.key}</h1>
          <table class="display no-border" id="list${c.count}"></table>
          <script type="text/javascript">
            jQuery(document).ready(function () {
              var config = ${config};
              config.${property} = ${tab.value};
              ListUtils.createStaticTable("list${c.count}", ${targetType}, config, ${data[tab.key]});
            });
          </script>
        </div>
      </c:forEach>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          var target = ${targetType};
          <c:choose>
            <c:when test="${empty customTitle}">
              document.getElementById('tableTitle').innerText = target.name;
            </c:when>
            <c:otherwise>
              document.getElementById('tableTitle').innerText = '${customTitle}';
            </c:otherwise>
          </c:choose>
          var storageKey = target.name + "_tab_${property}";
          jQuery("#tabs").tabs({
            active : parseInt(window.localStorage.getItem(storageKey) || 0),
            activate: function(event, ui) {
              window.localStorage.setItem(storageKey, ui.newTab.index());
            }
          });
          if (target.hasOwnProperty('getUserManualUrl')) {
            Utils.ui.updateHelpLink(target.getUserManualUrl());
          }
        });
      </script>
    </div>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
