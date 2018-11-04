<script>
var cy = cytoscape({
  container: document.getElementById('cy'),

  boxSelectionEnabled: false,
  autounselectify: true,

  style: cytoscape.stylesheet()
    .selector('node')
    .css({
      'content': 'data(title)',
      'text-valign': 'center',
      'shape': 'roundrectangle',
      'background-color': '#ddd',
      'color': '#222222'
    })
    .selector('edge')
    .style({
      'curve-style': 'bezier',
      'target-arrow-shape': 'triangle',
      'width': 2,
      'line-color': '#ddd',
      'target-arrow-color': '#ddd'
    }),

  layout: {
    name: 'breadthfirst',
    spacingFactor: 0.5,
    nodeDimensionsIncludeLabels: true,
  }
});

cy.on('tap', 'node', function() {
  document.getElementById("cydata").innerHTML = this.data('id');
});

function creategraph() {

  //console.log("Hello world!");

  cy.add([
	  { group: "nodes", data: { id: "n0", title: "n0" } },
	  { group: "nodes", data: { id: "n1", title: "n1" } },
	  { group: "edges", data: { id: "e0", source: "n0", target: "n1" } }
  ]);

  cy.layout({
    name: 'breadthfirst',
    spacingFactor: 0.5,
    nodeDimensionsIncludeLabels: true,
    roots: '#n0'
  }).run();

  return;

}
</script>