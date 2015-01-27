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
<script type="text/javascript" src="<c:url value='/scripts/run_ajax.js?ts=${timestamp.time}'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/task_ajax.js?ts=${timestamp.time}'/>"></script>
<div id="maincontent">
<div id="contentcolumn">

<nav class="navbar navbar-default" role="navigation">
   <div class="navbar-header">
      <span class="navbar-brand navbar-center">
        New Analysis Task
      </span>
   </div>
   <div class="navbar-right container-fluid">
      <button type="button" class="btn btn-default navbar-btn" value="Submit Task" onclick="Tasks.job.submitAnalysisTask();">Submit Task</button>
   </div>
</nav>

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