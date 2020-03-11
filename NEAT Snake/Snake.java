import java.util.ArrayList;

class Snake {
   private ArrayList<byte[]> body;
   private boolean alive;
   private byte direction = 1;
   private int id;
   private byte energy = Byte.MAX_VALUE;
   
   public Snake (int iid) {
      body = new ArrayList<byte[]>();
      body.add(Board.randomSquare());
      alive = true;
      id = iid;
   }
   
   
   public boolean isAlive() {
      return alive;
   }
   public byte length() {
      return (byte) body.size();
   }
   public void setStatus(boolean b) {
      alive = b;
   }
   
   
   public void setDirection(byte dd) {
      if (0 <= dd && dd <= 4) {
         direction = dd;
      }
   }
   
   public ArrayList<byte[]> getBody() {
      return body;
   }
   
   
   public void move(Board b, SnakeAI brain) {
      b.erase(body);
      
      
      final byte[] head = body.get(0);
      
      switch (direction) {
         case 0:
            head[1]--;
            break;
         case 1:
            head[1]++;
            break;
         case 2:
            head[0]--;
            break;
         case 3:
            head[0]++;
            break;
      }
      body.add(0, head);
      
      final byte[] food = b.getFood();
      if (head[0] == food[0] && head[1] == food[1]) {
         b.moveFood();
         brain.qLearn(false);
         energy = Byte.MAX_VALUE;
      } else {
         body.remove(body.size() - 1);
         energy--;
      }
      
      if (energy == 0) {
         energy = Byte.MAX_VALUE;
         brain.qLearn(true);
      }
      
      for (byte[] t: body) {
         if ((0 <= t[0] && t[0] < Board.WIDTH) && (0 <= t[1] && t[1] < Board.HEIGHT)) {
            b.activate(t[0], t[1]);
         } else {
            alive = false;
         }
      }
      if (!alive) {
         brain.qLearn(true);
      }
   }
   
   
   
   public void hits(Snake that, SnakeAI brain) {
      final byte[] head = body.get(0);
      if (id != that.id) {
         if (head[0] == that.body.get(0)[0] && head[1] == that.body.get(0)[1]) {
            alive = false;
            brain.qLearn(true);
         }
      }
      for (byte i = 1; i < that.body.size(); i++) {
         final byte[] segment = that.body.get(i);
         if (head[0] == segment[0] && head[1] == segment[1]) {
            alive = false;
            brain.qLearn(true);
            break;
         }
      }
   }
   
   
}