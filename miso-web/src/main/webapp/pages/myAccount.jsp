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
    <h1>My Account</h1>

    <div class="portlet">
      <div class="portlet-header">My Account</div>
      <div class="portlet-content">
        User: ${userRealName} <a href="<c:url value='/miso/user/${userId}'/>">Edit</a><br/>
        Groups: ${userGroups}<br/><br/>
        API Key: ${apiKey}
      </div>
    </div>

    <div class="column">
      <sec:authorize access="hasRole('ROLE_ADMIN')">
        <div class="portlet">
          <div class="portlet-header">MISO Configuration</div>
          <div class="portlet-content">
              <%--
              <a href="<c:url value='/miso/admin/configuration/general'/>">General</a><br/>
              <a href="<c:url value='/miso/admin/configuration/database'/>">Database</a><br/>
              <a href="<c:url value='/miso/admin/configuration/security'/>">Security</a><br/>
              --%>
            <a href="<c:url value='/miso/admin/configuration/printers'/>">Barcode Printers</a>
          </div>
        </div>

        <div class="portlet">
          <div class="portlet-header">Cache Administration</div>
          <div class="portlet-content">
            <a href="javascript:void(0);" onclick="flushAllCaches();">Flush All Caches</a><br/>
            <a href="javascript:void(0);" onclick="viewCacheStats();">Flush Cache...</a><br/>
            <a href="javascript:void(0);" onclick="viewCacheStats();">View Cache Stats</a><br/>
            <a href="javascript:void(0);" onclick="regenAllBarcodes();">Regenerate All Barcodes</a><br/>
            <a href="javascript:void(0);" onclick="reindexAlertManagers();">Reindex Alert Managers</a>
          </div>
        </div>
      </sec:authorize>

      <sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_SUBMITTER')">
        <div class="portlet">
          <div class="portlet-header">Submissions</div>
          <div class="portlet-content">
            <a href="<c:url value='/miso/submission/new'/>">Prepare new Submission</a><br/>
            <a href="<c:url value='/miso/submissions'/>">List Submissions</a>
          </div>
        </div>
      </sec:authorize>

      <sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_TECH')">
        <div class="portlet">
          <div class="portlet-header">Sequencing Machines</div>
          <div class="portlet-content">
            <a href="<c:url value='/miso/stats/ls454'/>">454</a><br/>
            <a href="<c:url value='/miso/stats/illumina'/>">Illumina</a><br/>
            <a href="<c:url value='/miso/stats/solid'/>">SOLiD</a><br/>
            <a href="<c:url value='/miso/stats/pacbio'/>">PacBio</a><br/><br/>
            <a href="<c:url value='/miso/stats'/>">Configure</a>
          </div>
        </div>
      </sec:authorize>

      <sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_TECH')">
        <div class="portlet">
          <div class="portlet-header">Custom Barcode</div>
          <div class="portlet-content">
            <a href="<c:url value='/miso/custombarcode'/>">Custom Barcode Printing</a><br/>
          </div>
        </div>
      </sec:authorize>
      <div class="portlet">
        <div class="portlet-header">Reporting</div>
        <div class="portlet-content">
          <a href="<c:url value='/miso/flexreports'/>">Reports</a><br/>
        </div>
      </div>
    </div>

    <div class="column">
      <div class="portlet">
        <div class="portlet-header">My Alerts</div>
        <div class="portlet-content">
          <input id="showReadAlerts" type="checkbox" onclick="toggleShowReadAlerts();"/>Show Read Alerts
          <a href="javascript:void(0)" style="float:right;" onclick="Utils.alert.confirmAllAlertsRead();">Mark
            All Alerts as Read</a>

          <div id="alertList" class="elementList" style="max-height: 380px"><i style="color: gray">No unread
            alerts</i></div>
        </div>
      </div>
      <sec:authorize access="hasRole('ROLE_ADMIN')">
        <div class="portlet">
          <div class="portlet-header">Recent Activity Alerts</div>
          <div class="portlet-content">
            <div id="systemAlertList" class="elementList" style="max-height: 380px"><i style="color: gray">No
              system alerts</i></div>
          </div>
        </div>
      </sec:authorize>
    </div>
  </div>
</div>

<style type="text/css">
  .column {
    width: 50%;
    float: left;
    padding-bottom: 100px;
    margin-top: 4px;
  }

  .portlet {
    margin: 0 1em 1em 0;
  }

  .portlet-header {
    margin: 0.3em;
    padding-bottom: 4px;
    padding-left: 0.2em;
  }

  .portlet-header .ui-icon {
    float: right;
  }

  .portlet-content {
    padding: 0.4em;
  }

  .ui-sortable-placeholder {
    border: 1px dotted black;
    visibility: visible !important;
    height: 50px !important;
  }

  .ui-sortable-placeholder * {
    visibility: hidden;
  }

  .ui-widget-header {
    -moz-background-clip: border;
    -moz-background-inline-policy: continuous;
    -moz-background-origin: padding;
    background: #F0F0FF none repeat scroll 0 0;
    border: 1px solid #AAAAAA;
    color: #666666;
    font-weight: bold;
  }

  .ui-widget {
    font-family: Verdana, Arial, sans-serif;
    font-size: 1em;
  }
</style>

<script type="text/javascript">
  jQuery(document).ready(function () {
    toggleShowReadAlerts();
    getSystemAlerts();
  });

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

  function flushCache(cacheName) {
    Fluxion.doAjax(
      'cacheHelperService',
      'flushCache',
      {'url': ajaxurl, 'cache': cacheName},
      {'doOnSuccess': function (json) {
        jQuery("body").append(json.html);
        jQuery("#dialog").dialog("destroy");

        jQuery("#dialog-message").dialog({
          zIndex: 10000,
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

  function viewCacheStats() {
    Fluxion.doAjax(
      'cacheHelperService',
      'viewCacheStats',
      {'url': ajaxurl},
      {'doOnSuccess': function (json) {
        var stats = "<b>Cache Stats:</b><br/><table class='list'><thead><tr><th>Name</th><th>Elements</th><th>Hits</th><th>Search Times</th><th>Flush</th></tr></thead><tbody>";
        for (var key in json.caches) {
          if (json.caches.hasOwnProperty(key)) {
            var cache = json.caches[key];
            stats += "<tr><td>" + cache.name + "</td><td>" + cache.size + "</td><td>" + cache.hits + "</td><td>" + cache.searchtimes + "</td><td><a href='#' onclick='flushCache(\"" + cache.name + "\");'>Flush</a></td></tr>";
          }
        }
        stats += "</tbody></table>";
        jQuery.colorbox({width: "80%", html: stats});
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

  function toggleShowReadAlerts() {
    Fluxion.doAjax(
      'dashboard',
      'getAlerts',
      {'showReadAlerts': jQuery('input[id=showReadAlerts]').is(':checked'), 'url': ajaxurl},
      {'doOnSuccess': Utils.alert.processAlerts}
    );
  }

  function getSystemAlerts() {
    Fluxion.doAjax(
      'dashboard',
      'getSystemAlerts',
      {'url': ajaxurl},
      {'doOnSuccess': Utils.alert.processSystemAlerts}
    );
  }
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>