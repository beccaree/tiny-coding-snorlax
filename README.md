# tiny-coding-snorlax
Team 8 (Team m8s) SOFTENG 306 2016 Semester 2


Project Overview:

This project consists of a program that schedules tasks on a given number of processors to find the
the optimal schedule. A valid .dot file is needed as an input containing data that represents a DAG.
At the end of the search, an output .dot file is generated with the optimal schedule results. If 
visualised, a window with the input graph tree and live search statistics is displayed. At the end
of the search, a popup containing the schedule in the form of a gantt chart and final search 
statistics are shown.


----------------------------------------------------------------------------------------------------
Team Details:

Name			upi		id		github
-------------------	-------		-------		-----------
Isabel Zhuang		izhu678		9097662		Tarotato
Kristy Tsoi		ytso868		6521229		kris-t-tsoi
Rebecca Lee		rlee291		8017413		beccaree
Stefan Gorgiovski	sgor395		8765614		StefanGor
Sabrina Zafarullah	szaf045		8703958		sabflik


----------------------------------------------------------------------------------------------------
Build Instructions:

1. Boot Linux Beta on the lab computers and open Eclipse in Java 1.8 environment.
2. Import the se306Project1 project. 
3. Add all jar libraries in the lib folder to the project build path. 
4. At this stage, you can run the program from Eclipse by typing the inputs into run configurations 
   and setting the main class as scheduling_solution.Main.java.

To create a brand new jar file:
1. Right click the project and click "export". 
2. Select "Runnable Jar file" and select the right run configurations (select main class) and export 
   destination.
3. Select "Package required libraries into generated JAR". 
4. You can run this jar using the instructions provided below and replacing "scheduler.jar" with the 
   name you gave the jar file.


To try the JUnit tests, run any of the tests under the junit package as Test Cases.


**NOTE: For best results, run the project on Java 1.8. In Java 1.7, for some cases, the parallelisation
has some issues.


----------------------------------------------------------------------------------------------------
How to run the supplied jar:

1. The program can be run by opening the command line on Linux and Navigating to the directory 
   containing the jar file and the "ColourKey.jpg" image. This image can be found at the top level in 
   the se306Project1 project directory. 
2. Type "java -jar scheculer.jar -args" where the -args are the input arguments to the program. 
   This should include the name of a valid .dot file name followed by the number of processors to 
   schedule it on. Other optional arguments include -v for visualisation, -o followed by an output 
   file name to customise the name of the output file and -p for parallelisation followed by the number 
   of threads.

By default, the output file name would be the input file name with "-output" appended to it. The 
program also assumes that the number of threads is 1, unless the -p is followed by a number.


----------------------------------------------------------------------------------------------------
Package Structure

The top level folders are junit and scheduling_solution. All past and present tests are in the junit
folder and all other program-related classes are in scheduling_solution. 

Within junit, the out_of_commission package contain tests that were used during the development 
process which don't work anymore.

Under scheduling_solution, there are 7 packages and the Main.java class. The packages are as follows:

astar:			This package contains classes for all versions of a* required for the 
			program to run.	The sub-package, parallel, contains classes needed for a* to 
			run in parallel.

basic_milestone_solver:	Contains all classes used for the basic milestone, which utilised topological 
			sort.

branch_and_bound:	Contains branch and bound implementation used for the validation stage. 

graph:			All basic object classes and interfaces required by the topological sort,
			branch and bound and a* implementations.

input:			Contains one class, GraphParser, that parses the input file and generates
			the JGraphT graph.

output:			Contains one class, OutputFileCreator, that creates and writes to an output
			file when it receives the final scheduling results.

visualisation:		All classes needed for visualising the program.


----------------------------------------------------------------------------------------------------
Details of documentation:

All documentation including meeting minutes and plans can be found on github. They are in top-level
folders named Meetings and Planning Documents respectively. The Final Documents folder contain
documents that are required for submission such as the Final Plan and a draft of the Final Report.
