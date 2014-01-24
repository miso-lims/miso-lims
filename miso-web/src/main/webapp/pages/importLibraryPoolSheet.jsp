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
            Library & Pool Sheet Import
        </h1>

        <div id="librarypoolsheetformdiv" class="simplebox ui-corner-all">
            <table class="in">
                <tr>
                    <td>
                        <form method='post'
                              id='librarypoolsheet_upload_form'
                              action='<c:url value="/miso/upload/importexport/librarypoolsheet"/>'
                              enctype="multipart/form-data"
                              target="plateform_target_upload"
                              onsubmit="Utils.fileUpload.fileUploadProgress('librarypoolsheet_upload_form', 'librarypoolsheet_statusdiv', ImportExport.libraryPoolSheetUploadSuccess);">
                            <input type="file" name="file"/>
                            <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                            <button type="button" class="br-button ui-state-default ui-corner-all"
                                    onclick="ImportExport.cancelLibraryPoolSheetUpload();">Cancel
                            </button>
                        </form>
                        <%--<iframe id='plateform_target_upload' name='plateform_target_upload' style='display: none'></iframe>--%>
                        <div id="librarypoolsheet_statusdiv"></div>
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