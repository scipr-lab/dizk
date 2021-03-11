package io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377BinaryReader;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G1;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G2;
import algebra.curves.barreto_naehrig.bn254a.BN254aBinaryReader;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import relations.objects.Assignment;
import scala.Tuple2;

public class AssignmentReaderTest extends TestWithData {

  protected <
          FrT extends AbstractFieldElementExpanded<FrT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>>
      void testReaderAgainstData(FrT one, AssignmentReader<FrT, G1T, G2T> assignmentReader)
          throws IOException {

    // Test data has the form [15, -15, 16, -16, 17, -17].

    final Tuple2<Assignment<FrT>, Assignment<FrT>> primAux =
        assignmentReader.readPrimaryAuxiliary(1);
    final Assignment<FrT> primary = primAux._1;
    final Assignment<FrT> auxiliary = primAux._2;

    // Check primary input values
    assertEquals(1, primary.size());
    assertEquals(one.construct(15), primary.get(0));

    // Check auxiliary input values
    assertEquals(5, auxiliary.size());
    assertEquals(one.construct(-15), auxiliary.get(0));
    assertEquals(one.construct(16), auxiliary.get(1));
    assertEquals(one.construct(-16), auxiliary.get(2));
    assertEquals(one.construct(17), auxiliary.get(3));
    assertEquals(one.construct(-17), auxiliary.get(4));
  }

  protected <
          FrT extends AbstractFieldElementExpanded<FrT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>>
      void testReaderAgainstDataRDD(
          final FrT one, final AssignmentReader<FrT, G1T, G2T> assignmentReader) {
    assertTrue(true, "unimplemented");
  }

  @Test
  public void testAssignmentReaderBN254a() throws IOException {
    final var in = openTestFile("assignment_alt-bn128.bin");
    final var binReader = new BN254aBinaryReader(in);
    testReaderAgainstData(
        BN254aFr.ONE, new AssignmentReader<BN254aFr, BN254aG1, BN254aG2>(binReader));
  }

  @Test
  public void testAssignmentReaderBLS12_377() throws IOException {
    final var in = openTestFile("assignment_bls12-377.bin");
    final var binReader = new BLS12_377BinaryReader(in);
    testReaderAgainstData(
        BLS12_377Fr.ONE, new AssignmentReader<BLS12_377Fr, BLS12_377G1, BLS12_377G2>(binReader));
  }

  // @Test
  // public void BN254aBinaryAssignmentReaderRDDTest() throws IOException {
  //   final InputStream in = openTestFile("ec_test_data_alt-bn128.bin");
  //   final BN254aBinaryReader binReader = new BN254aBinaryReader(in);

  //   testReaderAgainstDataRDD(
  //       BN254aFr.ONE,
  //       new BinaryAssignmentReader<BN254aFr, BN254aG1, BN254aG2>(binReader));
  // }
}
