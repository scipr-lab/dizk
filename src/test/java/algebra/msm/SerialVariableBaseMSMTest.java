/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.msm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.groups.AdditiveIntegerGroup;
import algebra.groups.integergroupparameters.LargeAdditiveIntegerGroupParameters;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scala.Tuple2;

public class SerialVariableBaseMSMTest implements Serializable {
  private LargeAdditiveIntegerGroupParameters GroupParameters;

  @BeforeEach
  public void setUp() {
    GroupParameters = new LargeAdditiveIntegerGroupParameters();
  }

  @Test
  public void NaiveMSMTest() {
    ArrayList<BigInteger> scalars = new ArrayList<>(4);
    scalars.add(new BigInteger("3"));
    scalars.add(new BigInteger("11"));
    scalars.add(new BigInteger("2"));
    scalars.add(new BigInteger("8"));

    ArrayList<AdditiveIntegerGroup> bases = new ArrayList<>(4);
    bases.add(new AdditiveIntegerGroup(5, GroupParameters));
    bases.add(new AdditiveIntegerGroup(2, GroupParameters));
    bases.add(new AdditiveIntegerGroup(7, GroupParameters));
    bases.add(new AdditiveIntegerGroup(3, GroupParameters));

    AdditiveIntegerGroup result = NaiveMSM.variableBaseMSM(scalars, bases);
    AdditiveIntegerGroup answer = new AdditiveIntegerGroup(75, GroupParameters);
    System.out.println(result.toString() + " == " + answer.toString());
    assertTrue(result.equals(answer));
  }

  @Test
  public void SortedMSMTest() {
    ArrayList<Tuple2<BigInteger, AdditiveIntegerGroup>> input = new ArrayList<>(4);
    input.add(new Tuple2<>(new BigInteger("3"), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(new Tuple2<>(new BigInteger("11"), new AdditiveIntegerGroup(2, GroupParameters)));
    input.add(new Tuple2<>(new BigInteger("2"), new AdditiveIntegerGroup(7, GroupParameters)));
    input.add(new Tuple2<>(new BigInteger("8"), new AdditiveIntegerGroup(3, GroupParameters)));

    AdditiveIntegerGroup result = VariableBaseMSM.sortedMSM(input);
    AdditiveIntegerGroup answer = new AdditiveIntegerGroup(75, GroupParameters);
    System.out.println(result.toString() + " == " + answer.toString());
    assertTrue(result.equals(answer));
  }

  @Test
  public void BosCosterMSMTest() {
    ArrayList<Tuple2<BigInteger, AdditiveIntegerGroup>> input = new ArrayList<>(4);
    input.add(new Tuple2<>(new BigInteger("3"), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(new Tuple2<>(new BigInteger("11"), new AdditiveIntegerGroup(2, GroupParameters)));
    input.add(new Tuple2<>(new BigInteger("2"), new AdditiveIntegerGroup(7, GroupParameters)));
    input.add(new Tuple2<>(new BigInteger("8"), new AdditiveIntegerGroup(3, GroupParameters)));

    AdditiveIntegerGroup result = VariableBaseMSM.bosCosterMSM(input);
    AdditiveIntegerGroup answer = new AdditiveIntegerGroup(75, GroupParameters);
    System.out.println(result.toString() + " == " + answer.toString());
    assertTrue(result.equals(answer));
  }
}
