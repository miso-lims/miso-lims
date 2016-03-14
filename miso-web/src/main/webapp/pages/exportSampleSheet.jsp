<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 5 of the License, or
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

<%--
  Created by IntelliJ IDEA.
  User: bianx

--%>
<%@ include file="../header.jsp" %>

<div id="maincontent">

<h1>
    Export Samples or Library & Pool Sheet
</h1>


<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
    <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">help text
</div>


<div id="sample-box" style="float:right;">
    <h2>Available Samples</h2>

    <div align="right">
        Filter:
        <input id="searchSamples" type="text" name="searchSamples" size="8"/>
    </div>
    <div id="sampleList" class="elementList ui-corner-all" style="height:800px;width:200px;" align="right">Select sample
        sheet type on the left first...
    </div>

    <script type="text/javascript">
        Utils.timer.typewatchFunc(jQuery('#searchSamples'), function () {
            ImportExport.searchSamples(jQuery('#searchSamples').val())
        }, 300, 2);
    </script>
</div>

<p>Sample Sheet Type: <input type="radio" id="selectplateform" name="selectForm" onclick="selectForm('plate');"/>Plate
    | <input type="radio" id="selecttubeform" name="selectForm" onclick="selectForm('tube');"/> Tubes</p>

<form id="sampleExportForm">

</form>
<br/>

<div id="exportButtons" style="display:none;">
    <table width="900">
        <tr>
            <td width="50%">
                <button class="button ui-state-default ui-corner-all" id="exportSampleForm"
                        onclick="ImportExport.exportSampleForm();">Export Sample Sheet
                </button>
            </td>
            <td width="50%">
                <button class="button ui-state-default ui-corner-all" id="exportLibraryPoolForm"
                        onclick="ImportExport.exportLibraryPoolForm();">Export Library & Pool Sheet
                </button>
            </td>
        </tr>
    </table>

</div>
<script type="text/javascript">

    function selectForm(selection) {
        ImportExport.searchSamples(jQuery('#searchSamples').val());
        if (selection == 'tube') {
            jQuery('#sampleExportForm').html(jQuery('#tubeformholder').html() + jQuery('#librarySelectionHolder').html());
            showPlatforms();
            showLibraryStrategyTypesString();
            showLibrarySelectionString();
        }
        else if (selection == 'plate') {
            jQuery('#sampleExportForm').html(jQuery('#plateformholder').html() + jQuery('#librarySelectionHolder').html());
            showPlatforms();
            showLibraryStrategyTypesString();
            showLibrarySelectionString();
        }
        jQuery('#exportButtons').show();
    }

    function showPlatforms(){
    Fluxion.doAjax(
            'importExportControllerHelperService',
            'platformsOptions',
            {'url': ajaxurl},
            {'doOnSuccess': function (json){
                jQuery('#platform').append(json.html);
            }
            }
    );
    }

    function showLibraryStrategyTypesString(){
        Fluxion.doAjax(
                'importExportControllerHelperService',
                'libraryStrategyTypesString',
                {'url': ajaxurl},
                {'doOnSuccess': function (json){
                    jQuery('#strategy').append(json.html);
                }
                }
        );
    }

    function showLibrarySelectionString(){
        Fluxion.doAjax(
                'importExportControllerHelperService',
                'librarySelectionTypesString',
                {'url': ajaxurl},
                {'doOnSuccess': function (json){
                    jQuery('#selection').append(json.html);
                }
                }
        );
    }

</script>


