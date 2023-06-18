package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
public class NewtonParallel {
	
	public static int processors = Runtime.getRuntime().availableProcessors();
	public static int tracks = 4 * processors;
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
        
        

    	NewtonParallel.crp = new ComplexRootedPolynomial(Complex.ONE, readRootsAsArray());
    	NewtonParallel.polynomial = crp.toComplexPolynom();
    	NewtonParallel.derived = polynomial.derive();
    	

        FractalViewer.show(new Producer());
    }
	
	
	
	
	/**
	 * Parsira argumente; broj procesora i broj radnika
	 * 
	 * @param args Argumenti
	 */
	public static void parseArgs(String args[]) {
		boolean setProcessors = false;
		boolean setTracks = false;
		
		if(args.length > 2) {
			throw new IllegalArgumentException("Too many arguments, only allow 2.");
		}
		
		for (String arg : args) {
			String[] sides = arg.split("=");
			if(sides.length!=2) {
				throw new IllegalArgumentException("Invalid syntax.");
			}
			if(sides[0].equals("--workers") || sides[0].equals("-w")) {
				if(setProcessors) {
					throw new IllegalArgumentException("Number of workers can't be set twice.");
				}
				processors = Integer.parseInt(sides[1]);
				setProcessors = true;
			}
			else if (sides[0].equals("--tracks") || sides[0].equals("-t")) {
				if(setTracks) {
					throw new IllegalArgumentException("Number of tracks can't be set twice.");
				}
				tracks = Integer.parseInt(sides[1]);
				setTracks = true;
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
    public static class PosaoIzracuna implements Runnable {
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
		public static PosaoIzracuna NO_JOB = new PosaoIzracuna();
		
		private PosaoIzracuna() {
		}
		
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
		public void run() {

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
    	

		/**
		 * Dohvaća podatke.
		 */
		@Override
		public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height,
				long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
			
			//ako je broj traka veći od broja redaka, "tiho" postavimo broj traka na broj redaka
			if(NewtonParallel.tracks>height) {
				NewtonParallel.tracks=height;
			}
			
			System.out.println("Task started with " + NewtonParallel.processors + " threads and " + 
								NewtonParallel.tracks + " jobs,");
			

			
	    	int brojYPoTraci = height / NewtonParallel.tracks;

			
			short[] data = new short[width * height];
			int m = 16*16*16;
			//int offset = 0;
			//int index;
			
	    	final BlockingQueue<PosaoIzracuna> queue = new LinkedBlockingQueue<>();
	    	
	    	Thread[] radnici = new Thread[NewtonParallel.processors];
	    	
	    	//radnici kad su gotovi sa prethodnim zadatkom, uzimaju novi zadatak iz reda
	    	//sve dok u redu ima zadataka
	    	
	    	for(int i = 0; i < radnici.length; i++) {
				radnici[i] = new Thread(new Runnable() {
					@Override
					public void run() {
						while(true) {
							PosaoIzracuna p = null;
							try {
								p = queue.take();
								if(p==PosaoIzracuna.NO_JOB) break;
							} catch (InterruptedException e) {
								continue;
							}
							p.run();
						}
					}
				});
			}
	    	
	    	//budimo radnike
	    	for(int i = 0; i < radnici.length; i++) {
				radnici[i].start();
			}
			
	    	//stavljamo poslove u red
			for(int i = 0; i < NewtonParallel.tracks; i++) {
				int yMin = i*brojYPoTraci;
				int yMax = (i+1)*brojYPoTraci-1;
				if(i==NewtonParallel.tracks-1) {
					yMax = height-1;
				}
				PosaoIzracuna posao = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin, yMax, m, data, cancel);
				while(true) {
					try {
						queue.put(posao);
						break;
					} catch (InterruptedException e) {
					}
				}
			}
			
			//stavljamo oznake za kraj posla u red
			for(int i = 0; i < radnici.length; i++) {
				while(true) {
					try {
						queue.put(PosaoIzracuna.NO_JOB);
						break;
					} catch (InterruptedException e) {
					}
				}
			}
			
			//crtanje na ekran čeka da svi radnici završe s poslom
			for(int i = 0; i < radnici.length; i++) {
				while(true) {
					try {
						radnici[i].join();
						break;
					} catch (InterruptedException e) {
					}
				}
			}
			
			
			observer.acceptResult(data, (short)(polynomial.order()+1), requestNo);
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setup() {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
