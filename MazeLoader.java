// File             : MazeLoader.java
// Author           : David W. Collins Jr.
// Date Created     : 03/01/2016
// Last Modified    : 03/21/2018
// Description      : This is the MazeLoader file for Math 271 where students
//                    will implement the recursive routine to "solve" the maze.
//package mazesolver;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;


/** This is the main class that defines the window to load the maze
 * 
 * @author collindw
 */
public class MazeLoader {
    
    private JFrame window;
    private Scanner fileToRead;
    private JPanel[][] grid;
    private static final Color WALL_COLOR = Color.BLUE.darker();
    private static final Color PATH_COLOR = Color.MAGENTA.brighter();
    private static final Color OPEN_COLOR = Color.WHITE;
    private static final Color BAD_PATH_COLOR  = Color.RED;
    private static int ROW;
    private static int COL;
    private String data;
    private Point start;
    private boolean allowMazeUpdate;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem[] loadMaze;
    private Timer timer;
    private JFileChooser mazeFile;
    private String lastDirectory = null;
    
    private int[][] path;
    private boolean[][] visited;
    
    /** Default constructor - initializes all private values
     * 
     */
    public MazeLoader() {
        // Intialize other "stuff"
        start = new Point();
        allowMazeUpdate = true;
        timer = new Timer(100, new TimerListener());
        
        // Create the maze window
        window = new JFrame("Maze Program");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Need to define the layout - as a grid depending on the number
        // of grid squares to use. Open the file and read in the size.
        try {
            
            fileToRead = new Scanner(new File("maze.txt"));
            ROW = fileToRead.nextInt();
            COL = fileToRead.nextInt();
        }
        catch(FileNotFoundException e) {
            JOptionPane.showMessageDialog(window,"Default maze not found. " +
                    "\nSelect a maze to solve from the menu," +
                    "\nor rename maze to maze.txt", "Error", JOptionPane.ERROR_MESSAGE);
            allowMazeUpdate = false;
        }

        if(allowMazeUpdate) {
            // Now establish the Layout - appropriate to the grid size
            window.setLayout(new GridLayout(ROW, COL));
            grid= new JPanel[ROW][COL];
            data = fileToRead.nextLine();
            for(int i=0; i<ROW; i++) {
                data = fileToRead.nextLine();
                for(int j=0; j<COL; j++) {
                    grid[i][j] = new JPanel();
                    grid[i][j].setName("" + i + ":" + j);
                    if(data.charAt(j) == '*') 
                        grid[i][j].setBackground(WALL_COLOR);
					// Do not add a mouse listener to the border square
                    else if(i != 0 && j != 0 && i != COL-1 && j != ROW-1) {
						grid[i][j].setBackground(OPEN_COLOR);
						grid[i][j].addMouseListener(new MazeListener());
                    }
					else // This should be the exit(s) on the maze
						grid[i][j].setBackground(OPEN_COLOR);
					
                    window.add(grid[i][j]);
                }
            }
            fileToRead.close();
            window.pack();
        }

        // Add the menu to the window
        menuBar = new JMenuBar();
        menu = new JMenu("Load Maze...");
        loadMaze = new JMenuItem[2];
        loadMaze[0] = new JMenuItem("Load New Maze from another file...");
        loadMaze[0].addActionListener(new LoadMazeFromFile());
        loadMaze[1] = new JMenuItem("Load New Maze from current maze...");
        loadMaze[1].addActionListener(new ReloadCurrentMaze());
        menu.add(loadMaze[0]);
        menu.add(loadMaze[1]);
        menuBar.add(menu);
        window.setJMenuBar(menuBar);
        
        if(!allowMazeUpdate)
            window.setSize(100,50);
       
        // Finally, show the maze
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
    
    /** MazeListener class reacts to mouse presses - only when the current
     *  block that is clicked is a valid starting point within the maze.
     */
    private class MazeListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        /** mousePressed method defines the (x,y) coordinate of the starting
         *  square within the maze. Note: the start Point object does NOT
         *  reference the pixel location, rather the matrix location.
         * @param e - the MouseEvent created upon mouse click.
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if(!((JPanel)e.getSource()).getBackground().equals(WALL_COLOR) &&
                    !timer.isRunning()) {
                data = ((JPanel)e.getSource()).getName();
                start.x = Integer.parseInt(data.substring(0,data.indexOf(":")));
                start.y = Integer.parseInt(data.substring(data.indexOf(":")+1));
              
                // Find the maze solution
                if(!findPath(start))
                    JOptionPane.showMessageDialog(window,"Cannot exit maze.");
                else{
                    JOptionPane.showMessageDialog(window, "Maze Exited!");
                }
                    
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        
    }
    
    /** findPath is the recursive routine to find the solution through the maze
     * 
     * @param p - the current Point in the maze
     * @return whether or not a solution has been found.
     */
    public boolean findPath(Point p)  {
        path = new int[ROW][COL];
        visited = new boolean[ROW][COL];
        for(int i = 0; i < path.length; i++){
            for(int j = 0; j < path[i].length; j++){
                path[i][j] = -1;
                visited[i][j] = false;
                if(grid[i][j].getBackground() == PATH_COLOR){
                    grid[i][j].setBackground(OPEN_COLOR);
                }
                
            }
        }
        
        path[(int)p.getX()][(int)p.getY()] = 0;
        visited[(int)p.getX()][(int)p.getY()] = true;
        grid[(int)p.getX()][(int)p.getY()].setBackground(PATH_COLOR);

        if(findPathR((int)p.getY(), (int)p.getX(), 1)){


            return true;
        }
        
        return false;
    }

    public boolean findPathR(int x, int y, int count){
        if(x == 0 || x == COL - 1 || y == 0 || y == ROW - 1){
            return true;
        }
        if(grid[y][x + 1].getBackground() == OPEN_COLOR && !visited[y][x + 1]){
            path[y][x + 1] = count;
            grid[y][x + 1].setBackground(PATH_COLOR);
            visited[y][x + 1] = true;
            if(findPathR(x + 1, y, count + 1)){
                return true;
            }
            path[y][x + 1] = -1;
            grid[y][x + 1].setBackground(OPEN_COLOR);
        }
        if(grid[y - 1][x].getBackground() == OPEN_COLOR && !visited[y - 1][x]){
            path[y-1][x] = count;
            grid[y-1][x].setBackground(PATH_COLOR);
            visited[y-1][x] = true;
            if(findPathR(x, y-1, count+1)){
                return true;
            }
            path[y-1][x] = -1;
            grid[y-1][x].setBackground(OPEN_COLOR);
        }
        if(grid[y][x-1].getBackground() == OPEN_COLOR && !visited[y][x-1]){
            path[y][x-1] = count;
            grid[y][x-1].setBackground(PATH_COLOR);
            visited[y][x-1] = true;
            if(findPathR(x-1, y, count+1)){
                return true;
            }
            path[y][x-1] = -1;
            grid[y][x - 1].setBackground(OPEN_COLOR);
        }
        if(grid[y+1][x].getBackground() == OPEN_COLOR && !visited[y+1][x]){
            path[y+1][x] = count;
            grid[y+1][x].setBackground(PATH_COLOR);
            visited[y+1][x] = true;
            if(findPathR(x, y+1, count+1)){
                return true;
            }
            path[y+1][x] = -1;
            grid[y+1][x].setBackground(OPEN_COLOR);
        }
        return false;
    }
    
    /** ReloadCurrentMaze class listens to menu clicks - simply
     *  wipes the current state of the maze.
     */
    private class ReloadCurrentMaze implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for(int i=0; i<ROW; i++)
                for(int j=0; j<COL; j++)
                    if(grid[i][j].getBackground().equals(PATH_COLOR) ||
                       grid[i][j].getBackground().equals(BAD_PATH_COLOR))
                         grid[i][j].setBackground(OPEN_COLOR);
        }
    }
    
    /** LoadMazeFromFile class listens to menu clicks - if the student
     *  wishes to earn extra credit, implement this method by utilizing a
     *  FileChooser to allow the user to choose the maze file, rather than
     *  have it hard-coded in the program as "maze.txt"
     */
    private class LoadMazeFromFile implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(window, "Feature not yet implemented",
                    "Extra Credit #2", JOptionPane.WARNING_MESSAGE);
        }
    } // end of LoadMazeFromFile class
    
    /** TimerListener class - Extra credit for students: instead of simply
     *  showing the solution path, show the solution path & any incorrect
     *  paths (and backtracking) by saving the Points in the maze visited
     *  in a "solutionArray", and in this timer method, each time the "timer"
     *  goes off, print the new state of the board according to the 
     *  solution Array. This will give the user a slowed down visualization
     *  of your recursive routine (although it would have finished already)
	 *  Additionally, you're welcome to use a container class to not only 
	 *  track the solution, but all the incorrect paths and display not only 
	 *  the correct path, but all the incorrect path choices made in the
	 *  recursive steps.
     */
    private class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}
