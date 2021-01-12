package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

// Checked

public class BLS12_377G2Parameters
    extends AbstractBLSG2Parameters<BLS12_377Fr, BLS12_377Fq, BLS12_377Fq2, BLS12_377G2, BLS12_377G2Parameters>
    implements Serializable {

  public static final BLS12_377G2 ZERO = new BLS12_377G2(BLS12_377Fq2.ZERO, BLS12_377Fq2.ONE, BLS12_377Fq2.ZERO);
  public static final BLS12_377G2 ONE =
      new BLS12_377G2(
          new BLS12_377Fq2(
              new BLS12_377Fq(
                  "111583945774695116443911226257823823434468740249883042837745151039122196680777376765707574547389190084887628324746"),
              new BLS12_377Fq(
                  "129066980656703085518157301154335215886082112524378686555873161080604845924984124025594590925548060469686767592854")),
          new BLS12_377Fq2(
              new BLS12_377Fq(
                  "168863299724668977183029941347596462608978380503965103341003918678547611204475537878680436662916294540335494194722"),
              new BLS12_377Fq(
                  "233892497287475762251335351893618429603672921469864392767514552093535653615809913098097380147379993375817193725968")),
              BLS12_377Fq2.ONE);
  public static final ArrayList<Integer> fixedBaseWindowTable =
      new ArrayList<>(
          Arrays.asList(
              1, // window 1 is unbeaten in [-inf, 5.10]
              5, // window 2 is unbeaten in [5.10, 10.43]
              10, // window 3 is unbeaten in [10.43, 25.28]
              25, // window 4 is unbeaten in [25.28, 59.00]
              59, // window 5 is unbeaten in [59.00, 154.03]
              154, // window 6 is unbeaten in [154.03, 334.25]
              334, // window 7 is unbeaten in [334.25, 742.58]
              743, // window 8 is unbeaten in [742.58, 2034.40]
              2034, // window 9 is unbeaten in [2034.40, 4987.56]
              4988, // window 10 is unbeaten in [4987.56, 8888.27]
              8888, // window 11 is unbeaten in [8888.27, 26271.13]
              26271, // window 12 is unbeaten in [26271.13, 39768.20]
              39768, // window 13 is unbeaten in [39768.20, 106275.75]
              106276, // window 14 is unbeaten in [106275.75, 141703.40]
              141703, // window 15 is unbeaten in [141703.40, 462422.97]
              462423, // window 16 is unbeaten in [462422.97, 926871.84]
              926872, // window 17 is unbeaten in [926871.84, 4873049.17]
              0, // window 18 is never the best
              4873049, // window 19 is unbeaten in [4873049.17, 5706707.88]
              5706708, // window 20 is unbeaten in [5706707.88, 31673814.95]
              0, // window 21 is never the best
              31673815 // window 22 is unbeaten in [31673814.95, inf]
              ));

  public BLS12_377G2 ZERO() {
    return ZERO;
  }

  public BLS12_377G2 ONE() {
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
