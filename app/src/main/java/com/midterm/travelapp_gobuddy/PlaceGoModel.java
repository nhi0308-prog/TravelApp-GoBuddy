package com.midterm.travelapp_gobuddy;

import java.io.Serializable;

public class PlaceGoModel implements Serializable {
    private int Id;
    private String ImagePath;
    private String Name;

    public PlaceGoModel() { }

    public int getId() { return Id; }
    public void setId(int Id) { this.Id = Id; }

    public String getImagePath() { return ImagePath; }
    public void setImagePath(String ImagePath) { this.ImagePath = ImagePath; }

    public String getName() { return Name; }
    public void setName(String Name) { this.Name = Name; }
}