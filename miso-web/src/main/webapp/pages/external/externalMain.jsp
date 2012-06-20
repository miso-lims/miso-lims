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
<div id="maincontent">
  <div id="contentcolumn">
    <h1>Project Status</h1>


    <div id="externalProjectStatus">
      <p>Please select the project to view.</p>
    </div>

  </div>
</div>

<div id="subcontent">
  <p>List of Available Projects</p>
  <div id="externalProjectsListing">Loading....</div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function() {
    externalProjectsListing();
  });

  function externalProjectsListing() {
    Fluxion.doAjax(
            'externalSectionControllerHelperService',
            'listProjects',
            {'url':ajaxurl},
            {
//              "doOnLoading":
//                      function(json) {
//                        jQuery('#externalProjectsListing').html("<img src='../styles/images/ajax-loader.gif'/>");
//                      },
              "doOnSuccess":
                      function(json) {
                        jQuery('#externalProjectsListing').html(json.html);
                      }
            });
  }

  function showProjectStatus(projectId){
                   Fluxion.doAjax(
            'externalSectionControllerHelperService',
            'projectStatus',
            {'projectId':projectId, 'url':ajaxurl},
            {
//              "doOnLoading":
//                      function(json) {
//                        jQuery('#externalProjectStatus').html("<img src='../styles/images/ajax-loader.gif'/>");
//                      },
              "doOnSuccess":
                      function(json) {
                        jQuery('#externalProjectStatus').html(json.html);
                      }
            });
  }

</script>

<%@ include file="externalFooter.jsp" %>