import java.lang.Math;

class SnakeAI{
   private int id;
   private static final int MEMORY_SIZE = 400;
   private float[][][] weights = new float[][][] {new float[Board.AREA + 1 + MEMORY_SIZE][127], 
                                                    new float[128][127], 
                                                    new float[128][127], 
                                                    new float[128][127],  
                                                    new float[128][127],   
                                                    new float[128][127],   
                                                    new float[128][127], 
                                                    new float[128][4 + MEMORY_SIZE]};
   
   
   private byte prevDirec = 1;
   private byte direction = 1;
   
   
   private float[][] network;
   private float[][] prevNetwork;
   private static final float LEARNING_RATE = 1f;
   
   
   
   
   public SnakeAI(int iid) {
      id = iid;
      for (byte layer = 0; layer < weights.length; layer++) {
         for (int in = 0; in < weights[layer].length; in++) {
            for (int out = 0; out < weights[layer][in].length; out++) {
               weights[layer][in][out] = (float) (2 * Math.random() - 1);
            }
         }
      }
      //generation = 0;
      
      network = new float[weights.length + 1][];
      network[weights.length] = new float[5 + MEMORY_SIZE];
      
      prevNetwork = new float[weights.length + 1][];
      prevNetwork[weights.length] = new float[5 + MEMORY_SIZE];
      
      for (int i = 0; i < weights.length; i++) {
         network[i] = new float[weights[i].length];
         prevNetwork[i] = new float[weights[i].length];
      }
   }
   
   
   
   
   public int getID() {
      return id;
   }
   
   
   
   public static float sigmoid(float x) {
      return 1/ (1 + (float) Math.exp(-x)) * 2 - 1;
   }
   public static float inverseSigmoid(float x) {
      return -(float) Math.log(2 / (x + 1) - 1);
   }
   public static float sigmoidDerivative(float x) {
      return 2 * (float) Math.exp(-x) / (float) Math.pow(1 + (float) Math.exp(-x), 2);
   }
   
   
   
   
   public float[] update(float[] lastLayerPrime, float[][] net) {
      float[][][] derivatives = new float[weights.length][][];
      for (byte layer = 0; layer < weights.length; layer++) {
         derivatives[layer] = new float [weights[layer].length][];
         for (int start = 0; start < weights[layer].length; start++) {
            derivatives[layer][start] = new float[weights[layer][start].length];
         }
      }
      
      float[] nodesPrime = lastLayerPrime;
      
      for (byte layer = (byte) (weights.length - 1); layer >= 0; layer--) {
         final float[] prevLayer = nodesPrime;
         nodesPrime = new float[net[layer].length];
         for (int start = 0; start < weights[layer].length; start++) {
            for (int end = 0; end < weights[layer][start].length; end++) {
               float sig = sigmoidDerivative(inverseSigmoid(net[layer + 1][end]));
               derivatives[layer][start][end] += sig * net[layer][start] * prevLayer[end];
               nodesPrime[start] += sig * weights[layer][start][end] * prevLayer[end];
            }
         }
      }
      for (byte layer = 0; layer < weights.length; layer++) {
         for (int start = 0; start < weights[layer].length; start++) {
            for (int end = 0; end < weights[layer][start].length; end++) {
               weights[layer][start][end] += LEARNING_RATE * derivatives[layer][start][end];
            }
         }
      }
      float[] out = new float[MEMORY_SIZE];
      for (int i = 0; i < MEMORY_SIZE; i++) {
         out[i] = nodesPrime[Board.AREA + i];
      }
      return out;
   }
   
   
   //This might not actually be qLearning
   public void qLearn(boolean punish) {
      float[] lastLayerPrime = new float[4 + MEMORY_SIZE];
      for (byte i = 0; i < 4; i++) {
         if (i == direction) {
            lastLayerPrime[i] = 1f;
         } else {
            lastLayerPrime[i] = -1f;
         }
         if (punish) {
            lastLayerPrime[i] *= -1f;
         }
      }
      for (int i = 0; i < MEMORY_SIZE; i++) {
         lastLayerPrime[i + 4] = 0;
      }
      
      final float[] memoryPrime = update(lastLayerPrime, network);
      for (byte i = 0; i < 4; i++) {
         if (i == prevDirec) {
            lastLayerPrime[i] = 1f;
         } else {
            lastLayerPrime[i] = -1f;
         }
         if (punish) {
            lastLayerPrime[i] *= -1f;
         }
      }
      for (int i = 0; i < MEMORY_SIZE; i++) {
         lastLayerPrime[i + 4] = memoryPrime[i];
      }
      update(lastLayerPrime, prevNetwork);
   }
   
   
   

   
   public byte getDirection(boolean[][] board) {
      Main.pause(5);
      
      prevDirec = direction;
      for (int layer = 0; layer < network.length; layer++) {
         for (int item = 0; item < network[layer].length; item++) {
            prevNetwork[layer][item] = network[layer][item];
            network[layer][item] = 0;
         }
      }
      
      for (int x = 0; x < Board.WIDTH; x++) {
         for (int y = 0; y < Board.HEIGHT; y++) {
            if (board[x][y]) {
               network[0][x + Board.WIDTH * y] = 1f;
            } else {
               network[0][x + Board.WIDTH * y] = -1f;
            }
         }
      }
      for (byte i = 0; i < 3; i++) {
         network[0][Board.WIDTH * Board.HEIGHT + i] = network[network.length - 1][4 + i];
      }
      network[0][Board.WIDTH * Board.HEIGHT + 3] = 1f;
      
      for (byte layer = 1; layer <= weights.length; layer++) {
         for (int j = 0; j < weights[layer - 1][0].length; j++) {
            for (int i = 0; i < network[layer - 1].length; i++) {
               network[layer][j] += weights[layer - 1][i][j] * network[layer - 1][i];
            }
            network[layer][j] = sigmoid(network[layer][j]);
         }
         network[layer][network[layer].length - 1] = 1f;
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
      return baby;
   }
}