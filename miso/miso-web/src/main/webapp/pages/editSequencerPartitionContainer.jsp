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

<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 12/01/12
  Time: 12:07
 --%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">
<form:form id="container-form" data-parsley-validate="" action="/miso/container" method="POST" commandName="container" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="container"/>
  <h1>
    <c:choose>
      <c:when test="${container.id != 0}">Edit</c:when>
      <c:otherwise>Create</c:otherwise>
    </c:choose> ${container.platform.platformType.containerName}
    <button type="button" class="fg-button ui-state-default ui-corner-all"
          onclick="return Container.validateContainer();">Save</button>
  </h1>
  <div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
    <c:if test="${container.id != 0 && not empty container.identificationBarcode}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('container', [${container.id}]);">Print Barcode</span></c:if>
  </div>

  <div class="bs-callout bs-callout-warning hidden">
    <h2>Oh snap!</h2>
    <p>This form seems to be invalid!</p>
  </div>

  <table class="in">
    <tr>
      <td class="h">Container ID:</td>
      <td>
        <c:choose>
          <c:when test="${container.id != 0}"><input type='hidden' id='containerId' name='id' value='${container.id}'/>${container.id}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Serial Number:</td>
      <td><form:input path="identificationBarcode"/> </td>
    </tr>

    <tr>
      <td>Platform:</td>
      <td>${container.platform.platformType.key}</td>
    </tr>
    <tr>
      <td>Sequencer Model:</td>
      <td>${container.platform.instrumentModel}</td>
    </tr>
    <tr>
      <td>Clustering Kit:</td>
      <td><miso:select id="clusteringKit" path="clusteringKit" items="${clusteringKits}" itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="" /></td>
    </tr>
    <tr>
      <td>Multiplexing Kit:</td>
      <td><miso:select id="multiplexingKit" path="multiplexingKit" items="${multiplexingKits}" itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="" /></td>
    </tr>
  </table>
</form:form>

  <c:if test="${container.id != 0}">
    <miso:list-section id="list_partition" name="${container.platform.platformType.partitionName}" target="partition" items="${containerPartitions}" config="{ 'platformType' : '${container.platform.platformType.name()}', 'showContainer' : false }"/>
  </c:if>
  <miso:list-section id="list_run" name="Runs" target="run" items="${containerRuns}"/>
  <miso:changelog item="${container}"/>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
