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
var tableLoadCounter = 0;

var Tissue = Tissue || {
  getTissueOrigins: function () {
    Options.makeXhrRequest('GET', '/miso/rest/tissueorigins', Tissue.createTissueOriginsTable);
  },
  
  createTissueOriginsTable: function (xhr) {
    var TOtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TO', 'allOrigins', 'tissueorigin', 'Origin', TOtable);
  },
  
  getTissueConditions: function () {
    Options.makeXhrRequest('GET', '/miso/rest/tissuetypes', Tissue.createTissueConditionsTable);
  },
  
  createTissueConditionsTable: function (xhr) {
    var TCtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TC', 'allConditions', 'tissuetype', 'Condition', TCtable);
  },
  
  getTissueMaterials: function () {
    Options.makeXhrRequest('GET', '/miso/rest/tissuematerials', Tissue.createTissueMaterialsTable);
  },
  
  createTissueMaterialsTable: function (xhr) {
    var TMtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TM', 'allMaterials', 'tissuematerial', 'Material', TMtable);
  },
  
  getSamplePurposes: function () {
    Options.makeXhrRequest('GET', '/miso/rest/samplepurposes', Tissue.createSamplePurposesTable);
  },
  
  createSamplePurposesTable: function (xhr) {
    var SPtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'SP', 'allPurposes', 'samplepurpose', 'Purpose', SPtable);
  },

  createTable: function (xhr, option, tableId, endpointWord, word, table) {
    tableBody = document.getElementById(tableId);
    tableBody.innerHTML = null;
    
    var data;
    if (xhr.responseText) {
      data = JSON.parse(xhr.responseText);
      data.sort(function (a,b) {
        return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
      });
    } // else collection is empty, so add only the "Add New" button
    if (data) {
      for (var i=0; i<data.length; i++) {
        id = data[i]["id"];
        alias = data[i]["alias"];
        description = data[i]["description"];
        endpoint = "/miso/rest/"+ endpointWord +"/" + id;
        
        table.push('<tr class="'+option+'"><td>');
        table.push(Options.createTextInput(option+'_alias_'+id, alias));
        table.push('</td><td>');
        table.push(Options.createTextInput(option+'_description_'+id, description));
        table.push('</td><td>');
        table.push(Options.createButton('Update', "Tissue.update('"+endpoint+"', "+id+", '"+option+"')"));
        table.push('</td><td>');
        table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"')"));
        table.push('</td></tr>');
      }
    }
    table.push('<tr id="new'+option+'RowButton" class="'+option+'"><td>');
    table.push(Options.createButton('New '+word, "Tissue.createNewRow('"+option+"')", 'newOrigin'));
    table.push('</td></tr>');
    tableBody.innerHTML = table.join('');
    tableLoadCounter += 1;
    
    if (tableLoadCounter > 6) { // if tables have all already been loaded once
      Options.displayCheckmark(tableBody.parentElement.id);
    }
  },
  
  update: function (endpoint, id, option) {
    var alias = document.getElementById(option+'_alias_'+ id).value;
    var description = document.getElementById(option+'_description_'+id).value;
    if (!alias || !description) {
      alert("Neither alias nor description can be blank.");
      return null;
    }
    Options.makeXhrRequest('PUT', endpoint, Options.reloadTable, JSON.stringify({ 'alias': alias, 'description': description }), option);
  },
  
  createNewRow: function (option) {
    var row = [];

    row.push('<tr><td>');
    row.push(Options.createTextInput(option+'_alias_new'));
    row.push('</td><td>');
    row.push(Options.createTextInput(option+'_description_new'));
    row.push('</td><td>');
    row.push(Options.createButton('Add', "Tissue.addNew('"+option+"')"));
    row.push('</td></tr>');

    document.getElementById('new'+option+'RowButton').insertAdjacentHTML('beforebegin', row.join(''));
    document.getElementById(option+'_alias_new').focus();
  },
  
  addNew: function (option) {
    var alias = document.getElementById(option+'_alias_new').value;
    var description = document.getElementById(option+'_description_new').value;
    if (alias == '' || description == '') {
      alert("Neither alias nor description can be blank.");
      return null;
    }
    var collection;
    if (option == 'TO') {
      collection = 'tissueorigin';
    } else if (option == 'TC') {
      collection = 'tissuetype'; 
    } else if (option == 'TM') {
      collection = 'tissuematerial'; 
    } else if (option == 'SP') {
      collection = 'samplepurpose';
    }
    Options.makeXhrRequest('POST', '/miso/rest/'+collection, Options.reloadTable, JSON.stringify({ 'alias': alias, 'description': description }), option);
  }
};

