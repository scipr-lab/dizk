/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.fieldparameters;

import algebra.fields.Fp;
import algebra.fields.Fp3;
import algebra.fields.abstractfieldparameters.AbstractFp3Parameters;

import java.io.Serializable;

public class SmallFp3Parameters extends AbstractFp3Parameters implements Serializable {
    private Fp3 ZERO;
    private Fp3 ONE;

    private Fp nonresidue;
    private Fp[] FrobeniusCoefficientsC1;
    private Fp[] FrobeniusCoefficientsC2;

    private SmallFpParameters FpParameters;

    public SmallFp3Parameters() {
        FpParameters = new SmallFpParameters();
    }

    public SmallFpParameters FpParameters() {
        return FpParameters;
    }

    public Fp3 ZERO() {
        if (ZERO == null) {
            ZERO = new Fp3(FpParameters.ZERO(), FpParameters.ZERO(), FpParameters.ZERO(), this);
        }

        return ZERO;
    }

    public Fp3 ONE() {
        if (ONE == null) {
            ONE = new Fp3(FpParameters.ONE(), FpParameters.ZERO(), FpParameters.ZERO(), this);
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
            FrobeniusCoefficientsC1 = new Fp[3];
            FrobeniusCoefficientsC1[0] = FpParameters.ONE();
            FrobeniusCoefficientsC1[1] = FpParameters.ONE();
            FrobeniusCoefficientsC1[2] = FpParameters.ONE();
        }

        return FrobeniusCoefficientsC1;
    }

    public Fp[] FrobeniusMapCoefficientsC2() {
        if (FrobeniusCoefficientsC2 == null) {
            FrobeniusCoefficientsC2 = new Fp[3];
            FrobeniusCoefficientsC2[0] = FpParameters.ONE();
            FrobeniusCoefficientsC2[1] = FpParameters.ONE();
            FrobeniusCoefficientsC2[2] = FpParameters.ONE();
        }

        return FrobeniusCoefficientsC2;
    }
}
