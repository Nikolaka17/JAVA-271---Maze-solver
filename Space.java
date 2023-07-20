import java.awt.Point;

public class Space {
    private int row;
    private int column;
    private int hCost; //How far from end
    private int gCost; //How far from start
    private Space parent = null;
    private boolean wall;

    public Space(Point pos, boolean wall){
        row = (int)pos.getX();
        column = (int)pos.getY();
        this.wall = wall;
    }

    public int cost(){
        return hCost + gCost;
    }

    public int getGCost(){
        return gCost;
    }

    public void setGCost(int newCost){
        gCost = newCost;
    }

    public void setHCost(int newCost){
        hCost = newCost;
    }

    public int getHCost(){
        return hCost;
    }

    public Point getPos(){
        return new Point(row, column);
    }

    public void setParent(Space newParent){
        parent = newParent;
    }

    public Space getParent(){
        return parent;
    }

    public boolean isWall(){
        return wall;
    }
}
