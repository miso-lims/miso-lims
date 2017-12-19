<%@ include file="../header.jsp" %>

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

<div id="maincontent">
  <div id="contentcolumn">
    <h1>My Account</h1>

    <div class="portlet">
      <div class="portlet-header">My Account</div>
      <div class="portlet-content">
        User: ${userRealName} <a href="<c:url value='/miso/user/${userId}'/>">Edit</a><br/>
        Groups: ${userGroups}<br/><br/>
        API Key: ${apiKey}
      </div>
    </div>

    <div class="column">
      <sec:authorize access="hasRole('ROLE_ADMIN')">
        <div class="portlet">
          <div class="portlet-header">Administration</div>
          <div class="portlet-content">
            <a href="javascript:void(0);" onclick="Admin.clearCache();">Clear Cache</a><br/>
            <c:if test="${autoGenerateIdBarcodes}">
              <a href="javascript:void(0);" onclick="Admin.regenBarcodes();">Regenerate All Barcodes</a><br/>
            </c:if>
          </div>
        </div>
      </sec:authorize>
    </div>
  </div>
</div>

<script type="text/javascript">
  jQuery(function () {
    jQuery(".column").sortable({
      connectWith: '.column',
      handle: '.portlet-header'
    });

    jQuery(".portlet").addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
        .find(".portlet-header")
        .addClass("ui-widget-header ui-corner-all")
        .prepend('<span class="ui-icon ui-icon-minusthick"></span>')
        .end()
        .find(".portlet-content");

    jQuery(".portlet-header .ui-icon").click(function () {
      jQuery(this).toggleClass("ui-icon-minusthick").toggleClass("ui-icon-plusthick");
      jQuery(this).parents(".portlet:first").find(".portlet-content").toggle();
    });

    jQuery(".column").disableSelection();
  });

</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
