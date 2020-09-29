/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package common;

import algebra.fields.Fp;
import algebra.fields.fieldparameters.LargeFpParameters;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerialNaiveEvaluationTest implements Serializable {
    @Test
    public void SerialNaiveTest() {
        final LargeFpParameters FpParameters = new LargeFpParameters();

        final ArrayList<Fp> input = new ArrayList<>(4);
        input.add(new Fp(7, FpParameters));
        input.add(new Fp(2, FpParameters));
        input.add(new Fp(5, FpParameters));
        input.add(new Fp(3, FpParameters));

        final Fp x = new Fp(2, FpParameters);

        final Fp answer = new Fp(55, FpParameters);
        final Fp result = NaiveEvaluation.evaluatePolynomial(input, x);

        System.out.println(result + " == " + answer);
        assertTrue(result.equals(answer));
    }
}
