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

var Defaults = Defaults || {
  all: null,
  
  getDefaults: function () {
    Options.makeXhrRequest('GET', '/miso/rest/ui/sampleoptions', Defaults.makeTables);
  },
  
  makeTables: function (xhr) {
    if (xhr.status == 200) {
      Defaults.all = JSON.parse(xhr.responseText);
    
      Tissue.createTissueMaterialsTable();
      Tissue.createSamplePurposesTable();
      Subproject.getReferenceGenomeOptions();
      Subproject.getProjects();
      Lab.createLabsTable();
      Lab.createInstitutesTable();
    }
    // expand table if anchor link was clicked
    var clickedAnchor = window.location.hash.substr(1);
    if (clickedAnchor && document.getElementById(clickedAnchor)) {
      document.getElementById(clickedAnchor).click();
    }
    
    var storedDiv = localStorage.tablediv;
    if (storedDiv && document.getElementById(storedDiv)) {
      document.getElementById(storedDiv).click();
    }
  }
};

var Tissue = Tissue || {
  createTissueMaterialsTable: function (xhr) {
    var TMtable = [];
    var id, alias, description, endpoint, temp, rowId;
    
    // if data is coming in from AJAX request, store it in Defaults.all
    if (xhr) Defaults.all.tissueMaterialsDtos = JSON.parse(xhr.responseText);
    Tissue.createTable(Defaults.all.tissueMaterialsDtos, 'TM', 'allMaterials', 'Material', TMtable);
  },

  createSamplePurposesTable: function (xhr) {
    var SPtable = [];
    var id, alias, description, endpoint, temp, rowId;
    // if data is coming in from AJAX request, store it in Defaults.all
    if (xhr) Defaults.all.samplePurposesDtos = JSON.parse(xhr.responseText);
    Tissue.createTable(Defaults.all.samplePurposesDtos, 'SP', 'allPurposes', 'Purpose', SPtable);
  },

  createTable: function (data, option, tableBodyId, word, table) {
    var tableBody = document.getElementById(tableBodyId);
    tableBody.innerHTML = null;

    // create rows if there is data; otherwise, add only the "Add New" button
    if (data) {
      data.sort(function (a,b) {
        return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
      });

      for (var i=0; i<data.length; i++) {
        id = data[i].id;
        alias = data[i].alias;
        description = data[i].description;
        endpoint = data[i].url;
        temp = endpoint.split('/');
    	rowId = temp[temp.length - 2] + id;

        table.push('<tr id="'+ rowId +'"><td>');
        table.push(Options.createTextInput(option+'_alias_'+id, alias));
        table.push('</td><td>');
        table.push(Options.createTextInput(option+'_description_'+id, description));
        table.push('</td><td>');
        table.push(Options.createButton('Update', "Tissue.update('"+endpoint+"', "+id+", '"+option+"')", "save_" + tableBodyId + "Table"));
        table.push('</td><td>');
        table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"', '"+rowId+"')", "save_" + tableBodyId + "Table"));
        table.push('</td></tr>');
      }
    }
    tableBody.innerHTML = table.join('');
    Options.tableLoadCounter += 1;

    // add button if it's not already present
    if (!document.getElementById('new'+option+'RowButton')) { 
      var button = ['<div id="new'+option+'RowButton">'];
      button.push(Options.createButton('New '+word, "Tissue.createNewRow('"+option+"')", 'new'+word));
      button.push('</div>');
      document.getElementById(tableBodyId).parentElement.insertAdjacentHTML('afterend', button.join(''));
    }

    // display checkmark if table has already been loaded once
    if (Options.tableLoadCounter > Options.tablesOnPage) { 
      Options.displayCheckmark(tableBody.parentElement.id);
    }
  },

  update: function (endpoint, suffix, option, givenMethod) {
    var alias = document.getElementById(option+'_alias_'+ suffix).value;
    var description = document.getElementById(option+'_description_'+suffix).value;
    if (!alias || !description) {
      alert("Neither alias nor description can be blank.");
      return null;
    }
    var method = givenMethod || 'PUT';
    Options.makeXhrRequest(method, endpoint, Options.reloadTable, JSON.stringify({ 'alias': alias, 'description': description }), option);
  },

  addNew: function (option) {
    var endpoint = '/miso/rest/';
    if (option == 'TM') {
      endpoint += 'tissuematerial';
    } else if (option == 'SP') {
      endpoint += 'samplepurpose';
    }
    Tissue.update(endpoint, 'new', option, 'POST');
  },

  createNewRow: function (option) {
    if (document.getElementById(option+'_alias_new')) {
      document.getElementById(option+'_alias_new').focus();
      return false;
    } else {
      var row = [];
      var tableBodyId = document.querySelectorAll('.'+ option)[0].id;
      
      row.push('<tr><td>');
      row.push(Options.createTextInput(option+'_alias_new'));
      row.push('</td><td>');
      row.push(Options.createTextInput(option+'_description_new'));
      row.push('</td><td>');
      row.push(Options.createButton('Add', "Tissue.addNew('"+option+"')", "save_" + tableBodyId));
      row.push('</td></tr>');

      document.getElementById(tableBodyId).insertAdjacentHTML('beforeend', row.join(''));
      document.getElementById(option+'_alias_new').focus();
    }
  },
  
  getTissueMaterials: function () {
    Options.makeXhrRequest('GET', '/miso/rest/tissuematerials', Tissue.createTissueMaterialsTable);
  },
  
  getSamplePurposes: function () {
    Options.makeXhrRequest('GET', '/miso/rest/samplepurposes', Tissue.createSamplePurposesTable);
  }
};

