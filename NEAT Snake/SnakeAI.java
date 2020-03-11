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
   private static final float LEARNING_RATE = 0.25f;
   
   
   
   
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
      
      for (int i = 0; i < weights[0].length; i++) {
         network[0][i] = 0;
         prevNetwork[0][i] = 0;
      }
   }
   
   
   
   
   public void resetNetworks() {
      for (int a = 0; a < network.length; a++) {
         for (int b = 0; b < network[a].length; b++) {
            network[a][b] = 0;
            prevNetwork[a][b] = 0;
         }
      }
   }
   
   
   
   
   public int getID() {
      return id;
   }
   
   
   
   
   
   public static float sigmoid(float x) {
      return 1/ (1 + (float) Math.exp(-x)) * 2 - 1;
   }
   public static float derivativeInverseSig(float x) {
      return (1 - x * x) / 2;
   }
   
   
   
   
   public float[] update(float[] lastLayerPrime, float[][] net) {
      final float[][][] derivatives = new float[weights.length][][];
      for (byte layer = 0; layer < weights.length; layer++) {
         derivatives[layer] = new float [weights[layer].length][];
         for (int start = 0; start < weights[layer].length; start++) {
            derivatives[layer][start] = new float[weights[layer][start].length];
         }
      }
      
      float[] nodesPrime = lastLayerPrime;
      
      for (byte layer = (byte) (weights.length); layer > 0; layer--) {
      
         final float[] prevLayer = nodesPrime;
         nodesPrime = new float[net[layer - 1].length];
         
         for (int start = 0; start < weights[layer - 1].length; start++) {
            for (int end = 0; end < weights[layer - 1][start].length; end++) {
            
               final float sig = derivativeInverseSig(net[layer][end]);               
               
               derivatives[layer - 1][start][end] += sig * net[layer - 1][start] * prevLayer[end];
               nodesPrime[start] += sig * weights[layer - 1][start][end] * prevLayer[end];
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
      final float[] out = new float[MEMORY_SIZE];
      for (int i = 0; i < MEMORY_SIZE; i++) {
         out[i] = nodesPrime[Board.AREA + i];
      }
      return out;
   }
   
   
   //This might not actually be qLearning
   public void qLearn(boolean punish) {
      final float[] lastLayerPrime = new float[4 + MEMORY_SIZE];
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
      Main.pause(20);
      
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
         network[0][Board.AREA + i] = network[network.length - 1][4 + i];
      }
      
      
      network[0][Board.AREA + 3] = 1f;
      
      for (byte layer = 0; layer < weights.length; layer++) {
         for (int j = 0; j < weights[layer][0].length; j++) {
            for (int i = 0; i < network[layer].length; i++) {
               network[layer + 1][j] += weights[layer][i][j] * network[layer][i];
            }
            network[layer + 1][j] = sigmoid(network[layer + 1][j]);
         }
         network[layer + 1][network[layer + 1].length - 1] = 1f;
      }
      
      
      direction = 0;
      for (byte j = 0; j < 4; j++) {
         if (network[network.length - 1][j] > network[network.length - 1][direction]) {
            direction = j;
         }
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