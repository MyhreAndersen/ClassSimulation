package simulation;

import java.util.Random;

public class Util {

	// *********************************************************************
	// *** Random drawing ***
	// *********************************************************************

	// **********************************************************************
	// *** Basic Drawing - Implementation using default Java-Seed.
	// **********************************************************************
	static final Random random = new Random();


	// **********************************************************************
	// *** Random drawing: Procedure setSeed
	// **********************************************************************
	public static void setSeed(final long seed) {
		random.setSeed(seed);
	}

	
	// **********************************************************************
	// *** Random drawing: Procedure draw
	// **********************************************************************
	/**
	 * <pre>
	 *  Boolean procedure draw (a,U);
	 *                name U; long real a; integer U;
	 * </pre>
	 * 
	 * The value is true with the probability a, false with the probability 1 - a.
	 * It is always true if a >= 1 and always false if a <= 0.
	 * 
	 * @param a
	 * @return
	 */
	public static boolean draw(final double a) {
		boolean val;
		if (a >= 1.0) val = true;
		else if (a <= 0.0) val = false;
		else val = a >= random.nextDouble();
		return (val);
	}

	// **********************************************************************
	// *** Random drawing: Procedure randint
	// **********************************************************************
	/**
	 * <pre>
	 *  integer procedure randint (a,b,U);
	 *		          name U; integer a,b,U;
	 * </pre>
	 * 
	 * The value is one of the integers a, a+1, ..., b-1, b with equal probability.
	 * If b < a, the call constitutes an error.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int randint(final int a,final int b) {
		if (b < a) throw new RuntimeException("Randint(a,b,u):  b < a");
		int val = entier(random.nextDouble() * ((b - a + 1))) + a;
		return (val);
	}
	private static int entier(final double d) {
		int j = (int) d;
		return ((((float) j) > d) ? (j - 1) : (j));
	}

	// **********************************************************************
	// *** Random drawing: Procedure uniform
	// **********************************************************************
	/**
	 * <pre>
	 * long real procedure uniform (a,b,U);
	 *          name U; long real a,b; integer U;
	 * </pre>
	 * 
	 * The value is uniformly distributed in the interval a <= u < b. If b < a, the
	 * call constitutes an error.
	 *
	 * @param a
	 * @param b
	 * @param U
	 * @return
	 */
	public static double uniform(final double a,final double b) {
		if (b < a) throw new RuntimeException("Uniform(a,b,u): b < a");
		double val = a + ((b - a) * random.nextDouble());
		return (val);
	}

	// **********************************************************************
	// *** Random drawing: Procedure normal
	// **********************************************************************
	/**
	 * <pre>
	 * long real procedure normal (a,b,U);
	 *         name U; long real a,b; integer U;
	 * </pre>
	 * 
	 * The value is normally distributed with mean a and standard deviation b. An
	 * approximation formula may be used for the normal distribution function.
	 * <p>
	 * See also: https://www.statisticshowto.com/probability-and-statistics/normal-distributions/
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double normal(final double a,final double b) {
		double t, p, q, v, x;
		boolean z;
		if (b < 0.0) throw new RuntimeException("Normal(a,b,u):  b <= 0.");
		v = random.nextDouble();
		if (v > 0.5) {
			z = true;
			v = 1.0f - v;
		} else
			z = false;
		t = Math.log(v); // log is natural logarithm (base e) in Java
		t = Math.sqrt(-t - t);
		p = 2.515517f + (t * (0.802853f + (t * 0.010328f)));
		q = 1.0f + (t * (1.432788f + (t * (0.189269f + (t * 0.001308f)))));
		x = b * (t - (p / q));
		double val = a + ((z) ? x : -x);
		return (val);
	}

	// **********************************************************************
	// *** Random drawing: Procedure negexp
	// **********************************************************************
	/**
	 * <pre>
	 * long real procedure negexp (a,U);
	 *          name U; long real a; integer U;
	 * </pre>
	 * 
	 * The value is a drawing from the negative exponential distribution with mean
	 * 1/a, defined by -ln(u)/a, where u is a basic drawing. This is the same as a
	 * random "waiting time" in a Poisson distributed arrival pattern with expected
	 * number of arrivals per time unit equal to a. If a is non-positive, a runtime
	 * error occurs.
	 * <p>
	 * See also: https://www.statisticshowto.com/exponential-distribution/
	 * 
	 * @param a
	 * @return
	 */
	public static double negexp(final double a) {
		if (a <= 0.0) throw new RuntimeException("Negexp(a,u): a <= 0");
		double v = random.nextDouble();
		double val = -Math.log(v) / a;
		return (val);
	}

