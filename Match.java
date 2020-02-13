import java.awt.Graphics;
import java.lang.Math;

class Match {
   private Board gameBoard;
   private Snake[] snakes = new Snake[2];
   
   
   public Match(Snake ss1, Snake ss2) {
      gameBoard = new Board();
      
      snakes[0] = ss1;
      snakes[1] = ss2;
      
      for (Snake s: snakes) {
         gameBoard.activate(s.getBody());
      }
      
   }
   
   public SnakeAI run() {//assuming only 2 snakes
      while (snakes[0].isAlive() && snakes[1].isAlive()) {
         for (Snake s: snakes) {
            gameBoard.draw(Main.window.getGraphics());
            
            s.setDirection(Main.scan.nextLine());
            s.move(gameBoard);
            for (byte[] t: s.getBody()) {
               System.out.println(t[0] + " , " + t[1]);
            }
            for (Snake t: snakes) {
               s.hits(t);
            }
         }
      }
      System.out.println("ended");
      Main.window.dispose();
      if (snakes[0].isAlive()) {
         if (snakes[1].isAlive()) {
            return snakes[(int) (Math.random() * 2)];
         }
         return snakes[0];
      } 
      if (snakes[1].isAlive()) {
         return snakes[1];
      }
      return snakes[(int) (Math.random() * 2)];
   }
}