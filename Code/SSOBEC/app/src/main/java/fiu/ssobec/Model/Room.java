package fiu.ssobec.Model;

import android.graphics.PointF;
import android.graphics.RectF;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by ShadowFox on 10/22/2015.
 */
public class Room {
    private RectF room;
    private String roomNumber;
    private double x,y,width,height;
    private boolean occupied;
    private int count;
    private Marker center;

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
        count = 0;
        occupied = false;
    }

    public double getLatitude()
    {
        return x;
    }
    public double getLongitude()
    {
        return y;
    }
    public double getRadius()
    {
        if(height > width)
            return height;
        return width;
    }
    public Marker getMarker()
    {
        return center;
    }
    public void setMarker(Marker m)
    {
        this.center = m;
    }
    public RectF getRoom()
    {
        return room;
    }
    public void enter()
    {
        count++;
        occupied = true;
    }
    public void exit()
    {
        if(count > 0)
        {
            count--;
        }
        else
        {
            count = 0;
            occupied = false;
        }

    }
    public String getRoomNumber()
    {
        return roomNumber;
    }
    public boolean getOccupied()
    {
        return occupied;
    }
    public void setOccupied(boolean occu)
    {
        occupied = occu;
    }
}
