import java.awt.Graphics;
import javax.swing.JFrame;
import java.awt.Component;


class Main {
   public static JFrame window;
   
   public static final Thread t = new Thread();
   
   public static void main(String[] args) {
      window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Snkae Training");
      
      
      window.setSize(Board.WIDTH * Board.PIXEL_SIZE, Board.HEIGHT * Board.PIXEL_SIZE + Board.PIXEL_SIZE);
		window.setResizable(false);
      window.setVisible(true);
      
      
      Population pop = new Population(512);
      for (int match = 0; true; match++){
         pop.getOneMatch(match % 16 == 0);
         if (match % 16 == 0) {
            System.out.println(match);
         }
      }
   }
   
   public static void pause(long time) {
      try {
         t.sleep(time);
      } catch (InterruptedException e) {
         System.out.println(e);
      }
   }
}