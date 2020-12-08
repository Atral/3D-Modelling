import gmaths.*;

import java.nio.*;

import javax.xml.crypto.dsig.Transform;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

/* Adapted from Ch7 of the exercise code by Dr Steve Maddock
which initially contained 2 shaded and textured cubes in a heirarchy*/

/* I declare that this code is my own work except for where stated */
/* Author <Jack Maskell> <jmaskell2@sheffield> */

public class Anilamp_GLEventListener implements GLEventListener {
  
  private static float INTENSITY1 = 0.5f;
  private static float INTENSITY2 = 0.5f;
  private static final boolean DISPLAY_SHADERS = false;
  
    
  public Anilamp_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,8f,15f));
    this.camera.setTarget(new Vec3(-2f,5f,0f));
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

  /* Called to turn off the rotating light in the scene */
  public static void light1Off(){
    light.setIntensity(0);
    light.hideModel();
  }

  /* Called to turn on the rotating light in the scene */
  public static void light1On() {
    light.setColor(light.getColor());
    light.setIntensity(INTENSITY1);
    light.unhideModel();
  }

  /* Called to turn off the stationary light in the scene */
  public static void light2Off() {
    light2.setIntensity(0);
    light2.hideModel();
  }

  /* Called to turn on the stationary light in the scene */
  public static void light2On() {
    light2.setColor(light2.getColor());
    light2.setIntensity(INTENSITY2);
    light2.unhideModel();
  }

  /* Boolean called by button to start spinning of the helicopter */
  public static void spinning(boolean s){
    spinning = s;
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
    light2.dispose(gl);
    spotlight.dispose(gl);
    floor.dispose(gl);
    cube.dispose(gl);
  }

  private Camera camera;
  private Mat4 perspective;
  private Model floor, cube, paper, sphere, desk, notice, wing, axel, lampSquare, lampSphere, lampManeTex, background;
  private static Light light;
  private static Light light2;
  private SpotLight spotlight;
  private SGNode roomRoot, deskRoot, paperRoot, heliRoot;
  public static float rotation = 0;
  public static boolean spinning = false;
  private double pauseStart = 0;
  private double pauseEnd = 0;

  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/Painted_Ornament_DIFF.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    int[] textureID4 = TextureLibrary.loadTexture(gl, "textures/Wood_021_basecolor.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/scribbles.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/noticeboard_color.jpg");
    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/Brick_Wall_018_basecolor.jpg");
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/Brick_Wall_018_specular.jpg");
    int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/Brick_Wall_018_ambientOcclusion.jpg");
    int[] textureId12 = TextureLibrary.loadTexture(gl, "textures/Wood_021_basecolor.jpg");
    int[] textureId13 = TextureLibrary.loadTexture(gl, "textures/Wood_021_height.png");
    int[] textureId14 = TextureLibrary.loadTexture(gl, "textures/Wood_ambientOcclusion.jpg");
    int[] floorSpec = TextureLibrary.loadTexture(gl, "textures/Painted_Ornament_SPEC.jpg");
    int[] fur = TextureLibrary.loadTexture(gl, "textures/fur.jpg");
    int[] furSpec = TextureLibrary.loadTexture(gl, "textures/furSpec.jpg");
    int[] mane = TextureLibrary.loadTexture(gl, "textures/mane.jpg");
    int[] window = TextureLibrary.loadTexture(gl, "textures/window.jpg");

    /* Initialising scene lights */
    light = new Light(gl);
    light.setCamera(camera);
    light.setColor(new Vec3(0.8f, 0f, 0));
    light.setIntensity(INTENSITY1);

    light2 = new Light(gl);
    light2.setCamera(camera);
    light2.setPosition(2, 3.2f, -7);
    light2.setColor(new Vec3(0f, 0.5f, 0f));
    light2.setIntensity(INTENSITY2);

    spotlight = new SpotLight(gl);
    spotlight.setCamera(camera);
    spotlight.setPosition(-1.75f,4.8f ,-6);
    spotlight.setDirection(4, -4, 0);
    spotlight.setColor(new Vec3(1f,0f,1f));
    spotlight.setIntensity(INTENSITY1);
    spotlight.setConstant(0.5f);
    spotlight.setLinear(0.09f);
    spotlight.setQuadratic(0.032f);
    spotlight.setCutOff((float)Math.cos(Math.toRadians(12.5f)));
    spotlight.setOuterCutOff((float)Math.cos(Math.toRadians(15.0f)));


    /* Creating Models for later use */
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f),
        new Vec3(0.3f, 0.3f, 0.3f), 10.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16, 1f, 16);
    floor = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, textureId0, floorSpec);

    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f),
        32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0, 0, 0), Mat4Transform.translate(0, 0.5f, 0));
    cube = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, textureId1, textureId1);
    desk = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, textureID4);
    lampSquare = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, fur, furSpec);

    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 2.0f);
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    notice = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, textureId6, null);
    background = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, window);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f),
        20.0f);
    paper = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, textureId5);

    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f),
    32.0f);
    sphere = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, textureId7, textureId8,
        textureId9);

    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    lampSphere = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, fur, furSpec);
    lampManeTex = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, mane, furSpec);
    axel = new Model(gl, camera, light, light2, spotlight, shader,  material, modelMatrix, mesh, textureId12, textureId13,
        textureId14);

    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 1.0f);
    wing = new Model(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, textureId12, textureId13,
        textureId14);

    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");

    /* -------- Structure of the floor, walls and background ------------*/
    roomRoot = new NameNode("room structure");

    NameNode floorNode = new NameNode("floor");
    Mat4 m = Mat4Transform.scale(16, 1f, 16);
    TransformNode floorTransform = new TransformNode("", m);
    ModelNode floorShape = new ModelNode("Floor(0)", floor);

    NameNode leftWallB = new NameNode("left wall bottom");
    m = Mat4Transform.scale(0.5f, 4, 16);
    m = Mat4.multiply(m, Mat4Transform.translate(-16, 0.5f, 0));
    TransformNode leftWallBTransform = new TransformNode("", m);
    ModelNode leftWallBShape = new ModelNode("Cube(0)", cube);

    NameNode leftWallL = new NameNode("left wall left");
    m = Mat4Transform.scale(0.5f, 11, 4);
    m = Mat4.multiply(m, Mat4Transform.translate(-16, 0.39f, 6/4f));
    TransformNode leftWallLTransform = new TransformNode(")", m);
    ModelNode leftWallLShape = new ModelNode("Cube(1)", cube);

    NameNode leftWallR = new NameNode("left wall right");
    m = Mat4Transform.scale(0.5f, 11, 4);
    m = Mat4.multiply(m, Mat4Transform.translate(-16, 0.39f, -6/4f + 0.2f/4));
    TransformNode leftWallRTransform = new TransformNode("", m);
    ModelNode leftWallRShape = new ModelNode("Cube(2)", cube);

    NameNode leftWallT = new NameNode("left wall top");
    m = Mat4Transform.scale(0.5f, 6.4f, 16);
    m = Mat4.multiply(m, Mat4Transform.translate(-16, 16/8, 0));
    TransformNode leftWallTTransform = new TransformNode("", m);
    ModelNode leftWallTShape = new ModelNode("Cube(3)", cube);

    NameNode backWall = new NameNode("back wall");
    m = Mat4Transform.scale(16, 16, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, -16));
    TransformNode backWallTransform = new TransformNode("", m);
    ModelNode backWallShape = new ModelNode("Cube(4)", cube);

    NameNode noticeBoard = new NameNode("Notice Board");
    m = Mat4Transform.translate(0, -0.1f, 0.8f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.5f, 0.3f, 0.1f));
    TransformNode noticeBoardTransform = new TransformNode("", m);
    ModelNode noticeBoardShape = new ModelNode("Notice(1)", notice);

    NameNode bg = new NameNode("city background");
    m = Mat4Transform.translate(-125, 15, -35);
    m = Mat4.multiply(m, Mat4Transform.scale(150, 150, 200));
    TransformNode bgTransform = new TransformNode("", m);
    ModelNode bgShape = new ModelNode("background(1)", background);

    roomRoot.addChild(floorNode);
    floorNode.addChild(floorTransform);
    floorTransform.addChild(floorShape);

    roomRoot.addChild(bg);
    bg.addChild(bgTransform);
    bgTransform.addChild(bgShape);

    floorNode.addChild(leftWallB);
    leftWallB.addChild(leftWallBTransform);
    leftWallBTransform.addChild(leftWallBShape);

    leftWallB.addChild(leftWallL);
      leftWallL.addChild(leftWallLTransform);
        leftWallLTransform.addChild(leftWallLShape);

    leftWallB.addChild(leftWallR);
    leftWallR.addChild(leftWallRTransform);
      leftWallRTransform.addChild(leftWallRShape);

    leftWallB.addChild(leftWallT);
    leftWallT.addChild(leftWallTTransform);
      leftWallTTransform.addChild(leftWallTShape);

    floorNode.addChild(backWall);
    backWall.addChild(backWallTransform);
    backWallTransform.addChild(backWallShape);

    backWallTransform.addChild(noticeBoard);
    noticeBoard.addChild(noticeBoardTransform);
    noticeBoardTransform.addChild(noticeBoardShape);

    roomRoot.update();

   /* -------- Structure of the desk and the simple objects on top of it ------------*/
    float legLength = 3;
    float legSide = 0.4f;
    float surfaceX = 9;
    float surfaceY = 0.2f;
    float surfaceZ = 4;

    deskRoot = new NameNode("desk structure");
    m = Mat4Transform.translate(0, 0, -16 / 2 + 0.25f + surfaceZ / 2);
    TransformNode deskTransform = new TransformNode("", m);

    NameNode deskTop = new NameNode("Desk top surface");
    m = Mat4Transform.translate(0, legLength, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(surfaceX, surfaceY, surfaceZ));
    TransformNode deskTopTransform = new TransformNode("", m);
    ModelNode deskTopShape = new ModelNode("Desk(0)", desk);

    NameNode deskBLLeg = new NameNode("Desk back left leg");
    m = Mat4Transform.translate(-surfaceX / 2 + legSide / 2, legLength / 2, -surfaceZ / 2 + legSide / 2);
    m = Mat4.multiply(m, Mat4Transform.scale(legSide, legLength, legSide));
    TransformNode deskBLLegTransform = new TransformNode("", m);
    ModelNode deskBLLegShape = new ModelNode("Desk(1)", desk);

    NameNode deskBRLeg = new NameNode("Desk back right leg");
    m = Mat4Transform.translate(surfaceX / 2 - legSide / 2, legLength / 2, -surfaceZ / 2 + legSide / 2);
    m = Mat4.multiply(m, Mat4Transform.scale(legSide, legLength, legSide));
    TransformNode deskBRLegTransform = new TransformNode("", m);
    ModelNode deskBRLegShape = new ModelNode("Desk(2)", desk);

    NameNode deskFLLeg = new NameNode("Desk front left leg");
    m = Mat4Transform.translate(-surfaceX / 2 + legSide / 2, legLength / 2, surfaceZ / 2 - legSide / 2);
    m = Mat4.multiply(m, Mat4Transform.scale(legSide, legLength, legSide));
    TransformNode deskFLLegTransform = new TransformNode("", m);
    ModelNode deskFLLegShape = new ModelNode("Desk(3)", desk);

    NameNode deskFRLeg = new NameNode("Desk front right leg");
    m = Mat4Transform.translate(surfaceX / 2 - legSide / 2, legLength / 2, surfaceZ / 2 - legSide / 2);
    m = Mat4.multiply(m, Mat4Transform.scale(legSide, legLength, legSide));
    TransformNode deskFRLegTransform = new TransformNode("", m);
    ModelNode deskFRLegShape = new ModelNode("Desk(4)", desk);

    NameNode deskPaper = new NameNode("Desk paper");
    m = Mat4Transform.translate(0, legLength + surfaceY / 2 + 0.05f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.825f, 0.05f, 1.175f));
    TransformNode deskPaperTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)",
        m);
    ModelNode deskPaperShape = new ModelNode("Paper(0)", paper);

    NameNode deskPen = new NameNode("Desk pen");
    m = Mat4Transform.translate(0.6f, legLength + surfaceY / 2 + 0.015f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.02f, 0.02f, 0.5f));
    TransformNode deskPenTransform = new TransformNode("scale(0.4f, 3, 0.4f); Mat4Transform.translate(-14, 0.5f, 7)",
        m);
    ModelNode deskPenShape = new ModelNode("Sphere(0)", sphere);

    deskRoot.addChild(deskTransform);

      deskTransform.addChild(deskTop);
        deskTop.addChild(deskTopTransform);
          deskTopTransform.addChild(deskTopShape);

        deskTop.addChild(deskPaper);
          deskPaper.addChild(deskPaperTransform);
            deskPaperTransform.addChild(deskPaperShape);

        deskTop.addChild(deskPen);
          deskPen.addChild(deskPenTransform);
            deskPenTransform.addChild(deskPenShape);

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
    // deskRoot.print(0, false);
 
  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition()); // changing light position each frame
    light.render(gl);
    light2.render(gl);
    spotlight.render(gl);
    roomRoot.draw(gl);
    deskRoot.draw(gl);

    /* -------- Structure of the helicopter ------------*/

    float legLength = 3;
    float surfaceY = 0.2f;
    float bodyD = 0.4f;
    float axelD = 0.2f;

    heliRoot = new NameNode("helicopter root");
    Mat4 m = Mat4Transform.translate(3, flyHeli(spinning), -6);
    TransformNode heliRootTransform = new TransformNode("",
        m);

    NameNode heliBody = new NameNode("");
    m = Mat4Transform.translate(0, legLength + surfaceY / 2 + 0.015f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.scale(bodyD, bodyD, bodyD));
    TransformNode heliBodyTransform = new TransformNode("", m);
    ModelNode heliBodyShape = new ModelNode("Sphere(0)", sphere);

    NameNode heliAxel = new NameNode("axel");
    m = Mat4Transform.translate(0, legLength + surfaceY / 2 + 0.015f + 0.25f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.scale(axelD, axelD, axelD));
    TransformNode heliAxelTransform = new TransformNode("", rotate(m, spinning)); //axel rotates as a function of time
    ModelNode heliAxelShape = new ModelNode("Axel(0)", axel);

    NameNode heliLWing = new NameNode("left wing");
    m = Mat4Transform.translate(-0.1f*5 - 0.25f*5, 0, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(0.5f*5, 0.02f*5, 0.04f*5));
    TransformNode heliLWingTransform = new TransformNode("", m);
    ModelNode heliLWingShape = new ModelNode("Wing(0)", wing);

    NameNode heliRWing = new NameNode("right wing");
   m = Mat4Transform.translate(0.1f*5 + 0.25f*5, 0, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(0.5f*5, 0.02f*5, 0.04f*5));
    TransformNode heliRWingTransform = new TransformNode("",m);
    ModelNode heliRWingShape = new ModelNode("Wing(1)", wing);

    heliRoot.addChild(heliRootTransform);
      heliRootTransform.addChild(heliBody);
      heliBody.addChild(heliBodyTransform);
        heliBodyTransform.addChild(heliBodyShape);

      heliBody.addChild(heliAxel);
        heliAxel.addChild(heliAxelTransform);
          heliAxelTransform.addChild(heliAxelShape);

          heliAxelTransform.addChild(heliLWing);
            heliLWing.addChild(heliLWingTransform);
              heliLWingTransform.addChild(heliLWingShape);

          heliAxelTransform.addChild(heliRWing);
            heliRWing.addChild(heliRWingTransform);
              heliRWingTransform.addChild(heliRWingShape);

    heliRoot.update();
    heliRoot.draw(gl);

    /* -------- Structure of the lamp ------------*/
    /* The lamp does not currently animate, but since
    it is intended for its position to be updated with each frame
    it has been placed here */

    final float BASE_XS = 0.6f;
    final float BASE_YS = 0.1f;
    final float BASE_ZS = 0.4f;
    
    NameNode lampRoot = new NameNode("lamp root");
    m = Mat4Transform.translate(-2f, legLength + surfaceY/2 + BASE_YS/2, -6);
    TransformNode lampRootTransform = new TransformNode("", m);

    NameNode lampBase = new NameNode("lamp body");
    m = Mat4Transform.translate(0, 0, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(0.6f, BASE_YS, 0.4f));
    TransformNode lampBaseTransform = new TransformNode("",m);
    ModelNode lampBaseShape = new ModelNode("Sphere(0)", lampSquare);

    float lowerArmXS = 0.1f;
    float lowerArmYT = 0.4f;
    float lowerArmYS = 0.8f;
    float lowerArmZS =  0.1f;

    NameNode lampLowerArm = new NameNode("lamp body");
    m = Mat4Transform.translate(0, 0.4f/BASE_YS + 0.5f, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(lowerArmXS/BASE_XS, lowerArmYS/BASE_YS, lowerArmZS/BASE_ZS));
    TransformNode lampLowerArmTransform = new TransformNode("",m);
    ModelNode lampLowerArmShape = new ModelNode("Sphere(0)", lampSphere);

    float jointS = 0.2f;
    NameNode lampJoint = new NameNode("lamp joint");
    m = Mat4Transform.translate(0, lowerArmYS/2 + BASE_YS, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(jointS/lowerArmXS, jointS/lowerArmYS, jointS/lowerArmZS));
    TransformNode lampJointTransform = new TransformNode("",m);
    ModelNode lampJointShape = new ModelNode("Sphere(0)", lampSphere);

    NameNode lampUpperArm = new NameNode("upper arm");
    m = Mat4Transform.translate(0, 1, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(1 ,1, 1));
    TransformNode lampUpperArmTransform = new TransformNode("", m);
    ModelNode lampUpperArmShape = new ModelNode("", lampSphere);

    NameNode lampHead = new NameNode("head");
    m = Mat4Transform.translate(0, 1.5f, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(4, lowerArmYS/3, 2));
    TransformNode lampHeadTransform = new TransformNode("", m);
    ModelNode lampHeadShape = new ModelNode("", lampSquare);

    NameNode lampEarL = new NameNode("upper arm");
    m = Mat4Transform.translate(-0.3f, lowerArmYS/3 + 0.4f, 0.28f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.05f, 1, 0.5f));
    TransformNode lampEarLTransform = new TransformNode("", m);
    ModelNode lampEarLShape = new ModelNode("", lampSphere);

    NameNode lampEarR = new NameNode("upper arm");
    m = Mat4Transform.translate(-0.3f, lowerArmYS/3 + 0.4f, -0.28f);
    m = Mat4.multiply(m, Mat4Transform.scale(0.05f, 1, 0.5f));
    TransformNode lampEarRTransform = new TransformNode("", m);
    ModelNode lampEarRShape = new ModelNode("", lampSphere);

    NameNode lampMane = new NameNode("upper arm");
    m = Mat4Transform.translate(-0.4f, 0, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(0.03f, 3, 3));
    TransformNode lampManeTransform = new TransformNode("", m);
    ModelNode lampManeShape = new ModelNode("", lampManeTex);

    lampRoot.addChild(lampRootTransform);

      lampRootTransform.addChild(lampBase);
        lampBase.addChild(lampBaseTransform);
          lampBaseTransform.addChild(lampBaseShape);
          lampBaseTransform.addChild(lampLowerArm);
            lampLowerArm.addChild(lampLowerArmTransform);
            lampLowerArmTransform.addChild(lampLowerArmShape);

              lampLowerArmTransform.addChild(lampJoint);
                lampJoint.addChild(lampJointTransform);
                  lampJointTransform.addChild(lampJointShape);

                  lampJoint.addChild(lampUpperArm);
                    lampUpperArm.addChild(lampUpperArmTransform);
                      lampUpperArmTransform.addChild(lampUpperArmShape);

                    lampUpperArm.addChild(lampHead);
                      lampHead.addChild(lampHeadTransform);
                        lampHeadTransform.addChild(lampHeadShape);

                      
                        lampHeadTransform.addChild(lampEarL);
                          lampEarL.addChild(lampEarLTransform);
                            lampEarLTransform.addChild(lampEarLShape);

                        lampHeadTransform.addChild(lampEarR);
                          lampEarR.addChild(lampEarRTransform);
                            lampEarRTransform.addChild(lampEarRShape);

                        lampHeadTransform.addChild(lampMane);
                          lampMane.addChild(lampManeTransform);
                            lampManeTransform.addChild(lampManeShape);
                

    lampRoot.update();
    lampRoot.draw(gl);
  }

  // The light's position is continually being changed, so needs to be calculated
  // for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds() - startTime;
    float x = 5.0f * (float) (Math.sin(Math.toRadians(elapsedTime * 50)));
    float y = 2.7f;
    float z = 5.0f * (float) (Math.cos(Math.toRadians(elapsedTime * 50)));
    return new Vec3(x, y, z);
    // return new Vec3(5f,3.4f,5f);
  }

  public Mat4 rotate(Mat4 m, boolean spinning) {
    if(spinning){
      pauseStart = getSeconds() - startTime;
      double elapsedTime = getSeconds() - startTime;
      rotation = (float) (500 * elapsedTime);
      return Mat4.multiply(m, Mat4Transform.rotateAroundY(rotation));
    }else{
      rotation = (float)(500 * pauseStart);
      return Mat4.multiply(m, Mat4Transform.rotateAroundY(rotation));
    }
  }

  public float flyHeli(boolean spinning){
    double elapsedTime = getSeconds() - startTime;
    float height = (float)(elapsedTime/20);
    if(spinning){
      return (0.2f + height);
    }else{
      double pauseTime = elapsedTime - pauseStart;
      float fallPos = 0.2f + (float)(pauseStart/20) - ((float)pauseTime*0.1f*9.8f*9.8f);
      if(fallPos > 0.2f){
        return fallPos;
      }else{
        return 0.2f;
      }
    }
  }
  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  private double spinStart;
  private double spinStop;
  
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