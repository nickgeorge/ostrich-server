package world.util;

import java.nio.ByteBuffer;

public class Vec3 {
  public float x, y, z;

  public static Vec3 I = new Vec3(1, 0, 0);
  public static Vec3 J = new Vec3(0, 1, 0);
  public static Vec3 K = new Vec3(0, 0, 1);

  public Vec3() {
  }

  public Vec3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vec3(Vec3 a) {
    if (a != null) {
      x = a.x;
      y = a.y;
      z = a.z;
    }
  }

  public Vec3(double x, double y, double z) {
    this((float) x, (float) y, (float) z);
  }

  public Vec3 negate() {
    x *= -1;
    y *= -1;
    z *= -1;
    return this;
  }

  public Vec3 negate(Vec3 out) {
    out.x = x * -1;
    out.y = y * -1;
    out.z = z * -1;
    return this;
  }

  public Vec3 add(Vec3 a) {
    return add(a, 1);
  }

  public Vec3 add(Vec3 a, float t) {
    return add(this, a, t);
  }

  public Vec3 add(Vec3 in, Vec3 a) {
    return add(in, a, 1);
  }

  public Vec3 add(Vec3 in, Vec3 a, float t) {
    x = in.x + a.x * t;
    y = in.y + a.y * t;
    z = in.z + a.z * t;
    return this;
  }

  public void normalize() {
    float norm;

    norm = (float) (1.0 / Math.sqrt(x * x + y * y + z * z));
    this.x = x * norm;
    this.y = y * norm;
    this.z = z * norm;
  }

  public void writeToBuffer(ByteBuffer buffer) {
    buffer.putFloat(x);
    buffer.putFloat(y);
    buffer.putFloat(z);
  }

  public Vec3 transformQuat(Quat q) {
    return transformQuat(this, q);
  }

  public Vec3 transformQuat(Vec3 in, Quat q) {
    float qx = q.x, qy = q.y, qz = q.z, qw = q.w;

    // calculate quat * vec
    float ix = qw * in.x + qy * in.z - qz * in.y;
    float iy = qw * in.y + qz * in.x - qx * in.z;
    float iz = qw * in.z + qx * in.y - qy * in.x;
    float iw = -qx * in.x - qy * in.y - qz * in.z;

    // calculate result * inverse quat
    x = ix * qw + iw * -qx + iy * -qz - iz * -qy;
    y = iy * qw + iw * -qy + iz * -qx - ix * -qz;
    z = iz * qw + iw * -qz + ix * -qy - iy * -qx;
    return this;
  }

  public Vec3 clampX(float min, float max) {
    x = Math.max(x, min);
    x = Math.min(x, max);
    return this;
  }

  public Vec3 clampY(float min, float max) {
    z = Math.max(y, min);
    z = Math.min(y, max);
    return this;
  }

  public Vec3 clampZ(float min, float max) {
    z = Math.max(z, min);
    z = Math.min(z, max);
    return this;
  }

  public float distanceSquaredXZ(Vec3 o) {
    float dx = o.x - x;
    float dz = o.z - z;
    return dx*dx + dz*dz;
  }

  @Override
  public String toString() {
    return "( " + x + " , " + y + " , " + z + " )";
  }

  public void set(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
