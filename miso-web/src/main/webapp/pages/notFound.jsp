<%@ include file="/header.jsp" %>

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

<p><a href="javascript:void(0);" onclick="window.history.back();">Return to Previous Page</a></p>
  
<script type="text/javascript">
  const p = document.createElement('P');
  const strong = document.createElement('STRONG');
  const notFound = document.createTextNode('The page you requested was not found.');
  strong.appendChild(notFound);
  p.appendChild(strong);
  document.getElementById('flasherror').parentNode.insertBefore(p, document.getElementById('flasherror'));
</script>

<%@ include file="/footer.jsp" %>