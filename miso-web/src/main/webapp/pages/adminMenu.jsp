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

<div id="maincontent">
  <div id="contentcolumn">
    <h1>Admin</h1>

    <div class="column">
      <div class="portlet">
        <div class="portlet-header">MISO Configuration</div>
        <div class="portlet-content">
          <a href="<c:url value='/miso/admin/configuration/general'/>">General</a><br/>
          <a href="<c:url value='/miso/admin/configuration/database'/>">Database</a><br/>
          <a href="<c:url value='/miso/admin/configuration/security'/>">Security</a><br/>
          <a href="<c:url value='/miso/admin/configuration/printers'/>">Barcode Printers</a>
        </div>
      </div>

      <div class="portlet">
        <div class="portlet-header">Cache Administration</div>
        <div class="portlet-content">
          <a href="javascript:void(0);" onclick="flushAllCaches();">Flush All Caches</a><br/>
          <a href="javascript:void(0);" onclick="regenAllBarcodes();">Regenerate All Barcodes</a>
          <a href="javascript:void(0);" onclick="reindexAlertManagers();">Reindex Alert Managers</a><br/>
        </div>
      </div>

      <div class="portlet">
        <div class="portlet-header">Submissions</div>
        <div class="portlet-content">
          <a href="<c:url value='/miso/admin/submission/new'/>">Prepare new Submission</a><br/>
          <a href="<c:url value='/miso/admin/submissions'/>">List Submissions</a>
        </div>
      </div>
    </div>

    <div class="column">
      <div class="portlet">
        <div class="portlet-header">Reporting</div>
        <div class="portlet-content">
          <a href="<c:url value='/miso/reports'/>">Reports</a><br/>
        </div>
      </div>

      <div class="portlet">
        <div class="portlet-header">Sequencing Machines</div>
        <div class="portlet-content">
          <a href="<c:url value='/miso/stats/ls454'/>">454</a><br/>
          <a href="<c:url value='/miso/stats/illumina'/>">Illumina</a><br/>
          <a href="<c:url value='/miso/stats/solid'/>">SOLiD</a><br/><br/>
          <a href="<c:url value='/miso/stats'/>">Configure</a>
        </div>
      </div>

    </div>

  </div>
</div>

<script type="text/javascript">

  function flushAllCaches() {
    Fluxion.doAjax(
      'cacheHelperService',
      'flushAllCaches',
      {'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery("body").append(json.html);
        jQuery("#dialog").dialog("destroy");

        jQuery("#dialog-message").dialog({
          modal: true,
          buttons: {
            Ok: function () {
              jQuery(this).dialog('close');
            }
          }
        });
      }
      }
    );
  }

  function regenAllBarcodes() {
    Fluxion.doAjax(
      'cacheHelperService',
      'regenerateAllBarcodes',
      {'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery("body").append(json.html);
        jQuery("#dialog").dialog("destroy");

        jQuery("#dialog-message").dialog({
          modal: true,
          buttons: {
            Ok: function () {
              jQuery(this).dialog('close');
            }
          }
        });
      }
      }
    );
  }

  function reindexAlertManagers() {
    Fluxion.doAjax(
      'cacheHelperService',
      'reindexAlertManagers',
      {'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery("body").append(json.html);
        jQuery("#dialog").dialog("destroy");

        jQuery("#dialog-message").dialog({
          modal: true,
          buttons: {
            Ok: function () {
              jQuery(this).dialog('close');
            }
          }
        });
      }
      }
    );
  }

  jQuery(function () {
    jQuery(".column").sortable({
      connectWith: '.column',
      handle: '.portlet-header'
    });

    jQuery(".portlet").addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
        .find(".portlet-header")
        .addClass("ui-widget-header ui-corner-all")
        .prepend('<span class="ui-icon ui-icon-minusthick"></span>')
        .end()
        .find(".portlet-content");

    jQuery(".portlet-header .ui-icon").click(function () {
      jQuery(this).toggleClass("ui-icon-minusthick").toggleClass("ui-icon-plusthick");
      jQuery(this).parents(".portlet:first").find(".portlet-content").toggle();
    });

    jQuery(".column").disableSelection();
  });

</script>


<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>