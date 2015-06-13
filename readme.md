Name Gaurav Desai
Course CS571

1) Problem statement can be found in 'Problem Statement.pdf'

2) Project Description can be found in 'Project Report.pdf'

3)  Running Instructions:

	- Programs should be tested on mason
	- The class files provided are compiled in java6
	- Execution instruction:

		RemoteInterface: [Only compile]
		javac edu/gmu/os/RemoteInterface.java

		Department: [Compile and run with various topology files as shown below]
		javac edu/gmu/os/Department.java
		
			For creating a ring of five department servers:
			1) java edu.gmu.os.Department ./Dept_Housing.txt 5 5
			2) java edu.gmu.os.Department ./Dept_Education.txt 5 5
			3) java edu.gmu.os.Department ./Dept_Engineering.txt 5 5
			4) java edu.gmu.os.Department ./Dept_History.txt 5 5
			5) java edu.gmu.os.Department ./Dept_Science.txt 5 5
		
		RMIClient: [Compile and run and follow instructions]
		javac edu/gmu/os/RMIClient.java
		java edu.gmu.os.RMIClient
			
2) Known problems:
	1) Ring is fully functional but i could not achieve fault tolerance functionality working.