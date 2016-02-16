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

/**
 * Created by bianx on 24/02/2014.
 */


var duration = 500, i = 0, root;
var r = 960 / 2;

var tree = d3.layout.tree()
        .size([360, r - 120])
        .separation(function (a, b) {
                      return (a.parent == b.parent ? 1 : 2) / a.depth;
                    });

var diagonal = d3.svg.diagonal.radial()
        .projection(function (d) {
                      return [d.y, d.x / 180 * Math.PI];
                    });

var vis = d3.select("#chart").append("svg:svg")
        .attr("width", r * 2)
        .attr("height", r * 2 - 150)
        .append("svg:g")
        .attr("transform", "translate(" + r + "," + r + ")");

function getProjectD3Json () {
  d3.json("/miso/d3graph/project/" + projectId_d3graph, function (json) {
    json.x0 = 800;
    json.y0 = 0;
    update(root = json);
  });
}

function update(source) {
  // Compute the new tree layout.
  var nodes = tree.nodes(root);

  // Update the links…
  var link = vis.selectAll("path.link")
          .data(tree.links(nodes), function (d) {
                  return d.target.id;
                });

  // Enter any new links at the parent's previous position.
  link.enter().append("svg:path")
          .attr("class", "link")
          .attr("d", diagonal)
          .transition()
          .duration(duration)
          .attr("d", diagonal);

  // Transition links to their new position.
  link.transition()
          .duration(duration)
          .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
  link.exit().transition()
          .duration(duration)
          .attr("d", diagonal)
          .remove();

  var node = vis.selectAll("circle.node").data(nodes);

  // Enter any new nodes at the parent's previous position.
  node.enter().append("svg:circle")
          .attr("dx", function (d) {
                  return source.x0;
                })
          .attr("dy", function (d) {
                  return source.y0;
                })
          .attr("r", 4)
          .attr("class", "node")
          .style("fill", function (d) {
                   return d._children ? "lightsteelblue" : "#fff";
                 })
          .style("stroke", function (d) {
                   return d.color == 0 ? "red"
                           : d.color == 1 ? "lightgreen"
                                  : d.color == 2 ? "gray"
                                     : "steelblue";
                 })
          .style("stroke-width", "1px")
          .attr("transform", function (d) {
                  return "rotate (" + (source.x0 - 90) + ")translate(" + source.y0 + ")";
                })
          .style("stroke", function (d) {
                   return d.color == 0 ? "red"
                           : d.color == 1 ? "lightgreen"
                                  : d.color == 2 ? "gray"
                                     : "steelblue";
                 })
          .on("click", click)
          .append("svg:title")
          .text(function (d) {
                  return d.description;
                })
          .transition()
          .duration(duration)
          .attr("dx", function (d) {
                  return d.x;
                })
          .attr("dy", function (d) {
                  return d.y;
                })
          .attr("transform", function (d) {
                  return "rotate (" + (d.x - 90) + ")translate(" + d.y + ")";
                });

  // Transition nodes to their new position.
  node.transition()
          .duration(duration)
          .attr("dx", function (d) {
                  return d.x;
                })
          .attr("dy", function (d) {
                  return d.y;
                })
          .style("fill", function (d) {
                   return d._children ? "lightsteelblue" : "#fff";
                 })
          .style("stroke", function (d) {
                   return d.color == 0 ? "red"
                           : d.color == 1 ? "lightgreen"
                                  : d.color == 2 ? "gray"
                                     : "steelblue";
                 })
          .attr("transform", function (d) {
                  return "rotate (" + (d.x - 90) + ")translate(" + d.y + ")";
                });

  // Transition exiting nodes to the parent's new position.
  node.exit().transition()
          .duration(duration)
          .attr("dx", function (d) {
                  return source.x;
                })
          .attr("dy", function (d) {
                  return source.y;
                })
          .remove();

  // Update the texts…
  var text = vis.selectAll("text")
          .data(nodes, function (d) {
                  return d.name;
                });

  text.enter().append("svg:text")
          .attr("class", "node1")
          .attr("dx", "8")
          .attr("dy", ".31em")
          .attr("transform", function (d) {
                  return "rotate (" + (d.x - 90) + ")translate(" + d.y + ")";
                })
          .text(function (d) {
                  return d.name;
                })
          .attr("text-anchor", "start")
          .transition()
          .duration(duration)
          .attr("transform", function (d) {
                  return "rotate (" + (d.x - 90) + ")translate(" + d.y + ")";
                })
          .attr("dx", "8")
          .attr("dy", ".31em");

  text.transition()
          .duration(duration)
          .attr("class", "node")
          .attr("dx", "8")
          .attr("dy", ".31em")
          .attr("transform", function (d) {
                  return "rotate (" + (d.x - 90) + ")translate(" + d.y + ")";
                });

  // Transition exiting nodes to the parent's new position.
  text.exit().transition()
          .duration(duration)
          .attr("dx", "8")
          .attr("dy", "31em")
          .remove();

  // Stash the old positions for transition.
  nodes.forEach(function (d) {
    d.x0 = d.x;
    d.y0 = d.y;
  });
}

// Toggle children on click.
function click(d) {
  if (d.children) {
    d._children = d.children;
    d.children = null;
  }
  else {
    d.children = d._children;
    d._children = null;
  }
  update(d);
}

d3.select(self.frameElement).style("height", "1000px");