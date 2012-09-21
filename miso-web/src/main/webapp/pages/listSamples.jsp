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
        <a href='<c:url value="/miso/sample/new"/>' class="add">Add Sample</a>

        <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
        </form>
        <br/>
        <table class="list" id="table">
            <thead>
            <tr>
                <th>Sample Name</th>
                <th>Sample Alias</th>
                <th>Type</th>
                <th>QC Passed</th>
                <th class="fit">Edit</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${samples}" var="sample">
                <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                    <td><b>${sample.name}</b></td>
                    <td>${sample.alias}</td>
                    <td>${sample.sampleType}</td>
                    <td>${sample.qcPassed}</td>
                    <td class="misoicon"
                        onclick="window.location.href='<c:url value="/miso/sample/${sample.id}"/>'"><span class="ui-icon ui-icon-pencil"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <script type="text/javascript">
            jQuery(document).ready(function() {
                writeTotalNo();
                jQuery("#table").tablesorter({
                    headers: {
                        4: {
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
                jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Samples");
            }
        </script>
    </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
