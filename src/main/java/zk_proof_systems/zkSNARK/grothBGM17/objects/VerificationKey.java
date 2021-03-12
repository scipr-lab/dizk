package zk_proof_systems.zkSNARK.grothBGM17.objects;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import java.util.List;

/** Groth16-BGM17 Verification key */
public class VerificationKey<G1T extends AbstractG1<G1T>, G2T extends AbstractG2<G2T>> {

  // [alpha]_1
  private final G1T alphaG1;
  // [beta]_2
  private final G2T betaG2;
  // [delta]_2
  private final G2T deltaG2;
  // {[beta * A_i(t) + alpha * B_i(t) + C_i(t)]_1}_{i=0}^{numInputs}
  private final List<G1T> ABC;

  public VerificationKey(
      final G1T _alphaG1, final G2T _betaG2, final G2T _deltaG2, final List<G1T> _ABC) {
    alphaG1 = _alphaG1;
    betaG2 = _betaG2;
    deltaG2 = _deltaG2;
    ABC = _ABC;
  }

  public boolean equals(final VerificationKey<G1T, G2T> other) {
    return alphaG1.equals(other.alphaG1())
        && betaG2.equals(other.betaG2())
        && deltaG2.equals(other.deltaG2())
        && ABC.equals(other.ABC);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (!(o instanceof VerificationKey<?, ?>)) {
      return false;
    }
    return (equals((VerificationKey<G1T, G2T>) o));
  }

  public G1T alphaG1() {
    return alphaG1;
  }

  public G2T betaG2() {
    return betaG2;
  }

  public G2T deltaG2() {
    return deltaG2;
  }

  public G1T ABC(final int i) {
    return ABC.get(i);
  }

  public List<G1T> ABC() {
    return ABC;
  }
}