<div id="tubeformholder" style="display:none;">
<div id="tubeformbox" style="border:1px solid grey;width:900px;height:500px;" class="ui-corner-all">
<div class="exportcolumn">
    Tube 1
    <div class="plateWell" id="t1"></div>
    <br/>
    Tube 2
    <div class="plateWell" id="t2"></div>
    <br/>
    Tube 3
    <div class="plateWell" id="t3"></div>
    <br/>
    Tube 4
    <div class="plateWell" id="t4"></div>
    <br/>
    Tube 5
    <div class="plateWell" id="t5"></div>
    <br/>
    Tube 6
    <div class="plateWell" id="t6"></div>
    <br/>
    Tube 7
    <div class="plateWell" id="t7"></div>
    <br/>
    Tube 8
    <div class="plateWell" id="t8"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 9
    <div class="plateWell" id="t9"></div>
    <br/>
    Tube 10
    <div class="plateWell" id="t10"></div>
    <br/>
    Tube 11
    <div class="plateWell" id="t11"></div>
    <br/>
    Tube 12
    <div class="plateWell" id="t12"></div>
    <br/>
    Tube 13
    <div class="plateWell" id="t13"></div>
    <br/>
    Tube 14
    <div class="plateWell" id="t14"></div>
    <br/>
    Tube 15
    <div class="plateWell" id="t15"></div>
    <br/>
    Tube 16
    <div class="plateWell" id="t16"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 17
    <div class="plateWell" id="t17"></div>
    <br/>
    Tube 18
    <div class="plateWell" id="t18"></div>
    <br/>
    Tube 19
    <div class="plateWell" id="t19"></div>
    <br/>
    Tube 20
    <div class="plateWell" id="t20"></div>
    <br/>
    Tube 21
    <div class="plateWell" id="t21"></div>
    <br/>
    Tube 22
    <div class="plateWell" id="t22"></div>
    <br/>
    Tube 23
    <div class="plateWell" id="t23"></div>
    <br/>
    Tube 24
    <div class="plateWell" id="t24"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 25
    <div class="plateWell" id="t25"></div>
    <br/>
    Tube 26
    <div class="plateWell" id="t26"></div>
    <br/>
    Tube 27
    <div class="plateWell" id="t27"></div>
    <br/>
    Tube 28
    <div class="plateWell" id="t28"></div>
    <br/>
    Tube 29
    <div class="plateWell" id="t29"></div>
    <br/>
    Tube 30
    <div class="plateWell" id="t30"></div>
    <br/>
    Tube 31
    <div class="plateWell" id="t31"></div>
    <br/>
    Tube 32
    <div class="plateWell" id="t32"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 33
    <div class="plateWell" id="t33"></div>
    <br/>
    Tube 34
    <div class="plateWell" id="t34"></div>
    <br/>
    Tube 35
    <div class="plateWell" id="t35"></div>
    <br/>
    Tube 36
    <div class="plateWell" id="t36"></div>
    <br/>
    Tube 37
    <div class="plateWell" id="t37"></div>
    <br/>
    Tube 38
    <div class="plateWell" id="t38"></div>
    <br/>
    Tube 39
    <div class="plateWell" id="t39"></div>
    <br/>
    Tube 40
    <div class="plateWell" id="t40"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 41
    <div class="plateWell" id="t41"></div>
    <br/>
    Tube 42
    <div class="plateWell" id="t42"></div>
    <br/>
    Tube 43
    <div class="plateWell" id="t43"></div>
    <br/>
    Tube 44
    <div class="plateWell" id="t44"></div>
    <br/>
    Tube 45
    <div class="plateWell" id="t45"></div>
    <br/>
    Tube 46
    <div class="plateWell" id="t46"></div>
    <br/>
    Tube 47
    <div class="plateWell" id="t47"></div>
    <br/>
    Tube 48
    <div class="plateWell" id="t48"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 49
    <div class="plateWell" id="t49"></div>
    <br/>
    Tube 50
    <div class="plateWell" id="t50"></div>
    <br/>
    Tube 51
    <div class="plateWell" id="t51"></div>
    <br/>
    Tube 52
    <div class="plateWell" id="t52"></div>
    <br/>
    Tube 53
    <div class="plateWell" id="t53"></div>
    <br/>
    Tube 54
    <div class="plateWell" id="t54"></div>
    <br/>
    Tube 55
    <div class="plateWell" id="t55"></div>
    <br/>
    Tube 56
    <div class="plateWell" id="t56"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 57
    <div class="plateWell" id="t57"></div>
    <br/>
    Tube 58
    <div class="plateWell" id="t58"></div>
    <br/>
    Tube 59
    <div class="plateWell" id="t59"></div>
    <br/>
    Tube 60
    <div class="plateWell" id="t60"></div>
    <br/>
    Tube 61
    <div class="plateWell" id="t61"></div>
    <br/>
    Tube 62
    <div class="plateWell" id="t62"></div>
    <br/>
    Tube 63
    <div class="plateWell" id="t63"></div>
    <br/>
    Tube 64
    <div class="plateWell" id="t64"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 65
    <div class="plateWell" id="t65"></div>
    <br/>
    Tube 66
    <div class="plateWell" id="t66"></div>
    <br/>
    Tube 67
    <div class="plateWell" id="t67"></div>
    <br/>
    Tube 68
    <div class="plateWell" id="t68"></div>
    <br/>
    Tube 69
    <div class="plateWell" id="t69"></div>
    <br/>
    Tube 70
    <div class="plateWell" id="t70"></div>
    <br/>
    Tube 71
    <div class="plateWell" id="t71"></div>
    <br/>
    Tube 72
    <div class="plateWell" id="t72"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 73
    <div class="plateWell" id="t73"></div>
    <br/>
    Tube 74
    <div class="plateWell" id="t74"></div>
    <br/>
    Tube 75
    <div class="plateWell" id="t75"></div>
    <br/>
    Tube 76
    <div class="plateWell" id="t76"></div>
    <br/>
    Tube 77
    <div class="plateWell" id="t77"></div>
    <br/>
    Tube 78
    <div class="plateWell" id="t78"></div>
    <br/>
    Tube 79
    <div class="plateWell" id="t79"></div>
    <br/>
    Tube 80
    <div class="plateWell" id="t80"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 81
    <div class="plateWell" id="t81"></div>
    <br/>
    Tube 82
    <div class="plateWell" id="t82"></div>
    <br/>
    Tube 83
    <div class="plateWell" id="t83"></div>
    <br/>
    Tube 84
    <div class="plateWell" id="t84"></div>
    <br/>
    Tube 85
    <div class="plateWell" id="t85"></div>
    <br/>
    Tube 86
    <div class="plateWell" id="t86"></div>
    <br/>
    Tube 87
    <div class="plateWell" id="t87"></div>
    <br/>
    Tube 88
    <div class="plateWell" id="t88"></div>
    <br/>
