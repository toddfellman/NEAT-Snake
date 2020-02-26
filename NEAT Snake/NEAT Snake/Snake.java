import java.util.ArrayList;

class Snake {
   private ArrayList<byte[]> body;
   private boolean alive;
   private byte direction = 0;
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
      byte[] head = body.get(0);
      
      
      switch (direction) {
         case 0:
            body.add(0, new byte[] {head[0], (byte) (head[1] - 1)});
            break;
         case 1:
            body.add(0, new byte[] {head[0], (byte) (head[1] + 1)});
            break;
         case 2:
            body.add(0, new byte[] {(byte) (head[0] - 1), head[1]});
            break;
         case 3:
            body.add(0, new byte[] {(byte) (head[0] + 1), head[1]});
            break;
      }
      
      final byte[] food = b.getFood();
      head = body.get(0);
      if (head[0] == food[0] && head[1] == food[1]) {
         b.moveFood();
         brain.qLearn(false);
      } else {
         body.remove(body.size() - 1);
      }
      
      try {
         b.activate(body);
      } catch (IndexOutOfBoundsException e) {
         alive = false;
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
   
   /*public void hits(Snake other, Board b) {
      ArrayList<int[]> otherBody = other.getBody();
      int[] head = body.get(0);
      boolean areDifferent = false;
      for (int i = 1; i < otherBody.size(); i++) {
         if (otherBody.get(i).equals(head)) {
            alive = false;
            reset(b);
            break;
         } else {
            areDifferent = true;
         }
      }
      if (alive && areDifferent) {
         alive = otherBody.get(0).equals(head);
         other.setStatus(false);
         reset(b);
         other.reset(b);
      }
   }*/
   
}