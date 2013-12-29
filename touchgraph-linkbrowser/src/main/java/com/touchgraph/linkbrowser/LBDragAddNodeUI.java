/*
 * TouchGraph LLC. Apache-Style Software License
 *
 *
 * Copyright (c) 2002 Alexander Shapiro. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by 
 *        TouchGraph LLC (http://www.touchgraph.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "TouchGraph" or "TouchGraph LLC" must not be used to endorse 
 *    or promote products derived from this software without prior written 
 *    permission.  For written permission, please contact 
 *    alex@touchgraph.com
 *
 * 5. Products derived from this software may not be called "TouchGraph",
 *    nor may "TouchGraph" appear in their name, without prior written
 *    permission of alex@touchgraph.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL TOUCHGRAPH OR ITS CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 */

package  com.touchgraph.linkbrowser;

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPaintListener;
import com.touchgraph.graphlayout.interaction.TGAbstractDragUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**  LBDragAddNodeUI contains code for adding LBNodes + LBEdges by dragging.
  *   
  * @author   Alexander Shapiro                                        
  * @version  1.20
  */

public class LBDragAddNodeUI extends TGAbstractDragUI implements TGPaintListener {

    Point mousePos = null;
    LBNode dragAddNode = null;
    TGLinkBrowser tgLinkBrowser;
  // ............

    public LBDragAddNodeUI( TGLinkBrowser tglb ) {        
        super(tglb.getTGPanel()); 
        tgLinkBrowser = tglb;
    }

    public void preActivate() {
        mousePos=null;
        tgPanel.addPaintListener(this);
    }

    public void preDeactivate() {
        tgPanel.removePaintListener(this);
    };

    public void mousePressed( MouseEvent e ) {
        dragAddNode = (LBNode) tgPanel.getMouseOverN();
    }    

    public void mouseReleased( MouseEvent e ) {
        mousePos=e.getPoint();
        LBNode mouseOverN = (LBNode) tgPanel.getMouseOverN();

        if (mouseOverN!=null && dragAddNode!=null && mouseOverN!=dragAddNode) {
            Edge ed=tgPanel.findEdge(dragAddNode,mouseOverN);
            if (ed==null) {                
                tgPanel.addEdge(new LBEdge(dragAddNode,mouseOverN)); 
            }
            else tgPanel.deleteEdge(ed);

        } else if ( mouseOverN == null && dragAddNode != null ) {
            try {
                LBNode n = new LBNode();      
                tgPanel.addNode(n);       
                tgPanel.addEdge(new LBEdge(dragAddNode,n)); 
                n.drawx = mousePos.x; 
                n.drawy = mousePos.y;
                tgPanel.updatePosFromDraw(n); 
                tgPanel.setSelect(n);
                tgLinkBrowser.lbNodeDialog.setLBNode(n);      
                tgLinkBrowser.lbNodeDialog.showDialog();
            } catch ( TGException tge ) {
                System.err.println(tge.getMessage());
                tge.printStackTrace(System.err);
            }
        }

        if (mouseWasDragged) { //Don't reset the damper on a mouseClicked
            tgPanel.resetDamper();
            tgPanel.startDamper();
        }   

        dragAddNode=null;
    }

    public void mouseDragged(MouseEvent e) {    
        mousePos=e.getPoint();
        tgPanel.repaint();
    }

    public void paintFirst(Graphics g) {};
    public void paintLast(Graphics g) {};

    public void paintAfterEdges(Graphics g) {

        if(mousePos==null) return;

        LBNode mouseOverN = (LBNode) tgPanel.getMouseOverN();

        if (mouseOverN==null) {
            g.setColor(Node.BACK_DEFAULT_COLOR);
            g.drawRect(mousePos.x-7, mousePos.y-7, 14, 14);
        }

        Color c;
        if (mouseOverN==dragAddNode)
            c = Color.lightGray;
        else
            c = LBEdge.DEFAULT_COLOR;

        LBEdge.paint(g, (int) dragAddNode.drawx, (int) dragAddNode.drawy,
               mousePos.x, mousePos.y, c, LBEdge.DEFAULT_TYPE);
    }

} // end com.touchgraph.graphlayout.interaction.LBDragAddNodeUI