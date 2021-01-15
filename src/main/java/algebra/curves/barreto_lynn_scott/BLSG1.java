package algebra.curves.barreto_lynn_scott;

import algebra.curves.AbstractG1;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFr;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG1Parameters;
import java.util.ArrayList;

public abstract class BLSG1<
        BLSFrT extends BLSFr<BLSFrT>,
        BLSFqT extends BLSFq<BLSFqT>,
        BLSG1T extends BLSG1<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>,
        BLSG1ParametersT extends AbstractBLSG1Parameters<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>>
    extends AbstractG1<BLSG1T> {
  public final BLSG1ParametersT G1Parameters;
  protected final BLSFqT X;
  protected final BLSFqT Y;
  public final BLSFqT Z;

  public BLSG1(
      final BLSFqT X, final BLSFqT Y, final BLSFqT Z, final BLSG1ParametersT G1Parameters) {
    this.X = X;
    this.Y = Y;
    this.Z = Z;
    this.G1Parameters = G1Parameters;
  }

  public abstract BLSG1T construct(BLSFqT X, BLSFqT Y, BLSFqT Z);

  public BLSG1T add(final BLSG1T other) {
    assert (other != null);

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

    final BLSFqT Z1Z1 = this.Z.square();
    final BLSFqT Z2Z2 = other.Z.square();

    final BLSFqT U1 = this.X.mul(Z2Z2);
    final BLSFqT U2 = other.X.mul(Z1Z1);

    final BLSFqT Z1_cubed = this.Z.mul(Z1Z1);
    final BLSFqT Z2_cubed = other.Z.mul(Z2Z2);

    // S1 = Y1 * Z2 * Z2Z2
    final BLSFqT S1 = this.Y.mul(Z2_cubed);
    // S2 = Y2 * Z1 * Z1Z1
    final BLSFqT S2 = other.Y.mul(Z1_cubed);

    if (U1.equals(U2) && S1.equals(S2)) {
      // Doubling case
      // Nothing above can be reused
      return dbl();
    }

    // Rest of the add case
    // H = U2-U1
    final BLSFqT H = U2.sub(U1);
    // I = (2 * H)^2
    final BLSFqT I = H.add(H).square();
    // J = H * I
    final BLSFqT J = H.mul(I);
    // r = 2 * (S2-S1)
    final BLSFqT S2MinusS1 = S2.sub(S1);
    final BLSFqT r = S2MinusS1.add(S2MinusS1);
    // V = U1 * I
    final BLSFqT V = U1.mul(I);
    // X3 = r^2 - J - 2 * V
    final BLSFqT X3 = r.square().sub(J).sub(V.add(V));
    final BLSFqT S1_J = S1.mul(J);
    // Y3 = r * (V-X3)-2 * S1_J
    final BLSFqT Y3 = r.mul(V.sub(X3)).sub(S1_J.add(S1_J));
    // Z3 = ((Z1+Z2)^2-Z1Z1-Z2Z2) * H
    final BLSFqT Z3 = this.Z.add(other.Z).square().sub(Z1Z1).sub(Z2Z2).mul(H);

    return this.construct(X3, Y3, Z3);
  }

  public BLSG1T sub(final BLSG1T other) {
    return this.add(other.negate());
  }

  public boolean isZero() {
    return this.Z.isZero();
  }

  public boolean isOne() {
    return this.X.equals(this.one().X)
        && this.Y.equals(this.one().Y)
        && this.Z.equals(this.one().Z);
  }

  public boolean isSpecial() {
    return isZero() || isOne();
  }

  public BLSG1T zero() {
    return this.G1Parameters.ZERO();
  }

  public BLSG1T one() {
    return this.G1Parameters.ONE();
  }

  public BLSG1T random(final Long seed, final byte[] secureSeed) {
    return this.one().mul(this.G1Parameters.oneFr().random(seed, secureSeed));
  }

  public BLSG1T negate() {
    return this.construct(this.X, this.Y.negate(), this.Z);
  }

  public BLSG1T dbl() {
    // Handle point at infinity
    if (this.isZero()) {
      return this.self();
    }

    // No need to handle points of order 2,4
    // (they cannot exist in a modulus-order subgroup)

    // NOTE: does not handle O and pts of order 2,4
    // http://www.hyperelliptic.org/EFD/g1p/auto-shortw-jacobian-0.html#doubling-dbl-2009-l

    // A = X1^2
    final BLSFqT A = this.X.square();
    // B = Y1^2
    final BLSFqT B = this.Y.square();
    // C = B^2
    final BLSFqT C = B.square();
    // D = 2 * ((X1 + B)^2 - A - C)
    BLSFqT D = this.X.add(B).square().sub(A).sub(C);
    D = D.add(D);
    // E = 3 * A
    final BLSFqT E = A.add(A).add(A);
    // F = E^2
    final BLSFqT F = E.square();
    // X3 = F - 2 D
    final BLSFqT X3 = F.sub(D.add(D));
    // Y3 = E * (D - X3) - 8 * C
    BLSFqT eightC = C.add(C);
    eightC = eightC.add(eightC);
    eightC = eightC.add(eightC);
    final BLSFqT Y3 = E.mul(D.sub(X3)).sub(eightC);
    // Z3 = 2 * Y1 * Z1
    final BLSFqT Y1Z1 = this.Y.mul(this.Z);
    final BLSFqT Z3 = Y1Z1.add(Y1Z1);

    return this.construct(X3, Y3, Z3);
  }

  public BLSG1T toAffineCoordinates() {
    if (this.isZero()) {
      return this.construct(this.X.zero(), this.Y.one(), this.Z.zero());
    } else {
      BLSFqT ZInverse = this.Z.inverse();
      BLSFqT Z2Inverse = ZInverse.square();
      BLSFqT Z3Inverse = Z2Inverse.mul(ZInverse);
      return this.construct(this.X.mul(Z2Inverse), this.Y.mul(Z3Inverse), this.Z.one());
    }
  }

  public int bitSize() {
    return Math.max(this.X.bitSize(), Math.max(this.Y.bitSize(), this.Z.bitSize()));
  }

  public ArrayList<Integer> fixedBaseWindowTable() {
    return this.G1Parameters.fixedBaseWindowTable();
  }

  public String toString() {
    if (isZero()) {
      return "0";
    }

    return this.X.toString() + ", " + this.Y.toString() + ", " + this.Z.toString();
  }

  public boolean equals(final BLSG1T other) {
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

    final BLSFqT Z1_squared = this.Z.square();
    final BLSFqT Z2_squared = other.Z.square();

    if (!this.X.mul(Z2_squared).equals(other.X.mul(Z1_squared))) {
      return false;
    }

    final BLSFqT Z1_cubed = this.Z.mul(Z1_squared);
    final BLSFqT Z2_cubed = other.Z.mul(Z2_squared);

    if (!this.Y.mul(Z2_cubed).equals(other.Y.mul(Z1_cubed))) {
      return false;
    }

    return true;
  }
}
