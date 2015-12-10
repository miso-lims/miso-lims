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
        data.sort(function (a,b) {
          return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
        });
        
        var html = '';
        for (var i=0; i<data.length; i++) {
          var id = data[i]["id"];
          var alias = data[i]["alias"];
          var description = data[i]["description"];
          var endpoint = "/miso/rest/tissueorigin/" + id;
          
          html += '<tr class="TO"><td>';
          html += '<input id="TO_alias_'+ id +'" value="'+ alias +'"/>&nbsp;';
          html += '<input id="TO_description_'+ id +'" value="'+ description +'"/>&nbsp';
          html += '<button class="inline" onclick="Tissue.update(\''+endpoint+'\','+id+', \'TO\')">Update</button>&nbsp';
          html += '</form></td>';
          html += '<td><button class="delete-button inline" onclick="Tissue.confirmDelete(\''+endpoint+'\')">Delete</button></td>';
        }
        html += '</tr><tr><td><button id="newOrigin" onclick="Tissue.createNewRow(\'TO\')">Create New Origin</button></tr></tr>';
        jQuery('#allOrigins').append(html);
      }
    });
  },
  
  getTissueConditions: function() {
    jQuery.ajax({
      url: '/miso/rest/tissuetypes',
      success: function (data) {
        data.sort(function (a,b) {
          return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
        });
        
        var html = '';
        for (var i=0; i<data.length; i++) {
          var id = data[i]["id"];
          var alias = data[i]["alias"];
          var description = data[i]["description"];
          var endpoint = "/miso/rest/tissuetype/" + id;
          
          html += '<tr class="TC"><td>';
          html += '<input id="TC_alias_'+ id +'" value="'+ alias +'"/>&nbsp;';
          html += '<input id="TC_description_'+ id +'" value="'+ description +'"/>&nbsp';
          html += '<button class="inline" onclick="Tissue.update(\''+endpoint+'\','+id+', \'TC\')">Update</button>&nbsp';
          html += '</form></td>';
          html += '<td><button class="delete-button inline" onclick="Tissue.confirmDelete(\''+endpoint+'\')">Delete</button></td>';
        }
        html += '</tr><tr><td><button id="newCondition" onclick="Tissue.createNewRow(\'TC\')">Create New Condition</button></tr></tr>';
        jQuery('#allConditions').append(html);
      }
    });
  },
  
  getTissueMaterials: function() {
    jQuery.ajax({
      url: '/miso/rest/tissuematerials',
      success: function (data) {
        data.sort(function (a,b) {
          return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
        });
        
        var html = '';
        for (var i=0; i<data.length; i++) {
          var id = data[i]["id"];
          var alias = data[i]["alias"];
          var description = data[i]["description"];
          var endpoint = "/miso/rest/tissuematerial/" + id;
          
          html += '<tr class="TM"><td>';
          html += '<input id="TM_alias_'+ id +'" value="'+ alias +'"/>&nbsp;';
          html += '<input id="TM_description_'+ id +'" value="'+ description +'"/>&nbsp';
          html += '<button class="inline" onclick="Tissue.update(\''+endpoint+'\','+id+', \'TM\')">Update</button>&nbsp';
          html += '</form></td>';
          html += '<td><button class="delete-button inline" onclick="Tissue.confirmDelete(\''+endpoint+'\')">Delete</button></td>';
        }
        html += '</tr><tr><td><button id="newMaterial" onclick="Tissue.createNewRow(\'TM\')">Create New Material</button></tr></tr>';
        jQuery('#allMaterials').append(html);
      }
    });
  },
  
  update: function (endpoint,id, option) {
    var alias = jQuery('#'+option+'_alias_new').val();
    var description = jQuery('#'+option+'_description_new').val();
    if (alias == '' || description == '') {
      alert("Neither alias nor description can be blank.");
      return null;
    }
    jQuery.ajax({
      url: endpoint,
      type: 'PUT',
      /*beforeSend: function (request) { 
        request.setRequestHeader("x-user", "admin");
        request.setRequestHeader("x-signature", "K8r1yrb9KA4l3QX2AKN4B6t5tGY"); // this is hard-coded for testing purposes
        request.setRequestHeader("x-url", "/miso/rest/tissueorigin/"+id);
      },*/
      data: JSON.stringify({'alias': jQuery('#'+option+'_alias_'+id).val(), 'description': jQuery('#'+option+'_description_'+id).val()}),
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
  
  createNewRow: function (option) {
    var html = '<tr><td><input id="'+option+'_alias_new" value=""/>&nbsp;<input id="'+option+'_description_new" value=""/>&nbsp;';
    html += '<button id="addNew'+option+'" onclick="Tissue.addNew(\''+option+'\');">Add</button></td></tr>';
    jQuery('tr.'+option+':nth-last-child(2)').after(html);
    jQuery('#'+option+'_alias_new').focus();
  },
  
  addNew: function (option) {
    var alias = jQuery('#'+option+'_alias_new').val();
    var description = jQuery('#'+option+'_description_new').val();
    if (alias == '' || description == '') {
      alert("Neither alias nor description can be blank.");
      return null;
    }
    var collection = ((option == 'TO') ? 'tissueorigin' : ((option == 'TC') ? 'tissuetype' : 'tissuematerial'));
    jQuery.ajax({
      url: '/miso/rest/'+ collection,
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ 'alias': alias, 'description': description }),
      success: function() {
        Utils.page.pageReload;
      }
    });
  }
};