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
    <form:form id="kitdescriptor-form" action="/miso/kitdescriptor" method="POST" commandName="kitDescriptor" autocomplete="off">

      <sessionConversation:insertSessionConversationId attributeName="kitDescriptor"/>

      <h1><c:choose><c:when
          test="${kitDescriptor.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
        Kit Descriptor
        <c:if test="${isUserAdmin}">
          <button id="save" type="submit" class="fg-button ui-state-default ui-corner-all"
          onclick="return KitDescriptor.validateKitDescriptor();">Save</button>
        </c:if>
      </h1>
      <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
        <div id="note_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notediv" class="note" style="display:none;">Kit Descriptor contains information about Consumable
      </div>
      <h2>Kit Descriptor Information</h2>
      <table class="in">
        <tr>
          <td class="h">ID:</td>
          <td id="kitDescriptorId">
            <c:choose>
              <c:when test="${kitDescriptor.id != 0}">${kitDescriptor.id}</c:when>
              <c:otherwise><i>Unsaved</i></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Name:</td>
          <td><form:input id="name" path="name"/><span id="nameCounter" class="counter"></span></td>
        </tr>
        <tr>
          <td class="h">Version:</td>
          <td><form:input id="version" path="version"/></td>
        </tr>
        <tr>
          <td class="h">Manufacturer:</td>
          <td><form:input id="manufacturer" path="manufacturer"/><span id="manufacturerCounter" class="counter"></span></td>
        </tr>
        <tr>
          <td class="h">Part Number:</td>
          <td><form:input id="partNumber" path="partNumber"/><span id="partNumberCounter" class="counter"></span></td>
        </tr>
        <tr>
          <td class="h">Stock Level:</td>
          <td><form:input id="stockLevel" path="stockLevel"/></td>
        </tr>
        <tr>
          <td class="h">Description:</td>
          <td><form:input id="description" path="description"/><span id="descriptionCounter" class="counter"></span></td>
        </tr>
        <tr>
          <td>Kit Type:</td>
          <c:choose>
            <c:when test="${kitDescriptor.id == 0 or empty kitDescriptor.kitType}">
              <td>
                <form:select id="kitTypes" path="kitType" items="${kitTypes}"/>
              </td>
            </c:when>
            <c:otherwise>
              <td id="kitTypes">${kitDescriptor.kitType}</td>
            </c:otherwise>
          </c:choose>
        </tr>
        <tr>
          <td>Platform Type:</td>
          <c:choose>
            <c:when test="${kitDescriptor.id == 0 or empty kitDescriptor.platformType}">
              <td>
                <form:select id="platformTypes" path="platformType">
                  <form:options items="${platformTypes}" itemValue="key" itemLabel="key"/>
                </form:select>
              </td>
            </c:when>
            <c:otherwise>
              <td id="platformTypes">${kitDescriptor.platformType}</td>
            </c:otherwise>
          </c:choose>
        </tr>
      </table>
    </form:form>
    <c:if test="${kitDescriptor.id != 0}">
      <c:if test="${kitDescriptor.kitType.key == 'Library'}">
        <miso:list-section id="list_associated_ts" name="Associated Targeted Sequencings" target="targetedsequencing" alwaysShow="true" items="${associatedTargetedSequencings}" config="{kitDescriptorId: ${kitDescriptor.id}}" />
        <miso:list-section-ajax id="list_available_ts" name="Available Targeted Sequencings" target="targetedsequencing" config="{kitDescriptorId: ${kitDescriptor.id}, add: true}" />
      </c:if>
      <miso:changelog item="${kitDescriptor}"/>
    </c:if>
  </div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#name').simplyCountable({
      counter: '#nameCounter',
      countType: 'characters',
      maxCount: ${maxLengths['name']},
      countDirection: 'down'
    });

    jQuery('#manufacturer').simplyCountable({
      counter: '#manufacturerCounter',
      countType: 'characters',
      maxCount: ${maxLengths['manufacturer']},
      countDirection: 'down'
    });
    
    jQuery('#partNumber').simplyCountable({
      counter: '#partNumberCounter',
      countType: 'characters',
      maxCount: ${maxLengths['partNumber']},
      countDirection: 'down'
    });
    jQuery('#description').simplyCountable({
      counter: '#descriptionCounter',
      countType: 'characters',
      maxCount: ${maxLengths['description']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
