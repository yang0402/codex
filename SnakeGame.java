import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

/**
 * 一个基于 Java Swing 的简单贪吃蛇示例。
 * 运行方式：
 *   javac SnakeGame.java
 *   java SnakeGame
 */
public class SnakeGame extends JPanel implements ActionListener {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int CELL_SIZE = 20;
    private static final int COLS = WIDTH / CELL_SIZE;
    private static final int ROWS = HEIGHT / CELL_SIZE;
    private static final int INITIAL_LENGTH = 4;

    private final LinkedList<Point> snake = new LinkedList<>();
    private final Random random = new Random();
    private final Timer timer = new Timer(110, this);

    private Point food;
    private Direction direction = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
    private boolean running = true;
    private int score = 0;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        initGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e.getKeyCode());
            }
        });

        timer.start();
    }

    private void initGame() {
        snake.clear();
        int startX = COLS / 2;
        int startY = ROWS / 2;

        for (int i = 0; i < INITIAL_LENGTH; i++) {
            snake.add(new Point(startX - i, startY));
        }

        score = 0;
        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        running = true;
        spawnFood();
    }

    private void handleInput(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (direction != Direction.DOWN) {
                    nextDirection = Direction.UP;
                }
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (direction != Direction.UP) {
                    nextDirection = Direction.DOWN;
                }
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (direction != Direction.RIGHT) {
                    nextDirection = Direction.LEFT;
                }
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (direction != Direction.LEFT) {
                    nextDirection = Direction.RIGHT;
                }
                break;
            case KeyEvent.VK_R:
                if (!running) {
                    initGame();
                    timer.start();
                }
                break;
            default:
                break;
        }
    }

    private void spawnFood() {
        Point p;
        do {
            p = new Point(random.nextInt(COLS), random.nextInt(ROWS));
        } while (snake.contains(p));
        food = p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) {
            return;
        }

        direction = nextDirection;

        Point head = snake.getFirst();
        Point nextHead = new Point(head.x, head.y);

        switch (direction) {
            case UP:
                nextHead.y--;
                break;
            case DOWN:
                nextHead.y++;
                break;
            case LEFT:
                nextHead.x--;
                break;
            case RIGHT:
                nextHead.x++;
                break;
            default:
                break;
        }

        if (isCollision(nextHead)) {
            running = false;
            timer.stop();
            repaint();
            return;
        }

        snake.addFirst(nextHead);

        if (nextHead.equals(food)) {
            score++;
            spawnFood();
        } else {
            snake.removeLast();
        }

        repaint();
    }

    private boolean isCollision(Point p) {
        if (p.x < 0 || p.x >= COLS || p.y < 0 || p.y >= ROWS) {
            return true;
        }

        // 允许移动到当前尾巴所在位置（若本回合不吃食物尾巴会前进）
        for (int i = 0; i < snake.size() - 1; i++) {
            if (snake.get(i).equals(p)) {
                return true;
            }
        }

        // 如果会吃到食物，尾巴不移动，需要额外检查尾巴
        return p.equals(food) && snake.getLast().equals(p);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(30, 30, 30));
        for (int x = 0; x < WIDTH; x += CELL_SIZE) {
            g.drawLine(x, 0, x, HEIGHT);
        }
        for (int y = 0; y < HEIGHT; y += CELL_SIZE) {
            g.drawLine(0, y, WIDTH, y);
        }

        g.setColor(Color.RED);
        g.fillOval(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            if (i == 0) {
                g.setColor(new Color(80, 255, 120));
            } else {
                g.setColor(new Color(20, 180, 60));
            }
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("Score: " + score, 15, 30);

        if (!running) {
            g.setFont(new Font("SansSerif", Font.BOLD, 40));
            String gameOver = "Game Over";
            int textWidth = g.getFontMetrics().stringWidth(gameOver);
            g.drawString(gameOver, (WIDTH - textWidth) / 2, HEIGHT / 2 - 10);

            g.setFont(new Font("SansSerif", Font.PLAIN, 22));
            String restart = "按 R 键重新开始";
            int restartWidth = g.getFontMetrics().stringWidth(restart);
            g.drawString(restart, (WIDTH - restartWidth) / 2, HEIGHT / 2 + 30);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Java 贪吃蛇");
        SnakeGame game = new SnakeGame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.requestFocusInWindow();
    }
}
