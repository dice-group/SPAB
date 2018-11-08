<script>
var cy = cytoscape({
  container: document.getElementById('cy'),

  autoungrabify: true,
  userPanningEnabled: false,
  
  style: cytoscape.stylesheet()
    .selector('node')
    .css({
      'content': 'data(title)',
      'text-valign': 'center',
      'shape': 'roundrectangle',
      'background-color': 'mapData(fmeasure, /*MIN*/, /*MAX*/, #ddd, #009FDF)',
      'color': '#222222',
      'font-size': '.7em'
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
    spacingFactor: 0.7
  }
});

cy.on('tap', 'node', function() {
  var html = 'Candidate number: ';
  html += this.data('id');
  html += '<br />F-measure: ';
  html += this.data('fmeasure');
  html += '<br />Regular expression: ';
  html += this.data('regex');
  html += '<br /><br />Regular expression hierarchy: <br />';
  html += this.data('hierarchy');
  document.getElementById("cydata").innerHTML = html;
});

function creategraph() {
	
// ELEMENTS
  cy.layout({
    name: 'breadthfirst',
    spacingFactor: 0.7,
    roots: '#0'
  }).run();

  return;

}

// Resize graph after window resize event

var resizer;
window.addEventListener('resize', function(event) {
  clearTimeout(resizer);
  resizer = setTimeout(resizeCy, 300);
});
function resizeCy() {
  cy.layout({
    name: 'breadthfirst',
    spacingFactor: 0.7,
    roots: '#0'
  }).run();
}
</script>