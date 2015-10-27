/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

var Sequencer = Sequencer || {};

Sequencer.ui = {
  insertSequencerReferenceRow : function() {
    var self = this;
    Fluxion.doAjax(
      'sequencerReferenceControllerHelperService',
      'listPlatforms',
      {'url':ajaxurl},
      {'doOnSuccess':self.processSequencerReferenceRow}
    );
  },

  processSequencerReferenceRow : function(json) {
    $('sequencerReferenceTable').insertRow(1);

    var column1 = $('sequencerReferenceTable').rows[1].insertCell(-1);
    column1.innerHTML = "<i>Unsaved</i>";
    var column2 = $('sequencerReferenceTable').rows[1].insertCell(-1);
    column2.innerHTML = "<input id='sequencername' name='sequencername' type='text'/>";
    var column3 = $('sequencerReferenceTable').rows[1].insertCell(-1);
    column3.innerHTML = "<select id='platforms' name='platform'>" +json.platforms+ "</select>";
    var column4 = $('sequencerReferenceTable').rows[1].insertCell(-1);
    column4.innerHTML = "<input id='server' name='server' type='text' onkeyup='Sequencer.ui.validateServer(this)'/>";
    var column5 = $('sequencerReferenceTable').rows[1].insertCell(-1);
    column5.innerHTML = "<div id='available'></div>";
    var column6 = $('sequencerReferenceTable').rows[1].insertCell(-1);
    column6.id = "addTd";
    column6.innerHTML = "Add";
  },

  validateServer : function(t) {
    $('available').innerHTML="<div align='center'><img src='../../styles/images/ajax-loader.gif'/></div>";

    if (t.value != t.lastValue) {
      if (t.timer) clearTimeout(t.timer);

      t.timer = setTimeout(function () {
        Fluxion.doAjax(
          'sequencerReferenceControllerHelperService',
          'checkServerAvailability',
          {'server':t.value, 'url':ajaxurl},
          {"doOnSuccess": function(json) {
            $('available').innerHTML = json.html;
            if (json.html == "OK") {
              $('available').setAttribute("style", "background-color:green");
              $('addTd').innerHTML = "<a href='javascript:void(0);' onclick='Sequencer.ui.addSequencerReference();'/>Add</a>";
            }
            else {
              $('available').setAttribute("style", "background-color:red");
            }
          }
        });
      }, 200);
      t.lastValue = t.value;
    }
  },

  addSequencerReference : function() {
    var f = Utils.mappifyForm("addReferenceForm");
    Fluxion.doAjax(
      'sequencerReferenceControllerHelperService',
      'addSequencerReference',
      {
        'name':f.sequencername,
        'platform':f.platform,
        'server':f.server,
        'url':ajaxurl},
      {'doOnSuccess':Utils.page.pageReload}
    );
  },

  deleteSequencerReference : function(refId, successfunc) {
    if (confirm("Are you sure you really want to delete sequencer reference "+refId+"? This operation is permanent!")) {
      Fluxion.doAjax(
        'sequencerReferenceControllerHelperService',
        'deleteSequencerReference',
        {'refId':refId, 'url':ajaxurl},
        {'doOnSuccess':function(json) {
            successfunc();
          }
        }
      );
    }
  }
};
