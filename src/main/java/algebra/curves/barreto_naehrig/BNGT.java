/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig;

import algebra.curves.AbstractGT;
import algebra.curves.barreto_naehrig.BNFields.BNFq;
import algebra.curves.barreto_naehrig.BNFields.BNFq12;
import algebra.curves.barreto_naehrig.BNFields.BNFq2;
import algebra.curves.barreto_naehrig.BNFields.BNFq6;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;

import java.math.BigInteger;

public abstract class BNGT<
        BNFqT extends BNFq<BNFqT>,
        BNFq2T extends BNFq2<BNFqT, BNFq2T>,
        BNFq6T extends BNFq6<BNFqT, BNFq2T, BNFq6T>,
        BNFq12T extends BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
        BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
        BNGTParametersT extends AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>>
        extends AbstractGT<BNGTT> {
    public final BNGTParametersT GTParameters;
    public final BNFq12T element;

    public BNGT(final BNFq12T value, final BNGTParametersT GTParameters) {
        this.element = value;
        this.GTParameters = GTParameters;
    }

    public abstract BNGTT construct(final BNFq12T element);

    public BNGTT add(final BNGTT that) {
        return this.construct(this.element.mul(that.element));
    }

    public BNGTT mul(final BigInteger that) {
        return this.construct(this.element.pow(that));
    }

    public BNGTT one() {
        return this.GTParameters.ONE();
    }

    public BNGTT negate() {
        return this.construct(this.element.unitaryInverse());
    }

    public boolean equals(final BNGTT that) {
        return this.element.equals(that.element);
    }

    public String toString() {
        return this.element.toString();
    }
}
