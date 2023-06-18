package hr.fer.zemris.java.fractals;

import hr.fer.zemris.math.Complex;

public class ImaginarniParser {
	/**
     * Provjerava jesu li nultočke u oblika:
     * 		a+ib, a-ib, a, ib, -ib, i, -i
     * @param line
     * @return
     */
    public static Complex parse(String line) {
        String[] parts = line.split(" ");
        Complex result = Complex.ZERO;

        if (parts.length == 1) { //samo jedan broj, realni ili imaginarni dio
            if (parts[0].charAt(0) == '-') {
                if (parts[0].charAt(1) == 'i') { //negativan imaginarni dio
                    if (parts[0].length() == 2) { //samo -i
                        double im = 1;
                        result = result.add(new Complex(0, -im));
                    } else { // b!=0
                        parts[0] = parts[0].substring(2);
                        try {
                            double im = Double.parseDouble(parts[0]);
                            result = result.add(new Complex(0, -im));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid input.");
                        }
                    }
                } else { //negativan realni dio
                    parts[0] = parts[0].substring(1);
                    try {
                        double re = Double.parseDouble(parts[0]);
                        result = result.add(new Complex(-re, 0));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid input.");
                    }
                }
            } else { //pozitivni
                if (parts[0].charAt(0) == 'i') { //pozitivan imaginarni dio
                    if (parts[0].length() == 1) { //samo i
                        double im = 1;
                        result = result.add(new Complex(0, im));
                    } else { //pozitivan imaginarni dio
                        parts[0] = parts[0].substring(1);
                        try {
                            double im = Double.parseDouble(parts[0]);
                            result = result.add(new Complex(0, im));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid input.");
                        }
                    }
                } else { //pozitivan realni dio
                    try {
                        double re = Double.parseDouble(parts[0]);
                        result = result.add(new Complex(re, 0));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid input.");
                    }
                }
            }
        }

        else if (parts.length == 3) { //realni dio +/- imaginarni dio
            try {
                double re = Double.parseDouble(parts[0]);
                result = result.add(new Complex(re, 0));
                if (parts[1].equals("+")) {
                    if (parts[2].charAt(0) == 'i') {
                        if (parts[2].length() == 1) {
                            double im = 1;
                            result = result.add(new Complex(0, im));
                        } else {
                            parts[2] = parts[2].substring(1);
                            try {
                                double im = Double.parseDouble(parts[2]);
                                result = result.add(new Complex(0, im));
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid input.");
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid input.");
                    }
                } else if (parts[1].equals("-")) {
                    if (parts[2].charAt(0) == 'i') {
                        if (parts[2].length() == 1) {
                            double im = 1;
                            result = result.add(new Complex(0, -im));
                        } else {
                            parts[2] = parts[2].substring(1);
                            try {
                                double im = Double.parseDouble(parts[2]);
                                result = result.add(new Complex(0, -im));
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid input.");
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid input.");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid input.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid input.");
            }

        } else

        {
            throw new IllegalArgumentException("Invalid input.");
        }

        return result;
    }
}
