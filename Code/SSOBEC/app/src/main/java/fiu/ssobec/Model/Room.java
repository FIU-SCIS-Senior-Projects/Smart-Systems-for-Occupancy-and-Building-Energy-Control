package fiu.ssobec.Model;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by ShadowFox on 10/22/2015.
 */
public class Room {
    private RectF room;
    private String roomNumber;
    private double x,y,width,height;
    private boolean occupied;

    public Room(String roomnum, double x, double y, double width, double height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        roomNumber = roomnum;
        float left = (float) (x-width);
        float right = (float) (x+width);
        float top = (float) (y-height);
        float bottom = (float) (y+height);
        room = new RectF(left, top, right, bottom);
    }

    public int getX()
    {
        return (int) x;
    }
    public int getY()
    {
        return (int) y;
    }
    public RectF getRoom()
    {
        return room;
    }
    public String getRoomNumber()
    {
        return roomNumber;
    }
    public boolean getOccupied()
    {
        return occupied;
    }
    public void rescale(PointF p)
    {
        float left = (float) (p.x-width);
        float right = (float) (p.x+width);
        float top = (float) (p.y-height);
        float bottom = (float) (p.y+height);
        room = new RectF(left, top, right, bottom);
    }
    public void setOccupied(boolean occu)
    {
        occupied = occu;
    }
}
