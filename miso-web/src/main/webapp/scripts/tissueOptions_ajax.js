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

var Tissue = Tissue || {
  getTissueOrigins: function() {
    jQuery.ajax({
      url: '/miso/rest/tissueorigins',
      success: function (data) {
        var html = '';
        for (var i=0; i<data.length; i++) {
          var id = data[i]["id"];
          var alias = data[i]["alias"];
          var description = data[i]["description"];
          var endpoint = "/miso/rest/tissueorigin/" + id;
          
          html += '<tr><td>';
          html += '<input id="alias_'+ id +'" value="'+ alias +'"/>&nbsp;';
          html += '<input id="description_'+ id +'" value="'+ description +'"/>&nbsp';
          html += '<button onclick="Tissue.update(\''+endpoint+'\','+id+')">Update</button>&nbsp';
          html += '</form></td>';
          html += '<td><button class="delete-button" onclick="Tissue.confirmDelete(\''+endpoint+'\')">Delete</button></td>';
        }
        html += '</tr><tr><td><button id="newOrigin" onclick="Tissue.createNewRow()">Create New Origin</button></tr></tr>';
        jQuery('#allOrigins').append(html);
      }
    })
  },
  
  update: function (endpoint,id) {
    jQuery.ajax({
      url: endpoint,
      type: 'PUT',
      data: JSON.stringify({'alias': jQuery('#alias_'+id).val(), 'description': jQuery('#description_'+id).val()}),
      dataType: 'json',
      contentType: 'application/json',
      success: function (data) {
        
      },
      error: function (response) {
        console.log(response.getResponseHeader());
      }
    });
  },
  
  confirmDelete: function (endpoint) {
    if (confirm('Are you sure you wish to delete? This operation cannot be undone!')) {
      jQuery.ajax({
        url: endpoint,
        type: 'DELETE',
        success: function() {
          Utils.page.pageReload;
        },
        error: function(data) { console.log(data); }
      });
    }
  },
  
  createNewRow: function () {
    var html = '<tr><td><input id="alias_new" value=""/>&nbsp;<input id="description_new" value=""/>&nbsp;';
    html += '<button id="addNewOrigin" onclick="Tissue.addNewOrigin();">Add</button></td></tr>';
    jQuery('tr:nth-last-child(2)').after(html);
    jQuery('#alias_new').focus();
  },
  
  addNewOrigin: function () {
    var alias = jQuery('#alias_new').val();
    var description = jQuery('#description_new').val();
    if (alias == '' || description == '') {
      alert("Neither alias nor description can be blank.");
      return null;
    }
    jQuery.ajax({
      url: '/miso/rest/tissueorigin',
      type: 'POST',
      contentType: 'application/json',
      dataType: 'json',
      data: JSON.stringify({ 'alias': alias, 'description': description }),
      success: function() {
        Utils.page.pageReload;
      }
    });
  }
};