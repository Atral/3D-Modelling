 import gmaths.*;

 /**
 * This class stores the Material properties for a Mesh
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (15/10/2017)
 */

public class Material {

  public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);
  public static final Vec3 DEFAULT_DIFFUSE = new Vec3(0.8f, 0.8f, 0.8f);
  public static final Vec3 DEFAULT_SPECULAR = new Vec3(0.5f, 0.5f, 0.5f);
  public static final Vec3 DEFAULT_EMISSION = new Vec3(0.0f, 0.0f, 0.0f);
  public static final Vec3 DEFAULT_ALBEDO = new Vec3(1.0f, 1.0f, 1.0f);
  public static final Vec3 NORMAL = new Vec3(0.0f, 0.0f, 0.0f);
  public static final float DEFAULT_ROUGHNESS = 0;
  public static final float DEFAULT_METALLIC = 0;
  public static final float DEFAULT_AO = 2;
  public static final float DEFAULT_SHININESS = 32;
  
  private Vec3 ambient;
  private Vec3 diffuse;
  private Vec3 specular;
  private Vec3 emission;
  private float shininess;
  private Vec3 albedo;
  private Vec3 normal;
  private float metallic;
  private float roughness;
  private float ao;
  
  /**
   * Constructor. Sets attributes to default initial values.
   */    
  public Material() {
    ambient = new Vec3(DEFAULT_AMBIENT);
    diffuse = new Vec3(DEFAULT_DIFFUSE);
    specular = new Vec3(DEFAULT_SPECULAR);
    emission = new Vec3(DEFAULT_EMISSION);
    shininess = DEFAULT_SHININESS;
  }

   /**
   * Constructor. Sets the ambient, diffuse, specular, emission and shininess values
   * 
   * @param  ambient    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  diffuse    vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  specular   vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  emission   vector of 3 values: red, green and blue, in the range 0.0..1.0.
   * @param  shininess   float value in the range 0.0..1.0.
   */  
   
  public Material(Vec3 ambient, Vec3 diffuse, Vec3 specular, float albedo, Vec3 normal, float metallic, float roughness, float ao, float shininess) {
    this.ambient = ambient;
    this.diffuse = diffuse;
    this.specular = specular;
    this.albedo = albedo;
    this.normal = normal;
    this.metallic = metallic;
    this.roughness = roughness;
    this.ao = ao;
    emission = new Vec3(DEFAULT_EMISSION);
    this.shininess = shininess;
  }
  
  /**
   * Sets the ambient value (as used in Phong local reflection model)
   * 
   * @param  red    the red value in the range 0.0..1.0
   * @param  green  the green value in the range 0.0..1.0
   * @param  blue   the blue value in the range 0.0..1.0
   */    
  public void setAmbient(float red, float green, float blue) {
    ambient.x = red;
    ambient.y = green;
    ambient.z = blue;
  }  
  
  /**
   * Sets the ambient value (as used in Phong local reflection model)
   * 
   * @param  rgb  vector of 3 values, where the  3 values are the values for red, green and blue, 
                   in the range 0.0..1.0.
   */    
  public void setAmbient(Vec3 rgb) {
    setAmbient(rgb.x, rgb.y, rgb.z);
  }
  
  /**
   * Gets the ambient value (as a clone)
   * 
   * @return  vector of 3 values, where the  3 values are the values for red, green and blue.
   */  
  public Vec3 getAmbient() {
    return new Vec3(ambient);
  }

  /**
   * Sets the diffuse value (as used in Phong local reflection model)
   * 
   * @param  red    the red value in the range 0.0..1.0
   * @param  green  the green value in the range 0.0..1.0
   * @param  blue   the blue value in the range 0.0..1.0
  */  
  public void setDiffuse(float red, float green, float blue) {
    diffuse.x = red;
    diffuse.y = green;
    diffuse.z = blue;
  }
  
  /**
   * Sets the diffuse value (as used in Phong local reflection model)
   * 
   * @param  rgb  vector of 3 values, where the  3 values are the values for red, green and blue, 
                   in the range 0.0..1.0.
   */      
  public void setDiffuse(Vec3 rgb) {
    setDiffuse(rgb.x, rgb.y, rgb.z);
  }

  /**
   * Gets the diffuse value (clone) (as used in Phong local reflection model)
   * 
   * @return  vector of 3 values, where the  3 values are the values for red, green and blue
   */    
  public Vec3 getDiffuse() {
    return new Vec3(diffuse);
  }

  /**
   * Sets the specular value (as used in Phong local reflection model)
   * 
   * @param  red    the red value in the range 0.0..1.0
   * @param  green  the green value in the range 0.0..1.0
   * @param  blue   the blue value in the range 0.0..1.0
  */    
  public void setSpecular(float red, float green, float blue) {
    specular.x = red;
    specular.y = green;
    specular.z = blue;
  }

  /**
   * Sets the specular value (as used in Phong local reflection model)
   * 
   * @param  rgb  vector of 3 values, where the first 3 values are the values for red, green and blue, 
                   in the range 0.0..1.0, and the last value is an alpha term, which is always 1.
   */    
  public void setSpecular(Vec3 rgb) {
    setSpecular(rgb.x, rgb.y, rgb.z);
  }
    
  /**
   * Gets the specular value (clone) (as used in Phong local reflection model)
   * 
   * @return  vector of 3 values, where the  3 values are the values for red, green and blue.
   */  
  public Vec3 getSpecular() {
    return new Vec3(specular);
  }

  /**
   * Sets the emission value (as used in OpenGL lighting model)
   * 
   * @param  red    the red value in the range 0.0..1.0
   * @param  green  the green value in the range 0.0..1.0
   * @param  blue   the blue value in the range 0.0..1.0
   */    
  public void setEmission(float red, float green, float blue) {
    emission.x = red;
    emission.y = green;
    emission.z = blue;
  }
  
  /**
   * Sets the emission value (as used in OpenGL lighting model)
   * 
   * @param  rgb  vector of 3 values, where the 3 values are the values for red, green and blue, 
                   in the range 0.0..1.0.
   */    
  public void setEmission(Vec3 rgb) {
    setEmission(rgb.x, rgb.y, rgb.z);
  }

  /**
   * Gets the emission value (clone) (as used in OpenGL lighting model)
   * 
   * @return  vector of 3 values, where the  3 values are the values for red, green and blue.
   */ 
  public Vec3 getEmission() {
    return new Vec3(emission);
  }

  public void setAlbedo(float red, float green, float blue) {
    albedo.x = red;
    albedo.y = green;
    albedo.z = blue;
  }

  public void setAlbedo(Vec3 rgb){
    setAlbedo(rgb.x, rgb.y, rgb.z);
  }

  public void getAlbedo(){
    return new Vec3(albedo);
  }

  public void setMetallic(float metal){
    setMetallic(metal);
  }

  public void getMetallic(){
    return metallic;
  }

  public void setRoughness(float rough){
    setMetallic(rough);
  }

  public void getRoughness(){
    return roughness;
  }

  public void setAo(float ambOcc){
    setAo(ambOcc);
  }

  public void getAo(){
    return ao;
  }

  public void setNormal(float red, float green, float blue) {
    normal.x = red;
    normal.y = green;
    normal.z = blue;
  }

  public void setNormal(Vec3 rgb){
    setNormal(rgb.x, rgb.y, rgb.z);
  }

    
  /**
   * Sets the shininess value (as used in Phong local reflection model)
   * 
   * @param  shininess  the shininess value.
   */   
  public void setShininess(float shininess) {
    this.shininess = shininess;
  }
  
  /**
   * Gets the shininess value (as used in Phong local reflection model)
   * 
   * @return  the shininess value.
   */   
  public float getShininess() {
    return shininess;
  }

  public String toString() {
    return "a:"+ambient+", d:"+diffuse+", s:"+specular+", e:"+emission+", shininess:"+shininess;
  }  

}