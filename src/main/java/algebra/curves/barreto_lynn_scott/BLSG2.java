package algebra.curves.barreto_lynn_scott;

import algebra.curves.AbstractG2;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq2;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFr;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG2Parameters;
import java.util.ArrayList;

public abstract class BLSG2<
        BLSFrT extends BLSFr<BLSFrT>,
        BLSFqT extends BLSFq<BLSFqT>,
        BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
        BLSG2T extends BLSG2<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>,
        BLSG2ParametersT extends
            AbstractBLSG2Parameters<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>>
    extends AbstractG2<BLSG2T> {
  protected final BLSG2ParametersT G2Parameters;
  protected BLSFq2T X;
  protected BLSFq2T Y;
  protected BLSFq2T Z;

  public BLSG2(final BLSFq2T X, final BLSFq2T Y, final BLSFq2T Z, final BLSG2ParametersT G2Parameters) {
    this.X = X;
    this.Y = Y;
    this.Z = Z;
    this.G2Parameters = G2Parameters;
  }

  public abstract BLSG2T construct(BLSFq2T X, BLSFq2T Y, BLSFq2T Z);

  public BLSG2T add(final BLSG2T other) {
    // Handle special cases having to do with O
    if (this.isZero()) {
      return other;
    }

    if (other.isZero()) {
      return this.self();
    }

    // No need to handle points of order 2,4
    // (they cannot exist in a prime-order subgroup)

    // Check for doubling case
    //
    // Using Jacobian coordinates so:
    // (X1:Y1:Z1) = (X2:Y2:Z2)
    // iff
    // X1/Z1^2 == X2/Z2^2 and Y1/Z1^3 == Y2/Z2^3
    // iff
    // X1 * Z2^2 == X2 * Z1^2 and Y1 * Z2^3 == Y2 * Z1^3

    // Z1Z1 = Z1*Z1
    final BLSFq2T Z1Z1 = this.Z.square();
    // Z2Z2 = Z2*Z2
    final BLSFq2T Z2Z2 = other.Z.square();

    // U1 = X1*Z2Z2
    final BLSFq2T U1 = this.X.mul(Z2Z2);
    // U2 = X2*Z1Z1
    final BLSFq2T U2 = other.X.mul(Z1Z1);

    final BLSFq2T Z1Cubed = this.Z.mul(Z1Z1);
    final BLSFq2T Z2Cubed = other.Z.mul(Z2Z2);

    // S1 = Y1 * Z2 * Z2Z2
    final BLSFq2T S1 = this.Y.mul(Z2Cubed);
    // S2 = Y2 * Z1 * Z1Z1
    final BLSFq2T S2 = other.Y.mul(Z1Cubed);

    // Check if the 2 points are equal, in which can we do a point doubling (i.e. P + P)
    if (U1.equals(U2) && S1.equals(S2)) {
      // Double case
      // Nothing of above can be reused
      return this.dbl();
    }

    // Point addition (i.e. P + Q, P =/= Q)
    // https://www.hyperelliptic.org/EFD/g1p/data/shortw/jacobian-0/addition/add-2007-bl
    // H = U2-U1
    final BLSFq2T H = U2.sub(U1);
    // I = (2 * H)^2
    final BLSFq2T I = H.add(H).square();
    // J = H * I
    final BLSFq2T J = H.mul(I);
    // r = 2 * (S2-S1)
    final BLSFq2T S2MinusS1 = S2.sub(S1);
    final BLSFq2T r = S2MinusS1.add(S2MinusS1);
    // V = U1 * I
    final BLSFq2T V = U1.mul(I);
    // X3 = r^2 - J - 2 * V
    final BLSFq2T X3 = r.square().sub(J).sub(V.add(V));
    // Y3 = r * (V-X3)-2*S1*J
    final BLSFq2T S1_J = S1.mul(J);
    final BLSFq2T Y3 = r.mul(V.sub(X3)).sub(S1_J.add(S1_J));
    // Z3 = ((Z1+Z2)^2-Z1Z1-Z2Z2) * H
    final BLSFq2T Z3 = (this.Z.add(other.Z).square().sub(Z1Z1).sub(Z2Z2)).mul(H);

    return this.construct(X3, Y3, Z3);
  }

  public BLSG2T sub(final BLSG2T other) {
    return this.add(other.negate());
  }

  public BLSG2T dbl() {
    if (this.isZero()) {
      return this.self();
    }

    // NOTE: does not handle O and pts of order 2,4
    // https://www.hyperelliptic.org/EFD/g1p/data/shortw/jacobian-0/doubling/dbl-2009-l
    // A = X1^2
    final BLSFq2T A = this.X.square();
    // B = Y1^2
    final BLSFq2T B = this.Y.square();
    // C = B^2
    final BLSFq2T C = B.square();
    // D = 2 * ((X1 + B)^2 - A - C)
    BLSFq2T D = this.X.add(B).square().sub(A).sub(C);
    D = D.add(D);
    // E = 3 * A
    final BLSFq2T E = A.add(A).add(A);
    // F = E^2
    final BLSFq2T F = E.square();
    // X3 = F - 2 D
    final BLSFq2T X3 = F.sub(D.add(D));
    // Y3 = E * (D - X3) - 8 * C
    BLSFq2T eightC = C.add(C);
    eightC = eightC.add(eightC);
    eightC = eightC.add(eightC);
    final BLSFq2T Y3 = E.mul(D.sub(X3)).sub(eightC);
    // Z3 = 2 * Y1 * Z1
    final BLSFq2T Y1Z1 = this.Y.mul(this.Z);
    final BLSFq2T Z3 = Y1Z1.add(Y1Z1);

    return this.construct(X3, Y3, Z3);
  }

  public boolean isZero() {
    return this.Z.isZero();
  }

  public boolean isSpecial() {
    return isZero() || this.Z.isOne();
  }

  public boolean isOne() {
    return this.X.isOne() && this.Y.isOne() && this.Z.isOne();
  }

  public BLSG2T zero() {
    return this.G2Parameters.ZERO();
  }

  public BLSG2T one() {
    return this.G2Parameters.ONE();
  }

  public BLSG2T random(final Long seed, final byte[] secureSeed) {
    return this.one().mul(this.G2Parameters.oneFr().random(seed, secureSeed).toBigInteger());
  }

  public BLSG2T negate() {
    return this.construct(this.X, this.Y.negate(), this.Z);
  }

  public void setX(final BLSFq2T X) {
    this.X = X;
  }

  public void setY(final BLSFq2T Y) {
    this.Y = Y;
  }

  public void setZ(final BLSFq2T Z) {
    this.Z = Z;
  }

  public BLSG2T toAffineCoordinates() {
    if (this.isZero()) {
      return this.construct(this.X.zero(), this.Y.one(), this.Z.zero());
    } else {
      final BLSFq2T ZInverse = this.Z.inverse();
      final BLSFq2T Z2Inverse = ZInverse.square();
      final BLSFq2T Z3Inverse = Z2Inverse.mul(ZInverse);
      return this.construct(this.X.mul(Z2Inverse), this.Y.mul(Z3Inverse), this.Z.one());
    }
  }

  public int bitSize() {
    return Math.max(this.X.bitSize(), Math.max(this.Y.bitSize(), this.Z.bitSize()));
  }

  public ArrayList<Integer> fixedBaseWindowTable() {
    return this.G2Parameters.fixedBaseWindowTable();
  }

  public String toString() {
    if (this.isZero()) {
      return "0";
    }

    return this.X.toString() + ", " + this.Y.toString() + ", " + this.Z.toString();
  }

  public boolean equals(final BLSG2T other) {
    if (this.isZero()) {
      return other.isZero();
    }

    if (other.isZero()) {
      return false;
    }

    // Now neither is O.

    // using Jacobian coordinates so:
    // (X1:Y1:Z1) = (X2:Y2:Z2)
    // iff
    // X1/Z1^2 == X2/Z2^2 and Y1/Z1^3 == Y2/Z2^3
    // iff
    // X1 * Z2^2 == X2 * Z1^2 and Y1 * Z2^3 == Y2 * Z1^3

    final BLSFq2T Z1Squared = this.Z.square();
    final BLSFq2T Z2Squared = other.Z.square();

    if (!this.X.mul(Z2Squared).equals(other.X.mul(Z1Squared))) {
      return false;
    }

    final BLSFq2T Z1Cubed = this.Z.mul(Z1Squared);
    final BLSFq2T Z2Cubed = other.Z.mul(Z2Squared);

    return this.Y.mul(Z2Cubed).equals(other.Y.mul(Z1Cubed));
  }
}
