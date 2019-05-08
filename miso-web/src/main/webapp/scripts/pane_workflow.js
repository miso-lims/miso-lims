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

PaneTarget.workflow = (function() {
  var title = "Workflows";
  var url = "/miso/rest/workflows";

  var transform = function(data) {
    return Tile.make([
      Tile.title(data.name),
      Tile.lines([
    	  data.complete ? "Complete" : "Step: " + data.stepNumber,
    	  data.message,
    	  "Last Modified: " + data.lastModified])], function() {
    	window.location = window.location.origin + '/miso/workflow/edit/' + data.workflowId;
    });
  };

	  return {
		createPane : function(paneId) {
			var divs = Pane.createPane(paneId, title);
			
			var createWorkflowTiles = favouriteWorkflows.map(function(data) {
		  	return Tile.make([Tile.title("Begin New " + data.description)], function(){
		  		window.location = window.location.origin + '/miso/workflow/new/' + data.workflowName;
		  	})
		  });

			createWorkflowTiles.push(Tile.make([ Tile.title("Begin New Workflow") ],
					function() {
						Utils.showWizardDialog("Begin New Workflow", Constants.workflows.map(function(workflow) {
							return {
								name : workflow.description,
								handler : function() {
									window.location = window.location.origin + '/miso/workflow/new/' + workflow.workflowName;
									}
							};
							}));
						}));
			
			createWorkflowTiles.push(Tile.make([ Tile.title("Edit Favourite Workflows") ],
					function() {
						Utils.showWizardDialog("Edit Favourite Workflows", Constants.workflows.map(function(workflow) {
							if(favouriteWorkflows.map(function(favourite){
								return favourite.workflowName;
							}).indexOf(workflow.workflowName) < 0){
								return {
									name : "Add " + workflow.description + " to Favourites",
									handler : function() {
										Utils.ajaxWithDialog('Adding Workflow to Favourites', 'POST', "/miso/rest/workflows/favourites/add/" + workflow.workflowName, 
												null, Utils.page.pageReload);
										}
								};
							} else {
								return {
									name : "Remove " + workflow.description + " from Favourites",
									handler : function() {
										Utils.ajaxWithDialog('Removing Workflow from Favourites', 'POST', "/miso/rest/workflows/favourites/remove/" + workflow.workflowName, 
												null, Utils.page.pageReload);
										}
								};
								}
							}
							));
						}));
			
			Pane.updateTiles(divs.content, transform, url, null, createWorkflowTiles);
		}
	};
})();
