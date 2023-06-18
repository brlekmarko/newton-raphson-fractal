package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

/**
 * Crta Newton-Raphson fraktal pomocu FractalViewera.
 * Radi paralelno.
 * 
 * Kroz System.in cita nultočke kompleksnog polinoma.
 * Kroz argumente cita broj procesora i radnika.
 * 
 * @author Marko Brlek
 *
 */
public class NewtonP2 {
	
	public static int mintracks = 16;
	public static ComplexRootedPolynomial crp;
	public static ComplexPolynomial polynomial;
	public static ComplexPolynomial derived;
	
	/**
     * Kroz System.in cita nultočke kompleksnog polinoma te zatim crta fraktal.
     * 
     * Kroz argumente cita broj procesora i radnika (oboje opcionalno).
     * 
     * 
     * @param args Broj procesora i radnika
     */
	public static void main(String[] args) {

		
		parseArgs(args);
		
        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
        System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");
        
        
        System.out.println("Image of fractal will appear shortly. Thank you.");
        
        

    	NewtonP2.crp = new ComplexRootedPolynomial(Complex.ONE, readRootsAsArray());
    	NewtonP2.polynomial = crp.toComplexPolynom();
    	NewtonP2.derived = polynomial.derive();
    	

        FractalViewer.show(new Producer());
    }
	
	
	
	
	/**
	 * Parsira argumente; broj procesora i broj radnika
	 * 
	 * @param args Argumenti
	 */
	public static void parseArgs(String args[]) {
		boolean setMinTracks = false;
		
		if(args.length > 1) {
			throw new IllegalArgumentException("Too many arguments, only allow 1.");
		}
		
		for (String arg : args) {
			String[] sides = arg.split("=");
			if(sides.length!=2) {
				throw new IllegalArgumentException("Invalid syntax.");
			}
			if (sides[0].equals("--mintracks") || sides[0].equals("-m")) {
				if(setMinTracks) {
					throw new IllegalArgumentException("Number of mintracks can't be set twice.");
				}
				mintracks = Integer.parseInt(sides[1]);
				setMinTracks = true;
			}
			else {
				throw new IllegalArgumentException("Invalid syntax.");
			}
		}
	}
	
	/**
	 * Čita korijene kompleksnog polinoma sa System.in.
	 * Očekuje barem 2 korijena.
	 * 
	 * @return Lista korijena
	 */
	public static Complex[] readRootsAsArray() {
		
		Scanner sc = new Scanner(System.in);
        
        List<Complex> roots = new ArrayList<>();
        
        
        int i = 1;
        while (true) {
            System.out.print("Root " + i + "> ");
            String line = sc.nextLine();
            if (line.equals("done")) {
                if (roots.size() < 2) {
                    System.out.println("Please enter at least two roots.");
                    continue;
                }
                sc.close();
                break;
            }
            try {
                roots.add(ImaginarniParser.parse(line));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input.");
                continue;
            }
            i++;
        }
        
        Complex[] rootsArray = new Complex[roots.size()];
    	int counter = 0;
    	
    	for(Complex root : roots) {
    		rootsArray[counter] = root;
    		counter++;
    	}
        
        return rootsArray;
	}

    
    /**
     * Računa podatke za neke retke te ih sprema u data[] listu.
     * 
     * @author Marko Brlek
     *
     */
    public static class PosaoIzracuna extends RecursiveAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		double reMin;
		double reMax;
		double imMin;
		double imMax;
		int width;
		int height;
		int yMin;
		int yMax;
		int m;
		short[] data;
		AtomicBoolean cancel;
		

		public PosaoIzracuna(double reMin, double reMax, double imMin,
				double imMax, int width, int height, int yMin, int yMax, 
				int m, short[] data, AtomicBoolean cancel) {
			super();
			this.reMin = reMin;
			this.reMax = reMax;
			this.imMin = imMin;
			this.imMax = imMax;
			this.width = width;
			this.height = height;
			this.yMin = yMin;
			this.yMax = yMax;
			this.m = m;
			this.data = data;
			this.cancel = cancel;
		}
		
		/**
		 * Računa za svaki piksel u retku konvergira li kojoj nultočki.
		 */
		@Override
		protected void compute() {
			int preostaloRedaka = yMax - yMin;
			
			//ako je malo redaka onda racunamo sa starim algoritmom
			if(preostaloRedaka <= NewtonP2.mintracks) {
				computeDirect();
				return;
			}
			
			//inace ga podijelimo na dva dijela
			
			int ySredina = yMin + (preostaloRedaka/2);
			
			PosaoIzracuna p1 = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin, ySredina, m, data, cancel);
			PosaoIzracuna p2 = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, ySredina+1, yMax, m, data, cancel);
			
			invokeAll(p1, p2);
			
		}
		
		
		private void computeDirect() {
			
			int offset = yMin * width;
			int index;
	    	
	    	for(int y = yMin; y <= yMax; y++) {
				if(cancel.get()) break;
				for(int x = 0; x < width; x++) {
					double zre = x / (width-1.0) * (reMax - reMin) + reMin;
					double zim = (height-1.0-y) / (height-1) * (imMax - imMin) + imMin;
					Complex zn = new Complex(zre, zim);
					double module = 0;
					int iters = 0;
					
					do {
						Complex numerator = polynomial.apply(zn);
						Complex denominator = derived.apply(zn);
						Complex znold = zn;
						Complex fraction = numerator.div(denominator);
						zn = zn.sub(fraction);
						module = znold.sub(zn).module();
						iters++;
					} while(iters < m && module > 0.001);
					
					index = iters>=m ? 0 : crp.indexOfClosestRootFor(zn, 0.002);
					data[offset] = (short)(index+1);
					offset++;
				}
			}
		}

		
	}
    
    
    /**
     * Klasa dijeli poslove na neki broj procesa i radnika.
     * 
     * Dohvaća podatke o konvergiranju svih piksela koji se prikazuju na ekranu.
     * 
     * Sprema podatke u listu data[] koju predaje FractalVieweru.
     * 
     * @author Marko Brlek
     *
     */
    public static class Producer implements IFractalProducer{
    	
    	private ForkJoinPool pool;
    	

		/**
		 * Dohvaća podatke.
		 */
		@Override
		public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height,
				long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
			
			
			System.out.println("Task started with " + Runtime.getRuntime().availableProcessors() + " threads and " + 
					NewtonP2.mintracks + " mintracks.");
			

			
			short[] data = new short[width * height];
			int m = 16*16*16;

			//zovemo jedan veliki posao, kasnije se on podijeli na manje
			
			PosaoIzracuna posao = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, 0, height-1, m, data, cancel);
			this.pool.invoke(posao);
			
			
			observer.acceptResult(data, (short)(polynomial.order()+1), requestNo);
			
		}

		@Override
		public void close() {
			this.pool.shutdownNow();
			//mozda this.pool.shutdown();
			
		}

		@Override
		public void setup() {
			this.pool = new ForkJoinPool();
		}
    	
    }
}
