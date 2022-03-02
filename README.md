Run with `mvn clean package && java --add-modules jdk.incubator.vector -jar target/benchmarks.jar`.

Results on my machine (AMD Ryzen 9 3900X):

```
Benchmark                                         Mode  Cnt   Score   Error   Units
DotProductBenchmark.dotProductBaseline           thrpt    5  27.587 ± 0.150  ops/us
DotProductBenchmark.dotProductStep8              thrpt    5  25.641 ± 0.586  ops/us
DotProductBenchmark.dotProductUnrolled8          thrpt    5  25.630 ± 0.323  ops/us
DotProductBenchmark.dotProductVector             thrpt    5  34.715 ± 0.723  ops/us
SquareDistanceBenchmark.squareDistanceBaseline   thrpt    5  14.601 ± 0.181  ops/us
SquareDistanceBenchmark.squareDistanceStep8      thrpt    5  18.891 ± 0.449  ops/us
SquareDistanceBenchmark.squareDistanceUnrolled8  thrpt    5  20.072 ± 0.578  ops/us
SquareDistanceBenchmark.squareDistanceVector     thrpt    5  31.000 ± 0.336  ops/us
```
