package world.util;

import java.nio.ByteBuffer;

public class Quat {
  public static final Quat IDENTITY = new Quat();
  public float x, y, z, w;

  public Quat() {
    x = y = z = 0;
    w = 1;
  }

  public Quat(float x, float y, float z, float w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public Quat(Quat a) {
    if (a != null) {
      x = a.x;
      y = a.y;
      z = a.z;
      w = a.w;
    }
  }

  public Quat(double x, double y, double z, double w) {
    this((float) x, (float) y, (float) z, (float) w);
  }

  public Quat conjugate(Quat out) {
    out.x = -x;
    out.y = -y;
    out.z = -z;
    out.w = w;
    return out;
  }

  public float magnitudeSquared() {
    return x*x + y*y + z*z + w*w;
  }

  public Quat calculateW() {
    w = -Util.sqrt(Math.abs(1.0 - x * x - y * y - z * z));
    return this;
  }

  public void writeToBuffer(ByteBuffer buffer) {
    buffer.putFloat(x);
    buffer.putFloat(y);
    buffer.putFloat(z);
    buffer.putFloat(w);
  }

  public Quat rotateY (float rad) {
    return rotateY(this, rad);
  }


  public Quat rotateY(Quat in, float rad) {
    rad *= 0.5;

    float inx = in.x, iny = in.y, inz = in.z, inw = in.w;

    float sinRad = (float) Math.sin(rad);
    float cosRad = (float) Math.cos(rad);

    x = inx * cosRad - inz * sinRad;
    y = iny * cosRad + inw * sinRad;
    z = inz * cosRad + inx * sinRad;
    w = inw * cosRad - iny * sinRad;
    return this;
  }


  public Quat rotateX (float rad) {
    return rotateX(this, rad);
  }


  public Quat rotateX(Quat in, float rad) {
    rad *= 0.5;

    float inx = in.x, iny = in.y, inz = in.z, inw = in.w;

    float sinRad = (float) Math.sin(rad);
    float cosRad = (float) Math.cos(rad);

    x = inx * cosRad + inw * sinRad;
    y = iny * cosRad + inz * sinRad;
    z = inz * cosRad - iny * sinRad;
    w = inw * cosRad - inx * sinRad;
    return this;
  }


  public Quat multiply(Quat b) {
    float ax = this.x, ay = this.y, az = this.z, aw = this.w;
    float bx = b.x, by = b.y, bz = b.z, bw = b.w;

    x = ax * bw + aw * bx + ay * bz - az * by;
    y = ay * bw + aw * by + az * bx - ax * bz;
    z = az * bw + aw * bz + ax * by - ay * bx;
    w = aw * bw - ax * bx - ay * by - az * bz;
    return this;
  }

  public static Quat randomColor() {
    return Quat.randomColor(0);
  }

  public static Quat randomColor(float minMagSqr) {
    float r = Util.random();
    float g = Util.random();
    float b = Util.random();
    if (r*r + g*g + b*b > minMagSqr) {
      return new Quat(r, g, b, 1);
    }
    return Quat.randomColor(minMagSqr);
  }

  public Quat setAxisAngle(Vec3 axis, float rad) {
    rad = rad * 0.5f;
    float s = Util.sin(rad);
    x = s * axis.x;
    y = s * axis.y;
    z = s * axis.z;
    w = Util.cos(rad);
    return this;
  }

  @Override
  public String toString() {
    return "( " + x + " , " + y + " , " + z + " , " + w + " )";
  }
}
