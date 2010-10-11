package cxcore;
/**
 * <i>native declaration : cxcore\include\cxtypes.h:1711</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvPluginFuncInfo extends com.ochafik.lang.jnaerator.runtime.Structure {
	public com.sun.jna.ptr.PointerByReference func_addr;
	public com.sun.jna.Pointer default_func_addr;
	public com.sun.jna.ptr.ByteByReference func_names;
	public int search_modules;
	public int loaded_from;
	public CvPluginFuncInfo() {
		super();
	}
	public CvPluginFuncInfo(com.sun.jna.ptr.PointerByReference func_addr, com.sun.jna.Pointer default_func_addr, com.sun.jna.ptr.ByteByReference func_names, int search_modules, int loaded_from) {
		super();
		this.func_addr = func_addr;
		this.default_func_addr = default_func_addr;
		this.func_names = func_names;
		this.search_modules = search_modules;
		this.loaded_from = loaded_from;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public CvPluginFuncInfo clone() { return setupClone(new CvPluginFuncInfo()); }
	public static class ByReference extends CvPluginFuncInfo implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends CvPluginFuncInfo implements com.sun.jna.Structure.ByValue {}
}
