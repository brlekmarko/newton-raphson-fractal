# Newton-Raphson fractal viewer
Newton-Raphson iteration-based fractal viewer

Two implementations, one with a basic ThreadPool, one with a ForkJoinPool.

Maven project, command line application, parameters are given as such "java hr.fer.zemris.java.fractals.NewtonP1 --workers=2 --tracks=10" (ThreadPool).
Class NewtonP2 (ForkJoinPool) also supports parameter "--mintracks", e.g. "java hr.fer.zemris.java.fractals.NewtonP1 --workers=2 --tracks=10 --mintracks=32".
