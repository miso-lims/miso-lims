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

<p class="errorTitle">${genericMessage}</p>
<div id="flasherror" class="flasherror">${specificMessage}</div>
<br/>

<c:if test="${showBugUrl and not empty misoBugUrl}">
  <p><a href="${misoBugUrl}">Report Bug</a></p>
</c:if>

<p><a href="javascript:void(0);" onclick="window.history.back();">Return to Previous Page</a></p>

<%@ include file="../footer.jsp" %>
