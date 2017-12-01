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
<%@ page import="uk.ac.bbsrc.tgac.miso.Version" %>

</div>
<div id="footer">
    <br/>

    <p>
        &copy; 2010 - <fmt:formatDate value="${timestamp}" pattern="yyyy"/>
        Brought to you by <a href="http://www.tgac.ac.uk/" target="_blank">The Genome Analysis Centre</a>,
        <a href="http://oicr.on.ca/" target="_blank">The Ontario Institute for Cancer Research</a>,
        and the element <a href="http://en.wikipedia.org/wiki/Sodium" target="_blank">sodium</a> | Version:
        <%=Version.VERSION%>
    </p>
</div>
</body>
</html>