</div>
<div class="exportcolumn">
    Tube 89
    <div class="plateWell" id="t89"></div>
    <br/>
    Tube 90
    <div class="plateWell" id="t90"></div>
    <br/>
    Tube 91
    <div class="plateWell" id="t91"></div>
    <br/>
    Tube 92
    <div class="plateWell" id="t92"></div>
    <br/>
    Tube 93
    <div class="plateWell" id="t93"></div>
    <br/>
    Tube 94
    <div class="plateWell" id="t94"></div>
    <br/>
    Tube 95
    <div class="plateWell" id="t95"></div>
    <br/>
    Tube 96
    <div class="plateWell" id="t96"></div>
    <br/>
</div>
</div>
</div>

<div id="plateformholder" style="display:none;">

<br/>

<div id="formbox" style="border:1px solid grey;width:900px;height:500px;" class="ui-corner-all">
<div id="column1" class="exportcolumn">
    A1<br/>

    <div class="plateWell" id="A1"></div>
    <br/>
    B1<br/>

    <div class="plateWell" id="B1"></div>
    <br/>
    C1<br/>

    <div class="plateWell" id="C1"></div>
    <br/>
    D1<br/>

    <div class="plateWell" id="D1"></div>
    <br/>
    E1<br/>

    <div class="plateWell" id="E1"></div>
    <br/>
    F1<br/>

    <div class="plateWell" id="F1"></div>
    <br/>
    G1<br/>

    <div class="plateWell" id="G1"></div>
    <br/>
    H1<br/>

    <div class="plateWell" id="H1"></div>
    <br/>
</div>
<div id="column2" class="exportcolumn">
    A2<br/>

    <div class="plateWell" id="A2"></div>
    <br/>
    B2<br/>

    <div class="plateWell" id="B2"></div>
    <br/>
    C2<br/>

    <div class="plateWell" id="C2"></div>
    <br/>
    D2<br/>

    <div class="plateWell" id="D2"></div>
    <br/>
    E2<br/>

    <div class="plateWell" id="E2"></div>
    <br/>
    F2<br/>

    <div class="plateWell" id="F2"></div>
    <br/>
    G2<br/>

    <div class="plateWell" id="G2"></div>
    <br/>
    H2<br/>

    <div class="plateWell" id="H2"></div>
    <br/>
