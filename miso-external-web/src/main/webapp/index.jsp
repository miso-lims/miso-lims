<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="externalHeader.jsp" %>

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

<link rel="stylesheet" href="<c:url value='/styles/progress.css'/>" type="text/css">
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
    <div id="login-form">
        <form method="POST" id="login_box">
            <div style="margin:0;padding:0;display:inline">
                <table>
                    <tr>
                        <td align="right">Username</td>
                        <td align="left"><input type="text" name="username" id="username"/></td>
                    </tr>
                    <tr>
                        <td align="right">Password</td>
                        <td align="left"><input type="password" name="password" id="password"/></td>
                    </tr>
                    <tr>
                        <td align="left">
                        </td>
                        <td align="right">
                            </button>
                        </td>
                    </tr>
                </table>
            </div>
        </form>
    </div>
    <button id="login_button" style="margin-top:-30px;margin-right:42%" class="fg-button ui-state-default ui-corner-all" onclick="login('login_box');">
        Login
    </button>
    <script type="text/javascript">
        Form.Element.focus('username');
    </script>
    <div id="contentcolumn">

        <div id="externalmaincontent" style="display: none;">
            <h1>Your Project Status</h1>

            <div id="externalProjectStatus">
                <p>Please select a project to view.</p>
            </div>
            <div id="externalSampleStatusWrapper">
                <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered display" id="externalSampleStatus">
                </table>
            </div>
            <div id="externalSampleQcStatus">
            </div>
            <div id="externalRunStatusWrapper">
                <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered display" id="externalRunStatus">
                </table>
            </div>
        </div>
    </div>
</div>

<div id="subcontent" style="display: none;">
    <p>List of Available Projects</p>

    <div id="externalProjectsListing">Loading....</div>
</div>


<%@ include file="externalFooter.jsp" %>