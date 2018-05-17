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
  var url = "/miso/rest/workflow";

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
    createPane: function(paneId) {
      var divs = Pane.createPane(paneId, title);
      
      Pane.updateTiles(divs.content, transform, url);
    }
  };
})();
/*
function(){
    	Utils.showWizardDialog("Create New Workflow", Constants.workflows.map(function(workflow){
    		return {
    			name: workflow.description,
    			handler: function(){
    				window.location = window.location.origin + '/miso/workflow/new/' + workflow.workflowName;
    			}
    		};
    	}));	
    }
