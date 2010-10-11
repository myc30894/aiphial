package cxcore;
/**
 * <i>native declaration : cxcore\include\cxcore.h:262</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvNArrayIterator extends com.ochafik.lang.jnaerator.runtime.Structure {
	/// number of arrays
	public int count;
	/// number of dimensions to iterate
	public int dims;
	/// maximal common linear size: { width = size, height = 1 }
	public cxcore.CvSize size;
	/// pointers to the array slices
	public com.sun.jna.ptr.ByteByReference[] ptr = new com.sun.jna.ptr.ByteByReference[(10)];
	/// for internal use
	public int[] stack = new int[(32)];
	/**
	 * pointers to the headers of the<br>
	 * matrices that are processed
	 */
	public cxcore.CvMatND.ByReference[] hdr = new cxcore.CvMatND.ByReference[(10)];
	public CvNArrayIterator() {
		super();
	}
	/**
	 * @param count number of arrays<br>
	 * @param dims number of dimensions to iterate<br>
	 * @param size maximal common linear size: { width = size, height = 1 }<br>
	 * @param ptr pointers to the array slices<br>
	 * @param stack for internal use<br>
	 * @param hdr pointers to the headers of the<br>
	 * matrices that are processed
	 */
	public CvNArrayIterator(int count, int dims, cxcore.CvSize size, com.sun.jna.ptr.ByteByReference ptr[], int stack[], cxcore.CvMatND.ByReference hdr[]) {
		super();
		this.count = count;
		this.dims = dims;
		this.size = size;
		if (ptr.length != this.ptr.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.ptr = ptr;
		if (stack.length != this.stack.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.stack = stack;
		if (hdr.length != this.hdr.length) 
			throw new java.lang.IllegalArgumentException("Wrong array size !");
		this.hdr = hdr;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public CvNArrayIterator clone() { return setupClone(new CvNArrayIterator()); }
	public static class ByReference extends CvNArrayIterator implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends CvNArrayIterator implements com.sun.jna.Structure.ByValue {}
}
