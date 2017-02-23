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

var RunGraph = (function() {
  // This is an example metric graph. It should never occur in real data coming
  // from run scanner.
  var example = function(metrics) {
    // Scan the metrics for anything we can graph
    return metrics.filter(function(metric) {
      return metric.type == 'example';
    }).map(function(metric) {
      // Output a graph definition
      var node = document.createElement('P');
      return {
        dom : node,
        priority : 7, // The sorting order. Lowest is first.
        render : function() {
          // Callback to render the graph after the DOM node is inserted.
          node.innerText = 'Look at me. I am gorgeous.';
        }
      };
      
    });
  };
  
  return {
    // Takes a list of metrics and renders them to #metricsdiv
    renderMetrics : function(metrics) {
      var container = document.getElementById('metricsdiv');
      while (container.hasChildNodes()) {
        container.removeChild(container.lastChild);
      }
      // Start with graphs we know how to make (see the example for a template).
      // Then filter them as appropriate for the data we have.
      var graphs = [ example, ].map(function(graph) {
        return graph(metrics);
      }).reduce(function(a, b) {
        return a.concat(b);
      }, []).filter(function(x) {
        return x;
      }).sort(function(a, b) {
        return a.priority - b.priority;
      });
      if (graphs.length == 0) {
        container.innerText = "No graphs available.";
        return;
      }
      var table = document.createElement('TABLE');
      container.appendChild(table);
      var row = null;
      graphs.forEach(function(graph) {
        if (row == null) {
          row = document.createElement('TR');
          row.appendChild(graph.dom);
          container.appendChild(row);
        } else {
          row.appendChild(graph.dom);
          row = null;
        }
      });
      graphs.forEach(function(graph) {
        graph.render();
      });
    }
  };
})();
