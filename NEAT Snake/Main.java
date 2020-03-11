import java.awt.Graphics;
import javax.swing.JFrame;
import java.awt.Component;


class Main {
   public static final JFrame window = new JFrame();
   
   public static final Thread t = new Thread();
   
   public static void main(String[] args) {
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Snake Training");
      
      
      window.setSize(Board.WIDTH * Board.PIXEL_SIZE, Board.HEIGHT * Board.PIXEL_SIZE + Board.PIXEL_SIZE);
		window.setResizable(false);
      window.setVisible(true);
      
      
      final Population pop = new Population(3);
      for (int match = 0; true; match++){
         pop.getOneMatch(true);//match % 16 == 0);
         if (match % 16 == 0) {
            System.out.println(match);
         }
      }
   }
   
   public static final void pause(long time) {
      try {
         t.sleep(time);
      } catch (InterruptedException e) {
         System.out.println(e);
      }
   }
}