package io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377BinaryReader;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G1;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_naehrig.bn254a.BN254aBinaryReader;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import relations.objects.LinearCombination;
import relations.objects.LinearTerm;
import relations.objects.R1CSConstraint;
import relations.objects.R1CSConstraints;
import relations.r1cs.R1CSRelation;

public class R1CSReaderTest {

  /**
   * Create the equivalent relation to libzeth/tests/snarks/groth16/groth16_snark_test.cpp to test
   * serialization. Accessible by other tests.
   */
  public static <FrT extends AbstractFieldElementExpanded<FrT>>
      R1CSRelation<FrT> buildExpectedRelation(final FrT oneFr) {

    // (2*x1 + 3*w1) * (4*x2 + 5*w2) = 6*w3 + 7*x1,
    LinearCombination<FrT> c1_A = new LinearCombination<FrT>();
    c1_A.add(new LinearTerm<FrT>(1, oneFr.construct(2))); // 2*x1
    c1_A.add(new LinearTerm<FrT>(3, oneFr.construct(3))); // 3*w1
    LinearCombination<FrT> c1_B = new LinearCombination<FrT>();
    c1_B.add(new LinearTerm<FrT>(2, oneFr.construct(4))); // 4*x2
    c1_B.add(new LinearTerm<FrT>(4, oneFr.construct(5))); // 5*w2
    LinearCombination<FrT> c1_C = new LinearCombination<FrT>();
    c1_C.add(new LinearTerm<FrT>(5, oneFr.construct(6))); // 6*w3
    c1_C.add(new LinearTerm<FrT>(1, oneFr.construct(7))); // 7*x1

    // (7*x2 + 6*w3) * (5*x1 + 4*w1) = 3*w2 + 2*x2,
    LinearCombination<FrT> c2_A = new LinearCombination<FrT>();
    c2_A.add(new LinearTerm<FrT>(2, oneFr.construct(7))); // 7*x2
    c2_A.add(new LinearTerm<FrT>(5, oneFr.construct(6))); // 6*w3
    LinearCombination<FrT> c2_B = new LinearCombination<FrT>();
    c2_B.add(new LinearTerm<FrT>(1, oneFr.construct(5))); // 5*x1
    c2_B.add(new LinearTerm<FrT>(3, oneFr.construct(4))); // 4*w1
    LinearCombination<FrT> c2_C = new LinearCombination<FrT>();
    c2_C.add(new LinearTerm<FrT>(4, oneFr.construct(3))); // 3*w2
    c2_C.add(new LinearTerm<FrT>(2, oneFr.construct(2))); // 2*x2

    R1CSConstraints<FrT> expectConstraints = new R1CSConstraints<FrT>();
    expectConstraints.add(new R1CSConstraint<FrT>(c1_A, c1_B, c1_C));
    expectConstraints.add(new R1CSConstraint<FrT>(c2_A, c2_B, c2_C));

    return new R1CSRelation<FrT>(expectConstraints, 2, 3);
  }

  <
          FrT extends AbstractFieldElementExpanded<FrT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>>
      void testAgainstData(final R1CSReader<FrT, G1T, G2T> reader, final FrT oneFr)
          throws IOException {
    final R1CSRelation<FrT> expectRelation = buildExpectedRelation(oneFr);

    // Load relation and compare to expected.
    final R1CSRelation<FrT> relation = reader.readR1CS();

    assertEquals(expectRelation, relation);
  }

  @Test
  void testR1CReaderBN254a() throws IOException {
    final var in = openTestFile("r1cs_alt-bn128.bin");
    final var reader = new R1CSReader<BN254aFr, BN254aG1, BN254aG2>(new BN254aBinaryReader(in));
    testAgainstData(reader, BN254aFr.ONE);
  }

  @Test
  void testR1CReaderBLS13_377() throws IOException {
    final var in = openTestFile("r1cs_bls12-377.bin");
    final var reader = new R1CSReader<BLS12_377Fr, BLS12_377G1, BLS12_377G2>(new BLS12_377BinaryReader(in));
    testAgainstData(reader, BLS12_377Fr.ONE);
  }

  protected InputStream openTestFile(final String filename) {
    return getClass().getClassLoader().getResourceAsStream(filename);
  }
}
