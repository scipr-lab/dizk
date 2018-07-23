/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b;

import algebra.curves.barreto_naehrig.BNG2;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq2;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;

public class BN254bG2
        extends BNG2<BN254bFr, BN254bFq, BN254bFq2, BN254bG2, BN254bG2Parameters> {

    private static final BN254bG2Parameters G2Parameters = new BN254bG2Parameters();

    public BN254bG2(final BN254bFq2 X, final BN254bFq2 Y, final BN254bFq2 Z) {
        super(X, Y, Z, G2Parameters);
    }

    public BN254bG2 self() {
        return this;
    }

    public BN254bG2 construct(final BN254bFq2 X, final BN254bFq2 Y, final BN254bFq2 Z) {
        return new BN254bG2(X, Y, Z);
    }
}
