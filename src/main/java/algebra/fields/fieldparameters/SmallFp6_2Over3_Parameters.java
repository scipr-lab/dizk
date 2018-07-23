/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.fieldparameters;

import algebra.fields.Fp;
import algebra.fields.Fp6_2Over3;
import algebra.fields.abstractfieldparameters.AbstractFp6_2Over3_Parameters;

import java.io.Serializable;

public class SmallFp6_2Over3_Parameters extends AbstractFp6_2Over3_Parameters
        implements Serializable {
    private Fp6_2Over3 ZERO;
    private Fp6_2Over3 ONE;

    private Fp nonresidue;
    private Fp[] FrobeniusCoefficientsC1;

    private SmallFpParameters FpParameters;
    private SmallFp3Parameters Fp3Parameters;

    public SmallFp6_2Over3_Parameters() {
        FpParameters = new SmallFpParameters();
        Fp3Parameters = new SmallFp3Parameters();
    }

    public SmallFpParameters FpParameters() {
        return FpParameters;
    }

    public SmallFp3Parameters Fp3Parameters() {
        return Fp3Parameters;
    }

    public Fp6_2Over3 ZERO() {
        if (ZERO == null) {
            ZERO = new Fp6_2Over3(Fp3Parameters.ZERO(), Fp3Parameters.ZERO(), this);
        }

        return ZERO;
    }

    public Fp6_2Over3 ONE() {
        if (ONE == null) {
            ONE = new Fp6_2Over3(Fp3Parameters.ONE(), Fp3Parameters.ZERO(), this);
        }

        return ONE;
    }

    public Fp nonresidue() {
        if (nonresidue == null) {
            nonresidue = new Fp(6, FpParameters);
        }

        return nonresidue;
    }

    public Fp[] FrobeniusMapCoefficientsC1() {
        if (FrobeniusCoefficientsC1 == null) {
            FrobeniusCoefficientsC1 = new Fp[6];
            FrobeniusCoefficientsC1[0] = FpParameters.ONE();
            FrobeniusCoefficientsC1[1] = FpParameters.ONE();
            FrobeniusCoefficientsC1[2] = FpParameters.ONE();
            FrobeniusCoefficientsC1[3] = FpParameters.ONE();
            FrobeniusCoefficientsC1[4] = FpParameters.ONE();
            FrobeniusCoefficientsC1[5] = FpParameters.ONE();
        }

        return FrobeniusCoefficientsC1;
    }
}
