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
public class SquareDistanceBenchmark {

  @Benchmark
  public float squareDistanceBaseline(VectorSimilarityState state) {
    return squareDistanceBaseline(state.v1, state.v2);
  }

  @Benchmark
  public float squareDistanceStep8(VectorSimilarityState state) {
    return squareDistanceStep8(state.v1, state.v2);
  }

  @Benchmark
  public float squareDistanceUnrolled8(VectorSimilarityState state) {
    return squareDistanceUnrolled8(state.v1, state.v2);
  }

  @Benchmark
  public float squareDistanceVector(VectorSimilarityState state) {
    return squareDistanceVector(state.v1, state.v2);
  }

  private static float squareDistanceBaseline(float[] v1, float[] v2) {
    float squareSum = 0.0f;
    int dim = v1.length;
    int i;
    for (i = 0; i < dim; i++) {
      float diff = v1[i] - v2[i];
      squareSum += diff * diff;
    }
    return squareSum;
  }

  private static float squareDistanceStep8(float[] v1, float[] v2) {
    float squareSum = 0.0f;
    int dim = v1.length;
    int i;
    for (i = 0; i + 8 <= dim; i += 8) {
      squareSum += squareDistance8(v1, v2, i);
    }
    for (; i < dim; i++) {
      float diff = v1[i] - v2[i];
      squareSum += diff * diff;
    }
    return squareSum;
  }

  private static float squareDistance8(float[] v1, float[] v2, int index) {
    float squareSum = 0;
    for (int i = 0; i < 8; ++i) {
      float diff = v1[index + i] - v2[index + i];
      squareSum += diff * diff;
    }
    return squareSum;
  }

  private static float squareDistanceUnrolled8(float[] v1, float[] v2) {
    float squareSum = 0.0f;
    int dim = v1.length;
    int i;
    for (i = 0; i + 8 <= dim; i += 8) {
      squareSum += squareDistanceUnrolled8(v1, v2, i);
    }
    for (; i < dim; i++) {
      float diff = v1[i] - v2[i];
      squareSum += diff * diff;
    }
    return squareSum;
  }

  private static float squareDistanceUnrolled8(float[] v1, float[] v2, int index) {
    float diff0 = v1[index + 0] - v2[index + 0];
    float diff1 = v1[index + 1] - v2[index + 1];
    float diff2 = v1[index + 2] - v2[index + 2];
    float diff3 = v1[index + 3] - v2[index + 3];
    float diff4 = v1[index + 4] - v2[index + 4];
    float diff5 = v1[index + 5] - v2[index + 5];
    float diff6 = v1[index + 6] - v2[index + 6];
    float diff7 = v1[index + 7] - v2[index + 7];
    return diff0 * diff0 + diff1 * diff1 + diff2 * diff2 + diff3 * diff3 + diff4 * diff4 + diff5 * diff5 + diff6 * diff6 + diff7 * diff7;
  }

  static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

  private static float squareDistanceVector(float[] v1, float[] v2) {
    int dim = v1.length;
    float sum = 0;
    int i;
    for (i = 0; i < SPECIES.loopBound(dim); i += SPECIES.length()) {
      FloatVector fv1 = FloatVector.fromArray(SPECIES, v1, i);
      FloatVector fv2 = FloatVector.fromArray(SPECIES, v2, i);
      FloatVector delta = fv1.sub(fv2);
      FloatVector product = delta.mul(delta);
      sum += product.reduceLanes(VectorOperators.ADD);
    }
    for (; i < dim; i++) {
      float diff = v1[i] - v2[i];
      sum += diff * diff;
    }
    return sum;
  }
}
