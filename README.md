# NEAT-Snake

To run, use the Main.java file.


I started off training a NEAT algorithm to play competitive snake.

After realizing how complicated that NEAT is, I changed it to a combination of reinforcement learning & a genetic algorithm. 

This is a competitive version of snake.
The snakes are playing on a 20 by 20 gameboard & they don't wrap around after going off the edge.
In each game, there are 2 snakes.  If they either hit themselves or the other player, they die.

Each snake has a neural network with 8 hidden layers, each with 127 nodes.  
For the inputs, the snakes can only see if a pixel on the screen is colored white or black.
The network outputs the absolute direction the snake should turn.

Each snake is also given 16 nodes for memory.  
This way the snakes should learn to figure out where they are, where their opponent is, and where the food is.
