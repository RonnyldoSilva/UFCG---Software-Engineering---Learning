package com.systematictesting.media.math;

import java.math.BigInteger;

public class IntMath {

	public static int gcd(int a, int b) {
		a = Math.abs(a);
		b = Math.abs(b);

		while (a > 0 && b > 0) {
			a = a % b;
			if (a > 0)
				b = b % a;
		}
		return a + b;
	}

	public static long gcd(long a, long b) {
		a = Math.abs(a);
		b = Math.abs(b);

		while (a > 0 && b > 0) {
			a = a % b;
			if (a > 0)
				b = b % a;
		}
		return a + b;
	}

	public static BigInteger gcd(BigInteger a, BigInteger b) {
		a = a.abs();
		b = b.abs();

		while (a.compareTo(BigInteger.ZERO) > 0 && b.compareTo(BigInteger.ZERO) > 0) {
			a = a.mod(b);
			if (a.compareTo(BigInteger.ZERO) > 0)
				b = b.mod(a);
		}
		return a.add(b);
	}

	public static int scm(int a, int b) {
		if (a == 0 || b == 0)
			return 0;
		a = Math.abs(a);
		b = Math.abs(b);
		int u = a;
		int v = b;
		while (a != b) {
			if (a < b) {
				b -= a;
				v += u;
			} else {
				a -= b;
				u += v;
			}
		}
		return (u + v) / 2;
	}

	public static long scm(long a, long b) {

		if (a == 0 || b == 0)
			return 0;

		a = Math.abs(a);
		b = Math.abs(b);
		if (b == 1)
			return a;
		if (a == 1)
			return b;

		long u = a;
		long v = b;

		while (a != b) {
			if (a < b) {
				b -= a;
				v += u;
			} else {
				a -= b;
				u += v;
			}
		}

		return (u + v) / 2;
	}

	public static BigInteger scm(BigInteger a, BigInteger b) {

		if (a.compareTo(BigInteger.ZERO) == 0 || b.compareTo(BigInteger.ZERO) == 0) {
			return BigInteger.ZERO;
		}

		a = a.abs();
		b = b.abs();
		if (b.compareTo(BigInteger.ONE) == 0)
			return a;
		if (a.compareTo(BigInteger.ONE) == 0)
			return b;

		BigInteger u = a;
		BigInteger v = b;

		while (a.compareTo(b) != 0) {
			if (a.compareTo(b) < 0) {
				b = b.subtract(a);
				v = v.add(u);
			} else {
				a = a.subtract(b);
				u = u.add(v);
			}
		}

		return (u.add(v)).divide(BigInteger.valueOf(2));
	}

}
