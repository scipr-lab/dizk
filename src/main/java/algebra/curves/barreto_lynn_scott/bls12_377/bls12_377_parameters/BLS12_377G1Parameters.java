package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG1Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G1;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class BLS12_377G1Parameters
    extends AbstractBLSG1Parameters<BLS12_377Fr, BLS12_377Fq, BLS12_377G1, BLS12_377G1Parameters>
    implements Serializable {

  public static final BLS12_377G1 ZERO =
      new BLS12_377G1(BLS12_377Fq.ZERO, BLS12_377Fq.ONE, BLS12_377Fq.ZERO);
  public static final BLS12_377G1 ONE =
      new BLS12_377G1(
          new BLS12_377Fq(
              "81937999373150964239938255573465948239988671502647976594219695644855304257327692006745978603320413799295628339695"),
          new BLS12_377Fq(
              "241266749859715473739788878240585681733927191168601896383759122102112907357779751001206799952863815012735208165030"),
          BLS12_377Fq.ONE);
  public static final ArrayList<Integer> fixedBaseWindowTable =
      new ArrayList<>(
          Arrays.asList(
              1, // window 1 is unbeaten in [-inf, 4.99]
              5, // window 2 is unbeaten in [4.99, 10.99]
              11, // window 3 is unbeaten in [10.99, 32.29]
              32, // window 4 is unbeaten in [32.29, 55.23]
              55, // window 5 is unbeaten in [55.23, 162.03]
              162, // window 6 is unbeaten in [162.03, 360.15]
              360, // window 7 is unbeaten in [360.15, 815.44]
              815, // window 8 is unbeaten in [815.44, 2373.07]
              2373, // window 9 is unbeaten in [2373.07, 6977.75]
              6978, // window 10 is unbeaten in [6977.75, 7122.23]
              7122, // window 11 is unbeaten in [7122.23, 57818.46]
              0, // window 12 is never the best
              57818, // window 13 is unbeaten in [57818.46, 169679.14]
              0, // window 14 is never the best
              169679, // window 15 is unbeaten in [169679.14, 439758.91]
              439759, // window 16 is unbeaten in [439758.91, 936073.41]
              936073, // window 17 is unbeaten in [936073.41, 4666554.74]
              0, // window 18 is never the best
              4666555, // window 19 is unbeaten in [4666554.74, 7580404.42]
              7580404, // window 20 is unbeaten in [7580404.42, 34552892.20]
              0, // window 21 is never the best
              34552892 // window 22 is unbeaten in [34552892.20, inf]
              ));

  public BLS12_377G1 ZERO() {
    return ZERO;
  }

  public BLS12_377G1 ONE() {
    return ONE;
  }

  public BLS12_377Fr zeroFr() {
    return BLS12_377Fr.ZERO;
  }

  public BLS12_377Fr oneFr() {
    return BLS12_377Fr.ONE;
  }

  public ArrayList<Integer> fixedBaseWindowTable() {
    return fixedBaseWindowTable;
  }
}
