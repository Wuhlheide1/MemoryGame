
public class Main {
    int scales = 600;
    public static void main(String[] args) {
        int scale = 600;  // default value
        
        if (args.length > 0) {
            scale = Integer.parseInt(args[0]);  // Allow custom scale from command line
        }
        
        try {
            MemoryGame game = new MemoryGame(scale);
            game.start();
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Error starting game: " + e.getMessage());
        }
    }
}