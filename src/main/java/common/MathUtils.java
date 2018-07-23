package common;

/**
 * Utilities for commonly used math operations.
 */
public class MathUtils {

    public static int log2(final int x) {
        return (int) (Math.log(x) / Math.log(2));
    }

    public static long log2(final long x) {
        return (long) (Math.log(x) / Math.log(2));
    }

    public static boolean isPowerOfTwo(final long x) {
        return (x & (x - 1)) == 0;
    }

    public static int lowestPowerOfTwo(final int n) {
        if (n < 1) {
            return 1;
        }
        int result = 1;
        while (result < n) {
            result <<= 1;
        }
        return result;
    }

    public static long lowestPowerOfTwo(final long n) {
        if (n < 1) {
            return 1;
        }

        long result = 1;
        while (result < n) {
            result <<= 1;
        }
        return result;
    }

    public static int bitreverse(int n, final int bits) {
        int count = bits - 1;
        int reverse = n;
        n >>= 1;
        while (n > 0) {
            reverse = (reverse << 1) | (n & 1);
            n >>= 1;
            count--;
        }
        return ((reverse << count) & ((1 << bits) - 1));
    }

}