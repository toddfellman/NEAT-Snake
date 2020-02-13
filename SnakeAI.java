

class SnakeAI extends Snake {
   static int counter = 0;
   private int id;
   
   public SnakeAI() {
      super();
      id = counter;
      counter++;
   }
   
   public boolean equals(SnakeAI that) {
      return id == that.id;
   }
}