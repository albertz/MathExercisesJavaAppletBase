/*
 *  MathExercisesJavaAppletBase
 * 
 * by Albert Zeyer / developed for Lehrstuhl A für Mathematik at the RWTH Aachen University
 * code under GPLv3+
 */
package applets.Termumformungen$in$der$Technik_08_Ethanolloesungen;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class OTNumber {
	private BigDecimal decimal = null;
	private BigInteger nominator = null, denominator = null;
	
	/** @throws NumberFormatException */
	OTNumber(String dec) {
		int p = dec.indexOf('/');
		if(p >= 0) {
			nominator = new BigInteger(dec.substring(0,p));
			denominator = new BigInteger(dec.substring(p+1));
		}
		else
			decimal = new BigDecimal(dec);
	}
	OTNumber(long num) { this(new BigDecimal(num)); }
	OTNumber(BigInteger dec) { this(new BigDecimal(dec)); }
	OTNumber(BigDecimal dec) {
		if(dec == null) throw new IllegalArgumentException("dec == null");
		decimal = dec;
	}
	OTNumber(long nom, long denom) { this(BigInteger.valueOf(nom), BigInteger.valueOf(denom)); }
	OTNumber(BigInteger nom, BigInteger denom) {
		if(nom == null) throw new IllegalArgumentException("nom == null");
		if(denom == null) throw new IllegalArgumentException("denom == null");
		nominator = nom;
		denominator = denom;
	}

	static OTNumber valueOf(long num) { return new OTNumber(num); }
	static final OTNumber ZERO = valueOf(0);
	static final OTNumber ONE = valueOf(1);
	
	OTNumber asNormatedFrac() {
		OTNumber frac = asFrac();
		BigInteger gcd = frac.nominator.gcd(frac.denominator);
		if(frac.denominator.compareTo(BigInteger.ZERO) <= 0) gcd = gcd.negate();
		return new OTNumber(frac.nominator.divide(gcd), frac.denominator.divide(gcd));
	}

	OTNumber asFrac() {
		if(decimal == null) return this; // we are already
		try { return new OTNumber(decimal.toBigIntegerExact(), BigInteger.ONE); }
		catch(ArithmeticException ignored) {}
		return new OTNumber(decimal.unscaledValue(), BigInteger.TEN.pow(decimal.scale()));
	}
	
	OTNumber normated() {
		if(decimal != null) return this;
		OTNumber frac = asNormatedFrac();
		if(frac.denominator.compareTo(BigInteger.ONE) == 0) return new OTNumber(frac.nominator);
		return frac;
	}
	
	/** @throws ArithmeticException */
	static BigInteger divideBigInt(BigInteger int1, BigInteger int2) {
		BigInteger[] div = int1.divideAndRemainder(int2);
		if(div[1].compareTo(BigInteger.ZERO) == 0) return div[0];
		throw new ArithmeticException("cannot divide " + int1 + " by " + int2);
	}
	
	/** @throws ArithmeticException */
	OTNumber asDecimal() {
		if(decimal != null) return this;
		return new OTNumber(divideBigInt(nominator, denominator));
	}
	/** @throws ArithmeticException */
	int intValueExact() { return asDecimal().decimal.intValueExact(); }
	/** @throws ArithmeticException */
	BigInteger toBigIntegerExact() { return asDecimal().decimal.toBigIntegerExact(); }	
	
	@Override public String toString() {
		if(decimal != null) return decimal.toPlainString();
		return nominator.toString() + "/" + denominator.toString();
	}

	boolean isDecimal(BigDecimal dec) {
		try { return asDecimal().decimal.compareTo(dec) == 0; }
		catch(ArithmeticException ignored) { return false; }
	}
	boolean isZero() { return isDecimal(BigDecimal.ZERO); }
	boolean isOne() { return isDecimal(BigDecimal.ONE); }
	/** @throws ArithmeticException */
	int compareToZero() {
		if(decimal != null) return decimal.compareTo(BigDecimal.ZERO);
		int nomC = nominator.compareTo(BigInteger.ZERO);
		int denomC = denominator.compareTo(BigInteger.ZERO);
		if(denomC == 0) throw new ArithmeticException("cannot compare undefined number to zero");
		if(nomC == 0) return 0;
		if(nomC > 0 && denomC > 0) return 1;
		if(nomC < 0 && denomC < 0) return 1;
		return -1;
	}
	/** @throws ArithmeticException */
	boolean isPositive() { return compareToZero() >= 0; }
	
	/** @throws ArithmeticException */
	int compareTo(OTNumber other) {
		if(other.isZero()) return compareToZero();
		OTNumber divided = divide(other).asNormatedFrac();
		if(divided.isOne()) return 0;
		if(divided.compareToZero() <= 0) return -1;
		return divided.nominator.compareTo(divided.denominator);
	}

	boolean isInteger() {
		try { toBigIntegerExact(); return true; }
		catch(ArithmeticException ignored) { return false; }
	}
	
	OTNumber negate() {
		if(decimal != null) return new OTNumber(decimal.negate());
		return new OTNumber(nominator.negate(), denominator);
	}
	
	OTNumber multiplicativeInverse() {
		if(decimal != null && decimal.scale() > 0) {
			try { return new OTNumber(BigDecimal.ONE.divide(decimal));
			} catch(ArithmeticException ignored) {}
		}
		OTNumber frac = asFrac();
		if(frac.nominator.compareTo(BigInteger.ZERO) >= 0)
			return new OTNumber(frac.denominator, frac.nominator);
		else
			return new OTNumber(frac.denominator.negate(), frac.nominator.negate());
	}
	
	OTNumber add(OTNumber other) {
		if(decimal != null && other.decimal != null) return new OTNumber(decimal.add(other.decimal));
		OTNumber thisFrac = asNormatedFrac(), otherFrac = other.asNormatedFrac();
		BigInteger gcd = thisFrac.denominator.gcd(otherFrac.denominator);
		BigInteger nom1 = thisFrac.nominator.multiply(otherFrac.denominator.divide(gcd));
		BigInteger nom2 = otherFrac.nominator.multiply(thisFrac.denominator.divide(gcd));
		BigInteger denom = thisFrac.denominator.divide(gcd).multiply(otherFrac.denominator);
		return new OTNumber(nom1.add(nom2), denom).normated();
	}
	
	OTNumber subtract(OTNumber other) {
		return add(other.negate());
	}
	
	OTNumber multiply(OTNumber other) {
		if(decimal != null && other.decimal != null) return new OTNumber(decimal.multiply(other.decimal));
		OTNumber thisFrac = asFrac(), otherFrac = other.asFrac();
		return new OTNumber(
				thisFrac.nominator.multiply(otherFrac.nominator),
				thisFrac.denominator.multiply(otherFrac.denominator)).normated();
	}

	OTNumber divide(OTNumber other) {
		if(decimal != null && other.decimal != null) {
			try {
				OTNumber divided = new OTNumber(decimal.divide(other.decimal));
				if(divided.isInteger()) return divided;
			} catch(ArithmeticException ignored) {}
		}
		return multiply(other.multiplicativeInverse());
	}
	
	OTNumber pow(int n) {
		if(decimal != null) return new OTNumber(decimal.pow(n));
		return new OTNumber(nominator.pow(n), denominator.pow(n));
	}
	
	OTNumber gcd(OTNumber other) {
		OTNumber thisFrac = asNormatedFrac(), otherFrac = other.asNormatedFrac();
		BigInteger gcd = thisFrac.denominator.gcd(otherFrac.denominator);
		BigInteger nom1 = thisFrac.nominator.multiply(otherFrac.denominator.divide(gcd));
		BigInteger nom2 = otherFrac.nominator.multiply(thisFrac.denominator.divide(gcd));
		return new OTNumber(nom1.gcd(nom2));
	}
	
	static BigInteger gcdInt(Iterable<BigInteger> nums) {
		BigInteger c = null;
		for(BigInteger n : nums) {
			if(c == null) c = n;
			else c = c.gcd(n); 
		}
		if(c == null) return BigInteger.ONE;
		return c;
	}
	
	static BigInteger lcmInt(Iterable<BigInteger> nums) {
		BigInteger c = null;
		for(BigInteger n : nums) {
			if(c == null) c = n;
			else c = c.divide(c.gcd(n)).multiply(n); 
		}
		if(c == null) return BigInteger.ONE;
		return c;
	}

	static OTNumber gcd(Iterable<OTNumber> nums) {
		List<OTNumber> numList = new ArrayList<OTNumber>(Utils.collFromIter(Utils.map(nums, new Utils.Function<OTNumber,OTNumber>() {
			public OTNumber eval(OTNumber obj) {
				return obj.asNormatedFrac();
			}
		})));
		final BigInteger lcm = lcmInt(Utils.map(numList, new Utils.Function<OTNumber,BigInteger>() {
			public BigInteger eval(OTNumber obj) {
				return obj.asNormatedFrac().denominator;
			}
		}) );
		numList = new ArrayList<OTNumber>(Utils.collFromIter(Utils.map(numList, new Utils.Function<OTNumber,OTNumber>() {
			public OTNumber eval(OTNumber obj) {
				return obj.multiply(new OTNumber(lcm));
			}
		})));
		return new OTNumber(
				gcdInt(Utils.map(numList, new Utils.Function<OTNumber,BigInteger>() {
					public BigInteger eval(OTNumber obj) {
						return obj.toBigIntegerExact();
					}
				})),
				lcm).normated();
	}
	
	static void test() {
		if(new OTNumber(3,2).compareTo(ONE) <= 0) throw new AssertionError();
	}
	
}
