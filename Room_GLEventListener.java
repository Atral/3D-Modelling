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
  private SGNode roomRoot;
  private SGNode deskRoot;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/Wood_Floor_007_COLOR.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/Stylized_Wall_001_BaseColor.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureID4 = TextureLibrary.loadTexture(gl, "textures/Wood_021_basecolor.jpg");
    
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
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0,0,0), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

    Model desk = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureID4);
    
    roomRoot = new NameNode("room structure");
    
    NameNode floorNode = new NameNode("floor");
    Mat4 m = Mat4Transform.scale(16, 1f, 16);
    TransformNode floorTransform = new TransformNode("scale(16, 1f, 16)", m);
    ModelNode floorShape = new ModelNode("Floor(0)", floor);

    NameNode leftWall = new NameNode("left wall");
    m = Mat4Transform.scale(0.5f, 16, 16);
    m = Mat4.multiply(m, Mat4Transform.translate(-16,0.5f,0));
    TransformNode leftWallTransform = new TransformNode("scale(0.5, 16, 16); translate(-16,0.5,0)", m);
    ModelNode leftWallShape = new ModelNode("Cube(0)", cube);
    
    NameNode backWall = new NameNode("back wall");
    m = Mat4Transform.scale(16, 16, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,-16));
    TransformNode backWallTransform = new TransformNode("scale(1.4f,3.9f,1.4f); translate(0,0.5,0)", m);
    ModelNode backWallShape = new ModelNode("Cube(1)", cube);
        
    roomRoot.addChild(floorNode);
      floorNode.addChild(floorTransform);
        floorTransform.addChild(floorShape);

      floorNode.addChild(leftWall);
        leftWall.addChild(leftWallTransform);
          leftWallTransform.addChild(leftWallShape);

      floorNode.addChild(backWall);
        backWall.addChild(backWallTransform);
          backWallTransform.addChild(backWallShape);

    roomRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    // Following two lines can be used to check scene graph construction is correct
    //twoBranchRoot.print(0, false);
    //System.exit(0);
    
    float legLength = 3;
    float legSide = 0.4f;
    float surfaceX = 12;
    float surfaceY = 0.2f;
    float surfaceZ = 4;

    deskRoot = new NameNode("desk structure");
    m = Mat4Transform.translate(0, 0, -16/2 + 0.25f + surfaceZ/2);
    TransformNode deskTransform = new TransformNode("translate(0, 0, -16)", m);

    NameNode deskTop = new NameNode("Desk top surface");
    m = Mat4Transform.translate(0, legLength, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(surfaceX, surfaceY, surfaceZ));
    TransformNode deskTopTransform = new TransformNode("scale(24, 0.2f, 7); translate(0, 0.5f, 0)", m);
    ModelNode deskTopShape = new ModelNode("Desk(0)", desk);

    NameNode deskBLLeg = new NameNode("Desk back left leg");
    m = Mat4Transform.translate(-surfaceX/2 + legSide/2, legLength/2, -surfaceZ/2 + legSide/2);
    m = Mat4.multiply(m, Mat4Transform.scale(legSide, legLength, legSide));
    TransformNode deskBLLegTransform = new TransformNode("scale(0.4f, 3, 0.4f); translate(-15.6f, 0.5f, -16f)", m);
    ModelNode deskBLLegShape = new ModelNode("Desk(1)", desk);

    NameNode deskBRLeg = new NameNode("Desk back right leg");
    m = Mat4Transform.translate(surfaceX/2 - legSide/2, legLength/2, -surfaceZ/2 + legSide/2);
    m = Mat4.multiply(m, Mat4Transform.scale(legSide, legLength, legSide));
    TransformNode deskBRLegTransform = new TransformNode("scale(0.4f, 3, 0.4f); translate(0, 0.5f, -16f)", m);
    ModelNode deskBRLegShape = new ModelNode("Desk(2)", desk);

    NameNode deskFLLeg = new NameNode("Desk front left leg");
    m = Mat4Transform.translate(-surfaceX/2 + legSide/2, legLength/2, surfaceZ/2 - legSide/2);
    m = Mat4.multiply(m, Mat4Transform.scale(legSide, legLength, legSide));
    TransformNode deskFLLegTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)", m);
    ModelNode deskFLLegShape = new ModelNode("Desk(3)", desk);

    NameNode deskFRLeg = new NameNode("Desk front right leg");
    m = Mat4Transform.translate(surfaceX/2 - legSide/2, legLength/2, surfaceZ/2 - legSide/2);
    m = Mat4.multiply(m, Mat4Transform.scale(legSide, legLength, legSide));
    TransformNode deskFRLegTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)", m);
    ModelNode deskFRLegShape = new ModelNode("Desk(4)", desk);

    deskRoot.addChild(deskTransform);

    deskTransform.addChild(deskTop);
        deskTop.addChild(deskTopTransform);
          deskTopTransform.addChild(deskTopShape);

      deskTop.addChild(deskBLLeg);
        deskBLLeg.addChild(deskBLLegTransform);
          deskBLLegTransform.addChild(deskBLLegShape);

      deskTop.addChild(deskBRLeg);
        deskBRLeg.addChild(deskBRLegTransform);
          deskBRLegTransform.addChild(deskBRLegShape);

      deskTop.addChild(deskFLLeg);
        deskFLLeg.addChild(deskFLLegTransform);
          deskFLLegTransform.addChild(deskFLLegShape);

      deskTop.addChild(deskFRLeg);
        deskFRLeg.addChild(deskFRLegTransform);
          deskFRLegTransform.addChild(deskFRLegShape);

      deskRoot.update();
    //deskRoot.print(0, false);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    roomRoot.draw(gl);
    deskRoot.draw(gl);
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