var Subproject = Subproject || {
  projectArray: null,
  referenceGenomeOptions: null,

  getProjects: function () {
    Options.makeXhrRequest('GET', '/miso/rest/project/lazy', Subproject.sortProjects);
  },

  getReferenceGenomeOptions: function () {
    Options.makeXhrRequest('GET', '/miso/rest/referencegenomes', Subproject.saveReferenceGenomeOptions);
  },
  
  getSubprojects: function () {
    Options.makeXhrRequest('GET', '/miso/rest/subprojects', Subproject.createSubprojectsTable);
  },

  sortProjects: function (pxhr) {
    Subproject.projectArray = JSON.parse(pxhr.response).sort(function (a, b) {
      return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
    });
    Subproject.getSubprojects();
  },

  saveReferenceGenomeOptions: function (pxhr) {
    Subproject.referenceGenomeOptions = JSON.parse(pxhr.response);
  },

  createSubprojectsTable: function (xhr) {
    var tableBody = document.getElementById('allSubprojects');
    tableBody.innerHTML = null;

    // if data is coming in from AJAX request, store it in Defaults.all
    if (xhr) Defaults.all.subprojectsDtos = JSON.parse(xhr.responseText);
    var data = Defaults.all.subprojectsDtos;
    var table = [];
    var id, alias, description, project, priority, endpoint;
  
    // create rows if there is data; otherwise, add only the "Add New" button
    if (data) {
      data.sort(function (a, b) {
        return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
      });
      
      for (var i=0; i<data.length; i++) {
        id = data[i].id;
        alias = data[i].alias;
        description = data[i].description;
        projectId = data[i].parentProjectId;
        projectName = Subproject.projectArray.filter(function(p) { return p.projectId == projectId; })[0].alias;
        priority = data[i].priority;
        endpoint = data[i].url;
        referenceGenomeId = data[i].referenceGenomeId;
        temp = endpoint.split('/');
    	rowId = temp[temp.length - 2] + id;

        table.push('<tr class="subP" id="'+ rowId +'"><td>');
        table.push('<b><span id="subP_parentProject_'+id+'">'+ projectName +'</span></b>'); // not editable after creation
        table.push('</td><td>');
        table.push(Options.createTextInput('subP_alias_'+ id, alias));
        table.push('</td><td>');
        table.push(Options.createTextInput('subP_description_'+id, description));
        table.push('</td><td>');
        table.push(Subproject.createPrioritySelect('subP_priority_'+ id, priority));
        table.push('</td><td>');
        table.push(Subproject.createReferenceGenomeSelect('subP_refGenome_'+ id, referenceGenomeId));
        table.push('</td><td>');
        table.push(Options.createButton('Update', "Subproject.update('"+endpoint+"', "+id+")", "save_allSubprojectsTable"));
        table.push('</td></tr>');
      }
    }
    tableBody.innerHTML = table.join('');
    Options.tableLoadCounter += 1;

    // add button if it's not already present
    if (!document.getElementById('newSubpRowButton')) { 
      var button = ['<div id="newSubpRowButton">'];
      button.push(Options.createButton('New Subproject', 'Subproject.createNewRow()', 'newDetails'));
      button.push('</button>');
      document.getElementById('allSubprojectsTable').insertAdjacentHTML('afterend', button.join(''));
    }

    // display checkmark if table has already been loaded once
    if (Options.tableLoadCounter > Options.tablesOnPage) {
      Options.displayCheckmark(tableBody.parentElement.id);
    }
  },

  createProjectsSelect: function(idValue, projectId) {
    var selectedProjectId = projectId || '';
    var select = [];
    select.push('<select id="'+ idValue +'" onchange="Subproject.selectReferenceGenome();">');
    for (var j=0;j<Subproject.projectArray.length;j++) {
      select.push('<option value="'+ Subproject.projectArray[j].projectId +'"');
      if (Subproject.projectArray[j].projectId == selectedProjectId) select.push(' selected=""');
      select.push('>'+ Subproject.projectArray[j].alias +'</option>');
    }
    select.push('</select>');
    return select.join('');
  },

  createPrioritySelect: function(idValue, priority) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    select.push('<option value="true"'+ (priority ? ' selected' : '') +'>High</option>');
    select.push('<option value="false"'+ (priority ? '' : ' selected') +'>Standard</option>');
    select.push('</select>');
    return select.join('');
  },

  getProjectGenomeReferenceId: function(projectId) {
    for (var j=0;j<Subproject.projectArray.length;j++) {
      if (Subproject.projectArray[j].projectId === parseInt(projectId)) {
        return Subproject.projectArray[j].referenceGenomeId;
      }
    }
    return null;
  },

  createReferenceGenomeSelect: function(idValue, referenceGenomeId) {
    var select = [];
    select.push('<select id="'+ idValue +'">');

    for (var j=0; j<Subproject.referenceGenomeOptions.length; j++) {
      select.push('<option value="'+ Subproject.referenceGenomeOptions[j].id +'"');
      if (Subproject.referenceGenomeOptions[j].id === referenceGenomeId) select.push(' selected=""');
      select.push('>'+ Subproject.referenceGenomeOptions[j].alias +'</option>');
    }
    select.push('</select>');
    return select.join('');
  },

  update: function (endpoint, suffix, givenMethod) {
    var alias = document.getElementById('subP_alias_'+suffix).value;
    var description = document.getElementById('subP_description_'+suffix).value;
    var priority = document.getElementById('subP_priority_'+suffix).value;
    var parentProjectId = parseInt(document.getElementById('subP_parentProject_'+suffix).value);
    var referenceGenomeId = document.getElementById('subP_refGenome_'+suffix).value;
    if (!alias || !description || !priority) {
      alert("Neither alias, description, nor priority can be blank.");
      return null;
    }
    var method = givenMethod || 'PUT';
    Options.makeXhrRequest(method, endpoint, Options.reloadTable,
        JSON.stringify({ 'alias': alias, 'description': description, 'priority': priority, 'parentProjectId': parentProjectId, 'referenceGenomeId': referenceGenomeId }), 'SubP');
  },

  addNew: function() {
    Subproject.update('/miso/rest/subproject', 'new', 'POST');
  },

  createNewRow: function () {
    if (document.getElementById('subP_parentProject_new')) {
      document.getElementById('subP_parentProject_new').focus();
      return false;
    } else {
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
      row.push(Subproject.createReferenceGenomeSelect('subP_refGenome_new'));
      row.push('</td><td>');
      row.push(Options.createButton('Add', "Subproject.addNew()", "save_allSubprojectsTable"));
      row.push('</td></tr>');

      document.getElementById('allSubprojects').insertAdjacentHTML('beforeend', row.join(''));
      document.getElementById('subP_alias_new').focus();

      this.selectReferenceGenome();
    }
  },

  selectReferenceGenome: function() {
    var projectId = document.getElementById("subP_parentProject_new").value;
    referenceGenomeId = this.getProjectGenomeReferenceId(projectId);

    var select = document.getElementById("subP_refGenome_new");
    for (var i = 0; i < select.options.length; i++){
      if (select.options[i].value === referenceGenomeId + "" ){
        select.options[i].selected = true;
      }
    }
  }
};

