import java.awt.Graphics;
import java.awt.Color;
import java.lang.Math;
import java.util.ArrayList;

class Board {
   public static final byte WIDTH = 20;
   public static final byte HEIGHT = 20;
   public static final int AREA = WIDTH * HEIGHT;
   public static final byte PIXEL_SIZE = 20;
   
   private final boolean[][] board;
   
   private final ArrayList<byte[]> food = new ArrayList<byte[]>();
   
   public Board() {
      board = new boolean[WIDTH][HEIGHT];
      for (byte x = 0; x < WIDTH; x++) {
         for (byte y = 0; y < HEIGHT; y++) {
            board[x][y] = false;
         }
      }
      
      food.add(randomSquare());
      
      activate(food);
   }
   
   public byte[] getFood() {
      return food.get(0);
   }
   public boolean[][] board() {
      return board;
   }
   
   
   
   public void draw() {
      final Graphics g = Main.window.getGraphics();
      board[food.get(0)[0]][food.get(0)[1]] = true;
      
      for (byte x = 0; x < WIDTH; x++) {
         for (byte y = 0; y < HEIGHT; y++) {
            if (board[x][y]) {
               g.setColor(Color.white);
            } else {
               g.setColor(Color.black);
            }
            g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE + PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
         }
      }
   }
   
   public void activate(byte x, byte y) {
      board[x][y] = true;
   }
   
   public void activate(ArrayList<byte[]> tiles) {
      for (byte[] t : tiles) {
         board[t[0]][t[1]] = true;
      }
   }
   
   public void erase(ArrayList<byte[]> tiles) {
      for (byte[] t : tiles) {
         board[t[0]][t[1]] = false;
      }
   }
   
   public void moveFood() {
      erase(food);
      food.set(0, randomSquare());
      activate(food);
   }
   
   public static byte[] randomSquare() {
      return new byte[]{(byte) (Math.random() * WIDTH), (byte)(Math.random() * HEIGHT)};
   }
}