package jpountz;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class DotProductBenchmark {

  @Benchmark
  public float dotProductBaseline(VectorSimilarityState state) {
    return dotProductBaseline(state.v1, state.v2);
  }

  @Benchmark
  public float dotProductStep8(VectorSimilarityState state) {
    return dotProductStep8(state.v1, state.v2);
  }

  @Benchmark
  public float dotProductUnrolled8(VectorSimilarityState state) {
    return dotProductUnrolled8(state.v1, state.v2);
  }

  @Benchmark
  public float dotProductVector(VectorSimilarityState state) {
    return dotProductVector(state.v1, state.v2);
  }

  public static float dotProductBaseline(float[] a, float[] b) {
    float res = 0f;
    /*
     * If length of vector is larger than 8, we use unrolled dot product to accelerate the
     * calculation.
     */
    int i;
    for (i = 0; i < a.length % 8; i++) {
      res += b[i] * a[i];
    }
    if (a.length < 8) {
      return res;
    }
    for (; i + 31 < a.length; i += 32) {
      res +=
          b[i + 0] * a[i + 0]
              + b[i + 1] * a[i + 1]
              + b[i + 2] * a[i + 2]
              + b[i + 3] * a[i + 3]
              + b[i + 4] * a[i + 4]
              + b[i + 5] * a[i + 5]
              + b[i + 6] * a[i + 6]
              + b[i + 7] * a[i + 7];
      res +=
          b[i + 8] * a[i + 8]
              + b[i + 9] * a[i + 9]
              + b[i + 10] * a[i + 10]
              + b[i + 11] * a[i + 11]
              + b[i + 12] * a[i + 12]
              + b[i + 13] * a[i + 13]
              + b[i + 14] * a[i + 14]
              + b[i + 15] * a[i + 15];
      res +=
          b[i + 16] * a[i + 16]
              + b[i + 17] * a[i + 17]
              + b[i + 18] * a[i + 18]
              + b[i + 19] * a[i + 19]
              + b[i + 20] * a[i + 20]
              + b[i + 21] * a[i + 21]
              + b[i + 22] * a[i + 22]
              + b[i + 23] * a[i + 23];
      res +=
          b[i + 24] * a[i + 24]
              + b[i + 25] * a[i + 25]
              + b[i + 26] * a[i + 26]
              + b[i + 27] * a[i + 27]
              + b[i + 28] * a[i + 28]
              + b[i + 29] * a[i + 29]
              + b[i + 30] * a[i + 30]
              + b[i + 31] * a[i + 31];
    }
    for (; i + 7 < a.length; i += 8) {
      res +=
          b[i + 0] * a[i + 0]
              + b[i + 1] * a[i + 1]
              + b[i + 2] * a[i + 2]
              + b[i + 3] * a[i + 3]
              + b[i + 4] * a[i + 4]
              + b[i + 5] * a[i + 5]
              + b[i + 6] * a[i + 6]
              + b[i + 7] * a[i + 7];
    }
    return res;
  }

  private static float dotProductStep8(float[] v1, float[] v2) {
    float sum = 0;
    int dim = v1.length;
    int i;
    for (i = 0; i + 8 <= dim; i += 8) {
      sum += dotProduct8(v1, v2, i);
    }
    for (; i < dim; i++) {
      sum += v1[i] + v2[i];
    }
    return sum;
  }

  private static float dotProduct8(float[] v1, float[] v2, int index) {
    float sum = 0;
    for (int i = 0; i < 8; ++i) {
      sum += v1[index + i] * v2[index + i];
    }
    return sum;
  }

  private static float dotProductUnrolled8(float[] v1, float[] v2) {
    float sum = 0;
    int dim = v1.length;
    int i;
    for (i = 0; i + 8 <= dim; i += 8) {
      sum += dotProductUnrolled8(v1, v2, i);
    }
    for (; i < dim; i++) {
      sum += v1[i] + v2[i];
    }
    return sum;
  }

  private static float dotProductUnrolled8(float[] v1, float[] v2, int index) {
    return v1[index] * v2[index] +
        v1[index+1] * v2[index+1] +
        v1[index+2] * v2[index+2] +
        v1[index+3] * v2[index+3] +
        v1[index+4] * v2[index+4] +
        v1[index+5] * v2[index+5] +
        v1[index+6] * v2[index+6] +
        v1[index+7] * v2[index+7];
  }

  static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

  private static float dotProductVector(float[] v1, float[] v2) {
    int dim = v1.length;
    float sum = 0;
    int i;
    for (i = 0; i < SPECIES.loopBound(dim); i += SPECIES.length()) {
      FloatVector fv1 = FloatVector.fromArray(SPECIES, v1, i);
      FloatVector fv2 = FloatVector.fromArray(SPECIES, v2, i);
      FloatVector product = fv1.mul(fv2);
      sum += product.reduceLanes(VectorOperators.ADD);
    }
    for (; i < dim; i++) {
      sum += v1[i] * v2[i];
    }
    return sum;
  }
}
