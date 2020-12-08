import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import java.util.ArrayList;
import java.util.Collections;

/* Much of this code is copied and
adapted from Light.java in Ch7 of the exercise code by Dr Steve Maddock */

/* I declare that this code is my own work except for where stated */
/* Author <Jack Maskell> <jmaskell2@sheffield> */

public class SpotLight {
    
    private Material material;
    private Vec3 position;
    private Vec3 direction;
    float constant;
    float linear;
    float quadratic;
    float cutOff;
    float outerCutOff;
    private Mat4 model;
    private Shader shader;
    private Camera camera;
    Vec3 rgb;
    

    public SpotLight(GL3 gl) {
    material = new Material();
    material.setAmbient(0.0f, 0.0f, 0.0f);
    position = new Vec3(3f,2f,1f);
    direction = new Vec3(-5,0,0);
    model = new Mat4(1);
    shader = new Shader(gl, "vs_light_01.txt", "fs_light_01.txt");
    fillBuffers(gl);
    }

    public void setDirection(Vec3 v){
        direction.x = v.x;
        direction.y = v.y;
        direction.z = v.z;
    }

    public void setDirection(float x, float y, float z){
        direction.x = x;
        direction.y = y;
        direction.z = z;
    }

    public Vec3 getDirection(){
        return direction;
    }

    public void setConstant(float f){
        constant = f;
    }

    public void setLinear(float f){
        linear = f;
    }

    public void setQuadratic(float f){
        linear = f;
    }

    public void setCutOff(float f){
        cutOff = f;
    }

    public void setOuterCutOff(float f){
        outerCutOff = f;
    }

    public void setPosition(Vec3 v) {
        position.x = v.x;
        position.y = v.y;
        position.z = v.z;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public Vec3 getPosition() {
        return position;
    }



    public void setMaterial(Material m) {
        material = m;
    }

    public Material getMaterial() {
        return material;
    }

    public void setColor(Vec3 rgb){
        material.setAmbient(0f, 0f, 0f);
        material.setDiffuse(rgb);
        material.setSpecular(rgb);
        this.rgb = new Vec3(rgb);
    }

    public Vec3 getColor(){
        return this.rgb;

    }

    public void hideModel(){
        model = new Mat4(0);
    }

    public void unhideModel(){
        model = new Mat4(1);
    }

    public void setIntensity(float i){
        material.setAmbient(Vec3.multiply(material.getAmbient(), i));
        material.setDiffuse(Vec3.multiply(material.getDiffuse(), i));
        material.setSpecular(Vec3.multiply(material.getSpecular(), i));
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void render(GL3 gl) {
        Mat4 model = this.model;
        model = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), model);
        model = Mat4.multiply(Mat4Transform.translate(position), model);

        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), model));

        shader.use(gl);
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }

        public void dispose(GL3 gl) {
            gl.glDeleteBuffers(1, vertexBufferId, 0);
            gl.glDeleteVertexArrays(1, vertexArrayId, 0);
            gl.glDeleteBuffers(1, elementBufferId, 0);
    }

    // ***************************************************
    /* THE DATA
    */
    // anticlockwise/counterclockwise ordering

    
    private float[] vertices = new float[] {  // x,y,z
        -0.2f, -0.2f, -0.2f,  // 0
        -0.2f, -0.2f,  0.2f,  // 1
        -0.2f,  0.2f, -0.2f,  // 2
        -0.2f,  0.2f,  0.2f,  // 3
         0.2f, -0.2f, -0.2f,  // 4
         0.2f, -0.2f,  0.2f,  // 5
         0.2f,  0.2f, -0.2f,  // 6
         0.2f,  0.2f,  0.2f   // 7
       };
      
      private int[] indices =  new int[] {
        0,1,3, // x -ve 
        3,2,0, // x -ve
        4,6,7, // x +ve
        7,5,4, // x +ve
        1,5,7, // z +ve
        7,3,1, // z +ve
        6,4,0, // z -ve
        0,2,6, // z -ve
        0,4,5, // y -ve
        5,1,0, // y -ve
        2,3,7, // y +ve
        7,6,2  // y +ve
      };

    private int vertexStride = 3;
    private int vertexXYZFloats = 3;

    // ***************************************************
    /* THE LIGHT BUFFERS
    */

 
    private int[] vertexBufferId = new int[1];
    private int[] vertexArrayId = new int[1];
    private int[] elementBufferId = new int[1];
      
    private void fillBuffers(GL3 gl) {
      gl.glGenVertexArrays(1, vertexArrayId, 0);
      gl.glBindVertexArray(vertexArrayId[0]);
      gl.glGenBuffers(1, vertexBufferId, 0);
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
      FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
      
      gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
      
      int stride = vertexStride;
      int numXYZFloats = vertexXYZFloats;
      int offset = 0;
      gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
      gl.glEnableVertexAttribArray(0);
       
      gl.glGenBuffers(1, elementBufferId, 0);
      IntBuffer ib = Buffers.newDirectIntBuffer(indices);
      gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
      gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
      gl.glBindVertexArray(0);
    } 
  
  }