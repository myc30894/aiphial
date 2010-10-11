package cv;
/**
 * <i>native declaration : cv\include\cvtypes.h:44</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvMoments extends com.ochafik.lang.jnaerator.runtime.Structure {
	/// spatial moments
	public double m00;
	/// spatial moments
	public double m10;
	/// spatial moments
	public double m01;
	/// spatial moments
	public double m20;
	/// spatial moments
	public double m11;
	/// spatial moments
	public double m02;
	/// spatial moments
	public double m30;
	/// spatial moments
	public double m21;
	/// spatial moments
	public double m12;
	/// spatial moments
	public double m03;
	/// central moments
	public double mu20;
	/// central moments
	public double mu11;
	/// central moments
	public double mu02;
	/// central moments
	public double mu30;
	/// central moments
	public double mu21;
	/// central moments
	public double mu12;
	/// central moments
	public double mu03;
	/// m00 != 0 ? 1/sqrt(m00) : 0
	public double inv_sqrt_m00;
	public CvMoments() {
		super();
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public CvMoments clone() { return setupClone(new CvMoments()); }
	public static class ByReference extends CvMoments implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends CvMoments implements com.sun.jna.Structure.ByValue {}
}