var QC = QC || {
  getQcDetails: function () {
    Options.makeXhrRequest('GET','/miso/rest/qcpasseddetails', QC.createQcDetailsTable);
  },
  
  createQcDetailsTable: function (xhr) {
    var tableBody = document.getElementById('allQcDetails');
    tableBody.innerHTML = null;
    
    var data;
    if (xhr.responseText) {
      data = JSON.parse(xhr.responseText);
      data.sort(function (a, b){
        return (a.description > b.description) ? 1 : ((b.description > a.description) ? -1 : 0);
      });
    } // else collection is empty, so render only the "Add New" button
   
    var table = [];
    var id, status, description, note, endpoint;

    if (data) {
      for (var i=0; i<data.length; i++) {
        id = data[i]["id"];
        status = data[i]["status"];
        description = data[i]["description"];
        note = data[i]["noteRequired"];
        endpoint = "/miso/rest/qcpasseddetail/" + id;
  
        table.push('<tr class="QC"><td>');
        table.push(Options.createTextInput('QC_description_'+id, description));
        table.push('</td><td>');
        table.push(QC.createStatusInput('QC_status_'+id, status));
        table.push('</td><td>');
        table.push(QC.createNoteReqdInput('QC_note_'+id, note));
        table.push('</td><td>');
        table.push(Options.createButton('Update', "QC.update('"+endpoint+"', "+id+")"));
        table.push('</td><td>');
        table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"')"));
        table.push('</td></tr>');
      }
    }
    table.push('<tr id="newQCRowButton" class="QC"><td>');
    table.push(Options.createButton('New QC Details', 'QC.createNewRow()', 'newDetails'));
    table.push('</td></tr>');
    tableBody.innerHTML = table.join('');
    tableLoadCounter += 1;
    
    if (tableLoadCounter > 6) { // if tables have all already been loaded once
      Options.displayCheckmark(tableBody.parentElement.id);
    }
  },

  createStatusInput: function (idValue, status) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    select.push('<option value="true"'+ (status === 'true' ? ' selected' : '') +'>True</option>');
    select.push('<option value="false"'+ (status === 'false' ? ' selected' : '') +'>False</option>');
    select.push('<option value=""'+ (status === '' ? ' selected' : '') +'>Unknown</option>');
    select.push('</select>');
    return select.join('');
  },

  createNoteReqdInput: function (idValue, note) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    select.push('<option value="true"'+ (note ? ' selected' : '') +'>Yes</option>');
    select.push('<option value="false"'+ (note ? '' : ' selected') +'>No</option>');
    select.push('</select>');
    return select.join('');
  },
  
  update: function (endpoint, id) {
    var description = document.getElementById('QC_description_'+id).value;
    var status = document.getElementById('QC_status_'+id).value;
    var note = document.getElementById('QC_note_'+id).value;
    if (!status || !description || !note) {
      alert("Neither status, description nor note required can be blank.");
      return null;
    }
    Options.makeXhrRequest('PUT', endpoint, Options.reloadTable, JSON.stringify({ 'status': status, 'description': description, 'noteRequired': note }), 'QC');
  },
  
  createNewRow: function () {
    var row = [];

    row.push('<tr><td>');
    row.push(Options.createTextInput('QC_description_new'));
    row.push('</td><td>');
    row.push(QC.createStatusInput('QC_status_new'));
    row.push('</td><td>');
    row.push(QC.createNoteReqdInput('QC_note_new'));
    row.push('</td><td>');
    row.push(Options.createButton('Add', "QC.addNew()"));
    row.push('</td></tr>');

    document.getElementById('newQCRowButton').insertAdjacentHTML('beforebegin', row.join(''));
    document.getElementById('QC_status_new').focus();
  },
  
  addNew: function() {
    var description = document.getElementById('QC_description_new').value;
    var status = document.getElementById('QC_status_new').value;
    var note = document.getElementById('QC_note_new').value;
    if (!description || !note) { //status can be blank, for QC Passed=Unknown
      alert("Neither description nor note required can be blank.");
      return null;
    }
    Options.makeXhrRequest('POST', '/miso/rest/qcpasseddetail', Options.reloadTable, JSON.stringify({ 'status': status, 'description': description, 'noteRequired': note }), 'QC');
  }
};