var Lab = Lab || {
  
  createLabsTable: function (xhr) {
    var table = [];
    var id, alias, endpoint;
    
    // if data is coming in from AJAX request, store it in Defaults.all
    if (xhr) Defaults.all.labsDtos = JSON.parse(xhr.responseText);
    Lab.createTable(Defaults.all.labsDtos, 'Lab', 'allLabs', 'Lab', table);
  },
  
  createInstitutesTable: function (xhr) {
    var table = [];
    var id, alias, endpoint;
    
    // if data is coming in from AJAX request, store it in Defaults.all
    if (xhr) Defaults.all.institutesDtos = JSON.parse(xhr.responseText);
    // sort institutes in place since labs makes use of them
    Defaults.all.institutesDtos = Defaults.all.institutesDtos.sort(function (a, b) {
      return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
    });
    Lab.createTable(Defaults.all.institutesDtos, 'In', 'allInstitutes', 'Institute', table);
  },

  createTable: function (data, option, tableBodyId, word, table) {
    var tableBody = document.getElementById(tableBodyId);
    tableBody.innerHTML = null;

    // optional variable for if option == "Lab"
    var selectedInstituteId = null;
    var id, alias, endpoint, selectedInstituteId;
    
    // create rows if there is data; otherwise, add only the "Add New" button
    if (data) {
      data.sort(function (a, b) {
        return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
      });

      for (var i=0; i<data.length; i++) {
        id = data[i].id;
        alias = data[i].alias;
        endpoint = data[i].url;
        selectedInstituteId = (option == 'Lab' ? data[i].instituteId : null);
        temp = endpoint.split('/');
    	rowId = temp[temp.length - 2] + id;

        table.push('<tr class="'+option+'" + id="'+ rowId +'"><td>');
        table.push(Options.createTextInput(option+'_alias_'+id, alias));
        table.push('</td><td>');
        if (selectedInstituteId) {
          table.push(Lab.createInstitutesDropdown(option+'_institute_'+id, selectedInstituteId));
          table.push('</td><td>');
        }
        table.push(Options.createButton('Update', "Lab.update('"+endpoint+"', "+id+", '"+option+"')", "save_" + tableBodyId + "Table"));
        table.push('</td><td>');
        table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"', '"+ rowId +"')", "save_" + tableBodyId + "Table"));
        table.push('</td></tr>');
      }
    }
    tableBody.innerHTML = table.join('');
    Options.tableLoadCounter += 1;

    // add button if it's not already present
    if (!document.getElementById('new'+option+'RowButton')) { 
      var button = ['<div id="new'+option+'RowButton">'];
      button.push(Options.createButton('New '+word, "Lab.createNewRow('"+option+"')", 'new'+word));
      button.push('</div>');
      document.getElementById(tableBodyId).parentElement.insertAdjacentHTML('afterend', button.join(''));
    }

    // display checkmark if tables have already been loaded once
    if (Options.tableLoadCounter > Options.tablesOnPage) { 
      Options.displayCheckmark(tableBody.parentElement.id);
    }
  },

  update: function (endpoint, suffix, option, givenMethod) {
    var alias = document.getElementById(option+'_alias_'+suffix).value;
    var instituteId = (option == 'Lab' ? document.getElementById(option+'_institute_'+suffix).value : null);
    if (!alias) {
      alert("Alias can not be blank.");
      return null;
    }
    var data = {
      'alias': alias
    };
    
    // institute cannot be blank when creating a lab
    if (instituteId === null && option == 'Lab') {
      alert("Please select an institute.");
      return false;
    }
    // use instituteId when creating a lab
    if (instituteId) {
      data.instituteId = instituteId;
    }
    var method = givenMethod || 'PUT';
    Options.makeXhrRequest(method, endpoint, Options.reloadTable, JSON.stringify(data), option);
  },

  addNew: function (option) {
    var endpoint = '/miso/rest/';
    if (option == 'In') {
      endpoint += 'institute';
    } else if (option == 'Lab') {
      endpoint += 'lab';
    }
    Lab.update(endpoint, 'new', option, 'POST');
  },

  createNewRow: function (option) {    
    if (document.getElementById(option+'_alias_new')) {
      document.getElementById(option+'_alias_new').focus();
      return false;
    } else {
      if (option == 'Lab' && Defaults.all.institutesDtos.length === 0) {
        alert ("Please add an Institute before creating a Lab.");
        return false;
      }
      var row = [];
      var tableBodyId = document.querySelectorAll('.'+ option)[0].id;
      
      row.push('<tr><td>');
      row.push(Options.createTextInput(option+'_alias_new'));
      row.push('</td><td>');
      if (option == 'Lab') {
        row.push(Lab.createInstitutesDropdown(option+'_institute_new'));
        row.push('</td><td>');
      }
      row.push(Options.createButton('Add', "Lab.addNew('"+option+"')", "save_" + tableBodyId + "Table"));
      row.push('</td></tr>');

      document.getElementById(tableBodyId).insertAdjacentHTML('beforeend', row.join(''));
      document.getElementById(option+'_alias_new').focus();
    }
  },
  
  createInstitutesDropdown: function (idValue, selectedInstituteId) {
    var instituteId = selectedInstituteId || '';
    var select = [];
    select.push('<select id="'+ idValue +'">');
    for (var i=0; i<Defaults.all.institutesDtos.length; i++) {
      select.push('<option value="'+ Defaults.all.institutesDtos[i].id +'"');
      if (Defaults.all.institutesDtos[i].id == instituteId) {
        select.push(' selected=""');
      }
      select.push('>'+ Defaults.all.institutesDtos[i].alias + '</option>');
    }
    select.push('</select>');
    return select.join('');
  },
  
  getLabs: function () {
    Options.makeXhrRequest('GET', '/miso/rest/labs', Lab.createLabsTable);
  },
  
  getInstitutes: function () {
    Options.makeXhrRequest('GET', '/miso/rest/institutes', Lab.createInstitutesTable);
  }
};

