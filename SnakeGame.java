import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;


public class SnakeGame extends Canvas implements Runnable, KeyListener {


    /* Initialize everything we're going to be using.*/
    private final int box_WIDTH = 15;
    private final int box_HEIGHT = 15;
    private final int grid_WIDTH = 25;
    private final int grid_HEIGHT = 25;

    LinkedList<Point> snake = new LinkedList<>();
    private Point apple;
    private int direction = Direction.no_direction;

    private Thread runThread; //gosh object -- helps application run multi-tasking
    private Graphics globalGraphics;


    private GameDisplay display;

    // Gives snake game a way to call methods in GameDisplay
    public SnakeGame(GameDisplay display) {
        this.display = display;
    }


    public void paint(Graphics g) {

        // Initialize snake and its starting location
        snake = new LinkedList<>();
        generateDefaultSnake();

        // Initialize apple and its starting location
        placeApple();


        globalGraphics = g.create(); // create a copy graphic


        if (runThread == null) {
            this.setPreferredSize(new Dimension(640, 480));
            this.addKeyListener(this);
            runThread = new Thread(this); //create a new thread and send in this snakeCanvas because it implements Runnable already
            runThread.start();
        }

    }

    /*
    Setup default snake game;
     */
    public void generateDefaultSnake() {

        // Set score back to 0
        display.updateScore(0);


        // Clear extra body length of snake
        snake.clear();

        // Snake returns to first place;
        snake.add(new Point(0, 2));
        snake.add(new Point(0, 1));
        snake.add(new Point(0, 0));

        // Clear direction
        direction = Direction.no_direction;
    }



    public void draw (Graphics g) {

        // Clear the trail where snake ran over
        g.clearRect(0,0, box_WIDTH * grid_HEIGHT + 10, box_HEIGHT * grid_HEIGHT + 20);

        // Create a new image
        BufferedImage buffer = new BufferedImage(box_WIDTH * grid_WIDTH + 10, box_HEIGHT * grid_HEIGHT + 20 ,BufferedImage.TYPE_INT_ARGB);
        Graphics bufferGraphics = buffer.getGraphics(); //let buffer draws on graphics


        // Draw Grid panel, Snake and Apple
        drawApple(bufferGraphics);
        drawGrid(bufferGraphics);
        drawSnake(bufferGraphics);

        //flip
        g.drawImage(buffer, 0, 0, box_WIDTH*grid_WIDTH + 10, box_HEIGHT * grid_HEIGHT + 20, this);
    }



    /*
    Draw Game board
     */
    public void drawGrid (Graphics g) {

        // Outside rectangle
        g.drawRect(0, 0, box_WIDTH * grid_WIDTH, box_HEIGHT * grid_HEIGHT);

        // Horizontal lines
        for (int y = box_HEIGHT; y < box_HEIGHT * grid_HEIGHT; y += box_HEIGHT) {
            g.drawLine(0, y, box_WIDTH * grid_WIDTH, y);
        }

        // Vertical lines
        for (int x = box_WIDTH; x < box_WIDTH * grid_WIDTH; x += box_WIDTH) {
            g.drawLine(x, 0, x, box_HEIGHT * grid_HEIGHT);
        }
    }


    /*
    Fill color to the box where the snake locates
    */
    public void drawSnake(Graphics g) {

        g. setColor(Color.GREEN);

        for (Point p : snake) {
            g.fillRect(p.x * box_WIDTH, p.y * box_HEIGHT, box_WIDTH, box_HEIGHT);
        }

        g.setColor(Color.BLACK);
    }


    /*
    Fill color to the box where apple locates
    */
    public void drawApple(Graphics g) {

        g.setColor(Color.RED);
        g.fillOval(apple.x * box_WIDTH, apple.y * box_HEIGHT, box_WIDTH, box_HEIGHT);
        g.setColor(Color.BLACK);
    }


    /*
    Generate location of apple
    */
    public void placeApple() {

        Random rng = new Random();
        int randomX = rng.nextInt(grid_WIDTH);
        int randomY = rng.nextInt(grid_HEIGHT);
        Point randomPoint = new Point(randomX, randomY );

        //generate a new apple when snake is at location of apple
        while (snake.contains(randomPoint)) {
            randomX = rng.nextInt(grid_WIDTH);
            randomY = rng.nextInt(grid_HEIGHT);
            randomPoint = new Point(randomX, randomY);
        }

        apple = randomPoint;
    }



    public void move() {

        Point head = snake.peekFirst(); // Head is the first item in snake LinkedList
        Point newPoint = head;


        // Set up a position for head of snake when user hits associated arrow key.
        switch (direction) {

            //User hits up arrow key -- snake head to the North by 1 unit (y+1)
            case Direction.NORTH:
                newPoint = new Point(head.x, head.y + 1 );
                break;

            //User hits down arrow key -- snake head to the South by 1 unit (y-1)
            case Direction.SOUTH:
                newPoint = new Point(head.x, head.y - 1 );
                break;

            //User hits left arrow key -- snake head to the East by 1 unit (x+1)
            case Direction.EAST:
                newPoint = new Point (head.x + 1, head.y);
                break;

            //User hits right arrow key -- snake head to the West by 1 unit (x-1)
            case Direction.WEST:
                newPoint = new Point(head.x - 1, head.y );
                break;
        }

        // Remove tail -- save it for growth later
        snake.remove(snake.peekLast());


        // If snake hits apple, add an extra point to snake length
        if (newPoint.equals(apple)) {


            display.updateScore(10);

            Point addPoint = new Point(apple.x, apple.y );
            switch (direction) {
                case Direction.NORTH:
                    newPoint = new Point(head.x, head.y + 1 );
                    break;
                case Direction.SOUTH:
                    newPoint = new Point(head.x, head.y - 1 );
                    break;
                case Direction.EAST:
                    newPoint = new Point (head.x + 1, head.y);
                    break;
                case Direction.WEST:
                    newPoint = new Point(head.x - 1, head.y );
                    break;
            }

            snake.push(addPoint);
            placeApple();
        }


        else if (newPoint.x < 0 || newPoint.x > (grid_WIDTH - 1)) {

            // If snake hits wall -- run out of play ground --  reset game
            generateDefaultSnake();
            return;


        }

        else if (newPoint.y < 0 || newPoint.y > (grid_HEIGHT - 1)) {

            // If snake hits wall -- run out of play ground --  reset game
            generateDefaultSnake();
            return;
        }


        else if (snake.contains(newPoint)) {

            // If snake run into its body, reset game
            generateDefaultSnake();
            return;
        }

        snake.push(newPoint);

    }




    @Override
    public void run() {

        //make snake moves indefinitely
        while(true) {
            move();
            draw(globalGraphics);

            try {
                Thread.currentThread();
                Thread.sleep(100); // games updates itself every 30 seconds
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }


    /*
    Add a key listener to the frame to process input (i.e. arrow keys)
     */
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {

            case KeyEvent.VK_UP:
                if (direction != Direction.NORTH)
                    direction = Direction.SOUTH;
                break;

            case KeyEvent.VK_DOWN:
                if (direction != Direction.SOUTH)
                    direction= Direction.NORTH;
                break;

            case KeyEvent.VK_LEFT:
                if (direction != Direction.EAST)
                    direction = Direction.WEST;
                break;

            case KeyEvent.VK_RIGHT:
                if (direction != Direction.WEST)
                    direction = Direction.EAST;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }




}


