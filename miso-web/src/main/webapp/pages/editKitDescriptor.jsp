<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
    <form:form action="/miso/kitdescriptor" method="POST" commandName="kitDescriptor" autocomplete="off">

      <sessionConversation:insertSessionConversationId attributeName="kitDescriptor"/>
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              <c:choose>
                <c:when test="${kitDescriptor.kitDescriptorId != 0}">Edit</c:when>
                <c:otherwise>Create</c:otherwise>
              </c:choose> Kit Descriptor
            </span>
         </div>
         <div class="navbar-right container-fluid">
            <button type="submit" class="btn btn-default navbar-btn">Save</button>
         </div>
      </nav>
      <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
        <div id="note_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notediv" class="note" style="display:none;">Kit Descriptor contains information about Consumable
      </div>
      <h2>Kit Descriptor Information</h2>
      <table class="in">
        <tr>
          <td class="h">ID:</td>
          <td>
            <c:choose>
              <c:when test="${kitDescriptor.kitDescriptorId != 0}">${kitDescriptor.kitDescriptorId}</c:when>
              <c:otherwise><i>Unsaved</i></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Name:</td>
          <td><form:input path="name" class="form-control"/></td>
        </tr>
        <tr>
          <td class="h">Version:</td>
          <td><form:input path="version" class="form-control"/></td>
        </tr>
        <tr>
          <td class="h">Manufacturer:</td>
          <td><form:input path="manufacturer" class="form-control"/></td>
        </tr>
        <tr>
          <td class="h">Part Number:</td>
          <td><form:input path="partNumber" class="form-control"/></td>
        </tr>
        <tr>
          <td class="h">Stock Level:</td>
          <td><form:input path="stockLevel" class="form-control"/></td>
        </tr>
        <tr>
          <c:choose>
            <c:when test="${kitDescriptor.kitDescriptorId == 0 or empty kitDescriptor.kitType}">
              <td>Kit Type:</td>
              <td>
                <form:select id="kitTypes" path="kitType" items="${kitTypes}"/>
              </td>
            </c:when>
            <c:otherwise>
              <td>Kit Type</td>
              <td>${kitDescriptor.kitType}</td>
            </c:otherwise>
          </c:choose>
        </tr>
        <tr>
          <c:choose>
            <c:when test="${kitDescriptor.kitDescriptorId == 0 or empty kitDescriptor.platformType}">
              <td>Platform Type:</td>
              <td>
                <form:select id="platformTypes" path="platformType" items="${platformTypes}"/>
              </td>
            </c:when>
            <c:otherwise>
              <td>Platform Type</td>
              <td>${kitDescriptor.platformType}</td>
            </c:otherwise>
          </c:choose>
        </tr>
      </table>
    </form:form>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>