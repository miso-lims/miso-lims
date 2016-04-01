<%--
  Created by IntelliJ IDEA.
  User: bianx
  Date: 04/12/2013
  Time: 11:16
  To change this template use File | Settings | File Templates.
--%>

<%@ include file="../header.jsp" %>
<jsp:useBean id="now" class="java.util.Date" scope="page" />

<div id="maincontent">
    <div id="contentcolumn">
        <div id="tabs">
            <ul>
                <li><a href="#pooling"><span>Pooling</span></a></li>
                <li><a href="#plateCSV"><span>Plate CSV</span></a></li>
            </ul>
            <div id="pooling">

                <h1>
                    Import & Export
                </h1>

                <div class="portlet">
                    <div class="portlet-header">Exports</div>
                    <div class="portlet-content">
                        <a href="<c:url value="/miso/importexport/exportsamplesheet"/>">Export Sample or Library & Pool
                            Sheet</a><br/><br/>
                        Export Sample or Library & Pool Sheet based on either plates or tubes.
                    </div>
                </div>

                <div class="portlet">
                    <div class="portlet-header">Import Sample Sheet</div>
                    <div class="portlet-content">
                        <a href="<c:url value="/miso/importexport/importsamplesheet"/>">Sample Sheet
                            Import</a><br/><br/>
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
            <div id="plateCSV">
                <h1>Illumina Nextera 6x 384 Barcode CSV Generator</h1>

                <form id="generateCSVBACForm" style="margin-top:20px;margin-left:20px;">

                    <table class="ui-corner-all">
                        <tr>
                            <td>[Header]:</td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>IEMFileVersion:</td>
                            <td><input type="text" name="IEMFileVersion" size="30" value="4"/></td>
                        </tr>
                        <tr>
                            <td>Investigator Name:</td>
                            <td><input type="text" name="InvestigatorName" size="30"/></td>
                        </tr>
                        <tr>
                            <td>Experiment Name:</td>
                            <td><input type="text" name="ExperimentName" size="30"/></td>
                        </tr>
                        <tr>
                            <td>Date:</td>
                            <td><input type="text" name="Date" size="30" value="<fmt:formatDate value='${now}' pattern='dd/MM/yyyy'/>"/></td>
                        </tr>
                        <tr>
                            <td>Workflow:</td>
                            <td><input type="text" name="Workflow" size="30" value="GenerateFASTQC"/></td>
                        </tr>
                        <tr>
                            <td>Application:</td>
                            <td><input type="text" name="Application" size="30" value="HiSeq FASTQC Only"/></td>
                        </tr>
                        <tr>
                            <td>Assay:</td>
                            <td><input type="text" name="Assay" size="30" value="Nextera"/></td>
                        </tr>
                        <tr>
                            <td>Description:</td>
                            <td><input type="text" name="Description" size="30"/></td>
                        </tr>
                        <tr>
                            <td>Chemistry:</td>
                            <td><input type="text" name="Chemistry" size="30" value="Amplicon"/></td>
                        </tr>
                        <tr>
                            <td>[Reads]:</td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Read 1:</td>
                            <td><input type="text" name="Read1" size="30"/></td>
                        </tr>
                        <tr>
                            <td>Read 2:</td>
                            <td><input type="text" name="Read2" size="30"/></td>
                        </tr>
                        <tr>
                            <td>Read 3:</td>
                            <td><input type="text" name="Read3" size="30"/></td>
                        </tr>
                        <tr>
                            <td>[Settings]:</td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>ReverseComplement:</td>
                            <td><select name="ReverseComplement">
                                <option selected="selected">0</option>
                                <option>1</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Adapter:</td>
                            <td><input type="text" name="Adapter" size="30" value="CTGTCTCTTATACACATCT"/></td>
                        </tr>
                        <tr>
                            <td>[Data]:</td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Lane 1:</td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Plate 1:</td>
                            <td><input type="text" name="Lane1Plate1" size="30"/></td>
                            <td><select name="BarcodeLane1Plate1">
                                <option selected="selected">1+7</option>
                                <option>1+9</option>
                                <option>1+11</option>
                                <option>2+7</option>
                                <option>2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 2:</td>
                            <td><input type="text" name="Lane1Plate2" size="30"/></td>
                            <td><select name="BarcodeLane1Plate2">
                                <option>1+7</option>
                                <option selected="selected">1+9</option>
                                <option>1+11</option>
                                <option>2+7</option>
                                <option>2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 3:</td>
                            <td><input type="text" name="Lane1Plate3" size="30"/></td>
                            <td><select name="BarcodeLane1Plate3">
                                <option>1+7</option>
                                <option>1+9</option>
                                <option selected="selected">1+11</option>
                                <option>2+7</option>
                                <option>2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 4:</td>
                            <td><input type="text" name="Lane1Plate4" size="30"/></td>
                            <td><select name="BarcodeLane1Plate4">
                                <option>1+7</option>
                                <option>1+9</option>
                                <option>1+11</option>
                                <option selected="selected">2+7</option>
                                <option>2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 5:</td>
                            <td><input type="text" name="Lane1Plate5" size="30"/></td>
                            <td><select name="BarcodeLane1Plate5">
                                <option>1+7</option>
                                <option>1+9</option>
                                <option>1+11</option>
                                <option>2+7</option>
                                <option selected="selected">2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 6:</td>
                            <td><input type="text" name="Lane1Plate6" size="30"/></td>
                            <td><select name="BarcodeLane1Plate6">
                                <option>1+7</option>
                                <option>1+9</option>
                                <option>1+11</option>
                                <option>2+7</option>
                                <option>2+9</option>
                                <option selected="selected">2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Lane 2:</td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Plate 1:</td>
                            <td><input type="text" name="Lane2Plate1" size="30"/></td>
                            <td><select name="BarcodeLane2Plate1">
                                <option selected="selected">1+7</option>
                                <option>1+9</option>
                                <option>1+11</option>
                                <option>2+7</option>
                                <option>2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 2:</td>
                            <td><input type="text" name="Lane2Plate2" size="30"/></td>
                            <td><select name="BarcodeLane2Plate2">
                                <option>1+7</option>
                                <option selected="selected">1+9</option>
                                <option>1+11</option>
                                <option>2+7</option>
                                <option>2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 3:</td>
                            <td><input type="text" name="Lane2Plate3" size="30"/></td>
                            <td><select name="BarcodeLane2Plate3">
                                <option>1+7</option>
                                <option>1+9</option>
                                <option selected="selected">1+11</option>
                                <option>2+7</option>
                                <option>2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 4:</td>
                            <td><input type="text" name="Lane2Plate4" size="30"/></td>
                            <td><select name="BarcodeLane2Plate4">
                                <option>1+7</option>
                                <option>1+9</option>
                                <option>1+11</option>
                                <option selected="selected">2+7</option>
                                <option>2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 5:</td>
                            <td><input type="text" name="Lane2Plate5" size="30"/></td>
                            <td><select name="BarcodeLane2Plate5">
                                <option>1+7</option>
                                <option>1+9</option>
                                <option>1+11</option>
                                <option>2+7</option>
                                <option selected="selected">2+9</option>
                                <option>2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Plate 6:</td>
                            <td><input type="text" name="Lane2Plate6" size="30"/></td>
                            <td><select name="BarcodeLane2Plate6">
                                <option>1+7</option>
                                <option>1+9</option>
                                <option>1+11</option>
                                <option>2+7</option>
                                <option>2+9</option>
                                <option selected="selected">2+11</option>
                            </select>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <button style="float: right;" type="button" onclick="ImportExport.generateCSVBAC();"
                                        id="generateCSVBACButton">Generate CSV
                                </button>
                            </td>
                        </tr>
                    </table>


                </form>
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
    jQuery("#tabs").tabs();
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