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
var projectArray;

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
          html += '<button class="inline" onclick="Tissue.update(\''+endpoint+'\', '+id+', \'TO\')">Update</button>&nbsp';
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
          html += '<button class="inline" onclick="Tissue.update(\''+endpoint+'\', '+id+', \'TC\')">Update</button>&nbsp';
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
          html += '<button class="inline" onclick="Tissue.update(\''+endpoint+'\', '+id+', \'TM\')">Update</button>&nbsp';
          html += '</form></td>';
          html += '<td><button class="delete-button inline" onclick="Tissue.confirmDelete(\''+endpoint+'\')">Delete</button></td>';
        }
        html += '</tr><tr><td><button id="newMaterial" onclick="Tissue.createNewRow(\'TM\')">Create New Material</button></tr></tr>';
        jQuery('#allMaterials').append(html);
      }
    });
  },
  
  getSamplePurposes: function() {
    jQuery.ajax({
      url: '/miso/rest/samplepurposes',
      success: function (data) {
        data.sort(function (a,b) {
          return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
        });
        
        var html = '';
        for (var i=0; i<data.length; i++) {
          var id = data[i]["id"];
          var alias = data[i]["alias"];
          var description = data[i]["description"];
          var endpoint = "/miso/rest/samplepurpose/" + id;
          
          html += '<tr class="SP"><td>';
          html += '<input id="SP_alias_'+ id +'" value="'+ alias +'"/>&nbsp;';
          html += '<input id="SP_description_'+ id +'" value="'+ description +'"/>&nbsp;';
          html += '<button class="inline" onclick="Tissue.update(\''+endpoint+'\', '+id+', \'SP\')">Update</button>&nbsp;';
          html += '</form></td>';
          html += '<td><button class="delete-button inline" onclick="Tissue.confirmDelete(\''+endpoint+'\')">Delete</button></td>';
        }
        html += '</tr><tr><td><button id="newPurpose" onclick="Tissue.createNewRow(\'SP\')">Create New Purpose</button></tr></tr>';
        jQuery('#allPurposes').append(html);
      }
    });
  },
  
  update: function (endpoint, option, id) {
    var alias = jQuery('#'+option+'_alias_'+ id).val();
    var description = jQuery('#'+option+'_description_'+id).val();
    if (!alias || !description) {
      alert("Neither alias nor description can be blank.");
      return null;
    }
    jQuery.ajax({
      url: endpoint,
      type: 'PUT',
      /*beforeSend: function (request) { 
        request.setRequestHeader("x-user", "admin");
        //request.setRequestHeader("x-signature", "K8r1yrb9KA4l3QX2AKN4B6t5tGY"); // this is hard-coded for testing purposes
        //request.setRequestHeader("x-url", "/miso/rest/tissueorigin/"+id);
      },*/
      data: JSON.stringify({ 'alias': alias, 'description': description }),
      contentType: 'application/json',
      success: function (data) {
        Utils.page.pageReload;
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
    var collection;
    switch (option) {
      case 'TO': collection = 'tissueorigin'; break;
      case 'TC': collection = 'tissuetype'; break;
      case 'TM': collection = 'tissuematerial'; break;
      case 'SP': collection = 'samplepurpose'; break;
    }
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

var QC = QC || {
  getQcDetails: function () {
    jQuery.ajax({
      url: '/miso/rest/qcpasseddetails',
      success: function (data) {
        data.sort(function (a, b){
          return (a.status > b.status) ? 1 : ((b.status > a.status) ? -1 : 0);
        });
        
        var html = '';
        for (var i=0; i<data.length; i++) {
          var id = data[i]["id"];
          var status = data[i]["status"];
          var description = data[i]["description"];
          var note = data[i]["noteRequired"];
          var endpoint = "/miso/rest/qcpasseddetail/" + id;
          
          html += '<tr class="QC"><td>';
          html += '<select id="QC_status+'+ id +'"><option value="1"'+ (status == 'true' ? ' selected' : '') +'>True</option>';
          html += '<option value="0"'+ (status == 'false' ? ' selected' : '') +'>False</option>';
          html += '<option value=""'+ (status == '' ? ' selected' : '') +'>Unknown</option></select>&nbsp;';
          html += '<input id="QC_description_'+ id +'" value="'+ description +'"/>&nbsp;';
          html += '<select id="QC_note_'+ id +'">';
          html += '<option value="1"'+ (note == 'true' ? ' selected' : '') +'>True</option>';
          html += '<option value="0"'+ (note == 'false' ? ' selected' : '') +'>False</option></select>'; 
          html += '<button class="inline" onclick="QC.update(\''+endpoint+'\, '+id+')">Update</button>&nbsp;';
          html += '</form></td>';
          html += '<td><button class="delete-button inline" onclick="Tissue.confirmDelete(\''+endpoint+'\')">Delete</button></td>';
        }
        html += '</tr><tr><td><button id="newDetails" onclick="QC.createNewRow()">Create New QC Details</button></td></tr>';
        jQuery('#allQcDetails').append(html);
      }
    });
  },
  
  update: function (endpoint, id) {
    var status = jQuery('#QC_status_'+id).val();
    var description = jQuery('#QC_description_'+id).val();
    var note = jQuery('#QC_note_'+id).val();
    if (!status || !description || !note) {
      alert("Neither status, description nor note required can be blank.");
      return null;
    }
    jQuery.ajax({
      url: endpoint,
      type: 'PUT',
      data: JSON.stringify({ 'status': status, 'description': description, 'noteRequired': note }),
      contentType: 'application/json',
      success: function (data) {
        Utils.page.pageReload;
      },
      error: function (response) {
        console.log(response.getResponseHeader());
      }
    });
  },
  
  createNewRow: function () {
    var html = '<tr><td>';
    html += '<select id="QC_status_new">';
    html += '<option value="1" selected>True</option><option value="0">False</option><option value="">Unknown</option></select>&nbsp;';
    html += '<input id="QC_description_new" value=""/>&nbsp;<select id="QC_note_new"><option value="1">True</option><option value="0">False</option></select>&nbsp;';
    html += '<button id="addNewQC" onclick="QC.addNew();">Add</button></td></tr>';
    jQuery('tr.QC:nth-last-child(2)').after(html);
    jQuery('#QC_status_new').focus();
  },
  
  addNew: function() {
    var status = jQuery('#QC_status_new').val();
    var description = jQuery('#QC_description_new').val();
    var note = jQuery('#QC_note_new').val();
    if (!status || !description || !note) {
      alert("Neither status, description nor note required can be blank.");
      return null;
    }
    jQuery.ajax({
      url: '/miso/rest/qcpasseddetail',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ 'status': status, 'description': description, 'noteRequired': note }),
      success: function() {
        Utils.page.pageReload;
      }
    });
  }
};

var Subproject = Subproject || {
  getSubprojects: function () {
    jQuery.get('/miso/rest/project', function (response) {
      projectArray = JSON.parse(response).sort(function (a, b) {
        return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
      });
    }).done(function() {
        jQuery.ajax({
          url: '/miso/rest/subprojects',
          success: function (data) {
            data.sort(function (a, b) {
              return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
            });
            
            var html = '';
            for (var i=0; i<data.length; i++) {
              var id = data[i]["id"];
              var alias = data[i]["alias"];
              var description = data[i]["description"];
              var project = data[i]["projectId"];
              var priority = data[i]["priority"];
              var endpoint = "/miso/rest/subproject/" + id;
              
              html += '<tr class="subP"><td><input id="subP_alias_'+ id +'" value="'+ alias +'"/>&nbsp;';
              html += '<input id="subP_description_'+ id +'" value="'+ description +'"/>&nbsp;';
              html += '<select id="subP_parentProject_'+ id +'">';
              for (var j=0; j<projectArray.length; j++) {
                html += '<option value="'+ projectArray[j]["projectId"] +'"'+ (projectArray[j]["projectId"] == project ? ' selected' : '') + '>' + projectArray[j]["alias"] + '</option>';
              }
              html += '</select>&nbsp;';
              html += '<select id="subP_priority_'+ id +'">';
              html += '<option value="1"'+ (priority ? ' selected' : '') +'>High</option>';
              html += '<option value="0"'+ (priority ? '' : ' selected')  +'>Standard</option></select>&nbsp;';
              html += '<button class="inline" onclick="Subproject.update(\''+endpoint+'\', '+id+')">Update</button>&nbsp;';
              html += '</form></td>';
              html += '<td><button class="delete-button inline" onclick="Tissue.confirmDelete(\''+endpoint+'\')">Delete</button></td>';
            }
            html += '</tr><tr><td><button id="newSubproject" onclick="Subproject.createNewRow()">Create New Subproject</button></td></tr>';
            jQuery('#allSubprojects').append(html);
          }
        });
      });
  },
  
  update: function (endpoint, id) {
    var alias = jQuery('#subP_alias_'+id).val();
    var description = jQuery('#subP_description_'+id).val();
    var parentProject = jQuery('#subP_parentProject_'+id).val();
    var priority = jQuery('#subP_priority_'+id).val();
    if (!alias || !description || !parentProject || !priority) {
      alert("Neither alias, description, project nor priority can be blank.");
      return null;
    }
    jQuery.ajax({
      url: endpoint,
      type: 'PUT',
      data: JSON.stringify({ 'alias': alias, 'description': description, 'parentProjectId': parentProject, 'priority': priority }),
      contentType: 'application/json',
      success: function (data) {
        Utils.page.pageReload;
      },
      error: function (response) {
        console.log(response.getResponseHeader());
      }
    });
  },
  
  createNewRow: function () {
    var html = '<tr><td>';
    html += '<input id="subP_alias_new">&nbsp;<input id="subP_description_new" value=""/>&nbsp;<select id="subP_parentProject_new">';
    for (var i=0; i<projectArray.length; i++) {
      html += '<option value="'+ projectArray[i]["projectId"] +'">' + projectArray[i]["alias"] + '</option>';
    }
    html += '</select>&nbsp;<select id="subP_priority_new"><option value="1">High</option><option value="0">Standard</option></select>&nbsp;';
    html += '<button id="addNewSubproject" onclick="Subproject.addNew();">Add</button></td></tr>';
    jQuery('tr.subP:nth-last-child(2)').after(html);
    jQuery('#subP_alias_new').focus();
  },
  
  addNew: function() {
    var alias = jQuery('#subP_alias_new').val();
    var description = jQuery('#subP_description_new').val();
    var parentProject = jQuery('#subP_parentProject_new').val();
    var priority = jQuery('#subP_priority_new').val();
    if (!alias || !description || !parentProject || !priority) {
      alert("Neither alias, description, project nor priority can be blank.");
      return null;
    }
    jQuery.ajax({
      url: '/miso/rest/subproject',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ 'alias': alias, 'description': description, 'parentProjectId': parentProject, 'priority': priority}),
      success: function() {
        Utils.page.pageReload;
      }
    });
  }
};