</div>
<div id="column3" class="exportcolumn">
    A3<br/>

    <div class="plateWell" id="A3"></div>
    <br/>
    B3<br/>

    <div class="plateWell" id="B3"></div>
    <br/>
    C3<br/>

    <div class="plateWell" id="C3"></div>
    <br/>
    D3<br/>

    <div class="plateWell" id="D3"></div>
    <br/>
    E3<br/>

    <div class="plateWell" id="E3"></div>
    <br/>
    F3<br/>

    <div class="plateWell" id="F3"></div>
    <br/>
    G3<br/>

    <div class="plateWell" id="G3"></div>
    <br/>
    H3<br/>

    <div class="plateWell" id="H3"></div>
    <br/>
</div>
<div id="column4" class="exportcolumn">
    A4<br/>

    <div class="plateWell" id="A4"></div>
    <br/>
    B4<br/>

    <div class="plateWell" id="B4"></div>
    <br/>
    C4<br/>

    <div class="plateWell" id="C4"></div>
    <br/>
    D4<br/>

    <div class="plateWell" id="D4"></div>
    <br/>
    E4<br/>

    <div class="plateWell" id="E4"></div>
    <br/>
    F4<br/>

    <div class="plateWell" id="F4"></div>
    <br/>
    G4<br/>

    <div class="plateWell" id="G4"></div>
    <br/>
    H4<br/>

    <div class="plateWell" id="H4"></div>
    <br/>
</div>
<div id="column5" class="exportcolumn">
    A5<br/>

    <div class="plateWell" id="A5"></div>
    <br/>
    B5<br/>

    <div class="plateWell" id="B5"></div>
    <br/>
    C5<br/>

    <div class="plateWell" id="C5"></div>
    <br/>
    D5<br/>

    <div class="plateWell" id="D5"></div>
    <br/>
    E5<br/>

    <div class="plateWell" id="E5"></div>
    <br/>
    F5<br/>

    <div class="plateWell" id="F5"></div>
    <br/>
    G5<br/>

    <div class="plateWell" id="G5"></div>
    <br/>
    H5<br/>

    <div class="plateWell" id="H5"></div>
    <br/>
</div>
<div id="column6" class="exportcolumn">
    A6<br/>

    <div class="plateWell" id="A6"></div>
    <br/>
    B6<br/>

    <div class="plateWell" id="B6"></div>
    <br/>
    C6<br/>

    <div class="plateWell" id="C6"></div>
    <br/>
    D6<br/>

    <div class="plateWell" id="D6"></div>
    <br/>
    E6<br/>

    <div class="plateWell" id="E6"></div>
    <br/>
    F6<br/>

    <div class="plateWell" id="F6"></div>
    <br/>
    G6<br/>

    <div class="plateWell" id="G6"></div>
    <br/>
    H6<br/>

    <div class="plateWell" id="H6"></div>
    <br/>
</div>
<div id="column7" class="exportcolumn">
    A7<br/>

    <div class="plateWell" id="A7"></div>
    <br/>
    B7<br/>

    <div class="plateWell" id="B7"></div>
    <br/>
    C7<br/>

    <div class="plateWell" id="C7"></div>
    <br/>
    D7<br/>

    <div class="plateWell" id="D7"></div>
    <br/>
    E7<br/>

    <div class="plateWell" id="E7"></div>
    <br/>
    F7<br/>

    <div class="plateWell" id="F7"></div>
    <br/>
    G7<br/>

    <div class="plateWell" id="G7"></div>
    <br/>
    H7<br/>

    <div class="plateWell" id="H7"></div>
    <br/>
</div>
<div id="column8" class="exportcolumn">
    A8<br/>

    <div class="plateWell" id="A8"></div>
    <br/>
    B8<br/>

    <div class="plateWell" id="B8"></div>
    <br/>
    C8<br/>

    <div class="plateWell" id="C8"></div>
    <br/>
    D8<br/>

    <div class="plateWell" id="D8"></div>
    <br/>
    E8<br/>

    <div class="plateWell" id="E8"></div>
    <br/>
    F8<br/>

    <div class="plateWell" id="F8"></div>
    <br/>
    G8<br/>

    <div class="plateWell" id="G8"></div>
    <br/>
    H8<br/>

    <div class="plateWell" id="H8"></div>
    <br/>
