import java.util.ArrayList;

class Snake {
   private ArrayList<byte[]> body;
   private boolean alive;
   private char direction = 'w';
   private int counter = 0;
   private int id;
   
   
   public Snake () {
      body = new ArrayList<byte[]>();
      body.add(Board.randomSquare());
      alive = true;
      id = counter;
      counter++;
   }
   
   
   public boolean isAlive() {
      return alive;
   }
   public void setStatus(boolean b) {
      alive = b;
   }
   
   
   public void setDirection(String d) {
      char dd = d.charAt(0);
      if (dd == 'w' || dd == 'a' || dd == 's' || dd == 'd') {
         direction = dd;
      }
   }
   
   public ArrayList<byte[]> getBody() {
      return body;
   }
   
   
   public void move(Board b) {
      b.erase(body);
      byte[] head = body.get(0);
      
      
      switch (direction) {
         case 'w':
            body.add(0, new byte[] {head[0], (byte) (head[1] - 1)});
            break;
         case 's':
            body.add(0, new byte[] {head[0], (byte) (head[1] + 1)});
            break;
         case 'a':
            body.add(0, new byte[] {(byte) (head[0] - 1), head[1]});
            break;
         case 'd':
            body.add(0, new byte[] {(byte) (head[0] + 1), head[1]});
            break;
      }
      
      final byte[] food = b.getFood();
      head = body.get(0);
      if (head[0] == food[0] && head[1] == food[1]) {
         b.moveFood();
      } else {
         body.remove(body.size() - 1);
      }
      
      try {
         b.activate(body);
      } catch (IndexOutOfBoundsException e) {
         alive = false;
      }
   }
   
   public void reset(Board b) {
      b.erase(body);
      body = new ArrayList<byte[]>();
      body.add(Board.randomSquare());
      b.activate(body);
      alive = true;
   }
   
   public void hits(Snake that) {
      final byte[] head = body.get(0);
      if (id != that.id) {
         if (head[0] == that.body.get(0)[0] && head[1] == that.body.get(0)[1]) {
            alive = false;
         }
      }
      for (byte i = 1; i < that.body.size(); i++) {
         final byte[] segment = that.body.get(i);
         if (head[0] == segment[0] && head[1] == segment[1]) {
            alive = false;
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