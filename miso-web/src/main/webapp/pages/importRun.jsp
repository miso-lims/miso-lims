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

<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 15-Feb-2010
  Time: 15:09:13

--%>
<%@ include file="../header.jsp" %>

<h1>Import Run</h1>
<form>
<input type="hidden" value="${experiment.experimentId}" name="experiment" id="experiment"/>

<%-- <form method="POST" action="<c:url value='/miso/run/import/${experiment.experimentId}/process'/>"> --%>
    <table class="in">
        <tr>
            <td>Choose Run Type:</td>
            <td>
                <c:forEach items="${platformtypes}" var="platformtype" varStatus="status">
                    <input type="radio" value="${platformtype}" name="platformType" id="platformtypes${status.count}"/>${platformtype}
                </c:forEach>

                <%-- <form:radiobuttons id="runtypes" path="platformType" items="${runtypes}"/> --%>
            </td>
        </tr>
        <tr>
            <td>Run Directory</td>
            <td><input type="text" id="runPath" name="runPath" width="100%"/></td>
            <%-- <td><input type="submit"/></td> --%>
        </tr>
        <tr>
            <td></td>
            <td>Please enter the directory path on the cluster where the run is stored:<br/>
            e.g. <b>100216_N73018_0002_desc</b>
            </td>
        </tr>
    </table>

<a href="javascript:void(0);" onclick="previewRunImport('platformtypes1', $('runPath').value, $('experiment').value);">Get Run Information</a>
</form><br/><br/>

<div id="runinfo"></div>

<%@ include file="../footer.jsp" %>