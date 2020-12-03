import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Helicopter implements GLEventListener{

   /* Initialisation */
   public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
  }

  private NameNode initialise(GL3 gl) {

  /*  List<Object> heliRootParams = new ArrayList<Object>();
    List<Object> heliBodyParams = new ArrayList<Object>();
    List<Object> heliAxelParams = new ArrayList<Object>();
    List<Object> lWingParams = new ArrayList<Object>();
    List<Object> rWingParams = new ArrayList<Object>(); */
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_ao_mm.txt");
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f),
        new Vec3(0.3f, 0.3f, 0.3f), 50.0f);

    NameNode heliRoot = new NameNode("helicopter root");
    Mat4 m = Mat4Transform.translate(3, 0.2f, -6);
    TransformNode heliRootTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)", m);
    

    NameNode heliBody = new NameNode("helicopter body");
    m = Mat4Transform.translate(0, legLength + surfaceY/2 + 0.015f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.4f, 0.4f, 0.4f));
    TransformNode heliBodyTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)", m);
    ModelNode heliBodyShape = new ModelNode("Sphere(0)", sphere);

    NameNode heliAxel = new NameNode("axel");
    m = Mat4Transform.translate(0, legLength + surfaceY/2 + 0.015f + 0.25f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.2f, 0.2f, 0.2f));
    TransformNode heliAxelTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)", m);
    ModelNode heliAxelShape = new ModelNode("Axel(0)", axel);

    NameNode heliLWing = new NameNode("left wing");
    m = Mat4Transform.translate(-0.41f +0.1f, legLength + surfaceY/2 + 0.3f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.5f, 0.02f, 0.04f));
    TransformNode heliLWingTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)", m);
    ModelNode heliLWingShape = new ModelNode("Wing(0)", wing);

    NameNode heliRWing = new NameNode("right wing");
    m = Mat4Transform.translate(0.41f - 0.1f, legLength + surfaceY/2 + 0.3f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.5f, 0.02f, 0.04f));
    TransformNode heliRWingTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)", m);
    ModelNode heliRWingShape = new ModelNode("Wing(1)", wing);

    heliRoot.addChild(heliRootTransform);
    heliRootTransform.addChild(heliBody);
      heliBody.addChild(heliBodyTransform);
        heliBodyTransform.addChild(heliBodyShape);

      heliBody.addChild(heliAxel);
        heliAxel.addChild(heliAxelTransform);
          heliAxelTransform.addChild(heliAxelShape);

      heliAxel.addChild(heliLWing);
        heliLWing.addChild(heliLWingTransform);
          heliLWingTransform.addChild(heliLWingShape);

      heliAxel.addChild(heliRWing);
        heliRWing.addChild(heliRWingTransform);
          heliRWingTransform.addChild(heliRWingShape);

    heliRoot.update();

    return heliRoot;
  }
}
