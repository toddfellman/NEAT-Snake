import java.lang.Math;
import java.awt.Graphics;

class Population {
   private SnakeAI[] pop;
   
   public Population (int popSize) {
      pop = new SnakeAI[popSize];
      
      for (int i = 0; i < popSize; i++) {
         pop[i] = new SnakeAI(i);
      }
   }
   
   public void getOneMatch(boolean display) {
      SnakeAI[] competitors = new SnakeAI[2];
      competitors[0] = pop[(int) (Math.random() * pop.length)];
      do {
         competitors[1] = pop[(int) (Math.random() * pop.length)];
      } while (competitors[0].getID() != competitors[1].getID());
      //System.out.println(competitors[0].getGen() + " , " + competitors[1].getGen());
      Match m = new Match(competitors[0], competitors[1]);
      SnakeAI winner = m.run(display);
      /*if (competitors[0].equals(winner)) {
         competitors[1] = competitors[0].mateWith(pop[(int) (Math.random() * pop.length)] , competitors[1].getID());
      } else {
         competitors[0] = competitors[1].mateWith(pop[(int) (Math.random() * pop.length)] , competitors[0].getID());
      }*/
   }
}