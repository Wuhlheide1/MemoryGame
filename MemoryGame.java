import sas.*;
import java.awt.Color;
import javax.sound.sampled.*;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class MemoryGame {
    private View view;
    private int[][] savedCoordinates = new int[20000000][2];
    private int roundPublic = 0;
    private int scaled;
    
    public Rectangle[][] cards = new Rectangle[2][2];
    public Text gameOver;
    public Text roundsCompleted;
    
    private Color[] colors = new Color[]{
        new Color(255, 153, 153),
        new Color(153, 204, 255),
        new Color(153, 255, 153),
        new Color(255, 255, 204)
    };

    public MemoryGame(int scale) {
        int spacing = scale/50;
        scaled = scale;
        int cardSize = (scale - 3 * spacing) / 2;
        
        int totalWidth = cardSize * 2 + spacing * 3;
        
        view = new View(totalWidth, totalWidth, "Memory Game");
        view.setBackgroundColor(Color.black);
        
        initializeCards(cardSize, spacing);
    }

    private void initializeCards(int cardSize, int spacing) {
        int colorIndex = 0;
        for(int i = 0; i < cards.length; i++) {
            for(int j = 0; j < cards.length; j++) {
                int x = spacing + (cardSize + spacing) * i;
                int y = spacing + (cardSize + spacing) * j;
                cards[i][j] = new Rectangle(x, y, cardSize, cardSize);
                cards[i][j].setColor(colors[colorIndex++]);
            }
        }
    }

    public void start() {
        int maxRounds = 20000000;
        for(int round = 0; round <= maxRounds; round++) {
            roundPublic = round;

            if(round > 0) {
                showPreviousSequence(round);
            }
            
            random();
            view.wait(500);

            if(!answer()) {
                showGameOver();
                while(!view.keyEnterPressed()) {
                    view.wait(10);
                }
                restart();
            }
            view.wait(500);
        }
    }

    private void showPreviousSequence(int round) {
        for(int i = 0; i < round; i++) {
            changeColor(savedCoordinates[i][0], savedCoordinates[i][1]);
            view.wait(500);
        }
    }

    private void showGameOver() {
        for(int i = 0; i < cards.length; i++) {
            for(int j = 0; j < cards.length; j++) {
                cards[i][j].setColor(Color.BLACK);
            }
        }
        
        String str = Integer.toString(roundPublic);
        gameOver = new Text(0, 0, "Game Over", Color.WHITE);
        roundsCompleted = new Text(0, 0, str, Color.WHITE);
        
        centerText("Game Over", gameOver, scaled/10, true);
        centerText(str, roundsCompleted, scaled/15, false);
    }

    public void centerText(String text, Text textObject, int fontSize, boolean offset) {
        textObject.setFontSerif(true, fontSize);
        
        int viewWidth = scaled;
        int viewHeight = scaled;
        int textWidth = text.length() * (int)(fontSize * 0.6);
        int textHeight = fontSize;
        int xCenter = (viewWidth - textWidth) / 2;
        int yCenter = (viewHeight - textHeight) / 2;
        
        if (offset) {
            textObject.moveTo(xCenter, yCenter - scaled*0.075);
            textObject.setHidden(false);
        } else {
            textObject.moveTo(xCenter, yCenter + scaled*0.075);
        }
    }

    private void random() {
        int randomCardI = (int)(Math.random()*2);
        int randomCardJ = (int)(Math.random()*2);
        savedCoordinates[roundPublic][0] = randomCardI;
        savedCoordinates[roundPublic][1] = randomCardJ;
        changeColor(randomCardI, randomCardJ);
    }

    private void changeColor(int CardI, int CardJ) {
        playSound("ping");
        Color currentColor = cards[CardI][CardJ].getColor();
        float[] hsb = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        Color saturatedColor = Color.getHSBColor(hsb[0], Math.min(1.0f, hsb[1] * 1.9f), Math.max(0.0f, hsb[2] * 0.5f));
        
        cards[CardI][CardJ].setColor(saturatedColor);
        view.wait(350);
        cards[CardI][CardJ].setColor(currentColor);
    }

    private void animate(int pos1, int pos2) {
        playSound("click");
        Rectangle clickedCard = cards[pos1][pos2];
        Color originalColor = clickedCard.getColor();
        clickedCard.setColor(Color.WHITE);
        view.wait(200);
        clickedCard.setColor(originalColor);
    }

    private boolean answer() {
        int countClicks = 0;
        while(countClicks <= roundPublic) {
            for(int i = 0; i < cards.length; i++) {
                for(int j = 0; j < cards.length; j++) {
                    if(cards[i][j].mousePressed()) {
                        animate(i,j);
                        if(i != savedCoordinates[countClicks][0] || j != savedCoordinates[countClicks][1]) {
                            return false;
                        }
                        countClicks++;
                        view.wait(200);

                        if(countClicks > roundPublic) {
                            return true;
                        }
                    }
                }
            }
            view.wait(10);
        }
        return false;
    }

    private void playSound(String soundName) {
        try {
            // Try multiple ways to load the sound file
            AudioInputStream audioInputStream = null;
            File soundFile = new File("sounds/" + soundName + ".wav");
            
            if (soundFile.exists()) {
                // Try loading from file system first
                audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            } else {
                // Try loading from resources/jar
                InputStream resourceStream = getClass().getResourceAsStream("/sounds/" + soundName + ".wav");
                if (resourceStream != null) {
                    audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(resourceStream));
                }
            }
            
            if (audioInputStream != null) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace(); // This will help us see what's going wrong
        }
    }

    private void restart() {
        // Reset game state
        roundPublic = 0;
        savedCoordinates = new int[20000000][2];
        
        // Clear game over text
        if (gameOver != null) {
            view.remove(gameOver);
            view.remove(roundsCompleted);
        }
        
        // Reset card colors
        int colorIndex = 0;
        for(int i = 0; i < cards.length; i++) {
            for(int j = 0; j < cards.length; j++) {
                cards[i][j].setColor(colors[colorIndex++]);
            }
        }
        
        // Start new game
        start();
    }
}