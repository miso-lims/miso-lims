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

        <h1>
            Sample Sheet Import & Library Sheet Export
        </h1>

        <div id="samplesheetformdiv" class="simplebox ui-corner-all">
            <table class="in">
                <tr>
                    <td>
                        <form method='post'
                              id='samplesheet_upload_form'
                              action='<c:url value="/miso/upload/importexport/samplesheet"/>'
                              enctype="multipart/form-data"
                              target="plateform_target_upload"
                              onsubmit="Utils.fileUpload.fileUploadProgress('samplesheet_upload_form', 'samplesheet_statusdiv', ImportExport.sampleSheetUploadSuccess);">
                            <input type="file" name="file"/>
                            <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                            <button type="button" class="br-button ui-state-default ui-corner-all"
                                    onclick="ImportExport.cancelSampleSheetUpload();">Cancel
                            </button>
                        </form>
                        <%--<iframe id='plateform_target_upload' name='plateform_target_upload' style='display: none'></iframe>--%>
                        <div id="samplesheet_statusdiv"></div>
                        <%--<div id="plateform_import"></div>--%>
                    </td>
                </tr>
            </table>
        </div>

    </div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {

    });
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>