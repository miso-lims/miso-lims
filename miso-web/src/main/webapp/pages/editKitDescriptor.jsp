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
    <form:form action="/miso/kitdescriptor" method="POST" commandName="kitDescriptor" autocomplete="off">

      <sessionConversation:insertSessionConversationId attributeName="kitDescriptor"/>

      <h1><c:choose><c:when
          test="${kitDescriptor.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
        Kit Descriptor
        <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
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
          <td>
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
          <td><form:input path="version"/></td>
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
          <td><form:input path="stockLevel"/></td>
        </tr>
        <tr>
          <td class="h">Description:</td>
          <td><form:input path="description"/><span id="descriptionCounter" class="counter"></span></td>
        </tr>
        <tr>
          <c:choose>
            <c:when test="${kitDescriptor.id == 0 or empty kitDescriptor.kitType}">
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
            <c:when test="${kitDescriptor.id == 0 or empty kitDescriptor.platformType}">
              <td>Platform Type:</td>
              <td>
                <form:select id="platformTypes" path="platformType">
                  <form:options items="${platformTypes}" itemValue="key" itemLabel="key"/>
                </form:select>
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
    <c:if test="${not empty kitDescriptor.changeLog}">
      <br/>
      <h1>Changes</h1>
      <div style="clear:both">
        <table class="list" id="changelog_table">
          <thead>
          <tr>
            <th>Editor</th>
            <th>Summary</th>
            <th>Time</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${kitDescriptor.changeLog}" var="change">
            <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td>${change.user.fullName} (${change.user.loginName})</td>
              <td><b>${change.summary}</b></td>
              <td>${change.time}</td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
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
