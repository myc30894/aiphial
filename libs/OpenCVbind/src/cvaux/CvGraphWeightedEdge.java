package cvaux;
/**
 * <i>native declaration : cvaux\include\cvaux.h:285</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvGraphWeightedEdge extends com.ochafik.lang.jnaerator.runtime.Structure {
	public int flags;
	public float weight;
	public cxcore.CvGraphEdge.ByReference[] next = new cxcore.CvGraphEdge.ByReference[(2)];
	public cxcore.CvGraphVtx.ByReference[] vtx = new cxcore.CvGraphVtx.ByReference[(2)];
	public CvGraphWeightedEdge() {
		super();
	}
	public CvGraphWeightedEdge(int flags, float weight, cxcore.CvGraphEdge.ByReference next[], cxcore.CvGraphVtx.ByReference vtx[]) {
		super();
		this.flags = flags;
		this.weight = weight;
		if (next.length != this.next.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.next = next;
		if (vtx.length != this.vtx.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.vtx = vtx;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public CvGraphWeightedEdge clone() { return setupClone(new CvGraphWeightedEdge()); }
	public static class ByReference extends CvGraphWeightedEdge implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends CvGraphWeightedEdge implements com.sun.jna.Structure.ByValue {}
}
