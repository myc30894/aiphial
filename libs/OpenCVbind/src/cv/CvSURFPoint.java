package cv;
/**
 * <i>native declaration : cv\include\cv.h:1058</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvSURFPoint extends com.ochafik.lang.jnaerator.runtime.Structure {
	public cxcore.CvPoint2D32f pt;
	public int laplacian;
	public int size;
	public float dir;
	public float hessian;
	public CvSURFPoint() {
		super();
	}
	public CvSURFPoint(cxcore.CvPoint2D32f pt, int laplacian, int size, float dir, float hessian) {
		super();
		this.pt = pt;
		this.laplacian = laplacian;
		this.size = size;
		this.dir = dir;
		this.hessian = hessian;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public CvSURFPoint clone() { return setupClone(new CvSURFPoint()); }
	public static class ByReference extends CvSURFPoint implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends CvSURFPoint implements com.sun.jna.Structure.ByValue {}
}
