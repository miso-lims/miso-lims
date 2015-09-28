<%--
  Created by IntelliJ IDEA.
  User: bianx
  Date: 04/12/2013
  Time: 11:16
  To change this template use File | Settings | File Templates.
--%>

<%@ include file="../header.jsp" %>
<script type="text/javascript" src="<c:url value='/scripts/import_export_ajax.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">
    <div id="contentcolumn">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              Import / Export
            </span>
         </div>
      </nav>

        <div class="portlet">
            <div class="portlet-header">Exports</div>
            <div class="portlet-content">
                <a href="<c:url value="/miso/importexport/exportsamplesheet"/>">Export Sample or Library & Pool Sheet</a><br/><br/>
                Export Sample or Library & Pool Sheet based on either plates or tubes.
            </div>
        </div>

        <div class="portlet">
            <div class="portlet-header">Import Sample Sheet</div>
            <div class="portlet-content">
                <a href="<c:url value="/miso/importexport/importsamplesheet"/>">Sample Sheet Import</a><br/><br/>
                Import the Sample Sheet and based on the entries, save sample QC info.
            </div>
        </div>

        <div class="portlet">
            <div class="portlet-header">Import Library & Pool Sheet</div>
            <div class="portlet-content">
                <a href="<c:url value="/miso/importexport/importlibrarypoolsheet"/>">Library & Pool Sheet
                    Import</a><br/><br/>
                Import the Library & Pool Sheet and create libraries, library dilutions and pools.
            </div>
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
    });
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>