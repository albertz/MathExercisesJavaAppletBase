MathExercisesJavaAppletBase
===========================

This code was developed by Albert Zeyer for the Lehrstuhl A für Mathematik at the RWTH Aachen University.

It was developed to implement Java Applets with Math exercises and demonstrations to include them on some [Ilias](http://www.ilias.de/) platform. All developed Java Applets can be seen online [here](http://www.matha.rwth-aachen.de/~ilias/jars/). Each new Java Applet was a fork of the previous one and was extended by the newly needed functionality. Thus the code base has evolved into some sort of generic framework with a wide range of functions.

This Git repository shows the full development history (always of the most-recent Applet).

Some of the functions:

* VTMeta: LaTeX-like language to define the content of an Applet.
 This is where every Applet gets its content from. The source itself is in the file `content.vtmeta`. There is some detailed German documentation about it in `VTMeta*.txt`.
* PGraph: 2D plotting/visualizing functions
* PGraph3D: 3D visualizing functions
* Utils: some generic useful Java utils (many of them pure functional)
* OperatorTree: math term rewriting and parsing system
* EquationSystem: simple CAS system which supports resolution for systems of simple math terms
* ElectronicCircuit: visualization and calculations of electronic circuits

All the code is under GPLv3.
