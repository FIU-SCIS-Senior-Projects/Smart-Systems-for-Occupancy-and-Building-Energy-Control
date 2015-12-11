package fiu.ssobec.Model;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * Created by ShadowFox on 10/22/2015.
 */
public class Room {
    private String roomNumber;
    private double lat,lng,width,height;
    private boolean occupied;
    private int count;
    private Marker center;
    private LatLng topLeft, topRight, botRight, botLeft;
    private Polygon area;
    private RectF fence;

    public Room(String roomnum, double x, double y, double width, double height)
    {
        this.lat = x;
        this.lng = y;
        this.width = width;
        this.height = height;
        roomNumber = roomnum;
        float left = (float) (x-width);
        float right = (float) (x+width);
        float top = (float) (y-height);
        float bottom = (float) (y+height);
        count = 0;
        occupied = false;
        topLeft = new LatLng(lat-width,lng+height);
        topRight = new LatLng(lat+width,lng+height);
        botRight = new LatLng(lat+width,lng-height);
        botLeft = new LatLng(lat-width,lng-height);
        fence = new RectF(left,top,right,bottom);
    }

    public PolygonOptions getPolyOptions()
    {
        return new PolygonOptions().add(botLeft, topLeft, topRight, botRight, botLeft)
                .strokeColor(Color.BLACK)
                .fillColor(Color.parseColor("#51000000")).strokeWidth(2);
    }
    public double getLatitude()
    {
        return lat;
    }
    public double getLongitude()
    {
        return lng;
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
    public boolean contains(PointF point)
    {
        return fence.contains(point.x,point.y);
    }
    public boolean getOccupied()
    {
        return occupied;
    }
    public void setOccupied(boolean occu)
    {
        occupied = occu;

    }
    public void occupied()
    {
        if(occupied)
            area.setFillColor(Color.GREEN);
        else
            area.setFillColor(Color.RED);
    }
    public void setPoly(Polygon poly)
    {
        this.area = poly;
    }
}
