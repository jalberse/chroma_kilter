package edu.ou.cs.cg.ck;

import java.util.*;

public final class Face
{
    private LinkedList<Point3D> verts;

    public Face(){
        verts = new LinkedList<Point3D>();
    }

    public Point3D get(int i){
        return verts.get(i);
    }

    public void addVert(Point3D v)
    {
        verts.add(v);
    }

    public int getSize()
    {
        return verts.size();
    }
}