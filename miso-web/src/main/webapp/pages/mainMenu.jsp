<%@ include file="../header.jsp" %>
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
<h1>Dashboard</h1>
<%--
  <div id="alertbox">
    <fieldset class="alertwidget">
      <legend>Alerts</legend>
     <div id="alertList" class="elementList"><i style="color: gray">No unread alerts</i></div>
    </fieldset>
  </div>
--%>
<table width="100%">
    <tbody>
    <tr>
        <td width="auto" valign="top">
            <table align="center">
                <tr>
                    <td width="50%">
                        <div class="widget_title ui-corner-top">Project <input type="text" size="20" id="searchProject"
                                                                               name="searchProject"/></div>
                        <div class="widget ui-corner-bottom">
                            <div id="searchProjectresult">
                            </div>
                        </div>

                    </td>
                    <td width="50%">
                        <div class="widget_title ui-corner-top">Run <input type="text" size="20" id="searchRun"
                                                                           name="searchRun"/></div>
                        <div class="widget ui-corner-bottom">
                            <div id="searchRunresult"></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td width="50%">
                        <div class="widget_title ui-corner-top">Study <input type="text" size="20" id="searchStudy"
                                                                             name="searchStudy"/></div>
                        <div class="widget ui-corner-bottom">
                            <div id="searchStudyresult">
                            </div>
                        </div>
                    </td>
                    <td width="50%">
                        <div class="widget_title ui-corner-top">Sample <input type="text" size="20" id="searchSample"
                                                                              name="searchSample"/></div>
                        <div class="widget ui-corner-bottom">
                            <div id="searchSampleresult">
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td width="50%">
                        <div class="widget_title ui-corner-top">Experiment <input type="text" size="20"
                                                                                  id="searchExperiment"
                                                                                  name="searchExperiment"/></div>
                        <div class="widget ui-corner-bottom">
                            <div id="searchExperimentresult"><img
                                    src="<c:url value='/styles/images/ajax-loader.gif'/>"/></div>
                        </div>
                    </td>
                    <td width="50%">
                        <div class="widget_title ui-corner-top">Library <input type="text" size="20" id="searchLibrary"
                                                                               name="searchLibrary"/></div>
                        <div class="widget ui-corner-bottom">
                            <div id="searchLibraryresult">
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
</table>
<script type="text/javascript">
    loadAll();
    typewatchFunc(jQuery('#searchProject'), function(){search(jQuery('#searchProject'))}, 300, 2);
    typewatchFunc(jQuery('#searchRun'), function(){search(jQuery('#searchRun'))}, 300, 2);
    typewatchFunc(jQuery('#searchStudy'), function(){search(jQuery('#searchStudy'))}, 300, 2);
    typewatchFunc(jQuery('#searchSample'), function(){search(jQuery('#searchSample'))}, 300, 2);
    typewatchFunc(jQuery('#searchExperiment'), function(){search(jQuery('#searchExperiment'))}, 300, 2);
    typewatchFunc(jQuery('#searchLibrary'), function(){search(jQuery('#searchLibrary'))}, 300, 2);
</script>

<%@ include file="../footer.jsp" %>