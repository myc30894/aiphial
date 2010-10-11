package cvaux;
/**
 * <i>native declaration : cvaux\include\cvaux.h:112</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CvEHMMState extends com.ochafik.lang.jnaerator.runtime.Structure {
	/// number of mixtures in this state
	public int num_mix;
	/// mean vectors corresponding to each mixture
	public com.sun.jna.ptr.FloatByReference mu;
	/// square root of inversed variances corresp. to each mixture
	public com.sun.jna.ptr.FloatByReference inv_var;
	/// sum of 0.5 (LN2PI + ln(variance[i]) ) for i=1,n
	public com.sun.jna.ptr.FloatByReference log_var_val;
	/// array of mixture weights. Summ of all weights in state is 1.
	public com.sun.jna.ptr.FloatByReference weight;
	public CvEHMMState() {
		super();
	}
	/**
	 * @param num_mix number of mixtures in this state<br>
	 * @param mu mean vectors corresponding to each mixture<br>
	 * @param inv_var square root of inversed variances corresp. to each mixture<br>
	 * @param log_var_val sum of 0.5 (LN2PI + ln(variance[i]) ) for i=1,n<br>
	 * @param weight array of mixture weights. Summ of all weights in state is 1.
	 */
	public CvEHMMState(int num_mix, com.sun.jna.ptr.FloatByReference mu, com.sun.jna.ptr.FloatByReference inv_var, com.sun.jna.ptr.FloatByReference log_var_val, com.sun.jna.ptr.FloatByReference weight) {
		super();
		this.num_mix = num_mix;
		this.mu = mu;
		this.inv_var = inv_var;
		this.log_var_val = log_var_val;
		this.weight = weight;
	}
	public ByReference byReference() { return setupClone(new ByReference()); }
	public ByValue byValue() { return setupClone(new ByValue()); }
	public CvEHMMState clone() { return setupClone(new CvEHMMState()); }
	public static class ByReference extends CvEHMMState implements com.sun.jna.Structure.ByReference {}
	public static class ByValue extends CvEHMMState implements com.sun.jna.Structure.ByValue {}
}
