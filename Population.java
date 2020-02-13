import java.lang.Math;
import java.awt.Graphics;

class Population {
   private SnakeAI[] pop;
   
   public Population (int popSize) {
      pop = new SnakeAI[popSize];
      
      for (int i = 0; i < popSize; i++) {
         pop[i] = new SnakeAI();
      }
   }
   
   public void getOneMatch(Graphics g) {
      SnakeAI[] competitors = new SnakeAI[] {pop[(int) (Math.random() * pop.length)], pop[(int) (Math.random() * pop.length)]};
      Match m = new Match(competitors[0], competitors[1]);
      SnakeAI winner = m.run();
   }
}