	// **********************************************************************
	// *** Random drawing: Procedure Poisson
	// **********************************************************************
	/**
	 * <pre>
	 * integer procedure Poisson (a,U);
	 *          name U; long real a; integer U;
	 * </pre>
	 * 
	 * The value is a drawing from the Poisson distribution with parameter a. It is
	 * obtained by n+1 basic drawings, u(i), where n is the function value. n is
	 * defined as the smallest non-negative integer for which
	 * <p>
	 * u(0) * u(1) * ... * u(n) < e**(-a)
	 * <p>
	 * The validity of the formula follows from the equivalent condition
	 * <p>
	 * -ln(u(0)) - ln(u(1)) - ... - ln(u(n)) > 1
	 * <p>
	 * where the left hand side is seen to be a sum of "waiting times" drawn from
	 * the corresponding negative exponential distribution.
	 * <p>
	 * When the parameter a is greater than some implementation-defined value, for
	 * instance 20.0, the value may be approximated by entier(normal(a,sqrt(a),U) +
	 * 0.5) or, when this is negative, by zero.
	 * <p>
	 * See also: https://www.statisticshowto.com/exponential-distribution/
	 * 
	 * @param a
	 * @return
	 */
	public static int Poisson(final double a) {
		int val;
		double acc, xpa, sqa;
		if (a <= 0.0)
			val = 0;
		else if (a > 20.0) {
			// entier(normal(a,sqrt(a),U) + 0.5)
			sqa = Math.sqrt(a);
			val = entier(normal(a, sqa) + 0.5);
		} else {
			acc = 1.0;
			val = 0;
			xpa = Math.exp(-a);
			do {
//				acc = acc * basicDRAW();
				acc = acc * random.nextDouble();
				val = val + 1;
			} while (acc >= xpa);
			val = val - 1;
		}
		return (val);
	}

	// **********************************************************************
	// *** Random drawing: Procedure Erlang
	// **********************************************************************
	/**
	 * <pre>
	 * long real procedure Erlang (a,b,U);
	 *          name U; long real a,b; integer U;
	 * </pre>
	 * 
	 * The value is a drawing from the Erlang distribution with mean 1/a and
	 * standard deviation 1/(a*sqrt(b)). It is defined by b basic drawings u(i), if
	 * b is an integer value,
	 * <p>
	 * - ( ln(u(1)) + ln(u(2)) + ... + ln(u(b)) ) / (a*b)
	 * <p>
	 * and by c+1 basic drawings u(i) otherwise, where c is equal to entier(b),
	 * <p>
	 * - ( ln(u(1)) + ... + ln(u(c)) + (b-c)*ln(u(c+1)) ) / (a*b)
	 * <p>
	 * Both a and b must be greater than zero.
	 * <p>
	 * The last formula represents an approximation.
	 * <p>
	 * See also: https://www.statisticshowto.com/erlang-distribution/
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double Erlang(final double a,final double b) {
		int c;
		double val, bc, ab, z, v;
		if (a <= 0.0 || b <= 0.0)
			throw new RuntimeException("Erlang(a,b,u):  a <= 0  or  b <= 0");
		val = 0;
		c = entier(b);
		bc = b - c;
		ab = a * b;
		while ((c--) > 0) {
			v = random.nextDouble();
			z = Math.log(v);
			val = val - (z / ab);
		}
		if (bc > 0.0) {
			v = random.nextDouble();
			z = Math.log(v);
			val = val - ((bc * z) / ab);
		}
		return (val);
	}

	// **********************************************************************
	// *** Random drawing: Procedure discrete
	// **********************************************************************
	/**
	 * <pre>
	 *  integer procedure discrete (A,U);
	           name U; <real-type> array A; integer U;
	 * </pre>
	 * 
	 * The one-dimensional array A, augmented by the element 1 to the right, is
	 * interpreted as a step function of the subscript, defining a discrete
	 * (cumulative) distribution function.
	 * <p>
	 * The function value satisfies
	 * <p>
	 * lowerbound(A,1) <= discrete(A,U) <= upperbound(A,1)+1.
	 * <p>
	 * It is defined as the smallest i such that A(i) > u, where u is a basic
	 * drawing and A(upperbound(A,1)+1) = 1.
	 *
	 * @param A
	 * @return
	 */
	public static int discrete(final double[] A) {
		int result, j, nelt;
		double v;
		int lb = 0; // A.LB[0];
		int ub = A.length-1; // A.UB[0];
//		v = basicDRAW(U);
		v = random.nextDouble();
		nelt = ub - lb + 1;
		result = ub + 1;
		j = 0;
		do {
			if (A[j] > v) {
				result = lb + j;
				nelt = 0;
			}
			j = j + 1;
		} while (j < nelt);
		return (result);
	}

