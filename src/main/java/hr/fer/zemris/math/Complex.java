package hr.fer.zemris.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelira kompleksni broj. Sadrži metode za računanje aritmetičkih operacija.
 * 
 * @author Marko Brlek
 *
 */
public class Complex {

    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);
    public static final Complex ONE_NEG = new Complex(-1, 0);
    public static final Complex IM = new Complex(0, 1);
    public static final Complex IM_NEG = new Complex(0, -1);

    private double real;
    private double imaginary;

    /**
     * Prazan konstruktor stvara kompleksni broj 0+0i.
     */
    public Complex() {
        this(0, 0);
    }

    /**
     * Konstruktor stvara kompleksni broj real+imaginaryi.
     * 
     * @param real      Realni dio kompleksnog broja.
     * @param imaginary Imaginarni dio kompleksnog broja.
     */
    public Complex(double re, double im) {
        this.real = re;
        this.imaginary = im;
    }

    /**
     * Getter za realni dio kompleksnog broja.
     * 
     * @return Realni dio kompleksnog broja.
     */
    public double getReal() {
        return real;
    }

    /**
     * Getter za imaginarni dio kompleksnog broja.
     * 
     * @return Imaginarni dio kompleksnog broja.
     */
    public double getImaginary() {
        return imaginary;
    }

    /**
     * Računa udaljenost kompleksnog broja od ishodišta.
     * 
     * @return Udaljenost kompleksnog broja od ishodišta.
     */
    public double module() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    /**
     * Množi instancu ovog kompleksnog broja s kompleksnim brojem c.
     * Koristi formulu (x+yi)(u+vi) = (xu-yv) + (xv+yu)i
     * 
     * @param c Kompleksni broj s kojim se množi instanca ovog kompleksnog broja.
     * @return Rezultat množenja.
     */
    public Complex mul(Complex c) {
        return new Complex(real * c.real - imaginary * c.imaginary, real * c.imaginary + imaginary * c.real);
    }

    /**
     * Dijeli instancu ovog kompleksnog broja s kompleksnim brojem c.
     * Koristi formulu (x+yi)/(u+vi) = (xu+yv)/(u^2+v^2) + (yu-xv)/(u^2+v^2)i
     * 
     * @param c Kompleksni broj s kojim se dijeli instanca ovog kompleksnog broja.
     * @return Rezultat dijeljenja.
     */
    public Complex div(Complex c) {
        double nazivnik = c.real * c.real + c.imaginary * c.imaginary;
        return new Complex((real * c.real + imaginary * c.imaginary) / nazivnik,
                (imaginary * c.real - real * c.imaginary) / nazivnik);
    }

    /**
     * Zbraja instancu ovog kompleksnog broja s kompleksnim brojem c.
     * 
     * @param c Kompleksni broj s kojim se zbraja instanca ovog kompleksnog broja.
     * @return Rezultat zbrajanja.
     */
    public Complex add(Complex c) {
        return new Complex(real + c.real, imaginary + c.imaginary);
    }

    /**
     * Oduzima instancu ovog kompleksnog broja s kompleksnim brojem c.
     * 
     * @param c Kompleksni broj s kojim se oduzima instanca ovog kompleksnog broja.
     * @return Rezultat oduzimanja.
     */
    public Complex sub(Complex c) {
        return new Complex(real - c.real, imaginary - c.imaginary);
    }

    /**
     * Negira instancu ovog kompleksnog broja.
     * 
     * @return Negirani kompleksni broj.
     */
    public Complex negate() {
        return new Complex(-real, -imaginary);
    }

    /**
     * Računa n-tu potenciju kompleksnog broja.
     * N je broj veći ili jednak 0.
     * 
     * @param n Potencija.
     * @return N-ta potencija kompleksnog broja.
     */
    public Complex power(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Power must be greater than or equal to 0.");
        }
        double module = Math.pow(module(), n);
        double angle = getAngleToXAxis() * n;
        return new Complex(module * Math.cos(angle), module * Math.sin(angle));
    }

    /**
     * Računa n-ti korijen kompleksnog broja.
     * N je pozitivan broj.
     * 
     * @param n Korijen.
     * @return N-ti korijen kompleksnog broja.
     */
    public List<Complex> root(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Root must be greater than 0.");
        }
        double module = Math.pow(module(), 1.0 / n);
        double angle = getAngleToXAxis();
        double dvaKPI;
        List<Complex> roots = new ArrayList<>();
        for (int k = 0; k < n; k++) {
            dvaKPI = 2 * k * Math.PI;
            roots.add(new Complex(module * Math.cos((angle + dvaKPI) / n),
                    module * Math.sin((angle + dvaKPI) / n)));
        }
        return roots;
    }

    /**
     * Računa kut kompleksnog broja naspram pozitivne x-osi.
     * 
     * @return Kut kompleksnog broja.
     */
    private double getAngleToXAxis() {

        return Math.atan2(imaginary , real);
    }

    /**
     * Pretvara instancu ovog kompleksnog broja u String.
     * Oblika a+ib.
     */
    @Override
    public String toString() {
        if (imaginary < 0) {
            return String.valueOf(real) + "-" + "i" + String.valueOf(-imaginary);
        }
        return String.valueOf(real) + "+" + "i" + String.valueOf(imaginary);
    }

}
