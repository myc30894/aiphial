package cvaux;
/**
 * <i>native declaration : cvaux\include\cvaux.h:863</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class Cv3dTrackerTrackedObject extends com.ochafik.lang.jnaerator.runtime.Structure {
	public int id;
	/// location of the tracked object
	public cxcore.CvPoint3D32f p;
	public Cv3dTrackerTrackedObject() {
		super();
	}
	/// @param p location of the tracked object
	public Cv3dTrackerTrackedObject(int id, cxcore.CvPoint3D32f p) {
		super();
		this.id = id;
		this.p = p;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public Cv3dTrackerTrackedObject clone() { return setupClone(new Cv3dTrackerTrackedObject()); }
	public static class ByReference extends Cv3dTrackerTrackedObject implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends Cv3dTrackerTrackedObject implements com.sun.jna.Structure.ByValue {}
}
