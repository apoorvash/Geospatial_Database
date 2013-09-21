
Resolution:
The goal of this assignment was to design an application that queries a spatial database.
This assignment got me familiar with spatial data types using Oracle11g, Oracle Spatial features, and Java (JDBC). Since I was new to Java, I found this assignment quite challenging. At the end of it all, I have this immense sense of satisfaction of having worked on something this interactive and challenging. It surely looks good on my resume.

Compilation Steps
1)Run the script within createdb.sql file on any sql developer platform.
2)To populate the database:
	compile : javac -classpath ojdbc.jar populate.java
	execute : java -classpath .;ojdbc.jar populate hydrant.xy building.xy firebuilding.txt (the command line arguments should be in the same order)
3)To run the gui code:
	compile : javac -classpath ojdbc.jar HW2.java
	execute : java -classpath .;ojdbc.jar HW2
4) Drop tables by running dropdb.sql