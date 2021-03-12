package zk_proof_systems.zkSNARK.grothBGM17;

import static org.junit.jupiter.api.Assertions.assertEquals;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377BinaryReader;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G1;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G2;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G2Parameters;
import algebra.curves.barreto_naehrig.bn254a.BN254aBinaryReader;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import algebra.fields.AbstractFieldElementExpanded;
import io.R1CSReaderTest;
import io.TestWithData;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import scala.Tuple2;
import zk_proof_systems.zkSNARK.grothBGM17.objects.ProvingKey;
import zk_proof_systems.zkSNARK.grothBGM17.objects.VerificationKey;

/** Test readers for grothBGM17-specific objects. */
public class ZKSnarkObjectReaderTest extends TestWithData {
  public <
          FrT extends AbstractFieldElementExpanded<FrT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>>
      void testReaderAgainstProvingKeyData(
          final ZKSnarkObjectReader<FrT, G1T, G2T> zkSnarkObjectReader,
          final FrT oneFr,
          final G1T oneG1,
          final G2T oneG2)
          throws IOException {

    // See zeth project file
    // libzeth/tests/snarks/groth16/groth16_snark_test.cpp, which constructs the
    // test data.

    var r1cs = R1CSReaderTest.buildExpectedRelation(oneFr);
    final var expectQueryA =
        new ArrayList<G1T>() {
          {
            add(oneG1.mul(oneFr.construct(7)));
            add(oneG1.mul(oneFr.construct(-3)));
            add(oneG1.mul(oneFr.construct(8)));
          }
        };

    final var expectQueryB =
        new ArrayList<Tuple2<G1T, G2T>>() {
          {
            add(new Tuple2<G1T, G2T>(oneG1.mul(oneFr.construct(9)), oneG2.mul(oneFr.construct(9))));
            add(
                new Tuple2<G1T, G2T>(
                    oneG1.mul(oneFr.construct(-9)), oneG2.mul(oneFr.construct(-9))));
            add(
                new Tuple2<G1T, G2T>(
                    oneG1.mul(oneFr.construct(10)), oneG2.mul(oneFr.construct(10))));
          }
        };

    final var expectQueryH =
        new ArrayList<G1T>() {
          {
            add(oneG1.mul(oneFr.construct(11)));
            add(oneG1.mul(oneFr.construct(-11)));
            add(oneG1.mul(oneFr.construct(12)));
          }
        };

    final var expectQueryL =
        new ArrayList<G1T>(3) {
          {
            add(oneG1.mul(oneFr.construct(13)));
            add(oneG1.mul(oneFr.construct(-13)));
            add(oneG1.mul(oneFr.construct(14)));
          }
        };
    assertEquals(3, expectQueryL.size());
    System.out.println("expectQueryL: " + String.valueOf(expectQueryL));

    final var expectProvingKey =
        new ProvingKey<FrT, G1T, G2T>(
            oneG1,
            oneG1.negate(),
            oneG2.negate(),
            oneG1.mul(oneFr.construct(-2)),
            oneG2.mul(oneFr.construct(-2)),
            expectQueryL,
            expectQueryA,
            expectQueryB,
            expectQueryH,
            r1cs);

    // Read proving key and compare to expected.
    final var provingKey = zkSnarkObjectReader.readProvingKey();
    assertEquals(expectProvingKey, provingKey);
  }

  public <
          FrT extends AbstractFieldElementExpanded<FrT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>>
      void testReaderAgainstVerificationKeyData(
          final ZKSnarkObjectReader<FrT, G1T, G2T> zkSnarkObjectReader,
          final FrT oneFr,
          final G1T oneG1,
          final G2T oneG2)
          throws IOException {

    // See zeth project file
    // libzeth/tests/snarks/groth16/groth16_snark_test.cpp, which constructs the
    // test data.

    final var expectVerificationKey =
        new VerificationKey<G1T, G2T>(
            oneG1.mul(oneFr.construct(21)),
            oneG2.mul(oneFr.construct(-21)),
            oneG2.mul(oneFr.construct(22)),
            new ArrayList<G1T>(3) {
              {
                add(oneG1.mul(oneFr.construct(13)));
                add(oneG1.mul(oneFr.construct(-13)));
                add(oneG1.mul(oneFr.construct(14)));
              }
            });

    final var verificationKey = zkSnarkObjectReader.readVerificationKey();
    assertEquals(expectVerificationKey, verificationKey);
  }

  @Test
  public void testReadProvingKeyALT254a() throws IOException {
    final var in = openTestFile("groth16_proving_key_alt-bn128.bin");
    final var binReader = new BN254aBinaryReader(in);
    testReaderAgainstProvingKeyData(
        new ZKSnarkObjectReader<BN254aFr, BN254aG1, BN254aG2>(binReader),
        BN254aFr.ONE,
        BN254aG1Parameters.ONE,
        BN254aG2Parameters.ONE);
  }

  @Test
  public void testReadProvingKeyBLS12_377() throws IOException {
    final var in = openTestFile("groth16_proving_key_bls12-377.bin");
    final var binReader = new BLS12_377BinaryReader(in);
    testReaderAgainstProvingKeyData(
        new ZKSnarkObjectReader<BLS12_377Fr, BLS12_377G1, BLS12_377G2>(binReader),
        BLS12_377Fr.ONE,
        BLS12_377G1Parameters.ONE,
        BLS12_377G2Parameters.ONE);
  }

  @Test
  public void testReadVerificationKeyALT254a() throws IOException {
    final var in = openTestFile("groth16_verification_key_alt-bn128.bin");
    final var binReader = new BN254aBinaryReader(in);
    testReaderAgainstVerificationKeyData(
        new ZKSnarkObjectReader<BN254aFr, BN254aG1, BN254aG2>(binReader),
        BN254aFr.ONE,
        BN254aG1Parameters.ONE,
        BN254aG2Parameters.ONE);
  }

  @Test
  public void testReadVerificationKeyBLS12_377() throws IOException {
    final var in = openTestFile("groth16_verification_key_bls12-377.bin");
    final var binReader = new BLS12_377BinaryReader(in);
    testReaderAgainstVerificationKeyData(
        new ZKSnarkObjectReader<BLS12_377Fr, BLS12_377G1, BLS12_377G2>(binReader),
        BLS12_377Fr.ONE,
        BLS12_377G1Parameters.ONE,
        BLS12_377G2Parameters.ONE);
  }
}
