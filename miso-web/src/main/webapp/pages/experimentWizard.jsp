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
  User: bianx

--%>
<%@ include file="../header.jsp" %>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>

<div id="maincontent">
    <div id="contentcolumn">
        <div id="result">
            <form id="experimentWizardForm" method="POST" autocomplete="off">
                <h1>
                    Create Experiment Wizard
                    <button type="button" class="fg-button ui-state-default ui-corner-all"
                            onclick="checkform('experimentWizardForm');">Save
                    </button>
                </h1>
                <div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
                    <div id="note_arrowclick" class="toggleLeft"></div>
                </div>
                <div id="notediv" class="note" style="display:none;">An experiment contains design information about the
                    sequencing experiment. Experiments are associated with Runs which contain the actual sequencing
                    results.
                    A Pool is attached to an Experiment which is then assigned to an instrument partition
                    (lane/chamber).
                </div>
                <h2>Study Information</h2>
                <table class="in">
                    <input type="hidden" name="projectId" value="${projectId}"/>
                    <tr>
                        <td>Study Type:</td>
                        <td>
                            <select name="studyType">
                                ${studyTypes}
                            </select>
                        </td>
                    </tr>
                </table>
                <br/>
                <hr/>

                <div id="new1">
                </div>

            </form>
        </div>
    </div>
</div>

<script type="text/javascript">
    addMaxDatePicker("creationDate", 0);

    jQuery(function() {
        jQuery("#poolList").sortable({
            revert: true
        });
        jQuery(".draggable").draggable({
            connectToSortable: '#poolList',
            revert: true,
            scroll: false
        });
        jQuery("ul, li").disableSelection();

        updateDroppables("#list_2");

        jQuery(".elementListDroppable").droppable({
            accept: '.draggable',
            activeClass: 'ui-state-hover',
            hoverClass: 'ui-state-active',
            tolerance: 'pointer',
            drop: function(event, ui) {
                jQuery(ui.draggable).find('input').attr("name", "");
                jQuery(ui.draggable).appendTo(jQuery(this));
            }
        });

    });

    jQuery(document).ready(function() {
        addExperimentForm('1');

        jQuery("#tabs").tabs();

        jQuery('#title').simplyCountable({
            counter: '#titlecounter',
            countType: 'characters',
            maxCount: ${maxLengths['title']},
            countDirection: 'down'
        });

        jQuery('#alias').simplyCountable({
            counter: '#aliascounter',
            countType: 'characters',
            maxCount: ${maxLengths['alias']},
            countDirection: 'down'
        });

        jQuery('#description').simplyCountable({
            counter: '#descriptioncounter',
            countType: 'characters',
            maxCount: ${maxLengths['description']},
            countDirection: 'down'
        });
    });

    function checkform(form) {
        var bool = true;
        jQuery('.needcheck').each(function() {
            if (jQuery(this).val() == '') {
                bool = false;
            }
        });
        if (bool) {
            var idarray = [];
            jQuery('.expids').each(function() {
            if (jQuery(this).val() == '') {
                bool = false;
            }
        });
            wizardAddExperiment(form);
        }
        else {
            alert("Title, Alias and Description cannot be empty");
        }
    }

    function confirmRemoveExperiment(id) {
        if (confirm("Are you sure you wish to remove this Experiment?")) {
            var obj = jQuery('#exp' + id);
            obj.remove();
        }
    }

</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>