var Subproject = Subproject || {
  getSubprojects: function (xhr) {
    Options.makeXhrRequest('GET', '/miso/rest/project', Subproject.listProjects, xhr);
  },
  
  listProjects: function (pxhr) {
    projectArray = JSON.parse(pxhr.response).sort(function (a, b) {
      return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
    });
    Options.makeXhrRequest('GET', '/miso/rest/subprojects', Subproject.createSubprojectTable);
  },
  
  createSubprojectTable: function (xhr) {
    var tableBody = document.getElementById('allSubprojects')
    tableBody.innerHTML = null;

    var data;
    if (xhr.responseText) {
      data = JSON.parse(xhr.response).sort(function (a, b) {
        return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
      });
    }    

    var table = [];
    var id, alias, description, project, priority, endpoint, tr, td, aliasInput, descriptionInput, parentProjectInput, priorityInput, updateButton, td2, deleteButton, newRowButton;
    
    if (data) {
      for (var i=0; i<data.length; i++) {
        id = data[i]["id"];
        alias = data[i]["alias"];
        description = data[i]["description"];
        projectId = data[i]["parentProjectId"];
        projectName = projectArray.filter(function(p) { return p.projectId == projectId; })[0].alias;
        priority = data[i]["priority"];
        endpoint = "/miso/rest/subproject/" + id;
  
        table.push('<tr class="subP"><td>');
        table.push('<b><span id="subP_parentProject_'+id+'">'+ projectName +'</span></b>'); // not editable after creation
        table.push('</td><td>');
        table.push(Options.createTextInput('subP_alias_'+ id, alias));
        table.push('</td><td>');
        table.push(Options.createTextInput('subP_description_'+id, description));
        table.push('</td><td>');
        table.push(Subproject.createPrioritySelect('subP_priority_'+ id, priority));
        table.push('</td><td>');
        table.push(Options.createButton('Update', "Subproject.update('"+endpoint+"', "+id+")"));
        table.push('</td><td>');
        table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"')"));
        table.push('</td></tr>');
      }
    }
    table.push('<tr id="newSubpRowButton" class="subP"><td>');
    table.push(Options.createButton('New Subproject', 'Subproject.createNewRow()', 'newDetails'));
    table.push('</td></tr>');
    tableBody.innerHTML = table.join('');
    tableLoadCounter += 1;
    
    if (tableLoadCounter > 6) { // if tables have all already been loaded once
      Options.displayCheckmark(tableBody.parentElement.id);
    }
  },
  
  createProjectsSelect: function(idValue, projectId) {
    var selectedProjectId = projectId || '';
    var select = [];
    select.push('<select id="'+ idValue +'">');
    for (var j=0;j<projectArray.length;j++) {
      select.push('<option value="'+ projectArray[j]["projectId"] +'"');
      if (projectArray[j]["projectId"] == selectedProjectId) select.push(' selected=""');
      select.push('>'+ projectArray[j]["alias"] +'</option>');
    }
    select.push('</select>');
    return select.join('');
  },
  
  createPrioritySelect: function(idValue, priority) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    select.push('<option value="true"'+ (priority ? ' selected' : '') +'>High</option>');
    select.push('<option value="false"'+ (priority ? '' : ' selected') +'>Standard</option>');
    select.push('</select>')
    return select.join('');
  },
  
  update: function (endpoint, id) {
    var alias = document.getElementById('subP_alias_'+id).value;
    var description = document.getElementById('subP_description_'+id).value;
    var priority = document.getElementById('subP_priority_'+id).value;
    if (!alias || !description || !priority) {
      alert("Neither alias, description, nor priority can be blank.");
      return null;
    }
    Options.makeXhrRequest('PUT', endpoint, Options.reloadTable, JSON.stringify({ 'alias': alias, 'description': description, 'priority': priority }), 'SubP');
  },
  
  createNewRow: function () {
    var row = [];

    row.push('<tr><td>');
    row.push(Subproject.createProjectsSelect('subP_parentProject_new'));
    row.push('</td><td>');
    row.push(Options.createTextInput('subP_alias_new'));
    row.push('</td><td>');
    row.push(Options.createTextInput('subP_description_new'));
    row.push('</td><td>');
    row.push(Subproject.createPrioritySelect('subP_priority_new'));
    row.push('</td><td>');
    row.push(Options.createButton('Add', "Subproject.addNew()"));
    row.push('</td></tr>');

    document.getElementById('newSubpRowButton').insertAdjacentHTML('beforebegin', row.join(''));
    document.getElementById('subP_alias_new').focus();
  },
  
  addNew: function() {
    var alias = document.getElementById('subP_alias_new').value;
    var description = document.getElementById('subP_description_new').value;
    var parentProjectId = document.getElementById('subP_parentProject_new').value;
    var priority = document.getElementById('subP_priority_new').value;
    if (!alias || !description || !parentProjectId || !priority) {
      alert("Neither alias, description, project nor priority can be blank.");
      return null;
    }
    Options.makeXhrRequest('POST', '/miso/rest/subproject', Options.reloadTable, JSON.stringify({ 'alias': alias, 'description': description, 'parentProjectId': parentProjectId, 'priority': priority}), 'SubP');
  }
};

