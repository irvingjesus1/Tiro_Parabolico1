package TiroParabolico;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.JPanel;

public class TiroParabolico extends MouseAdapter implements GLEventListener, MouseWheelListener, MouseMotionListener, KeyListener{
    //VARIABLES
    JPanel panel;
    GL2 gl;
    GLU glu = new GLU();
    GLUT glut;
    float VelocidadY, VelocidadX, gravedad, Velocidadfinal, Velocidadinicial;
    float altura, angulo, Tiempo, dvtiempo;
    float distancia_alcanzada;
    int altura_Pasto = 200;
    float curva = 0, desplazamiento = 0, gueltas = 0;
    float   vc = 0, rotar = 0f;
    float direccionX = 0, direccionY = 0, direccionZ = 0;
    Texture tex , tex2,tex3,tex4,tex5,tex6,tex7;
    //CAMARA
    private float UX = 0f;
    private float UY = 0f;
    private float UZ = 0f;
    private float PCX = -25f;
    private float PCY =20f;
    private float PCZ = 20f;
    private float TCZ = 5.0f;
    private float EX = 0;
    private float EY = 0;
    private float EZ = 10;
    private float GX2 = 0;
    private float GY2 = 0;
    private int cursorX;
    private int cusorY;
    private float x, y;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////METODOS OPENGL
    @Override
    public void init(GLAutoDrawable drawable) {
        altura = 2f;
        Velocidadinicial = 12f;
        angulo = 60;
        gravedad = 9.8f;
        vc = .1f;
        try {
        tex = TextureIO.newTexture(new File("src/Imagenes/balon22.jpg"), true);            
        tex2 = TextureIO.newTexture(new File("src/Imagenes/jordan.png"), true);            
        tex3 = TextureIO.newTexture(new File("src/Imagenes/cancha.jpg"), true);
        tex4=TextureIO.newTexture(new File("src/Imagenes/court.jpg"), true);
        tex5=TextureIO.newTexture(new File("src/Imagenes/placa.png"), true);
        tex6=TextureIO.newTexture(new File("src/Imagenes/tubo.png"), true);
        tex7=TextureIO.newTexture(new File("src/Imagenes/backplaca.jpg"), true);
        } catch (IOException e) {
            System.err.print( e);
            System.exit(1);
        }
        GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LESS);
         gl.glClearColor(0f, 0.256f, 0.0f, 1.0f);
    }
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
    @Override
    public void display(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glClear(GL2.GL_ACCUM_BUFFER_BIT);
        gl.glTexEnvi(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_DECAL);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, TCZ);
        //Camara
        glu.gluLookAt(PCX, PCY , PCZ,EX, EY , EZ,0, 1, 0);
        //Rotacion Escenario
        gl.glRotatef(GX2, 1, 0, 0);
        gl.glRotatef(GY2, 0, 1, 0);
        gl.glPointSize(10f);
         //Ejes(gl);
         Cancha(gl);
         Malla(gl);
         //crear jordan
         Jordan(gl);
         //crear canasta
         Cesto(gl);
         Cesto2(gl);
         //crear balon
         Balon(gl,UX,UY,UZ);
         Tiro();
        gl.glFlush();
        System.out.println("Desplazamiento: "+desplazamiento);
        System.out.println("Curvatura: "+desplazamiento);
    }
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        gl = drawable.getGL().getGL2();
        glu = new GLU();
        if (height <= 0)
            height = 1;
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 1000.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////MANIPULACION DE ENTORNO
    //Rueda del raton
    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        int raton = e.getWheelRotation();
        switch(raton){
            case -1:
                TCZ += 0.5f;
                break;
            case 1:
                TCZ -= 0.5f;
                break;
        }
    }
    @Override
    public void mouseDragged(MouseEvent e){
        x = e.getX();
        y = e.getY();
        Dimension size = e.getComponent().getSize();
        float thetaY = 50.0f * ( (float)( x - cursorX) / (float)size.width);
        float thetaX = 50.0f * ( (float)( cusorY - y) / (float)size.height);
        cursorX = (int)x;
        cusorY = (int)y;
        GX2 += thetaX;
        GY2 += thetaY;
    }
    @Override
    public void mousePressed(MouseEvent e){
        cursorX = e.getX();
        cusorY = e.getY();
    }
        public void Letras(GL2 gl, float x, float y, float z, String cadena) {
        char[] c = cadena.toCharArray();
        gl.glColor3f(1f, 0, 0);
        gl.glRasterPos3f(x, y, z);
        for (int s = 0; s < c.length; s++) {
        glut.glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, c[s]);
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////AMBIENTE    
    private void Ejes(GL2 gl) {
        // EJE X 
        gl.glLineWidth(1);
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3f(1f, 0f, 0f);
        gl.glVertex3f(-100, 0, 0);
        gl.glVertex3f(100, 0, 0);
        gl.glEnd();
        //EJE Y 
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3f(0f, 1f, 0f);
        gl.glVertex3f(0, -100, 0);
        gl.glVertex3f(0, 100, 0);
        gl.glEnd();
        //EJE Z
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3f(0f,0.4f, 1f);
        gl.glVertex3f(0, 0, -100);
        gl.glVertex3f(0, 0, 100);
        gl.glEnd();
    }
    private void Malla(GL2 gl) {
        gl.glColor3f(0.7f, 0.7f, 5f);
        gl.glLineWidth(1);
        float zExtent, xExtent;
        float xLocal, zLocal;
        int loopX, loopZ;
        float separacion = 1f;
        int lineasX = 100;
        int lineasY = 100;
        gl.glBegin( GL2.GL_LINES );
           zExtent = separacion * lineasY;
           for(loopX = -lineasX; loopX <=lineasX; loopX++ ) {
               xLocal = separacion*loopX;
               gl.glVertex3f( xLocal, -7f, -zExtent );
               gl.glVertex3f( xLocal, -7f,  zExtent );
           }
           xExtent = separacion * lineasX ;
           for(loopZ = -lineasY; loopZ <= lineasY; loopZ++ ) {
               zLocal = separacion * loopZ;
               gl.glVertex3f( -xExtent, -7f, zLocal );
               gl.glVertex3f(  xExtent, -7f, zLocal );
           }
        gl.glEnd();
    }
//////////////////////////////////////////////////////////////////////////////////////////////OBJETOS
//private void Jordan(GL2 gl)         {
////        gl.glEnable(GL2.GL_ALPHA_TEST);
////        gl.glAlphaFunc( GL2.GL_NOTEQUAL, 0);
//        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
//        tex2.enable(gl);
//        tex2.bind(gl);
//        //Cara 2    D
//        gl.glBegin(gl.GL_QUADS);
//            gl.glTexCoord2f(0, 0);
//            gl.glVertex3f(0, -7f, -4f);
//
//            gl.glTexCoord2f(1, 0);
//            gl.glVertex3f(0, -7f, 0);
//
//            gl.glTexCoord2f(1, 1);
//            gl.glVertex3f(0, 0, 0);
//
//            gl.glTexCoord2f(0, 1);
//            gl.glVertex3f(0, 0, -4f);                                
//        gl.glEnd();
//        tex2.disable(gl);
//}
    private void Jordan(GL2 gl)         {
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        //DERECHA
gl.glColor3f(0f, 2f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4f, -1.5f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -4f, -1.9f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f, -1f, -1.9f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.2f, -1f, -1.5f);                                
        gl.glEnd();
        /////////VERDE
        gl.glColor3f(0f, 2f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -4f, -1.5f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -4f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f, -1f, -1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -1f, -1.5f);                                
        gl.glEnd();
        /////////IZ
gl.glColor3f(0f, 2f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -4f, -1.9f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.2f, -4f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -1f, -1.9f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -1f, -1.5f);                                
        gl.glEnd();
        ///////////////ATRAS
                gl.glColor3f(0f, 2f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -4f, -1.9f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -4f, -1.9f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f, -1f, -1.9f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -1f, -1.9f);                                
        gl.glEnd();
        ////////////////////////////////////////////////////////////////CABEZA
        gl.glPushMatrix();
        gl.glTranslatef(0, -0.5f, -1.7f);
gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
double lados=50;
//glutSolidSphere(1,10,10);
for(float i=(float) -Math.PI;i<Math.PI;i+=Math.PI/lados)
{
gl.glBegin(GL2.GL_QUAD_STRIP);
for(int j=0;j<lados+1;j++)
{
                          gl.glColor3f(0.0f, 0.0f, 0.0f);
                            double radio = 0.5f;
                            gl.glVertex3d(
                            (Math.sqrt(Math.cos(i))/.6)*radio * Math.cos(2 * j * Math.PI/lados),//X
                            (Math.sqrt(Math.cos(i))/.6)*radio * Math.sin(2 * j * Math.PI/lados),
                            i*radio);
                            gl.glVertex3d(
                            (Math.sqrt(Math.cos(i+(Math.PI/lados)))/.6)*radio * Math.cos(2 * j * Math.PI/lados),
                            (Math.sqrt(Math.cos(i+(Math.PI/lados)))/.6)*radio * Math.sin(2 * j * Math.PI/lados),
                            (i+Math.PI/lados)*radio);
			}
			gl.glEnd();
		}
	gl.glPopMatrix();
//////////////////////////////////////////////////
  gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        //DERECHA
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -0.2f, 0);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -3.5f, -3.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f, -3.3f, -3.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.2f, 0, -0);                                
        gl.glEnd();
        /////////////////////////////CONTRAPARTE
      gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        //DERECHA
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -0.2f, 0);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.2f, -3.5f, -3.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -3.3f, -3.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, 0, -0);                                
        gl.glEnd();
        ///////////////////////////////////////ARRIBA
            gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, 0f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -3.3f, -3.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -3.3f, -3.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, 0f, 0f);                                
        gl.glEnd();
        /////////////////////////CONTRAPARTE
                    gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -0.2f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -3.5f, -3.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -3.5f, -3.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -0.2f, 0f);                                
        gl.glEnd();
        //////////////////////////ATRAS
 gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -3.5f, -3.5f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.2f, -3.5f, -3.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -3.3f, -3.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.2f, -3.3f, -3.5f);                                
        gl.glEnd();
        ////////////////////////////ADELANTE
         gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -0.2f, -0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -0.2f, 0f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f,0f, 0f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, 0f, 0f);                                
        gl.glEnd();
        ///////////////////////////////////////////////////////////////////////////////////////PIERNA TRASERA
          gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        //DERECHA
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4f, -1.9f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -7f, -4f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f, -6.6f, -4f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.2f, -3.6f, -1.9f);                                
        gl.glEnd();
        ////////CONTRAPARTE
 gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -4f, -1.9f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.2f, -7f, -4f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -6.6f, -4f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -3.6f, -1.9f);                                
        gl.glEnd();
        //ARRIBA
         gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -3.8f, -1.9f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -6.6f, -4f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -6.6f, -4f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -3.6f, -1.9f);                                
        gl.glEnd();
        //ABAJO
                 gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4f, -1.9f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -7f, -4f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -7f, -4f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -4f, -1.9f);                                
        gl.glEnd();
        ///ATRAS
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -7f, -4f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.2f, -7f, -4f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -6.6f, -4f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.2f, -6.6f, -4f);                                
        gl.glEnd();
        ////////////////////////////////////////PIERNA DELANTERA
          gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        //DERECHA
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4.4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -4f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f, -3.6f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.2f, -4f, 0f);                                
        gl.glEnd();
        //CONTRAPARTE
        gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -4.4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.2f, -4f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -3.6f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -4f, 0f);                                
        gl.glEnd();
        //ARRIBA
       gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -3.6f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -3.6f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -4f, 0f);                                
        gl.glEnd();
        //ABAJO
               gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4.4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -4f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -4f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -4.4f, 0f);                                
        gl.glEnd();
        /////////////////////////////////////////////////////PANTORRILLA///////////////////////////////////////////////////////////////////
        gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4.4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -7f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f, -6.6f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.2f, -4f, 0f);                                
        gl.glEnd();
        //CONTRAPARTE
                gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -4.4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.2f, -7f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -6.6f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -4f, 0f);                                
        gl.glEnd();
        //ARRIBA
     gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -6.6f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -6.6f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -4f, 0f);                                
        gl.glEnd();
        //ABAJO
             gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -4.4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -7f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -7f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -4.4f, 0f);                                
        gl.glEnd();
        //ATRAS
                     gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.2f, -7.4f, -1.5f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.2f, -7f, -1.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.2f, -6.6f,-1.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.2f, -6.6f, -1.5f);                                
        gl.glEnd();
        //RODILLA
         gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.2f, -4.4f, 0f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.2f, -4.4f, 0f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.2f, -4f,0f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.2f, -4f, 0f);                                
        gl.glEnd();
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private void Balon(GL2 gl,float UbicacionX, float UbicacionY, float UbicacionZ)         {
gl.glPushMatrix();
gl.glTranslatef(UbicacionX, UbicacionY, UbicacionZ);
// gl.glRotatef(90, 0, 1, 0);
gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        //gl.glColor3f(0, 0.f, 0);
//float  [][] colores= {{1,1,0},{0.839f,0.733f,0.003f},{1,1,0},{0.839f,0.733f,0.003f},{1,1,0},{0.839f,0.733f,0.003f}};
Random rand = new Random();
double lados=50;
//glutSolidSphere(1,10,10);
for(float i=(float) -Math.PI;i<Math.PI;i+=Math.PI/lados)
{
gl.glBegin(GL2.GL_QUAD_STRIP);
for(int j=0;j<lados+1;j++)
{
                          gl.glColor3f(1.0f, 0.5f, 0.0f);
//                            float color [] = colores[rand.nextInt(6)];
//                           gl.glColor3f(color[1],color[0],color[1]);
                            double radio = 0.5f;
                            gl.glVertex3d(
                            (Math.sqrt(Math.cos(i))/.6)*radio * Math.cos(2 * j * Math.PI/lados),//X
                            (Math.sqrt(Math.cos(i))/.6)*radio * Math.sin(2 * j * Math.PI/lados),
                            i*radio);
                            gl.glVertex3d(
                            (Math.sqrt(Math.cos(i+(Math.PI/lados)))/.6)*radio * Math.cos(2 * j * Math.PI/lados),
                            (Math.sqrt(Math.cos(i+(Math.PI/lados)))/.6)*radio * Math.sin(2 * j * Math.PI/lados),
                            (i+Math.PI/lados)*radio);
			}
			gl.glEnd();
		}
	gl.glPopMatrix();
/////////////////////////////////////////////////////////////////////////////////////////////////

}
private void Cesto(GL2 gl)         {
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
//gl.glColor3f(0f, 0f, 0f);
        tex6.enable(gl);
        tex6.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.5f, -7f, 17f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.5f, -7f, 16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.5f, 0, 16f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.5f, 0, 17f);                                
        gl.glEnd();
        //lado2
