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

<%--
  Created by IntelliJ IDEA.
  User: bian
  Date: 19-Apr-2010
  Time: 13:38:56

--%>
<%@ include file="../header.jsp" %>


<div id="maincontent">
    <div id="contentcolumn">
        <form:form method="POST" commandName="pool" autocomplete="off" onsubmit="return validate_pool(this);">
          <sessionConversation:insertSessionConversationId attributeName="pool"/>
            <h1><c:choose><c:when
                    test="${not empty pool.poolId}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
                Illumina Pool
                <button type="submit" class="fg-button ui-state-default ui-corner-all" onclick="processLists()">Save
                </button>
            </h1>
            <div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
                <div id="note_arrowclick" class="toggleLeft"></div>
            </div>
            <div id="notediv" class="note" style="display:none;">A Pool contains <b>one or more</b> Dilutions that are
                to be placed, as part of an Experiment, in a sequencer instrument Run partition (lane/chamber). Pools
                with more than one Dilution are said to be multiplexed.
            </div>
            <h2>Pool Information</h2>
            <div class="barcodes">
              <div class="barcodeArea ui-corner-all">
                <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">ID</span>
                  <c:if test="${not empty pool.identificationBarcode}">
                    <ul class="barcode-ddm">
                        <li><a
                                onmouseover="mopen('idBarcodeMenu')"
                                onmouseout="mclosetime()"><span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span><span id="idBarcode" style="float:right"></span></a>

                            <div id="idBarcodeMenu"
                                 onmouseover="mcancelclosetime()"
                                 onmouseout="mclosetime()">
                                <a href="javascript:void(0);" onclick="printPoolBarcodes(${pool.poolId});">Print</a>
                            </div>
                        </li>
                    </ul>
                    <script type="text/javascript">
                        jQuery(document).ready(function() {
                            Fluxion.doAjax(
                                    'poolControllerHelperService',
                                    'getPoolBarcode',
                            {'poolId':${pool.poolId},
                                'url':ajaxurl
                            },
                            {'doOnSuccess':function(json) {
                                jQuery('#idBarcode').html("<img style='border:0;' src='<c:url value='/temp/'/>" + json.img + "'/>");
                            }
                            });
                        });
                    </script>
                  </c:if>
              </div>
            </div>
            <div id="printServiceSelectDialog" title="Select a Printer"></div>
            <table class="in">
                <tr>
                    <td class="h">Pool ID:</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty pool.poolId}">${pool.poolId}</c:when>
                            <c:otherwise><i>Unsaved</i></c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td class="h">Pool Name:</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty pool.poolId}">${pool.name}</c:when>
                            <c:otherwise><i>Unsaved</i></c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td class="h">Pool Alias:</td>
                    <td><form:input path="alias"/></td>
                </tr>
                <tr>
                    <td class="h">Concentration</td>
                    <td><form:input path="concentration"/></td>
                </tr>
                <tr>
                    <td class="h">Creation Date:</td>
                    <td><c:choose>
                        <c:when test="${not empty pool.poolId}"><fmt:formatDate pattern="dd/MM/yy" type="both"
                                                                                value="${pool.creationDate}"/></c:when>
                        <c:otherwise><form:input path="creationDate"/></c:otherwise>
                    </c:choose>
                    </td>
                </tr>
                <tr>
                    <td class="h">Ready To Run</td>
                    <td><form:checkbox path="readyToRun"/></td>
                </tr>

              <%--
                <tr>
                    <td>
                        Available Dilutions:<br/>
                        <a href="javascript:void(0);"
                           onclick="return jQuery.dds.selectAll('list_1');">All</a>
                        / <a href="javascript:void(0);"
                             onclick="return jQuery.dds.selectNone('list_1');">None</a>
                    </td>
                    <td>
                        <div class="dd">
                            <ul id="list_1" dds="0" class="ui-droppable">
                                <c:forEach items="${availableDilutions}" var="dil">
                                    <li id="dil_${dil.dilutionId}" dilId="${dil.dilutionId}" dilName="${dil.name}"
                                        dds="1"
                                        style="cursor: pointer;"
                                        class="ui-draggable">${dil.name}

                                        <div class="toggleRight"
                                             onclick="toggleRightInfo(this, 'dildiv_${dil.dilutionId}');"></div>
                                        <div id="dildiv_${dil.dilutionId}" style="display:none;">
                                                ${dil.library.alias} (${dil.library.name})<br/>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td> Selected Dilutions:<br/>
                        <a href="javascript:void(0);"
                           onclick="return jQuery.dds.selectAll('list_2');">All</a>
                        / <a href="javascript:void(0);"
                             onclick="return jQuery.dds.selectNone('list_2');">None</a>
                    </td>
                    <td>
                      <div class="dd">
                          <ul id="list_2" dds="0" class="ui-droppable">
                              <c:forEach items="${pool.dilutions}" var="dil">
                                  <li id="dil_${dil.dilutionId}" dilId="${dil.dilutionId}" dilName="${dil.name}"
                                      dds="1"
                                      style="cursor: pointer;"
                                      class="ui-draggable">${dil.name}
                                      <input type="hidden" id="dilutions${dil.dilutionId}"
                                             value="${dil.dilutionId}"
                                             name="dilutions"/>

                                      <div class="toggleRight"
                                           onclick="toggleRightInfo(this, 'dildiv_${dil.dilutionId}');"></div>
                                      <div id="dildiv_${dil.dilutionId}" style="display:none;">
                                              ${dil.library.alias} (${dil.library.name})<br/>
                                      </div>
                                  </li>
                              </c:forEach>
                              <input type="hidden" value="on" name="_dilutions"/>
                          </ul>
                      </div>
                    </td>
                </tr>
                --%>
            </table>
            <%@ include file="permissions.jsp" %>
            <br/>

            <h1>Experiments</h1>
            <div class="note">
              <h2>Selected experiment(s):</h2>
              <div id="exptlist" class="elementList ui-corner-all">
                <c:if test="${not empty pool.experiments}">
                  <c:forEach items="${pool.experiments}" var="exp">
                    <div onMouseOver="this.className='dashboardhighlight'" onMouseOut="this.className='dashboard'" class="dashboard">
                      <span class='float-left'>
                      <input type="hidden" id="experiments${exp.experimentId}" value="${exp.experimentId}" name="experiments"/>
                      <b>Experiment:</b> <a href="<c:url value="/miso/experiment/${exp.experimentId}"/>">${exp.alias} (${exp.name})</a><br/>
                      <b>Description:</b> ${exp.description}<br/>
                      <b>Project:</b> <a href="<c:url value="/miso/project/${exp.study.project.projectId}"/>">${exp.study.project.alias} (${exp.study.project.name})</a><br/>
                      </span>
                      <span onclick='confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span>
                    </div>
                  </c:forEach>
                </c:if>
                <input type="hidden" value="on" name="_experiments"/>
              </div>
            </div>
            <h2 class="hrule">Select experiments:</h2>
            <table class="in">
              <tr>
                <td width="30%" style="vertical-align:top">
                  <label for="selectExpts"><b>Search experiments:</b></label><br/>
                  <input type="text" id='selectExpts' name="selectExpts" value="" onKeyup="timedFunc(poolSearchExperiments(this, 'ILLUMINA'),200);"/>
                  <div id='exptresult'></div>
                </td>
              </tr>
            </table>
            <br/>

            <h1>Dilutions</h1>
            <div class="note">
              <h2>Selected dilution(s):</h2>
              <div id="dillist" class="elementList ui-corner-all">
                <c:if test="${not empty pool.dilutions}">
                  <c:forEach items="${pool.dilutions}" var="dil">
                    <div onMouseOver="this.className='dashboardhighlight'" onMouseOut="this.className='dashboard'" class="dashboard">
                      <span style="float:left">
                      <input type="hidden" id="dilutions${dil.dilutionId}" value="${dil.name}" name="dilutions"/>
                      <b>Dilution:</b> ${dil.name}<br/>
                      <b>Library:</b> <a href="<c:url value="/miso/library/${dil.library.libraryId}"/>">${dil.library.alias} (${dil.library.name})</a><br/>
                      <b>Sample:</b> <a href="<c:url value="/miso/sample/${dil.library.sample.sampleId}"/>">${dil.library.sample.alias} (${dil.library.sample.name})</a><br/>
                      <c:choose>
                        <c:when test="${fn:length(pool.dilutions) > 1}">
                          <c:choose>
                            <c:when test="${not empty dil.library.tagBarcode}">
                              <b>Barcode:</b> ${dil.library.tagBarcode.sequence} (${dil.library.tagBarcode.name})
                              </span>
                              <span class="counter">
                                <img src="<c:url value='/styles/images/status/green.png'/>" border='0'>
                              </span>
                            </c:when>
                            <c:otherwise>
                              <b>Barcode:</b>
                              <a href="<c:url value="/miso/library/${dil.library.libraryId}"/>">Choose tag barcode</a>
                              </span>
                              <span class="counter">
                                <img src="<c:url value='/styles/images/status/red.png'/>" border='0'>
                              </span>
                            </c:otherwise>
                          </c:choose>
                        </c:when>
                        <c:otherwise>
                          </span>
                        </c:otherwise>
                      </c:choose>
                      <span onclick='confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span>
                    </div>
                  </c:forEach>
                </c:if>
              </div>
            </div>
            <input type="hidden" value="on" name="_dilutions"/>
        </form:form>

        <h2 class="hrule">Select dilutions:</h2>
        <table class="in">
            <tr>
                <td width="30%" style="vertical-align:top">
                    <label for="ldiInput"><b>Search dilution:</b></label><br/>
                    <input type="text" id='ldiInput' name="ldiInput" value="" onKeyup="timedFunc(poolSearchLibraryDilution(this, 'ILLUMINA'),200);"/>
                    <div id='libdilresult'></div>
                </td>
                <td width="30%" style="vertical-align:top">
                    <label for="ldiBarcodes"><b>Select dilutions by barcode(s):</b></label><br/>
                    <textarea id="ldiBarcodes" name="ldiBarcodes" rows="6" cols="40"></textarea><br/>
                    <button type="button" class="br-button ui-state-default ui-corner-all"
                            onclick="selectLibraryDilutionsByBarcodes(jQuery('#ldiBarcodes').val());">Select
                    </button>
                    <div id="dilimportlist"></div>
                </td>
                <td width="30%" style="vertical-align:top">
                    <b>Select dilutions by barcode file:</b><br/>
                    <form method='post'
                          id='ajax_upload_form'
                          action="<c:url value="/miso/upload/librarydilution-to-pool"/>"
                          enctype="multipart/form-data"
                          target="target_upload"
                          onsubmit="libraryDilutionFileUploadProgress();">
                        <input type="file" name="file"/><br/>
                        <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                    </form>
                    <iframe id='target_upload' name='target_upload' src='' style='display: none'></iframe>
                    <div id="statusdiv"></div>
                    <div id="dilimportfile"></div>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    addMaxDatePicker("creationDate", 0);

    var processLists = function() {
        // remove available li inputs
        jQuery('#list_1').find('li').find('input').each(function() {
            var jQueryitem = jQuery(this);
            jQueryitem.remove();
        });

        //add selected li inputs
        jQuery('#list_2').find('li').each(function() {
            var jQueryitem = jQuery(this);
            var h = jQueryitem.attr('dilName') + "<input type='hidden' id='dilutions" + jQueryitem.attr('dilId') + "' value='" + jQueryitem.attr('dilId') + "' name='dilutions'/>";
            jQueryitem.html(h);
        });
    }


    var updateDroppables = function(j) {
        jQuery("#list_2").droppable({
            accept: '.ui-draggable',
            activeClass: 'ui-state-hover',
            hoverClass: 'ui-state-active',
            tolerance: 'pointer',
            drop: function(event, ui) {
                if (jQuery(this).children().length == 0) {
                    jQuery(ui.draggable).find('input').attr("name", jQuery(this).attr("bind"));
                    jQuery(ui.draggable).appendTo(jQuery(this));
                }
            }
        });
    }

</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>