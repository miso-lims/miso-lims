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

<h1>
  New Analysis Task
  <input id="submitTaskButton" type="button" value="Submit Task" class="fg-button ui-state-default ui-corner-all" onclick="Tasks.job.submitAnalysisTask();"/>
</h1>

<c:if test="${not empty run}">
  Selected run: <b>${run.alias}</b><br/>
  <c:forEach items="${defaultRunValues}" var="drv">
    <input type="hidden" name="default-${drv.key}" value="${drv.value}"/>
  </c:forEach>
</c:if>

<form:form  action="/miso/analysis/task" id="taskForm" method="POST" autocomplete="off">
  <c:if test="${not empty run}">
    <input type="hidden" name="sample-sheet-string" value=""/>
  </c:if>

  <div id="pipelines" style="padding-top:6px">
    Pipeline: <select name='pipeline' id='pipeline' onchange='Tasks.ui.selectPipeline(this<c:if test="${not empty run}">, ${run.id}</c:if>);'>
      <option value='' selected='selected'>Choose a pipeline...</option>
      <c:forEach items="${pipelines}" var="p" varStatus="n">
        <option value='${p}'>${p}</option>
      </c:forEach>
    </select>
  </div>
  <hr>
  <div id="pipelineDetails"></div>
</form:form>

</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>