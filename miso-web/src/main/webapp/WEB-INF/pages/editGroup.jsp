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

    <h1><c:choose><c:when test="${group.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Group
      <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
    </h1>

    <form:form id="groupForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        FormUtils.createForm('groupForm', 'save', ${groupDto}, 'group', {});
        Utils.ui.updateHelpLink(FormTarget.group.getUserManualUrl());
      });
    </script>
    
    <c:if test="${group.id != 0}">
      <miso:list-section id="list_included" name="Included Users" target="user" alwaysShow="true" items="${includedUsers}" config="{groupId: ${group.id}, isAdmin: ${miso:isAdmin()}, listMode: 'included'}"/>
      <miso:list-section id="list_available" name="Available Users" target="user" alwaysShow="true" items="${availableUsers}" config="{groupId: ${group.id}, isAdmin: ${miso:isAdmin()}, listMode: 'available'}"/>
    </c:if>
    
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>