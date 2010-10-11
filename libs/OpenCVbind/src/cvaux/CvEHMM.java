package cvaux;
/**
 * <i>native declaration : cvaux\include\cvaux.h:123</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvEHMM extends com.ochafik.lang.jnaerator.runtime.Structure {
	/// 0 - lowest(i.e its states are real states), .....
	public int level;
	/// number of HMM states
	public int num_states;
	/// transition probab. matrices for states
	public com.sun.jna.ptr.FloatByReference transP;
	/**
	 * if level == 0 - array of brob matrices corresponding to hmm<br>
	 * if level == 1 - martix of matrices
	 */
	public com.sun.jna.ptr.PointerByReference obsProb;
	public u_union u;
	/// <i>native declaration : cvaux\include\cvaux.h:130</i>
	public static class u_union extends com.ochafik.lang.jnaerator.runtime.Union {
		/**
		 * if level == 0 points to real states array,<br>
		 * if not - points to embedded hmms
		 */
		public cvaux.CvEHMMState.ByReference state;
		/// pointer to an embedded model or NULL, if it is a leaf
		public cvaux.CvEHMM.ByReference ehmm;
		public u_union() {
			super();
		}
		/// @param ehmm pointer to an embedded model or NULL, if it is a leaf
		public u_union(cvaux.CvEHMM.ByReference ehmm) {
			super();
			this.ehmm = ehmm;
			setType(cvaux.CvEHMM.ByReference.class);
		}
		/**
		 * @param state if level == 0 points to real states array,<br>
		 * if not - points to embedded hmms
		 */
		public u_union(cvaux.CvEHMMState.ByReference state) {
			super();
			this.state = state;
			setType(cvaux.CvEHMMState.ByReference.class);
		}
		public ByReference byReference() { return setupClone(new ByReference()); }
		public ByValue byValue() { return setupClone(new ByValue()); }
		public u_union clone() { return setupClone(new u_union()); }
		public static class ByReference extends u_union implements com.sun.jna.Structure.ByReference {}
		public static class ByValue extends u_union implements com.sun.jna.Structure.ByValue {}
	}
	public CvEHMM() {
		super();
	}
	/**
	 * @param level 0 - lowest(i.e its states are real states), .....<br>
	 * @param num_states number of HMM states<br>
	 * @param transP transition probab. matrices for states<br>
	 * @param obsProb if level == 0 - array of brob matrices corresponding to hmm<br>
	 * if level == 1 - martix of matrices
	 */
	public CvEHMM(int level, int num_states, com.sun.jna.ptr.FloatByReference transP, com.sun.jna.ptr.PointerByReference obsProb, u_union u) {
		super();
		this.level = level;
		this.num_states = num_states;
		this.transP = transP;
		this.obsProb = obsProb;
		this.u = u;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public CvEHMM clone() { return setupClone(new CvEHMM()); }
	public static class ByReference extends CvEHMM implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends CvEHMM implements com.sun.jna.Structure.ByValue {}
}
