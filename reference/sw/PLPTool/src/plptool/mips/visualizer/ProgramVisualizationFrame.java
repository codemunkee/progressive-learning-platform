/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProgramVisualizationFrame.java
 *
 * Created on Nov 10, 2011, 1:35:41 PM
 */

package plptool.mips.visualizer;
import plptool.gui.ProjectDriver;
import plptool.*;
import plptool.mips.*;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Paint;
/*
import java.awt.Container;
 *
 */
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
/*
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import edu.uci.ics.jung.graph.*;
 *
 */
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.decorators.*;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;

/**
 * Create the JUNG Visualization graphically in a pop-up window.
 *
 * @author will
 */
public class ProgramVisualizationFrame extends javax.swing.JFrame {

    private ProgramVisualization progVis;
    private ProgramVisualization.programGraph progGraph;
    private ProjectDriver plp;

    private Layout<String, String> layout;
    private VisualizationViewer<String,String> progVisServ;
    private vertexRecolor<String> vertexRecolor;

    // changes the color of drawn vertices
    protected final class vertexRecolor<String> implements Transformer<String,Paint>{
            private String PaintMe;
            private String unPaintMe;
            public void setPaintMe(String vertex){
                this.PaintMe = vertex;
            }
            public void unPaintMe(String vertex){
                this.unPaintMe = vertex;
            }
            public Paint transform(String vertex) {
                if(vertex.equals(PaintMe)){
                    return Color.GREEN;
                }
                else if(vertex.equals(unPaintMe)){
                    return Color.BLUE;
                }
                else{
                    return Color.RED;
                }
            }
        };

    // changes the size of drawn vertices
    private Transformer<String,Shape> vertexResizer = new Transformer<String,Shape>(){
        public Shape transform(String vertex){
            Ellipse2D vertexShape = new Ellipse2D.Double(-5, -5, 15, 15);
            return vertexShape;
        }
    };
    
    /** Creates new form ProgramVisualizationFrame */
    public ProgramVisualizationFrame(ProgramVisualization progVis, ProgramVisualization.programGraph progGraph, ProjectDriver plp) {
        initComponents();
        int vertexYPos;
        this.progVis = progVis;
        this.progGraph = progGraph;
        this.plp = plp;
        vertexRecolor = new vertexRecolor<String>();
        // Vertical Layout
        layout = new StaticLayout<String,String>(progGraph.buildGraph());
        // Grab graph's vertices
        List<String> vertexList = new ArrayList<String>(layout.getGraph().getVertices());
        // Traverse vertices, arrange them vertically
        vertexYPos = 25;
        for(int i=0; i<vertexList.size(); i++){
                layout.setLocation(vertexList.get(i), new Point2D.Double(50, vertexYPos));
                vertexYPos+=30;
        }
        //layout.setSize(new Dimension(600,600));
        // create the vis viewer
        progVisServ = new VisualizationViewer<String,String>(layout);
        this.format();
        // create the pane
        final GraphZoomScrollPane progVisScrollPane = new GraphZoomScrollPane(progVisServ);
        progVisServ.setPreferredSize(new Dimension(250,1000));
        //progVisScrollPane.setPreferredSize(new Dimension(600,600));
        //progVisServ.setSize(this.getSize());
        //progVisScrollPane.setSize(this.getSize());
        //this.setResizable(true);
        this.setLayout(new BorderLayout());
        getContentPane().add(progVisScrollPane, BorderLayout.CENTER);
        this.pack();
        
    }
    @SuppressWarnings("unchecked")
    // formatting
    private void format(){
        //vertexRecolor.transform("BEGIN");
        progVisServ.getRenderContext().setVertexShapeTransformer(vertexResizer);
        progVisServ.getRenderContext().setVertexFillPaintTransformer(vertexRecolor);

        //vertexRecolor.setPaintMe("Begin");
        progVisServ.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        progVisServ.getRenderer().getVertexLabelRenderer().setPosition(Position.E);
    }

    // paint current label green
    public void vert_repaint(String vertexName){
        vertexRecolor.setPaintMe(vertexName);
    }
    // as you move to next label, repaint red
    public void vert_unpaint(String vertexName){
        vertexRecolor.unPaintMe(vertexName);
    }

    // called by SimCoreGUI when there's a GUI update in simulation
    public void updateComponents() {
        long currentAddress = plp.sim.visibleAddr;
        Msg.M("Current Address: " + currentAddress);
        //currentAddress -= 4;
        //Msg.M("Current Address - 4: " + currentAddress);
        String currentLabel = plp.asm.lookupLabel(currentAddress);
        Msg.M("currentLabel: " + currentLabel);
        if(currentLabel != null){
            this.vert_repaint(currentLabel);
        }
        //this.vert_unpaint("Begin");
        this.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(plptool.gui.PLPToolApp.class).getContext().getResourceMap(ProgramVisualizationFrame.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
