/* created by IntelliJ IDEA.
 * User: thankia
 * Date: 11/22/12
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */

function readStatsdbperbasecontent(jsonfile) {
  jQuery("#statschartperbasecontent").html(
    "<button type=\"button\" onClick=jQuery('.lineA').toggle();jQuery('.circleA').toggle();> <b><font color=red>A </font></b></button> " +
    "<button type=\"button\" onClick=jQuery('.lineC').toggle();jQuery('.circleC').toggle();><b>C</b></button> " +
    "<button type=\"button\" onClick=jQuery('.lineG').toggle();jQuery('.circleG').toggle();><b><font color=blue>G</font></b></button>" +
    "<button type=\"button\" onClick=jQuery('.lineT').toggle();jQuery('.circleT').toggle();><b><font color=green>T</font></b></button> ");
  var percentile = ['100%', '90%', '80%', '70%', '60%', '50%', '40%', '30%', '20%', '10%', '0%'];

  var w = jQuery("#statschartperbasecontent").width();
  var parentWidth = jQuery(window).width();

  var percent = 45 * parentWidth / 100;

  var m = [80, 160, 200, 160], h = 700 - m[0] - m[2];
  w = percent;
  var baseposition = ['Base Position'];
  var percentage = ['Percentage'];
  var datas = [1];
  var vis = d3.select("#statschartperbasecontent").append('svg:svg')
      .data(datas)
      .attr('width', w)
      .attr('height', h + m[0] + m[2])
      .append('svg:g')
      .attr('transform', 'translate(25,20)');
  var data;
  var file;

  file = jsonfile.stats;
  data = file;
  var space = (w - 30) / data.length;
  load();
  function load() {
    var scorebox = vis.selectAll('rect.day')
      .data(data);
    scorebox.enter().append('svg:rect')
      .attr('x', function (d, i) {
        return (i) * space;
      })
    //.attr('class', 'scorebox')
      .attr('y', 0)
      .attr('width', function () {
        return space;
      })
      .attr('stroke', function () {
        return 'darkgray';
      })
      .attr('fill', function () {
        return 'transparent';
      })
      .attr('height', function () {
        return  (percentile.length - 1) * space;
      });

    var basebox = vis.selectAll('rect.day')
      .data(percentile);
    basebox.enter().append('svg:rect')
      .attr('x', function (g) {
        return 0;
      })
    // .attr('class', 'basebox')
      .attr('y', function (d, i) {
        return (i * space);
      })
      .attr('width', function () {
        return  data.length * space;
      })
      .attr('stroke', function () {
        return 'darkgray';
      })
      .attr('fill', function () {
        return 'transparent';
      })
      .attr('height', space);

    var percentiletext = vis.selectAll('text.day')
      .data(percentile);
    percentiletext.enter().append('svg:text')
      .attr('class', 'label')
      .attr('x', 0)
      .attr('y', function (d, i) {
        return (i * space);
      })
      .attr('text-anchor', 'end')
      .text(function (d) {
        return d;
      });

    var legendtext = vis.selectAll('text.day')
      .data(baseposition);
    legendtext.enter().append('svg:text')
      .attr('class', 'label bold')
      .attr('x', (data.length / 2) * space)
      .attr('y', parseInt((percentile.length - 0.5) * space))
      .attr('text-anchor', 'middle')
      .text(function (d) {
        return d;
      });

    var percenttext = vis.selectAll('text.day')
        .data(percentage);
    percenttext.enter().append('svg:text')
      .attr('class', 'label bold')
      .attr('x', 30)
      .attr('y', 30)
      .attr('text-anchor', 'middle')
      .attr("transform", "translate(" + ((space)) + "," + ((percentile.length) * space) + ")rotate(90)")
      .text(function (d) {
        return d;
      });


    var scoretext = vis.selectAll('text.day')
      .data(data);

    scoretext.enter().append('svg:text')
      .attr('class', 'label')
      .attr('x', 0)
      .attr('y', 0)
      .attr('text-anchor', 'middle')
      .attr("transform", function (d, i) {
        return "translate(" + (((i) * space)) + "," + ((percentile.length) * space) + ")rotate(90)";
      })
      .text(function (d, i) {
        return d.base;
      });

    var circleA = vis.selectAll('circle.node')
        .data(data);

    circleA.enter().append('svg:circle')
      .attr('class', 'circle circleA')
      .attr("id", "A")
      .attr('cx', function (d, i) {
        return (i * space);
      })
      .attr('cy', function (d, i) {
        return ( (100 - d.A) * space * 0.1);
      })
      .attr('title', function (d, i) {
        return "A:" + d.A;
      })
      .attr('r', 4)
      .on('mouseover', function () {
        over('A');
      })
      .on('mouseout', function () {
        out('A');
      });

    var circleT = vis.selectAll('circle.node')
      .data(data);

    circleT.enter().append('svg:circle')
      .attr('class', 'circle circleT')
      .attr("id", "T")
      .attr('cx', function (d, i) {
        return (i * space);
      })
      .attr('cy', function (d, i) {
        return ( (100 - d.T) * space * 0.1);
      })
      .attr('title', function (d, i) {
        return "T:" + d.T;
      })
      .attr('r', 4)
      .on('mouseover', function () {
        over('T');
      })
      .on('mouseout', function () {
        out('T');
      });

    var circleC = vis.selectAll('circle.node')
      .data(data);

    circleC.enter().append('svg:circle')
      .attr('class', 'circle circleC')
      .attr("id", "C")
      .attr('cx', function (d, i) {
        return (i * space);
      })
      .attr('cy', function (d, i) {
        return ( (100 - d.C) * space * 0.1);
      })
      .attr('r', 4)
      .attr('title', function (d, i) {
        return "C:" + d.C;
      })
      .on('mouseover', function () {
        over('C');
      })
      .on('mouseout', function () {
        out('C');
      });

    var circleG = vis.selectAll('circle.node')
        .data(data);

    circleG.enter().append('svg:circle')
      .attr('class', 'circle circleG')
      .attr("id", "G")
      .attr('cx', function (d, i) {
        return (i * space);
      })
      .attr('cy', function (d, i) {
        return ( (100 - d.G) * space * 0.1);
      })
      .attr('title', function (d, i) {
        return "G:" + d.G;
      })
      .attr('r', 4)
      .on('mouseover', function () {
        over('G');
      })
      .on('mouseout', function () {
        out('G');
      });

    var lineC = vis.selectAll("line.topwhisker")
      .data(data);

    lineC.enter().insert("svg:line")
      .attr("class", "line lineC")
      .attr("id", "C")
      .attr("x1", function (d, i) {
        return (i * space);
      })
      .attr("y1", function (d, i) {
        return ( (100 - d.C) * space * 0.1);
      })
      .attr("x2", function (d, i) {
        return ((i + 1) * space);
      })
      .attr("y2",
        function (d, i) {
          var j = i + 1;
          if (data[j]) {
            return ((100 - data[j].C) * space * 0.1);
          }
          else {
            return ((100 - data[i].C) * space * 0.1);
          }
        }).on('mouseover', function () {
          over('C');
        })
        .on('mouseout', function () {
          out('C');
        });

    var lineA = vis.selectAll("line.topwhisker")
      .data(data);

    lineA.enter().insert("svg:line")
      .attr("class", "line lineA")
      .attr("x1", function (d, i) {
        return (i * space);
      })
      .attr("y1", function (d, i) {
        return ( (100 - d.A) * space * 0.1);
      })
      .attr("x2", function (d, i) {
        return ((i + 1) * space);
      })
      .attr("y2", function (d, i) {
        var j = i + 1;
        if (data[j]) {
          return ((100 - data[j].A) * space * 0.1);
        }
        else {
          return ((100 - data[i].A) * space * 0.1);
        }
      })
      .on('mouseover', function () {
        over('A');
      })
      .on('mouseout', function () {
        out('A');
      });

    var lineT = vis.selectAll("line.topwhisker")
      .data(data);

    lineT.enter().insert("svg:line")
      .attr("class", "line lineT")
      .attr("id", "T")
      .attr("x1", function (d, i) {
        return (i * space);
      })
      .attr("y1", function (d, i) {
        return ( (100 - d.T) * space * 0.1);
      })
      .attr("x2", function (d, i) {
        return ((i + 1) * space);
      })
      .attr("y2",
        function (d, i) {
          var j = i + 1;
          if (data[j]) {
            return ((100 - data[j].T) * space * 0.1);
          }
          else {
            return ((100 - data[i].T) * space * 0.1);
          }
        })
        .on('mouseover', function () {
          over('T');
        })
        .on('mouseout', function () {
          out('T');
        });

    var lineG = vis.selectAll("line.topwhisker")
      .data(data);

    lineG.enter().insert("svg:line")
      .attr("class", "line lineG")
      .attr("id", "G")
      .attr("x1", function (d, i) {
        return (i * space);
      })
      .attr("y1", function (d, i) {
        return ( (100 - d.G) * space * 0.1);
      })
      .attr("x2", function (d, i) {
        return ((i + 1) * space);
      })
      .attr("y2",
        function (d, i) {
          var j = i + 1;
          if (data[j]) {
            return ((100 - data[j].G) * space * 0.1);
          }
          else {
            return ((100 - data[i].G) * space * 0.1);
          }
        })
        .on('mouseover', function () {
          over('G');
        })
        .on('mouseout', function () {
          out('G');
        });
  }

  function over(d) {
    jQuery(".line").attr('opacity', 0.5);
    jQuery(".circle").attr('opacity', 0.5);

    jQuery(".line" + d).attr('opacity', 1);
    jQuery(".circle" + d).attr('opacity', 1);
  }

  function out(d) {
    jQuery(".line").attr('opacity', 1);
    jQuery(".circle").attr('opacity', 1);
  }
}