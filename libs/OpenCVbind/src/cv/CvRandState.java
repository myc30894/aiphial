package cv;
/**
 * <i>native declaration : cv\include\cvcompat.h:166</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvRandState extends com.ochafik.lang.jnaerator.runtime.Structure {
	/// RNG state (the current seed and carry)
	public long state;
	/// distribution type
	public int disttype;
	/// parameters of RNG
	public cxcore.CvScalar[] param = new cxcore.CvScalar[(2)];
	public CvRandState() {
		super();
	}
	/**
	 * @param state RNG state (the current seed and carry)<br>
	 * @param disttype distribution type<br>
	 * @param param parameters of RNG
	 */
	public CvRandState(long state, int disttype, cxcore.CvScalar param[]) {
		super();
		this.state = state;
		this.disttype = disttype;
		if (param.length != this.param.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.param = param;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public CvRandState clone() { return setupClone(new CvRandState()); }
	public static class ByReference extends CvRandState implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends CvRandState implements com.sun.jna.Structure.ByValue {}
}
