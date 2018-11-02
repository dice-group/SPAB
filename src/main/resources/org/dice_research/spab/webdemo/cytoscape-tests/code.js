var cy = cytoscape({
  container: document.getElementById('cy'),

  boxSelectionEnabled: false,
  autounselectify: true,

  style: cytoscape.stylesheet()
    .selector('node')
      .css({
        'content': 'data(name)',
        'text-valign': 'center',
        'shape': 'roundrectangle'
      })
    .selector('edge')
      .style({
        'curve-style': 'bezier',
        'target-arrow-shape': 'triangle',
        'width': 2,
        'line-color': '#ddd',
        'target-arrow-color': '#ddd'
      }),

  elements: {
    nodes: [
      { data: { id: 'desktop', name: 'Cytoscape', href: 'http://cytoscape.org' } },
      { data: { id: 'desktop2', name: 'Cytoscape', href: 'http://cytoscape.org' } },
      { data: { id: 'desktop3', name: 'Cytoscape', href: 'http://cytoscape.org' } },
      { data: { id: 'js', name: 'Cytoscape.js', href: 'http://js.cytoscape.org' } }
    ],
    edges: [
      { data: { source: 'js', target: 'desktop' } },
      { data: { source: 'js', target: 'desktop2' } },
      { data: { source: 'js', target: 'desktop3' } }
    ]
  },

  layout: {
    name: 'breadthfirst',
    spacingFactor: 0.5,
    nodeDimensionsIncludeLabels: true,

  }
});

cy.on('tap', 'node', function(){
/*
  try { // your browser may block popups
    window.open( this.data('href') );
  } catch(e){ // fall back on url change
    window.location.href = this.data('href');
  }
*/
});

function myFunction() { 

    console.log("Hello world!");

    return;
    cy.add([
        { group: "nodes", data: { id: "n0", name: "n0" } },
        { group: "edges", data: { id: "e0", source: "n0", target: "n1" } },
        { group: "nodes", data: { id: "n1", name: "n1"  } },
        { group: "edges", data: { source: "desktop", target: "n1" } }
    ]);


    cy.layout({
        name: 'breadthfirst',
    'nodeDimensionsIncludeLabels': true
    }).run();




}