</div>
<div id="column9" class="exportcolumn">
    A9<br/>

    <div class="plateWell" id="A9"></div>
    <br/>
    B9<br/>

    <div class="plateWell" id="B9"></div>
    <br/>
    C9<br/>

    <div class="plateWell" id="C9"></div>
    <br/>
    D9<br/>

    <div class="plateWell" id="D9"></div>
    <br/>
    E9<br/>

    <div class="plateWell" id="E9"></div>
    <br/>
    F9<br/>

    <div class="plateWell" id="F9"></div>
    <br/>
    G9<br/>

    <div class="plateWell" id="G9"></div>
    <br/>
    H9<br/>

    <div class="plateWell" id="H9"></div>
    <br/>
</div>
<div id="column10" class="exportcolumn">
    A10<br/>

    <div class="plateWell" id="A10"></div>
    <br/>
    B10<br/>

    <div class="plateWell" id="B10"></div>
    <br/>
    C10<br/>

    <div class="plateWell" id="C10"></div>
    <br/>
    D10<br/>

    <div class="plateWell" id="D10"></div>
    <br/>
    E10<br/>

    <div class="plateWell" id="E10"></div>
    <br/>
    F10<br/>

    <div class="plateWell" id="F10"></div>
    <br/>
    G10<br/>

    <div class="plateWell" id="G10"></div>
    <br/>
    H10<br/>

    <div class="plateWell" id="H10"></div>
    <br/>
</div>
<div id="column11" class="exportcolumn">
    A11<br/>

    <div class="plateWell" id="A11"></div>
    <br/>
    B11<br/>

    <div class="plateWell" id="B11"></div>
    <br/>
    C11<br/>

    <div class="plateWell" id="C11"></div>
    <br/>
    D11<br/>

    <div class="plateWell" id="D11"></div>
    <br/>
    E11<br/>

    <div class="plateWell" id="E11"></div>
    <br/>
    F11<br/>

    <div class="plateWell" id="F11"></div>
    <br/>
    G11<br/>

    <div class="plateWell" id="G11"></div>
    <br/>
    H11<br/>

    <div class="plateWell" id="H11"></div>
    <br/>
</div>
<div id="column12" class="exportcolumn">
    A12<br/>

    <div class="plateWell" id="A12"></div>
    <br/>
    B12<br/>

    <div class="plateWell" id="B12"></div>
    <br/>
    C12<br/>

    <div class="plateWell" id="C12"></div>
    <br/>
    D12<br/>

    <div class="plateWell" id="D12"></div>
    <br/>
    E12<br/>

    <div class="plateWell" id="E12"></div>
    <br/>
    F12<br/>

    <div class="plateWell" id="F12"></div>
    <br/>
    G12<br/>

    <div class="plateWell" id="G12"></div>
    <br/>
    H12<br/>

    <div class="plateWell" id="H12"></div>
    <br/>
</div>
</div>
</div>

<div id="librarySelectionHolder" style="display: none;">
    <br/>
    <br/>
    <table width="900">
        <tr>
            <td width="50%"></td>
            <td width="50%">
                <table style="border:1px solid grey;" class="ui-corner-all">
                    <tr>
                        <td style="background-color:yellow;"></td>
                        <td style="background-color:yellow;">Only Complete this for library export</td>
                    </tr>
                    <tr>
                        <td>Paired:</td>
                        <td><select name="paired">
                            <option>TRUE</option>
                            <option>FALSE</option>
                        </select></td>
                    </tr>
                    <tr>
                        <td>Platform:</td>
                        <td><select name="platform" id="platform" onchange="ImportExport.changePlatformName(this);">
                            <option>Select platform</option>
                        </select></td>
                    </tr>
                    <tr>
                        <td>Type:</td>
                        <td><select name="type" id="type">
                        </select></td>
                    </tr>
                    <tr>
                        <td>Selection:</td>
                        <td><select name="selection" id="selection">
                        </select></td>
                    </tr>
                    <tr>
                        <td>Strategy:</td>
                        <td><select name="strategy" id="strategy">
                        </select></td>
                    </tr>
                    <tr>
                        <td>Barcode Kit:</td>
                        <td><select name="barcodekit" id="barcodekit">
                        </select></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div>

</div>

<%@ include file="../footer.jsp" %>