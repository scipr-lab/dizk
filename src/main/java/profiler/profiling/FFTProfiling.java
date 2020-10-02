package profiler.profiling;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.fft.DistributedFFT;
import algebra.fft.SerialFFT;
import common.MathUtils;
import configuration.Configuration;
import java.util.ArrayList;
import org.apache.spark.api.java.JavaPairRDD;
import profiler.generation.FFTGenerator;

public class FFTProfiling {

  public static void serialFFTProfiling(final Configuration config, final long size) {
    final BN254aFr fieldFactory = new BN254aFr(2L);

    final ArrayList<BN254aFr> serial = new ArrayList<>();
    for (int j = 0; j < size; j++) {
      serial.add(fieldFactory.random(config.seed(), config.secureSeed()));
    }

    final SerialFFT<BN254aFr> domain = new SerialFFT<>(size, fieldFactory);

    config.setContext("FFTProfiling-Serial");
    config.beginRuntimeMetadata("Size (inputs)", size);

    config.beginLog("FFT");
    config.beginRuntime("FFT");
    domain.radix2FFT(serial);
    config.endRuntime("FFT");
    config.endLog("FFT");

    config.writeRuntimeLog(config.context());
  }

  public static void distributedFFTProfiling(final Configuration config, final long size) {
    final BN254aFr fieldFactory = new BN254aFr(2L);
    final JavaPairRDD<Long, BN254aFr> distributed = FFTGenerator.generateData(config, size);

    final long k = MathUtils.lowestPowerOfTwo((long) Math.sqrt(size));
    final long rows = size / k;
    final long cols = k;

    config.setContext("FFT");
    config.beginRuntimeMetadata("Size (inputs)", size);
    config.beginRuntimeMetadata("Column Size (inputs)", k);

    config.beginLog("FFT");
    config.beginRuntime("FFT");
    DistributedFFT.radix2FFT(distributed, rows, cols, fieldFactory).count();
    config.endRuntime("FFT");
    config.endLog("FFT");

    config.writeRuntimeLog(config.context());
  }
}
