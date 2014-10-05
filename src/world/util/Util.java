package world.util;

import javax.swing.JOptionPane;

public class Util {
	
	public final static float T = .003f;
	public final static float G = T * -9.8f;
	public final static float TO_RAD = 3.1415f / 180;
	public final static float TO_DEG = 180 / 3.1415f;
	public final static float MY_TIME = 22;
	public final static float SQRT2 = 1.414f;
	public final static float PI = 3.1415f;
  public static final float ROOT_2 = (float) Math.sqrt(2);
	
	public static float sin(double phi) {
		return (float)Math.sin(phi);
	}
	
	public static float cos(double phi) {
		return (float)Math.cos(phi);
	}
	
	public static float tan(double phi) {
		return (float)Math.tan(phi);
	}
	
	public static float atan2(double a, double b) {
		return (float)Math.atan2(a, b);
	}
	
	public static float sqrt(double a) {
		return (float)Math.sqrt(a);
	}
	
	public static void error( String err , boolean quit ){
		JOptionPane.showMessageDialog( null , err , "Error!" , JOptionPane.ERROR_MESSAGE );
		if( quit ){
			System.exit( 1 );
		}
	}

  public static float random() {
    return (float)(Math.random());
  }
  
  public static float random(float min, float max) {
    return min + Util.random()*(max - min); 
  }
}
