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
    Options.makeXhrRequest('GET', '/miso/rest/tissueorigins', Tissue.createTissueOriginsTable);
  },
  
  createTissueOriginsTable: function (xhr) {
    var TOtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TO', 'allOrigins', 'tissueorigin', 'Origin', TOtable);
  },
  
  getTissueConditions: function() {
    Options.makeXhrRequest('GET', '/miso/rest/tissuetypes', Tissue.createTissueConditionsTable);
  },
  
  createTissueConditionsTable: function (xhr) {
    var TCtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TC', 'allConditions', 'tissuetype', 'Condition', TCtable);
  },
  
  getTissueMaterials: function() {
    Options.makeXhrRequest('GET', '/miso/rest/tissuematerials', Tissue.createTissueMaterialsTable);
  },
  
  createTissueMaterialsTable: function (xhr) {
    var TMtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TM', 'allMaterials', 'tissuematerial', 'Material', TMtable);
  },
  
  getSamplePurposes: function() {
    Options.makeXhrRequest('GET', '/miso/rest/samplepurposes', Tissue.createSamplePurposesTable);
  },
  
  createSamplePurposesTable: function (xhr) {
    var SPtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'SP', 'allPurposes', 'samplepurpose', 'Purpose', SPtable);
  },

  createTable: function (xhr, option, tableId, endpointWord, word, table) {
    var data = JSON.parse(xhr.responseText);
    data.sort(function (a,b) {
      return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
    });

    document.getElementById(tableId).innerHTML = null;
    for (var i=0; i<data.length; i++) {
      id = data[i]["id"];
      alias = data[i]["alias"];
      description = data[i]["description"];
      endpoint = "/miso/rest/"+ endpointWord +"/" + id;
      
      table.push('<tr class="'+option+'"><td>');
      table.push(Options.createTextInput(option+'_alias_'+id, alias));
      table.push(Options.createTextInput(option+'_description_'+id, description));
      table.push(Options.createButton('Update', "Tissue.update('"+endpoint+"', "+id+", '"+option+"')"));
      table.push('</td><td>');
      table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"')"));
      table.push('</td></tr>');
    }
    table.push('<tr id="new'+option+'RowButton"><td>');
    table.push(Options.createButton('Create New '+word, "Tissue.createNewRow('"+option+"')", 'newOrigin'));
    table.push('</td></tr>');
    document.getElementById(tableId).innerHTML = table.join('');
  },
  
  update: function (endpoint, id, option) {
    var alias = document.getElementById(option+'_alias_'+ id).value;
    var description = document.getElementById(option+'_description_'+id).value;
    if (!alias || !description) {
      alert("Neither alias nor description can be blank.");
      return null;
    }
    var callback;
    switch (option) {
      case 'TO': callback = Tissue.getTissueOrigins;
      case 'TC': callback = Tissue.getTissueConditions;
      case 'TM': callback = Tissue.getTissueMaterials;
      case 'SP': callback = Tissue.getSamplePurposes;
    }
    Options.makeXhrRequest('PUT', endpoint, callback, JSON.stringify({ 'alias': alias, 'description': description }));
  },
  
  createNewRow: function (option) {
    var row = [];

    row.push('<tr><td>');
    row.push(Options.createTextInput(option+'_alias_new'));
    row.push(Options.createTextInput(option+'_description_new'));
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
    var collection, callback;
    switch (option) {
      case 'TO': 
        collection = 'tissueorigin'; 
        callback = Tissue.getTissueOrigins;
        break;
      case 'TC': 
        collection = 'tissuetype'; 
        callback = Tissue.getTissueConditions;
        break;
      case 'TM': 
        collection = 'tissuematerial'; 
        callback = Tissue.getTissueMaterials;
        break;
      case 'SP': 
        collection = 'samplepurpose'; 
        callback = Tissue.getSamplePurposes;
        break;
    }
    Options.makeXhrRequest('POST', '/miso/rest/'+collection, callback, JSON.stringify({ 'alias': alias, 'description': description }));
  }
};