var Options = Options || {
  makeXhrRequest: function (method, endpoint, callback, data, callbackarg) {
    var expectedStatus;
    var unauthorizedStatus = 401;
    if (method == 'POST') {
      expectedStatus = [201];
    } else {
      expectedStatus = [200, 404];
    }
    var xhr = new XMLHttpRequest();
    xhr.open(method, endpoint);
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (expectedStatus.indexOf(xhr.status) != -1) {
          if (!callback) {
            document.location.reload(); 
          } else {
            data ? ( callbackarg ? callback(callbackarg) : callback() ) : callback(xhr) ;
          }
        } else if (xhr.status === unauthorizedStatus) {
          alert("You are not authorized to view this page.");
        } else {
          alert("Sorry, something went wrong. Please try again. If the issue persists, contact your administrator.");
        }
      }
    }
    xhr.setRequestHeader('Content-Type', 'application/json');
    data ? xhr.send(data) : xhr.send();
  },

  confirmDelete: function (endpoint) {
    if (confirm('Are you sure you wish to delete? This operation cannot be undone!')) {
      Options.makeXhrRequest('DELETE', endpoint);
    }
  },

  createTextInput: function(idValue, valueText) {
    return '<input type="text" id="'+ idValue +'" value="'+ (valueText ? valueText : '') +'"></input>';
  },

  createButton: function(valueText, onclickFunction, idText) {
    return '<button class="inline"'+(idText ? ' id="'+ idText +'"' : '')+' onclick="'+ onclickFunction +'">'+ valueText +'</button>';
  },
  
  reloadTable: function (option) {
    var reloadTableFunc, table;
    if (option == 'TO') {
      reloadTableFunc = Tissue.getTissueOrigins;
      table = document.getElementById('allOriginsTable');
    } else if (option == 'TC') {
      reloadTableFunc = Tissue.getTissueConditions;
      table = document.getElementById('allConditionsTable');
    } else if (option == 'TM') {
      reloadTableFunc = Tissue.getTissueMaterials;
      table = document.getElementById('allMaterialsTable');
    } else if (option == 'SP') {
      reloadTableFunc = Tissue.getSamplePurposes;
      table = document.getElementById('allPurposesTable');
    } else if (option == 'QC') {
      reloadTableFunc = QC.getQcDetails;
      table = document.getElementById('allQcDetailsTable');
    } else if (option == 'SubP') {
      reloadTableFunc = Subproject.getSubprojects;
      table = document.getElementById('allSubprojectsTable');
    }
    reloadTableFunc();
  },
  
  displayCheckmark: function (tableId) {
    var table = document.getElementById(tableId);
    table.setAttribute('style', 'float:left');
    var checkmark = '<div><img id="checkmark"  src="/styles/images/ok.png"/></div><div class="clear"></div>';
    table.insertAdjacentHTML('afterend', checkmark);
    var check = jQuery('#checkmark');
    check.fadeOut("slow", function() {
      jQuery(this).remove();
      table.setAttribute('style', 'clear:both');
    });
  }
};
