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
  Date: 16-Feb-2010
  Time: 08:51:03
--%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
    <div id="contentcolumn">
        <h1>
            <div id="totalCount">
            </div></h1>

        <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
        </form>
        <br/>
        <table class="list" id="table">
            <thead>
            <tr>
                <th>Library Name</th>
                <th>Library Alias</th>
                <th>Library Type</th>
                <th>Sample Name</th>
                <th>QC Passed</th>
                <th class="fit">Edit</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${libraries}" var="library">
                <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                    <td><b>${library.name}</b></td>
                    <td>${library.alias}</td>
                    <td>${library.libraryType.description}</td>
                    <td>${library.sample.name}</td>
                    <td>${library.qcPassed}</td>
                    <td class="misoicon"
                        onclick="window.location.href='<c:url value="/miso/library/${library.libraryId}"/>'"><span class="ui-icon ui-icon-pencil"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <script type="text/javascript">
            jQuery(document).ready(function() {
                writeTotalNo();
                jQuery("#table").tablesorter({
                    headers: {
                        5: {
                            sorter: false
                        }
                    }
                });
            });

            jQuery(function() {
                var theTable = jQuery("#table");

                jQuery("#filter").keyup(function() {
                    jQuery.uiTableFilter(theTable, this.value);
                    writeTotalNo();
                });

                jQuery('#filter-form').submit(function() {
                    theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
                    return false;
                }).focus(); //Give focus to input field
            });

            function writeTotalNo() {
                jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Libraries");
            }
        </script>
    </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>