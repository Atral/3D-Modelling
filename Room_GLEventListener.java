import gmaths.*;

import java.nio.*;

import javax.xml.crypto.dsig.Transform;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Room_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Room_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,8f,18f));
    this.camera.setTarget(new Vec3(0f,2f,0f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

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
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    cube.dispose(gl);
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, cube;
  private Light light;
  private SGNode twoBranchRoot;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/Wood_Floor_007_COLOR.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/Stylized_Wall_001_BaseColor.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureID4 = TextureLibrary.loadTexture(gl, );
    
    light = new Light(gl);
    light.setCamera(camera);
    
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0);

    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

    desk = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureID4);
    
    twoBranchRoot = new NameNode("two-branch structure");
    
    NameNode floorNode = new NameNode("floor");
    ModelNode floorShape = new ModelNode("Floor(0)", floor);

    NameNode leftWall = new NameNode("left wall");
    Mat4 m = Mat4Transform.scale(0.5f,16,16);
    m = Mat4.multiply(m, Mat4Transform.translate(-16,0.5f,0));
    TransformNode leftWallTransform = new TransformNode("scale(0.5, 16, 16); translate(-16,0.5,0)", m);
    ModelNode leftWallShape = new ModelNode("Cube(0)", cube);
    
    NameNode backWall = new NameNode("back wall");
    m = Mat4Transform.scale(16, 16, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,-16));
    TransformNode backWallTransform = new TransformNode("scale(1.4f,3.9f,1.4f); translate(0,0.5,0)", m);
    ModelNode backWallShape = new ModelNode("Cube(1)", cube);

    NameNode deskBLLeg = new NameNode("Desk back left leg");
    
        
    twoBranchRoot.addChild(floorNode);
      floorNode.addChild(floorShape);

      floorNode.addChild(leftWall);
        leftWall.addChild(leftWallTransform);
          leftWallTransform.addChild(leftWallShape);

      floorNode.addChild(backWall);
        backWall.addChild(backWallTransform);
          backWallTransform.addChild(backWallShape);
    twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    // Following two lines can be used to check scene graph construction is correct
    //twoBranchRoot.print(0, false);
    //System.exit(0);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl);
    twoBranchRoot.draw(gl);
  }
  
  // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }
  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}