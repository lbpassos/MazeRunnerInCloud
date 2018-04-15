# MazeRunnerInCloud

- To run the webserver use java 1.7 (because of the BIT tool)
- In the command line: 
  - To compile (in the \src folder): ```webserver_cnv/src$ javac -d ../bin $(find . -name "*.java")```
  - To run (in the root folder): ```webserver_cnv$ java -cp bin pt.ulisboa.WebServer```
- To run the client in the browser: ```http://localhost:8000/mzrun.html?m=Maze100.maze&x0=3&y0=9&x1=78&y1=89&v=50&s=dfs```
- To run BIT. 
  - Take the .class (tool used) from samples and put it int bin folder: ```webserver_cnv/bin```
  - Take the .class (instrumented class) from ```BIT/samples``` and put it respective path int the webserver.
  - To generate the instrumented class run: ```java myStatisticsTool -dynamic <path_to_folder_to_instrument>/. <full_path>/BIT/examples/output```
 

