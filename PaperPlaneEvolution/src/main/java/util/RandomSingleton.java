package util;

import java.util.Random;

public class RandomSingleton {
    private static RandomSingleton instance;
    private Random _rnd;

    private RandomSingleton() {
    	_rnd = new Random(98);
    }
    private RandomSingleton(long s) {
    	_rnd = new Random(s);
    }

    public static RandomSingleton getInstance() {
        if(instance == null) {
            instance = new RandomSingleton();
        }
        return instance;
    }

    public static double nextDouble() {
         return getInstance()._rnd.nextDouble();
    }
    public static int nextInt(int u) {
        return getInstance()._rnd.nextInt(u);
   }
    public static float nextFloat() {
        return getInstance()._rnd.nextFloat();
   }
    public static double nextGaussian() {
        return getInstance()._rnd.nextGaussian();
   }
    public static boolean nextBoolean() {
        return getInstance()._rnd.nextBoolean();
   }
    public static void setSeed(long s) {
    	instance = new RandomSingleton(s);
    }
}