//gl.glColor3f(1.0f, 0.5f, 0.0f);
        tex6.enable(gl);
        tex6.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.5f, -7f, 17f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.5f, -7f, 17f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.5f, 0, 17f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.5f, 0, 17f);                                
        gl.glEnd();
//lado3
//gl.glColor3f(3.0f, 2.0f, 0.2f);
tex6.enable(gl);
        tex6.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.5f, -7f, 16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.5f, -7f, 17f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.5f, 0, 17f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.5f, 0, 16f);                                
        gl.glEnd();
////lado4 Viendo hacia el balon
//gl.glColor3f(8.0f, 2.0f, 0.2f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.5f, -7f, 16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.5f, -7f, 16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.5f, 0, 16f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.5f, 0, 16f);                                
        gl.glEnd();
//ARRIBA
//gl.glColor3f(10.0f, 6.0f, 0.2f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.5f, 0, 16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.5f, 0, 16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.5f, 0, 17f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.5f, 0, 17f);                                
        gl.glEnd();
        tex6.disable(gl);
//////////////////////////////////////////////////////////////////////////////////rebote
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        tex5.enable(gl);
        tex5.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2f, -1f, 16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-2f, -1f, 15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-2f, 3f, 15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2f, 3f, 16f);                                
        gl.glEnd();
        tex5.disable(gl);
        //lado2 ATRAS
        tex7.enable(gl);
        tex7.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(2f, -1f, 16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-2f, -1f, 16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-2f, 3f, 16f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(2f, 3f, 16f);                                
        gl.glEnd();
        tex7.disable(gl);
//lado3
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(2f, -1f, 16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2f, -1f, 15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2f, 3f, 15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(2f, 3f, 16f);                                
        gl.glEnd();
        tex7.disable(gl);
//lado4 Viendo hacia el balon
 tex5.enable(gl);
 tex5.bind(gl);
gl.glColor3f(1.0f, 0.5f, 0.0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(2f, -1f, 15.5f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-2f, -1f, 15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-2f, 3f, 15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(2f, 3f, 15.5f);                                
        gl.glEnd();
         tex5.disable(gl);
//ARRIBA
tex5.enable(gl);
tex5.bind(gl);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2f,3f, 16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2f, 3f, 16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2f, 3f, 15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2f, 3f,15.5f);                                
        gl.glEnd();
        tex5.disable(gl);
        ////////////////////////////////////////////////////////////////////CANASTA
        //DER
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3f(1.0f, 0f, 0f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-1f, -1f, 13f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-1f, -1f, 15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-1f, -0.5f, 15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-1f, -0.5f, 13f);                                
        gl.glEnd();
        //IZQ
         gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3f(1.0f, 0f, 0f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(1f, -1f, 13f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(1f, -1f, 15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(1f, -0.5f, 15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(1f, -0.5f, 13f);                                
        gl.glEnd();
        //FRENTE
                 gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3f(1.0f, 0f, 0f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(1f, -1f, 13f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(1f, -0.5f, 13f);

            gl.glTexCoord2f(1, 1);
             gl.glVertex3f(-1f, -0.5f, 13f);        
           
            gl.glTexCoord2f(0, 1);
             gl.glVertex3f(-1f, -1f, 13f);                       
        gl.glEnd();
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private void Cesto2(GL2 gl)         {
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
//gl.glColor3f(0f, 0f, 0f);
        tex6.enable(gl);
        tex6.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.5f, -7f,- 17f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.5f, -7f, -16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.5f, 0,- 16f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.5f, 0, -17f);                                
        gl.glEnd();
        //lado2
//gl.glColor3f(1.0f, 0.5f, 0.0f);
        tex6.enable(gl);
        tex6.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.5f, -7f,- 17f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.5f, -7f, -17f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.5f, 0, -17f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.5f, 0, -17f);                                
        gl.glEnd();
//lado3
//gl.glColor3f(3.0f, 2.0f, 0.2f);
tex6.enable(gl);
        tex6.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.5f, -7f, -16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.5f, -7f, -17f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.5f, 0, -17f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.5f, 0, -16f);                                
        gl.glEnd();
////lado4 Viendo hacia el balon
//gl.glColor3f(8.0f, 2.0f, 0.2f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.5f, -7f,- 16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.5f, -7f, -16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.5f, 0, -16f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.5f, 0, -16f);                                
        gl.glEnd();
//ARRIBA
//gl.glColor3f(10.0f, 6.0f, 0.2f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0.5f, 0, -16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-0.5f, 0, -16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-0.5f, 0, -17f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0.5f, 0, -17f);                                
        gl.glEnd();
        tex6.disable(gl);
//////////////////////////////////////////////////////////////////////////////////rebote
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        tex5.enable(gl);
        tex5.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(2f, -1f, -16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2f, -1f, -15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2f, 3f, -15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(2f, 3f, -16f);                                
        gl.glEnd();
        tex5.disable(gl);
        //lado2 ATRAS
        tex7.enable(gl);
        tex7.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2f, -1f, -16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2f, -1f,- 16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2f, 3f, -16f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2f, 3f, -16f);                                
        gl.glEnd();
        tex7.disable(gl);
//lado3
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2f, -1f, -16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-2f, -1f, -15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-2f, 3f,- 15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2f, 3f,- 16f);                                
        gl.glEnd();
        tex7.disable(gl);
//lado4 Viendo hacia el balon
 tex5.enable(gl);
 tex5.bind(gl);
gl.glColor3f(1.0f, 0.5f, 0.0f);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2f, -1f, -15.5f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2f, -1f, -15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2f, 3f, -15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2f, 3f, -15.5f);                                
        gl.glEnd();
         tex5.disable(gl);
//ARRIBA
tex5.enable(gl);
tex5.bind(gl);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(2f,3f, -16f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-2f, 3f, -16f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-2f, 3f, -15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(2f, 3f,-15.5f);                                
        gl.glEnd();
        tex5.disable(gl);
        ////////////////////////////////////////////////////////////////////CANASTA
        //DER
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3f(1.0f, 0f, 0f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(1f, -1f, -13f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(1f, -1f, -15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(1f, -0.5f, -15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(1f, -0.5f, -13f);                                
        gl.glEnd();
        //IZQ
         gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3f(1.0f, 0f, 0f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-1f, -1f, -13f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-1f, -1f, -15.5f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(-1f, -0.5f, -15.5f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-1f, -0.5f,- 13f);                                
        gl.glEnd();
        //FRENTE
                 gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3f(1.0f, 0f, 0f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-1f, -1f, -13f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(-1f, -0.5f, -13f);

            gl.glTexCoord2f(1, 1);
             gl.glVertex3f(1f, -0.5f, -13f);        
           
            gl.glTexCoord2f(0, 1);
             gl.glVertex3f(1f, -1f, -13f);                       
        gl.glEnd();
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private void Cancha(GL2 gl)         {
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        tex4.enable(gl);
        tex4.bind(gl);
        gl.glBegin(gl.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-10f, -7f, 17f);

            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(10f, -7f, 17f);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(10f, -7f, -17f);

            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-10f, -7f, -17f);                                
        gl.glEnd();
        tex4.disable(gl);
}
//////////////////////////////////////////////////////////////////////////////////////////////FISICA 
        public void recta_caida(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glColor3f(1f, 1f, 0);
        gl.glLineWidth(distancia_alcanzada);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(0f, 0, 0);// eje -x
        gl.glVertex3f(distancia_alcanzada, 0, 0);// eje +x
        gl.glEnd();
        gl.glFlush();
    }
        public void Tiro(){
        desplazamiento = Velocidadinicial * (float) Math.cos(Math.toRadians(angulo)) * dvtiempo;
        UZ=desplazamiento;
        curva = altura + Velocidadinicial * (float) Math.sin(Math.toRadians(angulo)) * dvtiempo + (-0.5f * gravedad * dvtiempo * dvtiempo);
        UY=curva;
        dvtiempo = dvtiempo + vc;
        distancia_alcanzada = (VelocidadX * dvtiempo);
        if (curva < -altura) {
            dvtiempo = 0.f;
        }
        rotar += .5f;
        }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode){
            case KeyEvent.VK_UP:
                Velocidadinicial+=0.1f;
                break;
                            case KeyEvent.VK_DOWN:
                Velocidadinicial-=0.1f;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