var Options = Options || {
  tableLoadCounter: 0,
  tablesOnPage: 5,

  makeXhrRequest: function (method, endpoint, callback, data, callbackarg) {
    var expectedStatus;
    var unauthorizedStatus = 401;
    if (method == 'POST') {
      expectedStatus = 201; 
    } else {
      expectedStatus = 200;
    }
    var xhr = new XMLHttpRequest();
    xhr.open(method, endpoint);
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (expectedStatus == xhr.status) {
          if (!callback) {
            document.location.reload();
          } else {
            data ? ( callbackarg ? callback(callbackarg) : callback() ) : callback(xhr) ;
          }
        } else if (xhr.status === unauthorizedStatus) {
          alert("You are not authorized to view this page.");
        } else {
          console.log(xhr);
          var response = JSON.parse(xhr.response);
          alert(response.detail);
        }
      }
    };
    xhr.setRequestHeader('Content-Type', 'application/json');
    data ? xhr.send(data) : xhr.send();
  },

  confirmDelete: function (endpoint, rowId) {
	// toggle-able divs have the same #ids as the resource (singular) of the endpoint. Store this in localStorage. Divs are toggled on page load if they are present in localStorage.
	var pieces = endpoint.split('/');
	var resource = pieces[pieces.length - 2];
	localStorage['tablediv'] = resource;
	
	// remove row from table and make it clear to the user
	var removeRow = function () {
	  var row = document.getElementById(rowId);
	  var tableId = row.parentNode.parentNode.id;
	  row.parentNode.removeChild(row);
	  Options.displayCheckmark(tableId);
	}
	
    if (endpoint.indexOf("institute") != -1) {
      // confirm there are no Labs which reference the Institute to be deleted
      var instituteId = endpoint.split('/').pop();
      var relatedLabs = Defaults.all.labsDtos.filter(function(lab) { return lab.instituteId == instituteId; });
      if (relatedLabs.length > 0) {
        var labAliasesString = relatedLabs.map(function(lab) { return lab.alias; });
        alert("Please delete the related Labs ("+ labAliasesString.join(", ") +") before you delete this Institute.");
        return false;
      } else {
    	  Options.makeXhrRequest('DELETE', endpoint, removeRow);
      }
    } else if (endpoint.indexOf("lab") != -1) {
    	// need to deliberately remove lab from labsDtos as well, otherwise can't delete institutes associated with now-deleted lab
    	var removeLabRow = function () {
    	  var labIndex; 
    	  for (var j = 0; j < Defaults.all.labsDtos.length; j++) {
      		if (Defaults.all.labsDtos[j].id == endpoint.split('/').pop()) {
      			labIndex = j;
      			break;
      		}
      	}
      	Defaults.all.labsDtos.splice(labIndex, 1);
  		  var row = document.getElementById(rowId);
  		  var tableId = row.parentNode.parentNode.id;
  		  row.parentNode.removeChild(row);
  		  Options.displayCheckmark(tableId);
    	};
    	Options.makeXhrRequest('DELETE', endpoint, removeLabRow);
    } else if (confirm('Are you sure you wish to delete? This operation cannot be undone!')) {
      Options.makeXhrRequest('DELETE', endpoint, removeRow);
    }
  },

  createTextInput: function(idValue, valueText) {
    return '<input type="text" id="'+ idValue +'" value="'+ (valueText ? valueText : '') +'"></input>';
  },

  createButton: function(valueText, onclickFunction, idText) {
    return '<button class="inline"'+(idText ? ' id="'+ idText +'"' : '')+' onclick="'+ onclickFunction +'">'+ valueText +'</button>';
  },

  reloadTable: function (option) {
    var reloadTableFunc;
    if (option == 'TM') { reloadTableFunc = Tissue.getTissueMaterials; }
    else if (option == 'SP') { reloadTableFunc = Tissue.getSamplePurposes; }
    else if (option == 'SubP') { reloadTableFunc = Subproject.getSubprojects; }
    else if (option == 'In') { reloadTableFunc = Lab.getInstitutes; }
    else if (option == 'Lab') { reloadTableFunc = Lab.getLabs; }
    reloadTableFunc();
  },

  displayCheckmark: function (tableId) {
    var table = document.getElementById(tableId);
    table.setAttribute('style', 'float:left');
    var checkmark = '<div><img id="checkmark'+tableId+'"  src="/styles/images/ok.png"/></div><div class="clear"></div>';
    table.insertAdjacentHTML('afterend', checkmark);
    var saveButtons = document.getElementsByClassName("save_" + tableId);
    for (var i = 0; i < saveButtons.length; i++) {
      saveButtons[i].classList.remove('disabled');
    }
    var check = jQuery('#checkmark'+tableId);
    check.fadeOut("slow", function() {
      jQuery(this).remove();
      table.setAttribute('style', 'clear:both');
    });
  }
};
