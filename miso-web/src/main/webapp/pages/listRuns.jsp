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
<script type="text/javascript" src="<c:url value='/scripts/runCalendar.js?ts=${timestamp.time}'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.popup.js'/>"></script>


<div id="maincontent">
    <div id="contentcolumn">
        <h1>
            <div id="totalCount">
            </div>
        </h1>
        <div id="traftrigger" onclick="window.location.href='flexreports#tab-3'"
             class="ui-corner-all">
            Run Calendar
        </div>

        <%--<div style='display:none'>--%>
            <%--<div id="trafpanel">--%>

                <%--<div id="trafresultgraph">--%>
                    <%--<div id="statusmenu" class="menuD3Calendar">--%>
                        <%--<ul><b> Machines </b></ul>--%>
                    <%--</div>--%>
                    <%--<div id="yearmenu" class="menuD3Calendar">--%>
                        <%--<ul><b> Year </b></ul>--%>
                    <%--</div>--%>
                    <%--<div id="legendD3Calendar">--%>
                        <%--<fieldset style="text-align:right;">--%>
                            <%--<legend>--%>
                                <%--<b>Legend</b>--%>
                            <%--</legend>--%>
                            <%--HWI-ST319--%>
                            <%--<div style="position:absolute;top:20px;left:10px;width:100px;height:10px;border:1px solid #000;background:green;"></div>--%>
                            <%--<br/>--%>
                            <%--N73018--%>
                            <%--<div style="position:absolute;top:37px;left:10px;width:100px;height:10px;border:1px solid #000;background:red;"></div>--%>
                            <%--<br/>--%>
                            <%--HWI-ST790--%>
                            <%--<div style="position:absolute;top:54px;left:10px;width:100px;height:10px;border:1px solid #000;background:blue;"></div>--%>
                            <%--<br/>--%>
                            <%--s0207--%>
                            <%--<div style="position:absolute;top:71px;left:10px;width:100px;height:10px;border:1px solid #000;background:yellow;"></div>--%>
                            <%--<br/>--%>

                            <%--N73019--%>
                            <%--<div style="position:absolute;top:90px;left:10px;width:100px;height:10px;border:1px solid #000;background:gray;"></div>--%>
                            <%--<br/>--%>
                        <%--</fieldset>--%>
                    <%--</div>--%>


                    <%--<div class='gallery' id='chartD3Calendar'>--%>
                        <%--&lt;%&ndash;Chart will come here&ndash;%&gt;--%>
                    <%--</div>--%>

                    <%--<div id="messageD3Calendar"></div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
        <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
        </form>
        <br/>
        <table class="list" id="table">
            <thead>
            <tr>
                <th>Run Name</th>
                <th>Alias</th>
                <th>Status</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Type</th>
                <th class="fit">Edit</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${runs}" var="run">
                <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                    <td><b>${run.name}</b></td>
                    <td>${run.alias}</td>
                    <td>${run.status.health.key}</td>
                    <td>${run.status.startDate}</td>
                    <td>${run.status.completionDate}</td>
                    <td>${run.platformType}</td>
                    <td class="misoicon" onclick="window.location.href='<c:url value="/miso/run/${run.runId}"/>'"><span class="ui-icon ui-icon-pencil"/></td>
                </tr>

            </c:forEach>
            </tbody>
        </table>
        <script type="text/javascript">

            jQuery(document).ready(function() {
                jQuery('#table').find('tr').each(function() {
                  if (jQuery(this.cells[2]).html() === "Completed" && Utils.validation.isNullCheck(jQuery(this.cells[4]).html())) {
                    jQuery(this.cells[4]).addClass("error");
                  }
                });

                writeTotalNo();
//                jQuery("#traftrigger").colorbox({width:"90%", height:"1000px", inline:true, href:"#trafpanel"});
                jQuery("#table").tablesorter({
                                                 headers: {
                                                     6: {
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

                jQuery('#filter-form').submit(
                        function() {
                            theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
                            return false;
                        }).focus(); //Give focus to input field
            });

            function writeTotalNo() {
                jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Runs");
            }
        </script>
    </div>
</div>
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>