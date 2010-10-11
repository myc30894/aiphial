package opencv;
import cv.*;
import cvaux.*;
import cxcore.*;
import highgui.*;
import ml.*;
/// JNA Wrappers instances
public class OpenCV {
	public static final highgui.HighguiLibrary highgui = (highgui.HighguiLibrary)com.sun.jna.Native.loadLibrary(com.ochafik.lang.jnaerator.runtime.LibraryExtractor.getLibraryPath("highgui", true, highgui.HighguiLibrary.class), highgui.HighguiLibrary.class, com.ochafik.lang.jnaerator.runtime.MangledFunctionMapper.DEFAULT_OPTIONS);
	public static final cxcore.CxcoreLibrary cxcore = (cxcore.CxcoreLibrary)com.sun.jna.Native.loadLibrary(com.ochafik.lang.jnaerator.runtime.LibraryExtractor.getLibraryPath("cxcore", true, cxcore.CxcoreLibrary.class), cxcore.CxcoreLibrary.class, com.ochafik.lang.jnaerator.runtime.MangledFunctionMapper.DEFAULT_OPTIONS);
	public static final cv.CvLibrary cv = (cv.CvLibrary)com.sun.jna.Native.loadLibrary(com.ochafik.lang.jnaerator.runtime.LibraryExtractor.getLibraryPath("cv", true, cv.CvLibrary.class), cv.CvLibrary.class, com.ochafik.lang.jnaerator.runtime.MangledFunctionMapper.DEFAULT_OPTIONS);
	public static final ml.MlLibrary ml = (ml.MlLibrary)com.sun.jna.Native.loadLibrary(com.ochafik.lang.jnaerator.runtime.LibraryExtractor.getLibraryPath("ml", true, ml.MlLibrary.class), ml.MlLibrary.class, com.ochafik.lang.jnaerator.runtime.MangledFunctionMapper.DEFAULT_OPTIONS);
	public static final cvaux.CvauxLibrary cvaux = (cvaux.CvauxLibrary)com.sun.jna.Native.loadLibrary(com.ochafik.lang.jnaerator.runtime.LibraryExtractor.getLibraryPath("cvaux", true, cvaux.CvauxLibrary.class), cvaux.CvauxLibrary.class, com.ochafik.lang.jnaerator.runtime.MangledFunctionMapper.DEFAULT_OPTIONS);
}
