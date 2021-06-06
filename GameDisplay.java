import javax.swing.*;
import java.awt.*;

public class GameDisplay extends JFrame {

    private SnakeGame game = new SnakeGame(this);
    private JPanel root = new JPanel();
    private int score = 0;
    private JLabel scoreLabel = new JLabel("Score: " + score);


    public GameDisplay() {

        /* Instruction pops up when open game */
        JOptionPane.showMessageDialog(this, "Use arrow keys to direct your snake eating the apple.\n" +
                        "Snake eats apple: +10 on score and getting growth.\n" +
                        "Snake hits wall or its body: Game Over.\n" +
                        "Good luck!",
                "Snake Game Instruction",
                JOptionPane.PLAIN_MESSAGE);


        // Add game to the center and scoreLabel to the bottom of BorderLayout
        root.setLayout(new BorderLayout());
        root.add(game, BorderLayout.CENTER);
        root.add(scoreLabel, BorderLayout.SOUTH);


        this.getContentPane().add(root);
        this.setTitle("Snake Game");
        this.setSize(450,450);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);

    }

    public void updateScore(int extraScore) {

        if (extraScore > 0) {
            // update the score field
            this.score += extraScore;
            // reset the scoreLabel text
            scoreLabel.setText("Score: " + this.score);
            // refresh the GUI
            repaint();
        }

        else {
            //update score to 0
            this.score = extraScore;
            // reset the scoreLabel text

            scoreLabel.setText("Score: " + this.score);
            // refresh the GUI
            repaint();
        }

    }



    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new GameDisplay());
    }

}