	// **********************************************************************
	// *** Random drawing: Procedure linear
	// **********************************************************************
	/**
	 * <pre>
	 *  long real procedure linear (A,B,U);
	 *       name U; <real-type> array A,B; integer U;
	 * </pre>
	 * 
	 * The value is a drawing from a (cumulative) distribution function F, which is
	 * obtained by linear interpolation in a non-equidistant table defined by A and
	 * B, such that A(i) = F(B(i)).
	 * <p>
	 * It is assumed that A and B are one-dimensional arrays of the same length,
	 * that the first and last elements of A are equal to 0 and 1 respectively and
	 * that A(i) >= A(j) and B(i) > B(j) for i>j. If any of these conditions are not
	 * satisfied, the effect is implementation-defined.
	 * <p>
	 * The steps in the function evaluation are:
	 * <p>
	 * l. draw a uniform <0,1> random number, u.
	 * <p>
	 * 2. determine the lowest value of i, for which
	 * <p>
	 * A(i-1) <= u <= A(i)
	 * <p>
	 * 3. compute D = A(i) - A(i-1)
	 * <p>
	 * 4. if D = 0: linear = B(i-1) if D <> 0: linear = B(i-1) + (B(i) -
	 * B(i-1))*(u-A(i-1))/D
	 * <p>
	 * NOTE: The name specified parameter 'U' is not used. Instead, the default seed
	 * in the Java Library is used.
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
//	public double linear(ARRAY$<double[]> A, ARRAY$<double[]> B, NAME$<Integer> U) {
	public static double linear(final double[] A,final double[] B) {
		int i, nelt;
		double val, a_val, a_lag, a_dif, b_val, b_lag, v;

		int lb = 0; // A.LB[0];
		int ub = A.length-1; // A.UB[0];

		nelt = ub - lb + 1;
//		if (nelt != (B.UB[0] - B.LB[0] + 1))
		if (nelt != B.length)
			throw new RuntimeException("Linear(A,B,U): The number of elements in A and B are different.");
		v = random.nextDouble();
		i = 0;
		while (A[i] < v)
			i = i + 1;
		if (i == 0) {
			if (v == 0.0 && A[i] == 0.0)
				i = 1;
			else
				throw new RuntimeException("Linear(A,B,U): The array a does not satisfy the stated assumptions.");
		} else if (i >= nelt)
			throw new RuntimeException("Linear(A,B,U): The array a does not satisfy the stated assumptions.");

		a_val = A[i];
		a_lag = A[i - 1];
		a_dif = a_val - a_lag;
		if (a_dif == 0.0)
			val = B[i - 1];
		else {
			b_val = B[i];
			b_lag = B[i - 1];
			val = (((b_val - b_lag) / a_dif) * (v - a_lag)) + b_lag;
		}
		return (val);
	}

	// **********************************************************************
	// *** Utility: Procedure histd
	// **********************************************************************
	/**
	 * <pre>
	 * integer procedure histd (A,U);
	 *          name U; <real-type> array A; integer U;
	 * </pre>
	 * 
	 * The value is an integer in the range (lsb,usb), where lsb and usb are the
	 * lower and upper subscript bounds of the one-dimensional array A. The latter
	 * is interpreted as a histogram defining the relative frequencies of the
	 * values.
	 * 
	 * @param A
	 * @return
	 */
	public static int histd(final float[] A) {
		int result = 0;
		int j; // Array index.
		int nelt; // Number of array elements.
		double sum; // Sum of all array element values.
		double wsum; // Weighted sum of all array element values.
		double tmp; // Temporary variabel.

		int lb = 0;//A.LB[0];
		int ub = A.length-1; //A.UB[0];
		nelt = ub - lb + 1;
		j = 0;
		sum = 0.0;
		do {
			tmp = A[j];
			if (tmp < 0.0)
				throw new RuntimeException("Histd(a,u):  An element of the array a is negative");
			sum = sum + tmp;
			j = j + 1;
		} while (j < nelt);
//		wsum = sum * basicDRAW(U); // Make 0 <= wsum < sum
		wsum = sum * random.nextDouble(); // Make 0 <= wsum < sum
		j = 0;
		sum = 0.0;
		do {
			sum = sum + A[j];
			if (sum >= wsum) {
				result = lb + j;
				nelt = 0;
			} // We will do this once and only once.
			j = j + 1;
		} while (j < nelt);
		return (result);
	}


	public static void ASSERT(final boolean test, final String msg) {
		if (!test) {
			System.out.println("ASSERT(" + msg + ") -- FAILED");
//			BREAK("Press [ENTER] Continue or [Q] for a Stack-Trace");
			System.out.println("STACK-TRACE");
			printStackTrace();
			System.exit(0);
		}
	}

	public static void IERR(final String msg) {
		System.out.println("IERR: " + msg);
		System.out.println("STACK-TRACE");
		printStackTrace();
		System.exit(0);
	}

	public static void NOT_IMPLEMENTED(final String s) {
		System.err.println("*** NOT IMPLEMENTED: " + s);
		BREAK("Press [ENTER] Continue or [Q] for a Stack-Trace");
	}

	public static boolean BREAKING=true;
	public static void BREAK(final String title) {
		if (BREAKING) {
			try {
				System.out.println(title + ": <");
				char c=(char) System.in.read();
				if (c == 'Q' || c == 'q') {
					System.out.println("STACK-TRACE");
					printStackTrace();
				}
				while (System.in.available() > 0) System.in.read();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	public static void exit() {
		System.out.println("FORCED EXIT");
		printStackTrace();
		while(true);
//		System.exit(0);
	}

	public static void printStackTrace() {
		StackTraceElement stackTraceElement[] = Thread.currentThread().getStackTrace();
		int n = stackTraceElement.length;
		for (int i = 2; i < n; i++)
			System.out.println("   at "+stackTraceElement[i]);
	}

}
