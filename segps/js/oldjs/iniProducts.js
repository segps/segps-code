// get the data
d3.csv("data/ProjectSecurityVulnerabilities.csv", function(error, links) {

var nodes = {};
 

var width = 800,
    height = 650,
    color = d3.scale.category20c();

//define a scale for color mapping
var colormapping = d3.scale.ordinal()
    .domain([0,nodes.length])
    .range(['#A700E6','#D95B96','#F4DA88','#22C1BE','#F24957','#DBEF91','#CF8EE8','#FF9B58','#B8FFC4','#91AEFF','#E873D3','#CCB298']);

var svg = d3.select("#visualization").append("svg")
    .attr("width", width)
    .attr("height", height);

//create label node tooltip
var labeltooltip = d3.select("body").append("div")
    .attr("class", "labeltooltip")
    .style("opacity", 1e-6);


//add zoom and pan behavior
var panrect = svg.append("rect")
    .attr("width", width)
    .attr("height", height)
    .style("fill", "none")
    .style("pointer-events", "all");

var container = svg.append("g")
    .attr("id", "container");
	
var zoom = d3.behavior.zoom()
    .scaleExtent([1, 10])
    .on("zoom", function() {
    	 container.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
     });
    svg.call(zoom);

var link = svg.selectAll(".link")
    .data(force.links())
    .enter().append("line")
    .attr("class", "link");
 // Compute the distinct nodes from the links.
links.forEach(function(link) {
    link.source = nodes[link.Project] || (nodes[link.Project] = {name: link.Project});
    link.target = nodes[link.Vulnerability] || (nodes[link.Vulnerability] = {name: link.Vulnerability});
    link.Score = +link.Score;
});

var force = d3.layout.force()
    .nodes(d3.values(nodes))
    .links(links)
    .gravity(0.2)
    .friction(0.8)
    .size([width, height])
    .linkDistance(200)
    .charge(-1500)
    .on("tick", tick)
    .start();

//label nodes
var labelnode = container.selectAll("circle.labelnode")
	.data(force.nodes())
	.enter().append("g")
	.attr("class", "labelnode")
	.call(force.drag);
	
	var circle = labelnode.append("svg:circle")
	.attr("r", function(d) {return nodewidth(d.num_issues);})
	.attr("fill", function (d,i) {return d3.rgb(colormapping(i)); });
	
	/*circle.on("mousemove", function(d, index, element) {
		labeltooltip.selectAll("p").remove();
		labeltooltip.style("left", (d3.event.pageX+15) + "px")
                            .style("top", (d3.event.pageY-10) + "px");
		labeltooltip.append("p").attr("class", "tooltiptext").html("<span>label: </span>" + d.name);
                labeltooltip.append("p").attr("class", "tooltiptext").html("<span>Vulnerability Severity: </span>" + d.Score);
    }); 
	
	circle.on("mouseover", function(d) {
    	labeltooltip.transition()
          .duration(500)
          .style("opacity", 1);
		link.style('stroke', function(l) {
			if (d === l.source || d === l.target)
			  return d3.rgb('#9E00D9');
			else
			  return 'gray';
			});
		link.style('opacity', function(o) {
			return o.source === d || o.target === d ? 1 : 0;
		});
		labelnode.style("opacity", function(o) {
			if (o.id != d.id)
				return neighboring(d.id, o.id) ? 1 : 0;
		});
    });    

    circle.on("mouseout", function(d) {
    	labeltooltip.transition()
          .duration(500)
          .style("opacity", 1e-6);
		link.style('stroke', 'gray');
		link.style('opacity', 1);
		labelnode.style("opacity", 1);  
    });
/*
// define the nodes
var node = svg.selectAll(".node")
    .data(force.nodes())
  .enter().append("g")
    .attr("class", "node")
    .on("click", click)
    .on("dblclick", dblclick)
    .call(force.drag);
 
// add the nodes
node.append("circle")
    .attr("r", 5)
    .style("fill", function(d) { return color(d.name); });

 
// add the text 
node.append("text")
    .attr("x", 10)
    .attr("dy", ".35em")
    .text(function(d) { return d.name; });
 
function tick() {
  link
      .attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  node
      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
}
 
// action to take on mouse click
function click() {
    d3.select(this).select("text").transition()
        .duration(750)
        .attr("x", 22)
        .style("stroke", "lightsteelblue")
        .style("stroke-width", ".5px")
        .style("font", "20px sans-serif");
    d3.select(this).select("circle").transition()
        .duration(750)
        .attr("r", 16);
}
 
// action to take on mouse double click
function dblclick() {
    d3.select(this).select("circle").transition()
        .duration(750)
        .attr("r", 6);
    d3.select(this).select("text").transition()
        .duration(750)
        .attr("x", 12)
        .style("stroke", "none")
        .style("fill", "black")
        .style("stroke", "none")
        .style("font", "10px sans-serif");
}
 */
});
