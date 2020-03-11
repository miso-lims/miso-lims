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
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">

    <h1><c:choose><c:when test="${kitDescriptor.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
      Kit Descriptor
      <c:if test="${miso:isAdmin()}">
        <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
      </c:if>
    </h1>
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
      <div id="note_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="notediv" class="note" style="display:none;">Kit Descriptor contains information about Consumable
    </div>
    
    <form:form id="kitDescriptorForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        FormUtils.createForm('kitDescriptorForm', 'save', ${kitDescriptorDto}, 'kitdescriptor', {
          isAdmin: ${miso:isAdmin()},
          kitTypes: ${kitTypes}
        });
        Utils.ui.updateHelpLink(FormTarget.kitdescriptor.getUserManualUrl());
      });
    </script>

    <c:if test="${kitDescriptor.id != 0}">
      <c:if test="${kitDescriptor.kitType.key == 'Library'}">
        <miso:list-section id="list_associated_ts" name="Associated Targeted Sequencings" target="targetedsequencing" alwaysShow="true" items="${associatedTargetedSequencings}" config="{isAdmin: ${miso:isAdmin()}, kitDescriptorId: ${kitDescriptor.id}}" />
        <miso:list-section-ajax id="list_available_ts" name="Available Targeted Sequencings" target="targetedsequencing" config="{isAdmin: ${miso:isAdmin()}, kitDescriptorId: ${kitDescriptor.id}, add: true}" />
      </c:if>
      <miso:changelog item="${kitDescriptor}"/>
    </c:if>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
