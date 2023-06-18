package hr.fer.zemris.math;

/**
 * Modelira polinom nad kompleksnim brojevima.
 * Radi se o polinomu f(z) oblika z0*(z-z1)*(z-z2)*...*(z-zn), gdje su z1 do zn
 * njegove nultočke a z0 konstanta
 * 
 * @author Marko Brlek
 *
 */
public class ComplexRootedPolynomial {

    private Complex constant;
    private Complex[] roots;

    /**
     * Konstruktor prima konstantu i nultočke polinoma.
     * 
     * @param constant konstanta
     * @param roots    nultočke
     */
    public ComplexRootedPolynomial(Complex constant, Complex... roots) {
        this.constant = constant;
        this.roots = roots;
    }

    /**
     * Getter za konstantu.
     * 
     * @return konstanta
     */
    public Complex getConstant() {
        return constant;
    }

    /**
     * Getter za nultočke.
     * 
     * @return nultočke
     */
    public Complex[] getRoots() {
        return roots;
    }

    /**
     * Prima neki konkretan z i računa koju vrijednost ima polinom u toj točki.
     * 
     * @param z točka u kojoj računamo vrijednost polinoma
     * @return vrijednost polinoma u toj točki
     */
    public Complex apply(Complex z) {
        Complex result = constant;
        for (Complex root : roots) {
            result = result.mul(z.sub(root));
        }
        return result;
    }

    /**
     * Pretvara polinom u obliku z0*(z-z1)*(z-z2)*...*(z-zn) u oblik z0 + z1*z + ...
     * 
     * @return
     */
    public ComplexPolynomial toComplexPolynom() {
        ComplexPolynomial result = new ComplexPolynomial(constant);
        for (Complex root : roots) {
            result = result.multiply(new ComplexPolynomial(root.negate(), Complex.ONE)); // obrnuti poredak, znaci z-zn
        }
        return result;
    }

    /**
     * Pretvara instancu razreda ComplexRootedPolynomial u obliku Stringa.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(" + constant + ")");
        for (Complex root : roots) {
            sb.append("*(z-(").append(root).append("))");
        }
        return sb.toString();
    }

    /**
     * Traži index najbliže nultočke od zadane točke z kojoj je udaljenost manja od
     * tresholda.
     * 
     * @param z        točka od koje tražimo najbližu nultočku
     * @param treshold udaljenost od koje se više ne traži
     * @return index najbliže nultočke
     */
    public int indexOfClosestRootFor(Complex z, double treshold) {
        double najmanja = Double.MAX_VALUE;
        int index = -1;

        double rezultat;
        for (int i = 0; i < roots.length; i++) {
            rezultat = z.sub(roots[i]).module();
            if (rezultat < treshold && rezultat < najmanja) {
                najmanja = rezultat;
                index = i;
            }
        }
        return index;
    }
}
