<%@ include file="../header.jsp" %>
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
<div id="maincontent">
    <div id="contentcolumn">
        <h1>Dashboard</h1>
        <c:if test="${not empty duplicateBarcodes}">
          <div class="dashboard_widget">
            <c:forEach items="${duplicateBarcodes}" var="duplicateBarcode">
              <div class="widget_title ui-corner-top">Duplicate barcode: ${duplicateBarcode.key}</div>
              <div class="widget ui-corner-bottom">
                <div>
                  <c:forEach items="${duplicateBarcode.value}" var="item">
                    <a href="${item.url}" class="dashboardresult"> <div onmouseover="this.className='dashboardhighlight'" onmouseout="this.className='dashboard'" class="dashboard">Name: <b>${item.name}</b><br>Alias: <b>${item.alias}</b><br></div></a>
                  </c:forEach>
                </div>
              </div>
            </div>
          </c:forEach>
        </c:if>
        <%--
          <div id="alertbox">
            <fieldset class="alertwidget">
              <legend>Alerts</legend>
             <div id="alertList" class="elementList"><i style="color: gray">No unread alerts</i></div>
            </fieldset>
          </div>
        --%>
        <div id="rightpanel" style="float:right;margin-top:10px;">
            <div align="right" style="margin-right:5px;">
                <h2>Projects with recently<br/> received samples:</h2>
            </div>
            <div id="latestSamplesList" class="elementList ui-corner-all"
                 style="height:720px;width:250px;margin-top:10px;"
                 align="right"></div>
        </div>

        <div class="dashboard_widget">
            <div class="widget_title ui-corner-top">
                Project <input type="text" size="20" id="searchProject" name="searchProject"/>
            </div>
            <div class="widget ui-corner-bottom">
                <div id="searchProjectresult">
                </div>
            </div>
        </div>

        <div class="dashboard_widget">

            <div class="widget_title ui-corner-top">
                Run <input type="text" size="20" id="searchRun" name="searchRun"/>
            </div>
            <div class="widget ui-corner-bottom">
                <div id="searchRunresult"></div>
            </div>
        </div>

        <div class="dashboard_widget">

            <div class="widget_title ui-corner-top">
                Sample <input type="text" size="20" id="searchSample" name="searchSample"/>
            </div>
            <div class="widget ui-corner-bottom">
                <div id="searchSampleresult">
                </div>
            </div>
        </div>

        <div class="dashboard_widget">

            <div class="widget_title ui-corner-top">
                Library <input type="text" size="20" id="searchLibrary" name="searchLibrary"/>
            </div>
            <div class="widget ui-corner-bottom">
                <div id="searchLibraryresult">
                </div>
            </div>
        </div>
        <div class="dashboard_widget">

            <div class="widget_title ui-corner-top">
                Pool <input type="text" size="20" id="searchPool" name="searchPool"/>
            </div>
            <div class="widget ui-corner-bottom">
                <div id="searchPoolresult">
                </div>
            </div>
        </div>
        <div class="dashboard_widget">

            <div class="widget_title ui-corner-top">
                Library Dilution<input type="text" size="20" id="searchLibraryDilution" name="searchLibraryDilution"/>
            </div>
            <div class="widget ui-corner-bottom">
                <div id="searchLibraryDilutionresult">
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
  jQuery(document).ready(function() {
    Dashboard.showLatestReceivedtSamples();
    Search.loadAll();
    Utils.timer.typewatchFunc(jQuery('#searchProject'), function () {
        Search.dashboardSearch(jQuery('#searchProject'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchRun'), function () {
        Search.dashboardSearch(jQuery('#searchRun'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchSample'), function () {
        Search.dashboardSearch(jQuery('#searchSample'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchLibrary'), function () {
        Search.dashboardSearch(jQuery('#searchLibrary'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchLibraryDilution'), function () {
        Search.dashboardSearch(jQuery('#searchLibraryDilution'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchPool'), function () {
      Search.dashboardSearch(jQuery('#searchPool'))
    }, 300, 2);
  });
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
