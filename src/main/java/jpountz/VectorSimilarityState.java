package jpountz;

import java.io.IOException;
import java.util.SplittableRandom;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class VectorSimilarityState {

  private static final int DIMENSION = 128;

  public final float[] v1 = new float[DIMENSION];
  public final float[] v2 = new float[DIMENSION];

  @Setup(Level.Trial)
  public void setupTrial() throws IOException {
    SplittableRandom r = new SplittableRandom(0);
    for (int i = 0; i < DIMENSION; ++i) {
      v1[i] = r.nextFloat();
      v2[i] = r.nextFloat();
    }
  }
}
