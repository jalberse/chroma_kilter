package edu.ou.cs.cg.ck;

public final class Point3D
{
    float x;
    float y;
    float z;

    public Point3D(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getZ(){
        return z;
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }

    public void setZ(float z){
        this.z = z;
    }
}