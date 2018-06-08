package TiroParabolico;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;

public class RunTiro{
    public static void main(String[] args) {
        // TODO code application logic here
        GLProfile perfil = GLProfile.getDefault();
        GLCapabilities capacidades = new GLCapabilities(perfil);
        GLCanvas panelcanvas = new GLCanvas(capacidades);
        TiroParabolico tiro = new TiroParabolico();
        panelcanvas.addGLEventListener(tiro);
        panelcanvas.addMouseWheelListener(tiro);
        panelcanvas.addMouseMotionListener(tiro);
        panelcanvas.addKeyListener(tiro);
        JFrame frame = new JFrame("Tiro Parabólico");
        FPSAnimator animador = new FPSAnimator(panelcanvas, 30, true);
        frame.add(panelcanvas);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        animador.start();
    }
    
}
