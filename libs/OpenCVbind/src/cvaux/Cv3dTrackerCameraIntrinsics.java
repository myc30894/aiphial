package cvaux;
/**
 * <i>native declaration : cvaux\include\cvaux.h:885</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class Cv3dTrackerCameraIntrinsics extends com.ochafik.lang.jnaerator.runtime.Structure {
	public cxcore.CvPoint2D32f principal_point;
	public float[] focal_length = new float[(2)];
	public float[] distortion = new float[(4)];
	public Cv3dTrackerCameraIntrinsics() {
		super();
	}
	public Cv3dTrackerCameraIntrinsics(cxcore.CvPoint2D32f principal_point, float focal_length[], float distortion[]) {
		super();
		this.principal_point = principal_point;
		if (focal_length.length != this.focal_length.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.focal_length = focal_length;
		if (distortion.length != this.distortion.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.distortion = distortion;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public Cv3dTrackerCameraIntrinsics clone() { return setupClone(new Cv3dTrackerCameraIntrinsics()); }
	public static class ByReference extends Cv3dTrackerCameraIntrinsics implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends Cv3dTrackerCameraIntrinsics implements com.sun.jna.Structure.ByValue {}
}
