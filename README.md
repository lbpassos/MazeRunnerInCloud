# MazeRunnerInCloud

The goal is to design and develop an elastic cluster of web servers that is able to
execute a simple gaming function:  to solve escape paths from labyrinths (mazes), on-demand, by exe-
cuting a set of search/exploration algorithms (serving as a demonstrator of CPU-intensive processing).
The  system  will  receive  a  stream  of  web  requests  from  users.   Each  request  is  for  the  solving  of  the
escape path of a given maze, providing the coordinates of the initial position (entry) and the location
of the escape door (exit), and in the end displaying the maze and the escape path to the user.

Each request can result in a task of varying complexity to process, as different search algorithms will
take different number of steps to solve the escape path, taking into account different maze configurations,
maze size, entry and exit coordinates, and simulating different thinking speed/delay.

To have scalability, good performance and efficiency, the system will have to optimize the selection
of the cluster node for each incoming request and to optimize the number of active nodes in the cluster.


# Architecture

![alt text](/figures/architecture.png)

# Instructions

- To run the webserver use java 1.7 (because of the BIT tool)
- In the command line: 
  - To compile (in the \src folder): ```webserver_cnv/src$ javac -d ../bin $(find . -name "*.java")```
  - To run (in the root folder): ```webserver_cnv$ java -cp bin pt.ulisboa.WebServer```
- To run the client in the browser: ```http://<LBawsaddress>:8000/mzrun.html?m=Maze100.maze&x0=3&y0=9&x1=78&y1=89&v=50&s=dfs```
- To run BIT. 
  - Take the .class (tool used) from samples and put it int bin folder: ```webserver_cnv/bin```
  - Take the .class (instrumented class) from ```BIT/samples``` and put it respective path int the webserver.
  - To generate the instrumented class run: ```java myStatisticsTool -dynamic <path_to_folder_to_instrument>/. <full_path>/BIT/examples/output```
- To run dynamo
  - java -cp "bin:/tmp/aws-java-sdk-1.11.321/lib/aws-java-sdk-1.11.321.jar:/tmp/aws-java-sdk-1.11.321/third-party/lib/*:." pt.ulisboa.WebServer
  - Don't forget ./AWS

