![alt text](/figures/todo.jpeg)

Falta fazer o ponto 4

Escolher que classes instrumentar sem aumentar o overhead.

Intsrumentar o RobotController não faz sentido porque o overhead aumenta terrivelmente. Só os algoritmos não dá para extrair conclusões. 

**Em estudo**.

Valores retirados para os pedidos (RobotController instrumentada): 

- http://localhost:8000/mzrun.html?m=Maze100.maze&x0=3&y0=9&x1=78&y1=89&v=50&s=dfs
  - Number of basic blocks:  2453763165
- http://localhost:8000/mzrun.html?m=Maze100.maze&x0=3&y0=9&x1=78&y1=89&v=50&s=astar
  - Number of basic blocks:  7353073466
- http://localhost:8000/mzrun.html?m=Maze100.maze&x0=3&y0=9&x1=78&y1=89&v=50&s=bfs
  - Number of basic blocks: 14486670090
  
