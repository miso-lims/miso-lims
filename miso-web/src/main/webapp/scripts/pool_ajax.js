/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

var Pool = {};

Pool.ui = {
  showPoolNoteDialog: function(poolId) {
    var self = this;
    jQuery('#addNoteDialog')
        .html(
            "<form>"
                + "<fieldset class='dialog'>"
                + "<label for='internalOnly'>Internal Only?</label>"
                + "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />"
                + "<br/>" + "<label for='notetext'>Text</label>"
                + "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' autofocus />"
                + "</fieldset></form>");

    jQuery('#addNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function() {
          if (jQuery('#notetext').val().length > 0) {
            Utils.notes.addNote('pool', poolId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          } else {
            jQuery('#notetext').focus();
          }
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  deletePoolNote: function(poolId, noteId) {
    var deleteIt = function() {
      Utils.notes.deleteNote('pool', poolId, noteId);
    }
    Utils.showConfirmDialog('Delete Note', 'Delete', ["Are you sure you want to delete this note?"], deleteIt);
  }
};
