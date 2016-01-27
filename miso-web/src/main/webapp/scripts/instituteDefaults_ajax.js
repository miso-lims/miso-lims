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
  getTissueOrigins: function () {
    Options.makeXhrRequest('GET', '/miso/rest/tissueorigins', Tissue.createTissueOriginsTable);
  },

  createTissueOriginsTable: function (xhr) {
    var TOtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TO', 'allOrigins', 'Origin', TOtable);
  },

  getTissueConditions: function () {
    Options.makeXhrRequest('GET', '/miso/rest/tissuetypes', Tissue.createTissueConditionsTable);
  },

  createTissueConditionsTable: function (xhr) {
    var TCtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TC', 'allConditions', 'Condition', TCtable);
  },

  getTissueMaterials: function () {
    Options.makeXhrRequest('GET', '/miso/rest/tissuematerials', Tissue.createTissueMaterialsTable);
  },

  createTissueMaterialsTable: function (xhr) {
    var TMtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'TM', 'allMaterials', 'Material', TMtable);
  },

  getSamplePurposes: function () {
    Options.makeXhrRequest('GET', '/miso/rest/samplepurposes', Tissue.createSamplePurposesTable);
  },

  createSamplePurposesTable: function (xhr) {
    var SPtable = [];
    var id, alias, description, endpoint;
    Tissue.createTable(xhr, 'SP', 'allPurposes', 'Purpose', SPtable);
  },

  createTable: function (xhr, option, tableBodyId, word, table) {
    var tableBody = document.getElementById(tableBodyId);
    tableBody.innerHTML = null;

    var data;
    if (xhr.status == 200) {
      data = JSON.parse(xhr.responseText);
      data.sort(function (a,b) {
        return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
      });
    } // else collection is empty, so add only the "Add New" button
    if (data) {
      for (var i=0; i<data.length; i++) {
        id = data[i].id;
        alias = data[i].alias;
        description = data[i].description;
        endpoint = data[i].url;

        table.push('<tr><td>');
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
    tableBody.innerHTML = table.join('');
    Options.tableLoadCounter += 1;

    if (!document.getElementById('new'+option+'RowButton')) { // add button if it's not already present
      var button = ['<div id="new'+option+'RowButton">'];
      button.push(Options.createButton('New '+word, "Tissue.createNewRow('"+option+"')", 'new'+word));
      button.push('</div>');
      document.getElementById(tableBodyId).parentElement.insertAdjacentHTML('afterend', button.join(''));
    }

    if (Options.tableLoadCounter > Options.tablesOnPage) { // if tables have all already been loaded once
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
    method = givenMethod || 'PUT';
    Options.makeXhrRequest(method, endpoint, Options.reloadTable, JSON.stringify({ 'alias': alias, 'description': description }), option);
  },

  addNew: function (option) {
    var endpoint = '/miso/rest/';
    if (option == 'TO') {
      endpoint += 'tissueorigin';
    } else if (option == 'TC') {
      endpoint += 'tissuetype';
    } else if (option == 'TM') {
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

      row.push('<tr><td>');
      row.push(Options.createTextInput(option+'_alias_new'));
      row.push('</td><td>');
      row.push(Options.createTextInput(option+'_description_new'));
      row.push('</td><td>');
      row.push(Options.createButton('Add', "Tissue.addNew('"+option+"')"));
      row.push('</td></tr>');

      var tableBodyId = document.querySelectorAll('.'+ option)[0].id;
      document.getElementById(tableBodyId).insertAdjacentHTML('beforeend', row.join(''));
      document.getElementById(option+'_alias_new').focus();
    }
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
    if (xhr.status == 200) {
      data = JSON.parse(xhr.responseText);
      data.sort(function (a, b){
        return (a.description > b.description) ? 1 : ((b.description > a.description) ? -1 : 0);
      });
    } // else collection is empty, so render only the "Add New" button

    var table = [];
    var id, status, description, note, endpoint;

    if (data) {
      for (var i=0; i<data.length; i++) {
        id = data[i].id;
        status = data[i].status;
        description = data[i].description;
        note = data[i].noteRequired;
        endpoint = data[i].url;

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
    tableBody.innerHTML = table.join('');
    Options.tableLoadCounter += 1;

    if (!document.getElementById('newQCRowButton')) { // add button if it's not already present
      var button = ['<div id="newQCRowButton">'];
      button.push(Options.createButton('New QC Details', 'QC.createNewRow()', 'newDetails'));
      button.push('</div>');
      document.getElementById('allQcDetailsTable').insertAdjacentHTML('afterend', button.join(''));
    }

    if (Options.tableLoadCounter > Options.tablesOnPage) { // if tables have all already been loaded once
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

  update: function (endpoint, suffix, givenMethod) {
    var description = document.getElementById('QC_description_'+suffix).value;
    var status = document.getElementById('QC_status_'+suffix).value;
    var note = document.getElementById('QC_note_'+suffix).value;
    if (!description || !note) { //status can be blank, for QC Passed=Unknown
      alert("Neither description nor note required can be blank.");
      return null;
    }
    var method = givenMethod || 'PUT';
    Options.makeXhrRequest(method, endpoint, Options.reloadTable, JSON.stringify({ 'status': status, 'description': description, 'noteRequired': note }), 'QC');
  },

  addNew: function() {
    QC.update('/miso/rest/qcpasseddetail', 'new', 'POST');
  },

  createNewRow: function () {
    if (document.getElementById('QC_description_new')) {
      document.getElementById('QC_description_new').focus();
      return false;
    } else {
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

      document.getElementById('allQcDetails').insertAdjacentHTML('beforeend', row.join(''));
      document.getElementById('QC_status_new').focus();
    }
  }
};

var Subproject = Subproject || {
  projectArray: null,

  getProjects: function () {
    Options.makeXhrRequest('GET', '/miso/rest/project', Subproject.listProjects);
  },

  listProjects: function (pxhr) {
    Subproject.projectArray = JSON.parse(pxhr.response).sort(function (a, b) {
      return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
    });
    Options.makeXhrRequest('GET', '/miso/rest/subprojects', Subproject.createSubprojectTable);
  },

  createSubprojectTable: function (xhr) {
    var tableBody = document.getElementById('allSubprojects');
    tableBody.innerHTML = null;

    var data;
    if (xhr.status == 200) {
      data = JSON.parse(xhr.response).sort(function (a, b) {
        return (a.alias > b.alias) ? 1 : ((b.alias > a.alias) ? -1 : 0);
      });
    } // else collection is empty, so render only the "Add New" button

    var table = [];
    var id, alias, description, project, priority, endpoint;

    if (data) {
      for (var i=0; i<data.length; i++) {
        id = data[i].id;
        alias = data[i].alias;
        description = data[i].description;
        projectId = data[i].parentProjectId;
        projectName = Subproject.projectArray.filter(function(p) { return p.projectId == projectId; })[0].alias;
        priority = data[i].priority;
        endpoint = data[i].url;

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
    tableBody.innerHTML = table.join('');
    Options.tableLoadCounter += 1;

    if (!document.getElementById('newSubpRowButton')) { // add button if it's not already present
      var button = ['<div id="newSubpRowButton">'];
      button.push(Options.createButton('New Subproject', 'Subproject.createNewRow()', 'newDetails'));
      button.push('</button>');
      document.getElementById('allSubprojectsTable').insertAdjacentHTML('afterend', button.join(''));
    }

    if (Options.tableLoadCounter > Options.tablesOnPage) { // if tables have all already been loaded once
      Options.displayCheckmark(tableBody.parentElement.id);
    }
  },

  createProjectsSelect: function(idValue, projectId) {
    var selectedProjectId = projectId || '';
    var select = [];
    select.push('<select id="'+ idValue +'">');
    for (var j=0;j<Subproject.projectArray.length;j++) {
      select.push('<option value="'+ Subproject.projectArray[j]["projectId"] +'"');
      if (Subproject.projectArray[j]["projectId"] == selectedProjectId) select.push(' selected=""');
      select.push('>'+ Subproject.projectArray[j]["alias"] +'</option>');
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

  update: function (endpoint, suffix, givenMethod) {
    var alias = document.getElementById('subP_alias_'+suffix).value;
    var description = document.getElementById('subP_description_'+suffix).value;
    var priority = document.getElementById('subP_priority_'+suffix).value;
    var parentProjectId = document.getElementById('subP_parentProject_'+suffix).value;
    if (!alias || !description || !priority) {
      alert("Neither alias, description, nor priority can be blank.");
      return null;
    }
    var method = givenMethod || 'PUT';
    Options.makeXhrRequest(method, endpoint, Options.reloadTable,
        JSON.stringify({ 'alias': alias, 'description': description, 'priority': priority, 'parentProjectId': parentProjectId }), 'SubP');
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
      row.push(Options.createButton('Add', "Subproject.addNew()"));
      row.push('</td></tr>');

      document.getElementById('allSubprojects').insertAdjacentHTML('beforeend', row.join(''));
      document.getElementById('subP_alias_new').focus();
    }
  }
};

var Hierarchy = Hierarchy || {
  categoriesArray: [],
  data: {},
  allClasses: [],
  allRelationships: {},

  getSampleCategories: function () {
    Options.makeXhrRequest('GET', '/miso/rest/samplecategories', Hierarchy.getSampleClasses);
  },

  getSampleClasses: function (cxhr) {
    Hierarchy.categoriesArray = JSON.parse(cxhr.response);
    Options.makeXhrRequest('GET', '/miso/rest/sampleclasses', Hierarchy.createClassesAndRelationships);
  },

  createClassesAndRelationships: function (xhr) {
    Hierarchy.createSampleClassesTable(xhr);
    Hierarchy.getRelationships(); // relationships depend on already having classes available, and should be reloaded every time a class changes
  },

  createSampleClassesTable: function (xhr) {
    var tableBody = document.getElementById('allClasses');
    tableBody.innerHTML = null;

    if (xhr.status == 200) {
      Hierarchy.allClasses = JSON.parse(xhr.response);
      // data = { 1: {class1}, 2: {class2}, ... }
      for (var i=0; i<Hierarchy.allClasses.length; i++) {
        Hierarchy.data[Hierarchy.allClasses[i].id] = Hierarchy.allClasses[i];
      }
    } // else collection is empty, so render only the "Add New" button

    var table = [];
    var id, alias, category, endpoint;

    for (var key in Hierarchy.data) {
      if (Hierarchy.data.hasOwnProperty(key)) {
        id = Hierarchy.data[key].id;
        alias = Hierarchy.data[key].alias;
        category = Hierarchy.data[key].sampleCategory;
        endpoint = Hierarchy.data[key].url;

        table.push('<tr class="sampleClass"><td>');
        table.push(Options.createTextInput('class_alias_'+id, alias));
        table.push('</td><td>');
        table.push(Hierarchy.createCategorySelect('class_category_'+ id, category));
        table.push('</td><td>');
        table.push(Options.createButton('Update', "Hierarchy.updateClass('"+endpoint+"', "+id+")"));
        table.push('</td><td>');
        table.push(Options.createButton('Delete', "Options.confirmDelete('"+endpoint+"')"));
        table.push('</td></tr>');
      }
    }
    tableBody.innerHTML = table.join('');
    Sortable.initTable(document.getElementById('allClassesTable'));
    Options.tableLoadCounter += 1;

    if (!document.getElementById('newClassRowButton')) { // add button if it's not already present
      var button = ['<div id="newClassRowButton">'];
      button.push(Options.createButton('New Class', 'Hierarchy.createNewClassRow()', 'newClass'));
      button.push('</div>');
      document.getElementById('allClassesTable').insertAdjacentHTML('afterend', button.join(''));
    }

    if (Options.tableLoadCounter > Options.tablesOnPage) { // if tables have all already been loaded once
      Options.displayCheckmark(tableBody.parentElement.id);
    }
  },

  createCategorySelect: function (idValue, category) {
    var select = [];
    select.push('<select id="'+ idValue +'">');
    select.push('<option value="">Choose Category</option>');
    var categories = Hierarchy.categoriesArray;
    for (var i=0; i<categories.length; i++) {
      select.push('<option value="'+ categories[i].alias +'"');
      if (categories[i].alias == category) select.push(' selected=""');
      select.push('>'+ categories[i].alias + '</option>');
    }
    select.push('</select>');
    return select.join('');
  },

  updateClass: function (endpoint, suffix, givenMethod) {
    var alias = document.getElementById('class_alias_'+suffix).value;
    var category = document.getElementById('class_category_'+suffix).value;
    if (!alias || !category) {
      alert("Neither class nor category can be blank.");
      return false;
    }
    var method = givenMethod || 'PUT';
    Options.makeXhrRequest(method, endpoint, Hierarchy.getSampleCategories, JSON.stringify({ 'alias': alias, 'sampleCategory': category }));
  },

  addNewClass: function () {
    Hierarchy.updateClass('/miso/rest/sampleclass', 'new', 'POST');
  },

  createNewClassRow: function () {
    if (document.getElementById('class_alias_new')) {
      document.getElementById('class_alias_new').focus();
      return false;
    } else {
      var row = [];

      row.push('<tr><td>');
      row.push(Options.createTextInput('class_alias_new'));
      row.push('</td><td>');
      row.push(Hierarchy.createCategorySelect('class_category_new')); // don't pass in category value because none is selected
      row.push('</td><td>');
      row.push(Options.createButton('Add', 'Hierarchy.addNewClass()'));
      row.push('</td></tr>');

      document.getElementById('allClasses').insertAdjacentHTML('beforeend', row.join(''));
      document.getElementById('class_alias_new').focus();
    }
  },

  getRelationships: function () {
    Options.makeXhrRequest('GET', '/miso/rest/samplevalidrelationships', Hierarchy.createRelationshipsTable);
  },

  createRelationshipsTable: function (xhr) {
    var tableBody = document.getElementById('allRelationshipsTable');
    // tableBody exists only when reloading the table
    if (tableBody) tableBody.parentNode.removeChild(tableBody);

    if (xhr.status == 200) {
      Hierarchy.allRelationships = JSON.parse(xhr.response);
    } // else collection is empty, so render only the "Add New" button

    var table = [];
    var id, parentClass, childClass, endpoint;

    // add the table itself
    table.push('<table id="allRelationshipsTable" class="clear default-table" data-sortable>');
    table.push('<thead><tr class="relationship">');
    table.push('<th colspan="2">Parent</th><th colspan="2">Child</th>');
    table.push('</tr><tr>');
    table.push('<th>Category</th><th>Class</th><th>Category</th><th>Class</th>');
    table.push('</tr></thead>');
    table.push('<tbody id="allRelationships">');
    // add any existing relationships to the table
    for (var i=0; i<Hierarchy.allRelationships.length; i++) {
      id = Hierarchy.allRelationships[i].id;
      parentClass = Hierarchy.allClasses.filter(function (sampleClass) {
        return sampleClass.id == Hierarchy.allRelationships[i].parentId;
      })[0];
      childClass = Hierarchy.allClasses.filter(function (sampleClass) {
        return sampleClass.id == Hierarchy.allRelationships[i].childId;
      })[0];
      endpoint = Hierarchy.allRelationships[i].url;

      table.push('<tr class="relationship" data-id="'+ id +'"><td>');
      table.push(parentClass.sampleCategory);
      table.push('</td><td>');
      table.push(parentClass.alias);
      table.push('</td><td>');
      table.push(childClass.sampleCategory);
      table.push('</td><td>');
      table.push(childClass.alias);
      table.push('</td><td>');
      table.push(Options.createButton('Delete', 'Options.confirmDelete("'+ endpoint +'")'));
      // no Edit button
      table.push('</td></tr>');
    }
    table.push('</tbody></table>');
    document.getElementById('relationships').insertAdjacentHTML('afterend', table.join(''));
    Sortable.initTable(document.getElementById('allRelationshipsTable')); // makes this table sortable
    Options.tableLoadCounter += 1;

    if (!document.getElementById('newRelationshipRowButton')) { // add button if it's not already present
      var button = ['<div id="newRelationshipRowButton">'];
      button.push(Options.createButton('New Relationship', 'Hierarchy.createNewRelationshipRow()', 'newRelationship'));
      button.push('</div>');
      document.getElementById('allRelationshipsTable').insertAdjacentHTML('afterend', button.join(''));
    }

    if (Options.tableLoadCounter > Options.tablesOnPage) { // if all tables have already been loaded once
      Options.displayCheckmark('allRelationshipsTable');
    }
  },

  createNewRelationshipRow: function () {
    if (document.getElementById('relationship_parent_category_new')) {
      document.getElementById('relationship_parent_category_new').focus();
      return false;
    } else {
      var row = [];
      row.push ('<tr id="newRelationshipRow"><td id="parentCategoryNew">');
      row.push(Hierarchy.createCategorySelect('relationship_parent_category_new'));
      row.push('</td><td id="parentClassNew"></td><td id="childCategoryNew">'); // create placeholder dropdown, which will get filled in once user chooses a category
      row.push(Hierarchy.createCategorySelect('relationship_child_category_new'));
      row.push('</td><td id="childClassNew"></td><td>');
      row.push(Options.createButton('Add', 'Hierarchy.addNewRelationship()'));
      row.push('</td></tr>');

      document.getElementById('allRelationships').insertAdjacentHTML('beforeend', row.join(''));
      var parentCategorySelect = document.getElementById('relationship_parent_category_new');
      var childCategorySelect = document.getElementById('relationship_child_category_new');
      parentCategorySelect.addEventListener('change', Hierarchy.createClassForCategorySelect);
      parentCategorySelect.setAttribute('data-role', 'parent');
      childCategorySelect.addEventListener('change', Hierarchy.createClassForCategorySelect);
      childCategorySelect.setAttribute('data-role', 'child');
    }
  },

  createClassForCategorySelect: function (event) {
    var select = [];
    var category = event.target.value; // event is fired by selecting a category
    var role = event.target.getAttribute('data-role');
    var categoryTdId = event.target.parentElement.getAttribute('id');
    var classTdId;
    var classesForCategory = Hierarchy.allClasses.filter(function (sampleClass) {
      return sampleClass.sampleCategory == category;
    }); // grab the classes that fall under that category
    var newRowTds = document.getElementById('newRelationshipRow').children;
    for (i=0;i<newRowTds.length; i++) {
      if (newRowTds[i].id == categoryTdId) classTdId = newRowTds[i+1].id;
    }

    select.push('<select id="relationship_'+ role +'_class_new" data-role="'+ role +'">');
    for (var i=0; i<classesForCategory.length; i++) {
      select.push('<option value="'+ classesForCategory[i].id +'">'+ classesForCategory[i].alias +'</option>');
    }
    select.push('</select>');

    document.getElementById(classTdId).innerHTML = select.join('');
  },

  addNewRelationship: function () {
    var parentId = document.getElementById('relationship_parent_class_new').value;
    var childId = document.getElementById('relationship_child_class_new').value;
    if (parentId == '' || childId == '') {
      alert("You must choose a parent class and a child class.");
      return false;
    }
    Options.makeXhrRequest('POST', '/miso/rest/samplevalidrelationship', Options.reloadTable,
      JSON.stringify({ 'parentId': parentId, 'childId': childId }), 'Rel');
  }
};

var Options = Options || {
  tableLoadCounter: 0,
  tablesOnPage: 8,

  makeXhrRequest: function (method, endpoint, callback, data, callbackarg) {
    var expectedStatus;
    var unauthorizedStatus = 401;
    if (method == 'POST') {
      expectedStatus = [201, 200]; // will be 200 in case of a POST to sampleclass which triggers a GET for samplevalidrelationship
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
    };
    xhr.setRequestHeader('Content-Type', 'application/json');
    data ? xhr.send(data) : xhr.send();
  },

  confirmDelete: function (endpoint) {
    if (endpoint.indexOf("sampleclass") != -1) {
      var sampleClassId = endpoint.split('/').pop();
      var sampleClassAlias = Hierarchy.allClasses.filter(function(sampleClass) { return sampleClass.id == sampleClassId; })[0].alias;
      var countRelatedClasses = 0;
      for (var i=0;i<Hierarchy.allRelationships.length; i++) {
        var relationship = Hierarchy.allRelationships[i];
        if (relationship.parentId == sampleClassId || relationship.childId == sampleClassId) {
          countRelatedClasses += 1;
        }
      }
      if (countRelatedClasses > 0) {
        if (confirm('Are you sure you wish to delete '+ sampleClassAlias +' and its '+ countRelatedClasses +' relationships? This operation cannot be undone!')) {
          Options.makeXhrRequest('DELETE', endpoint);
        }
      } else if (confirm('Are you sure you wish to delete '+ sampleClassAlias +'? This opearation cannot be undone!')) {
        Options.makeXhrRequest('DELETE', endpoint);
      }
    } else if (confirm('Are you sure you wish to delete? This operation cannot be undone!')) {
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
    var reloadTableFunc;
    if (option == 'TO') { reloadTableFunc = Tissue.getTissueOrigins; }
    else if (option == 'TC') { reloadTableFunc = Tissue.getTissueConditions; }
    else if (option == 'TM') { reloadTableFunc = Tissue.getTissueMaterials; }
    else if (option == 'SP') { reloadTableFunc = Tissue.getSamplePurposes; }
    else if (option == 'QC') { reloadTableFunc = QC.getQcDetails; }
    else if (option == 'SubP') { reloadTableFunc = Subproject.getProjects; }
    reloadTableFunc();
  },

  displayCheckmark: function (tableId) {
    var table = document.getElementById(tableId);
    table.setAttribute('style', 'float:left');
    var checkmark = '<div><img id="checkmark'+tableId+'"  src="/styles/images/ok.png"/></div><div class="clear"></div>';
    table.insertAdjacentHTML('afterend', checkmark);
    var check = jQuery('#checkmark'+tableId);
    check.fadeOut("slow", function() {
      jQuery(this).remove();
      table.setAttribute('style', 'clear:both');
    });
  }
};
