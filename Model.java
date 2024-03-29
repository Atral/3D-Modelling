import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

/* This code is adapted from Light.java in Ch7 of the exercise code by 
Dr Steve Maddock I have added support for AO texture maps and multiple lights*/

/* I declare that this code is my own work except for where stated */
/* Author <Jack Maskell> <jmaskell2@sheffield> */

public class Model {
  
  private Mesh mesh;
  private int[] textureId1; 
  private int[] textureId2; 
  private int[] textureId3; 
  private int[] textureId4; 
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light light;
  private Light light2;
  private SpotLight spotLight;
  
  public Model(GL3 gl, Camera camera, Light light, Light light2, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, int[] texture3, int[] texture4) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.light = light;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
    this.textureId3 = textureId3;
    this.textureId4 = textureId4;
    this.light2 = light2;
    this.spotLight = spotLight;
  }

  public Model(GL3 gl, Camera camera, Light light, Light light2, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, int[] textureId3) {
    this(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId1, textureId2, textureId3, null);
  }

  public Model(GL3 gl, Camera camera, Light light, Light light2, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId1, textureId2, null, null);
  }
  public Model(GL3 gl, Camera camera, Light light, Light light2, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId1, null, null, null);
  }
  
  public Model(GL3 gl, Camera camera, Light light, Light light2, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, null, null, null, null);
  }
  
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public void setLight(Light light) {
    this.light = light;
  }

  public void setLight2(Light light2){
    this.light2 = light2;
  }

  public void setSpotLight(SpotLight spotLight){
    this.spotLight = spotLight;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.position", light.getPosition());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());

    shader.setVec3(gl, "light2.position", light2.getPosition());
    shader.setVec3(gl, "light2.ambient", light2.getMaterial().getAmbient());
    shader.setVec3(gl, "light2.diffuse", light2.getMaterial().getDiffuse());
    shader.setVec3(gl, "light2.specular", light2.getMaterial().getSpecular());

    shader.setVec3(gl, "spotLight.position", spotLight.getPosition());
    shader.setVec3(gl, "spotLight.ambient", spotLight.getMaterial().getAmbient());
    shader.setVec3(gl, "spotLight.diffuse", spotLight.getMaterial().getDiffuse());
    shader.setVec3(gl, "spotLight.specular", spotLight.getMaterial().getSpecular());
    shader.setVec3(gl, "spotLight.direction", spotLight.getDirection());
    shader.setFloat(gl, "spotLight.constant", 1.0f);
    shader.setFloat(gl, "spotLight.linear", 0.09f);
    shader.setFloat(gl, "spotLight.quadratic", 0.032f);
    shader.setFloat(gl, "spotLight.cutOff", (float)Math.cos(Math.toRadians(12.5f)));
    shader.setFloat(gl, "spotLight.outerCutOff", (float)Math.cos(Math.toRadians(15.0f)));   

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());  

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    if (textureId3!=null) {
      shader.setInt(gl, "third_texture", 2);
      gl.glActiveTexture(GL.GL_TEXTURE2);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId3[0]);
    }
    if (textureId4!=null) {
      shader.setInt(gl, "fourth_texture", 3);
      gl.glActiveTexture(GL.GL_TEXTURE3);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId4[0]);
    }
    mesh.render(gl);
  } 
  
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
    if (textureId3!=null) gl.glDeleteBuffers(1, textureId3, 0);
    if (textureId4!=null) gl.glDeleteBuffers(1, textureId4, 0);
  }
  
}