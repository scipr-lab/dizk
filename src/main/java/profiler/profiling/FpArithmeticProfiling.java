package profiler.profiling;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import configuration.Configuration;
import java.util.ArrayList;
import java.util.Random;

public class FpArithmeticProfiling {

  public static void BN128FrArithmeticProfiling(final Configuration config, final int size) {
    final BN254aFr fieldFactory = new BN254aFr(2L);

    final Random rand = new Random();
    final ArrayList<BN254aFr> inputA = new ArrayList<>(size);
    final ArrayList<BN254aFr> inputB = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      inputA.add(fieldFactory.random(rand.nextLong(), null));
      inputB.add(fieldFactory.random(rand.nextLong(), null));
    }

    for (int i = 0; i < size; i++) {
      inputA.get(i).add(inputB.get(i));
    }

    config.setContext("BN128FrArithmeticProfiling");
    config.beginRuntimeMetadata("Size (inputs)", (long) size);

    config.beginLog("FpAddition");
    config.beginRuntime("FpAddition");
    long startAddition = System.nanoTime();
    for (int i = 0; i < size; i++) {
      inputA.get(i).add(inputB.get(i));
    }
    long totalNanoAddition = System.nanoTime() - startAddition;
    config.endRuntime("FpAddition");
    config.endLog("FpAddition");

    System.out.println(
        "Addition: "
            + totalNanoAddition
            + " ns / "
            + size
            + " = "
            + ((double) totalNanoAddition / size)
            + " ns");

    config.beginLog("FpMultiplication");
    config.beginRuntime("FpMultiplication");
    long startMultiplication = System.nanoTime();
    for (int i = 0; i < size; i++) {
      inputA.get(i).mul(inputB.get(i));
    }
    long totalNanoMultiplication = System.nanoTime() - startMultiplication;
    config.endRuntime("FpMultiplication");
    config.endLog("FpMultiplication");

    System.out.println(
        "Multiplication: "
            + totalNanoMultiplication
            + " ns / "
            + size
            + " = "
            + ((double) totalNanoMultiplication / size)
            + " ns");

    config.writeRuntimeLog(config.context());
  }

  public static void BN254bFrArithmeticProfiling(final Configuration config, final int size) {
    final BN254bFr fieldFactory = new BN254bFr(2L);

    final Random rand = new Random();
    final ArrayList<BN254bFr> inputA = new ArrayList<>(size);
    final ArrayList<BN254bFr> inputB = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      inputA.add(fieldFactory.random(rand.nextLong(), null));
      inputB.add(fieldFactory.random(rand.nextLong(), null));
    }

    for (int i = 0; i < size; i++) {
      inputA.get(i).add(inputB.get(i));
    }

    config.setContext("BN256FrArithmeticProfiling");
    config.beginRuntimeMetadata("Size (inputs)", (long) size);

    config.beginLog("FpAddition");
    config.beginRuntime("FpAddition");
    long startAddition = System.nanoTime();
    for (int i = 0; i < size; i++) {
      inputA.get(i).add(inputB.get(i));
    }
    long totalNanoAddition = System.nanoTime() - startAddition;
    config.endRuntime("FpAddition");
    config.endLog("FpAddition");

    System.out.println(
        "Addition: "
            + totalNanoAddition
            + " ns / "
            + size
            + " = "
            + ((double) totalNanoAddition / size)
            + " ns");

    config.beginLog("FpMultiplication");
    config.beginRuntime("FpMultiplication");
    long startMultiplication = System.nanoTime();
    for (int i = 0; i < size; i++) {
      inputA.get(i).mul(inputB.get(i));
    }
    long totalNanoMultiplication = System.nanoTime() - startMultiplication;
    config.endRuntime("FpMultiplication");
    config.endLog("FpMultiplication");

    System.out.println(
        "Multiplication: "
            + totalNanoMultiplication
            + " ns / "
            + size
            + " = "
            + ((double) totalNanoMultiplication / size)
            + " ns");

    config.writeRuntimeLog(config.context());
  }
}
