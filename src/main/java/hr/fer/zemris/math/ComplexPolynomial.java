package hr.fer.zemris.math;

/**
 * Modelira polinom nad kompleksnim brojevima prema predlošku
 * f(z) oblika zn*z^n+z^(n-1)*zn-1+...+z^2*z2+z^1*z+z0
 * 
 * @author Marko Brlek
 *
 */
public class ComplexPolynomial {

    private Complex[] factors;

    /**
     * Konstruktor prima koeficijente polinoma od z0 do zn.
     * 
     * @param factors koeficijenti polinoma
     */
    public ComplexPolynomial(Complex... factors) {
        this.factors = factors;
    }

    /**
     * Getter za faktore polinoma.
     * 
     * @return faktori polinoma
     */
    public Complex[] getFactors() {
        return factors;
    }

    /**
     * Računa red polinoma.
     * 
     * @return red polinoma
     */
    public short order() {
        return (short) (factors.length - 1);
    }

    /**
     * Množi polinom s predanim polinomom.
     * 
     * @param p polinom s kojim se množi
     * @return rezultat množenja
     */
    public ComplexPolynomial multiply(ComplexPolynomial p) {
        Complex[] mnozeniFaktori = new Complex[factors.length + p.factors.length - 1];

        for (int i = 0; i < mnozeniFaktori.length; i++) {
            mnozeniFaktori[i] = Complex.ZERO;
            // inicijalno postavlja sve faktore na nulu
        }
        for (int i = 0; i < factors.length; i++) {
            for (int j = 0; j < p.factors.length; j++) {
                // mnozi svaki sa svakim i dodaje na odgovarajuce mjesto
                mnozeniFaktori[i + j] = mnozeniFaktori[i + j].add(factors[i].mul(p.factors[j]));
            }
        }
        return new ComplexPolynomial(mnozeniFaktori);
    }

    /**
     * Računa prvu derivaciju polinoma.
     * 
     * @return prva derivacija polinoma
     */
    public ComplexPolynomial derive() {
        Complex[] deriviraniFaktori = new Complex[factors.length - 1];

        for (int i = 0; i < deriviraniFaktori.length; i++) {
            deriviraniFaktori[i] = factors[i + 1].mul(new Complex(i + 1, 0));
            // 3*x^2 -> 3*2*x^(2-1) -> 6x
        }
        return new ComplexPolynomial(deriviraniFaktori);
    }

    /**
     * Računa vrijednost polinoma u točki z.
     * f(z) = zn*z^n+z^(n-1)*zn-1+...+z^2*z2+z^1*z+z0
     * 
     * @param z točka u kojoj se računa vrijednost polinoma
     * @return vrijednost polinoma u točki z
     */
    public Complex apply(Complex z) {
        Complex result = factors[0];
        for (int i = 1; i < factors.length; i++) {
            result = result.add((z.power(i)).mul(factors[i]));
        }
        return result;
    }

    /**
     * Pretvara polinom u string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = factors.length - 1; i >= 0; i--) {
            if (factors[i].equals(Complex.ZERO)) {
                continue;
            }
            if (i != factors.length - 1) {
                sb.append("+");
            }
            sb.append("(" + factors[i] + ")");

            if (i != 0) {
                sb.append("*z");
                sb.append("^" + i);
            }
        }
        return sb.toString();
    }
}