var QC = QC || {
  getQcDetails: function () {
    Options.makeXhrRequest('GET','/miso/rest/qcpasseddetails', QC.createQcDetailsTable);
  },
  
  createQcDetailsTable: function (xhr) {
    var data = JSON.parse(xhr.responseText);
    data.sort(function (a, b){
      return (a.status > b.status) ? 1 : ((b.status > a.status) ? -1 : 0);
    });
   
    document.getElementById('allQcDetails').innerHTML = null;

    var table = [];
    var id, status, description, note, endpoint;

    for (var i=0; i<data.length; i++) {
      id = data[i]["id"];
      status = data[i]["status"];
      description = data[i]["description"];
      note = data[i]["noteRequired"];
      endpoint = "/miso/rest/qcpasseddetail/" + id;

      table.push('<tr class="QC"><td>');
      table.push(QC.createStatusInput('QC_status_'+id, status));
      table.push(Options.createTextInput('QC_description_'+id, description));
      table.push(QC.createNoteReqdInput('QC_note_'+id, note));
      table.push(Options.createButton('Update', "QC.update('"+endpoint+"', "+id+")"));
      table.push('</td><td>');
      table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"')"));
      table.push('</td></tr>');
    }
    table.push('<tr id="newQCRowButton"><td>');
    table.push(Options.createButton('Create New QC Details', 'QC.createNewRow()', 'newDetails'));
    table.push('</td></tr>');
    document.getElementById('allQcDetails').innerHTML = table.join('');
  },

  createStatusInput: function (idValue, status) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    select.push('<option value="true"'+ (status === 'true' ? select.push(Options.selectedOpt) : '') +'>True</option>');
    select.push('<option value="false"'+ (status === 'false' ? select.push(Options.selectedOpt) : '') +'>False</option>');
    select.push('<option value=""'+ (status === '' ? select.push(Options.selectedOpt) : '') +'>Unknown</option>');
    select.push('</select>');
    return select.join('');
  },

  createNoteReqdInput: function (idValue, note) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    select.push('<option value="1"'+ (note ? select.push(Options.selectedOpt) : '') +'>Yes</option>');
    select.push('<option value="0"'+ (note ? '' : select.push(Options.selectedOpt)) +'>No</option>');
    select.push('</select>');
    return select.join('');
  },
  
  update: function (endpoint, id) {
    var status = document.getElementById('QC_status_'+id).value;
    var description = document.getElementById('QC_description_'+id).value;
    var note = document.getElementById('QC_note_'+id).value;
    if (!status || !description || !note) {
      alert("Neither status, description nor note required can be blank.");
      return null;
    }
    Options.makeXhrRequest('PUT', endpoint, QC.getQcDetails, JSON.stringify({ 'status': status, 'description': description, 'noteRequired': note }));
  },
  
  createNewRow: function () {
    var row = [];

    row.push('<tr><td>');
    row.push(QC.createStatusInput('QC_status_new'));
    row.push(Options.createTextInput('QC_description_new'));
    row.push(QC.createNoteReqdInput('QC_note_new'));
    row.push(Options.createButton('Add', "QC.addNew()"));
    row.push('</td></tr>');

    document.getElementById('newQCRowButton').insertAdjacentHTML('beforebegin', row.join(''));
    document.getElementById('QC_status_new').focus();
  },
  
  addNew: function() {
    var status = document.getElementById('QC_status_new').value;
    var description = document.getElementById('QC_description_new').value;
    var note = document.getElementById('QC_note_new').value;
    if (!status || !description || !note) {
      alert("Neither status, description nor note required can be blank.");
      return null;
    }
    Options.makeXhrRequest('POST', '/miso/rest/qcpasseddetail', QC.getQcDetails, JSON.stringify({ 'status': status, 'description': description, 'noteRequired': note }));
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
    var data = JSON.parse(xhr.response).sort(function (a, b) {
      return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
    });
    
    document.getElementById('allSubprojects').innerHTML = null;

    var table = [];
    var id, alias, description, project, priority, endpoint, tr, td, aliasInput, descriptionInput, parentProjectInput, priorityInput, updateButton, td2, deleteButton, newRowButton;
    
    for (var i=0; i<data.length; i++) {
      id = data[i]["id"];
      alias = data[i]["alias"];
      description = data[i]["description"];
      project = data[i]["projectId"];
      priority = data[i]["priority"];
      endpoint = "/miso/rest/subproject/" + id;

      table.push('<tr class="subP"><td>');
      table.push(Options.createTextInput('subP_alias_'+ id, alias));
      table.push(Options.createTextInput('subP_description_'+id, description));
      table.push(Subproject.createProjectsSelect('subP_parentProject_'+ id, project));
      table.push(Subproject.createPrioritySelect('subP_priority_'+ id, priority));
      table.push(Options.createButton('Update', "Subproject.update('"+endpoint+"', "+id+")"));
      table.push('</td><td>');
      table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"')"));
      table.push('</td></tr>');
    }
    table.push('<tr id="newSubpRowButton"><td>');
    table.push(Options.createButton('Create New Subproject', 'Subproject.createNewRow()', 'newDetails'));
    table.push('</td></tr>');
    document.getElementById('allSubprojects').innerHTML = table.join('');
  },
  
  createProjectsSelect: function(idValue, project) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    for (var j=0;j<projectArray.length;j++) {
      select.push('<option value="'+ projectArray[j]["projectId"] +'"');
      if (project) {
        projectArray[j]["projectId"] == project ? select.push(' selected="selected"') : null ;
      }
      select.push('>'+ projectArray[j]["alias"] +'</option>');
    }
    select.push('</select>');
    return select.join('');
  },
  
  createPrioritySelect: function(idValue, priority) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    select.push('<option value="1"'+ (priority ? 'selected="selected"' : '') +'>High</option>');
    select.push('<option value="0"'+ (priority ? '' : 'selected="selected"') +'>Standard</option>');
    select.push('</select>')
    return select.join('');
  },
  
  update: function (endpoint, id) {
    var alias = document.getElementById('subP_alias_'+id).value;
    var description = document.getElementById('subP_description_'+id).value;
    var parentProject = document.getElementById('subP_parentProject_'+id).value;
    var priority = document.getElementById('subP_priority_'+id).value;
    if (!alias || !description || !parentProject || !priority) {
      alert("Neither alias, description, project nor priority can be blank.");
      return null;
    }
    Options.makeXhrRequest('PUT', endpoint, Subproject.getSubprojects, JSON.stringify({ 'alias': alias, 'description': description, 'parentProjectId': parentProject, 'priority': priority }));
  },
  
  createNewRow: function () {
    var row = [];

    row.push('<tr><td>');
    row.push(Options.createTextInput('subP_alias_new'));
    row.push(Options.createTextInput('subP_description_new'));
    row.push(Subproject.createProjectsSelect('subP_parentProject_new'));
    row.push(Subproject.createPrioritySelect('subP_priority_new'));
    row.push(Options.createButton('Add', "Subproject.addNew()"));
    row.push('</td></tr>');

    document.getElementById('newSubpRowButton').insertAdjacentHTML('beforebegin', row.join(''));
    document.getElementById('subP_alias_new').focus();
  },
  
  addNew: function() {
    var alias = document.getElementById('subP_alias_new').value;
    var description = document.getElementById('subP_description_new').value;
    var parentProject = document.getElementById('subP_parentProject_new').value;
    var priority = document.getElementById('subP_priority_new').value;
    if (!alias || !description || !parentProject || !priority) {
      alert("Neither alias, description, project nor priority can be blank.");
      return null;
    }
    Options.makeXhrRequest('POST', '/miso/rest/subproject', Subproject.getSubprojects, JSON.stringify({ 'alias': alias, 'description': description, 'parentProjectId': parentProject, 'priority': priority}));
  }
};

var Options = Options || {
  makeXhrRequest: function (method, endpoint, callback, data) {
    var expectedStatus = (method == 'POST' ? 201 : 200);
    if (!callback) callback = document.location.reload;
    var xhr = new XMLHttpRequest();
    xhr.open(method, endpoint);
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status === expectedStatus) {
          data ? callback() : callback(xhr) ;
        } else {
          console.log(xhr.response);
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
  }
};
