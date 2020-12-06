import gmaths.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Room extends JFrame {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private GLEventListener glEventListener;
  private final FPSAnimator animator;
  private boolean spinning = false;

  public static void main(String[] args) {
    Room b1 = new Room("Room");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public Room(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    Camera camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Room_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    JButton heliUp = new JButton("Start Rotors");
    heliUp.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Room_GLEventListener.spinning(true);
      }
    });
    JButton heliDown = new JButton("Stop Rotors");
    heliDown.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Room_GLEventListener.spinning(false);
          
      }
    });

    JButton light1On = new JButton("Light 1 ON");

    light1On.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
          Room_GLEventListener.light1On();
      }
    });

    JButton light1Off = new JButton("Light 1 OFF");

    light1Off.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
          Room_GLEventListener.light1Off();
      }
    });
    
    JButton light2On = new JButton("Light 2 ON");

    light2On.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
          Room_GLEventListener.light2On();
      }
    });
    JButton light2Off = new JButton("Light 2 OFF");

    light2Off.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
          Room_GLEventListener.light2Off();
      }
    });

    JButton lampPose = new JButton("Random Lamp Pose");

    JPanel buttons = new JPanel();
    buttons.add(heliUp);
    buttons.add(heliDown);
    buttons.add(light1On);
    buttons.add(light1Off);
    buttons.add(light2On);
    buttons.add(light2Off);
    buttons.add(lampPose);

    getContentPane().add(buttons, BorderLayout.PAGE_END);


    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }
}
 
class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
}