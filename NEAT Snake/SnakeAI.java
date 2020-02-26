import java.lang.Math;

class SnakeAI{
   private int id;
   private int memorySize = 128;
   private double[][][] weights = new double[][][] {new double[Board.WIDTH * Board.HEIGHT + 1 + memorySize][127], 
                                                    new double[128][127], 
                                                    new double[128][127], 
                                                    new double[128][127],  
                                                    new double[128][127],   
                                                    new double[128][127],   
                                                    new double[128][127],   
                                                    new double[128][127], 
                                                    new double[128][4 + memorySize]};
   
   
   private int generation;
   private byte direction;
   
   
   private double[] memoryPrime = new double[memorySize];
   private double[][] network;
   private static double LEARNING_RATE = 1;//0.0625;
   
   
   
   
   public SnakeAI(int iid) {
      id = iid;
      for (byte layer = 0; layer < weights.length; layer++) {
         for (int in = 0; in < weights[layer].length; in++) {
            for (int out = 0; out < weights[layer][in].length; out++) {
               weights[layer][in][out] = 2 * Math.random() - 1;
            }
         }
      }
      generation = 0;
      
      network = new double[weights.length + 1][];
      network[weights.length] = new double[5 + memorySize];
      for (int i = 0; i < weights.length; i++) {
         network[i] = new double[weights[i].length];
      }
   }
   
   
   
   
   public int getID() {
      return id;
   }
   public int getGen() {
      return generation;
   }
   public void setGen(int gen) {
      generation = gen;
   }
   
   
   
   public static double sigmoid(double x) {
      return 1/ (1 + Math.exp(-x)) * 2 - 1;
   }
   public static double sigmoidDerivative(double x) {
      return sigmoid(x) * (1 - sigmoid(x));
   }
   
   
   
   
   //This might not acually by qLearning
   public void qLearn(boolean punish) {
      double[][][] derivatives = new double[weights.length][][];
      for (int layer = 0; layer < weights.length; layer++) {
         derivatives[layer] = new double [weights[layer].length][];
         for (int start = 0; start < weights[layer].length; start++) {
            derivatives[layer][start] = new double [weights[layer][start].length];
         }
      }
      
      double[] nodesPrime = new double[4 + memorySize];
      for (byte i = 0; i < 4; i++) {
         if (i == direction) {
            nodesPrime[i] = 1;
         } else {
            nodesPrime[i] = -1;
         }
         if (punish) {
            nodesPrime[i] *= -1;
         }
      }
      for (int i = 0; i < memorySize; i++) {
         nodesPrime[i + 4] = memoryPrime[i];
      }
      for (int layer = weights.length - 1; layer >= 0; layer--) {
         final double[] lastLayer = nodesPrime;
         nodesPrime = new double[network[layer].length];
         
         for (int start = 0; start < weights[layer].length; start++) {
            for (int end = 0; end < weights[layer][start].length; end++) {
               double sig = 0;
               for (int i = 0; i < network[layer].length; i++) {
                  sig += weights[layer][i][end] * network[layer][i];
               }
               sig = sigmoidDerivative(sig);
               derivatives[layer][start][end] += sig * network[layer][start] * lastLayer[end];
               nodesPrime[start] += sig * weights[layer][start][end] * lastLayer[end];
            }
         }
      }
      for (int i = 0; i < memorySize; i++) {
         memoryPrime[i] = nodesPrime[Board.WIDTH * Board.HEIGHT + i];
      }
      
      for (int layer = 0; layer < weights.length; layer++) {
         for (int start = 0; start < weights[layer].length; start++) {
            for (int end = 0; end < weights[layer][start].length; end++) {
               weights[layer][start][end] += LEARNING_RATE * derivatives[layer][start][end];
            }
         }
      }
   }
   
   
   

   
   public byte getDirection(boolean[][] board) {
      Main.pause(5);
      
      for (int layer = 0; layer < network.length; layer++) {
         for (int item = 0; item < network[layer].length; item++) {
            network[layer][item] = 0;
         }
      }
      
      for (int x = 0; x < Board.WIDTH; x++) {
         for (int y = 0; y < Board.HEIGHT; y++) {
            if (board[x][y]) {
               network[0][x + Board.WIDTH * y] = 1;
            } else {
               network[0][x + Board.WIDTH * y] = -1;
            }
         }
      }
      for (byte i = 0; i < 3; i++) {
         network[0][Board.WIDTH * Board.HEIGHT + i] = network[network.length - 1][4 + i];
      }
      network[0][Board.WIDTH * Board.HEIGHT + 3] = 1;
      
      for (byte layer = 1; layer <= weights.length; layer++) {
         for (int j = 0; j < weights[layer - 1][0].length; j++) {
            for (int i = 0; i < network[layer - 1].length; i++) {
               network[layer][j] += weights[layer - 1][i][j] * network[layer - 1][i];
            }
            network[layer][j] = sigmoid(network[layer][j]);
         }
         network[layer][network[layer].length - 1] = 1;
      }
      
      
      direction = 0;
      for (byte j = 0; j < 4; j++) {
         if (network[network.length - 1][j] > network[network.length - 1][direction]) {
            direction = j;
         }
         //System.out.println(network[1][j]);
      }
      return direction;
   }
   
   
   
   public SnakeAI mateWith(SnakeAI that, int id) {
      SnakeAI baby = new SnakeAI(id);
      for (int layer = 0; layer < weights.length; layer++) {
         for (int in = 0; in < weights[layer].length; in++) {
            for (int out = 0; out < weights[layer][in].length; out++) {
               if (Math.random() < 0.5) {
                  baby.weights[layer][in][out] = weights[layer][in][out];
               } else {
                  baby.weights[layer][in][out] = that.weights[layer][in][out];
               }
               
               baby.weights[layer][in][out] += 0.125 * (Math.random() * 2 - 1);
            }
         }
      }
      baby.generation = this.generation + 1;
      if (that.getGen() > generation) {
         baby.setGen(that.generation + 1);
      }
      return baby;
   }
}