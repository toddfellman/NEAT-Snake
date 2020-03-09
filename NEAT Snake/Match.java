import java.awt.Graphics;
import java.lang.Math;

class Match {
   private Board gameBoard;
   private SnakeAI[] brains = new SnakeAI[2];
   private Snake[] snakes = new Snake[2];
   
   
   public Match(SnakeAI ss1, SnakeAI ss2) {
      gameBoard = new Board();
      
      brains[0] = ss1;
      brains[1] = ss2;
      
      brains[0].resetNetworks();
      brains[1].resetNetworks();
      
      snakes[0] = new Snake(ss1.getID());
      snakes[1] = new Snake(ss2.getID());
      
      for (Snake s: snakes) {
         gameBoard.activate(s.getBody());
      }
      
   }
   
   public SnakeAI run(boolean display) {//assuming only 2 snakes
      //Main.pause(100);
      
      while (snakes[0].isAlive() && snakes[1].isAlive()) {
         for (byte i = 0; i < 2; i++) {
            if(display) { 
               gameBoard.draw();
            }
            
            snakes[i].setDirection(brains[i].getDirection(gameBoard.board()));
            snakes[i].move(gameBoard, brains[i]);
            
            for (Snake t: snakes) {
               snakes[i].hits(t, brains[i]);
            }
         }
      }
      
      boolean s0 = snakes[0].isAlive();
      boolean s1 = snakes[1].isAlive();
      
      //brains[0].qLearn(!s0);
      //brains[1].qLearn(!s1);
      
      if (s0 && !s1) {
         return brains[0];
      }
      if (!s0 && s1) {
         return brains[1];
      }
      
      if (snakes[0].length() > snakes[1].length()) {
         return brains[0];
      }
      if (snakes[0].length() < snakes[1].length()) {
         return brains[1];
      }
      return brains[(int) (Math.random() * 2)];
      
   }
}