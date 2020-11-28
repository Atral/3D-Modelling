private final class Helicopter {

    private Model  sphere, wing, axel;

    heliRoot = new NameNode("helicopter root");
    m = Mat4Transform.translate(3, 0.2f, -6);
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

    return model();
}
