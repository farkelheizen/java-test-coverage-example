package com.example.service;

public class MathService {

    public boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public long fibonacci(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    public int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public long lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return (long) Math.abs(a) / gcd(a, b) * Math.abs(b);
    }

    public long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Factorial of negative number is undefined");
        if (n == 0 || n == 1) return 1;
        if (n > 20) throw new IllegalArgumentException("Factorial overflow for n > 20");
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }

    public double power(double base, int exponent) {
        if (exponent < 0) return 1.0 / Math.pow(base, -exponent);
        return Math.pow(base, exponent);
    }
}
