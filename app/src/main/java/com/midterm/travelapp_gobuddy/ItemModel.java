package com.midterm.travelapp_gobuddy;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemModel implements Serializable {
    private String title;
    private String address;
    private String description;
    private String duration;
    private String timeTour;
    private String dateTour;
    private int price;
    private int bed;
    private String distance;
    private double score;
    private ArrayList<String> pic;
    private boolean wifi;
    private boolean guide;

    // Thêm 2 biến này vào đây (nằm ngoài các hàm)
    private String guideName;
    private int totalGuest;

    // Thêm biến số điện thoại để sửa lỗi đỏ bên TicketActivity
    private String tourGuidePhone;

    // Constructor rỗng
    public ItemModel() {
    }

    // Các hàm Getter và Setter
    public String getTimeTour() { return timeTour; }
    public void setTimeTour(String timeTour) { this.timeTour = timeTour; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getDateTour() { return dateTour; }
    public void setDateTour(String dateTour) { this.dateTour = dateTour; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getBed() { return bed; }
    public void setBed(int bed) { this.bed = bed; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public ArrayList<String> getPicList() { return pic; }
    public void setPic(ArrayList<String> pic) { this.pic = pic; }

    // Hàm lấy ảnh đầu tiên cho Glide
    public ArrayList<String> getPic() {
        if (pic != null) {
            return pic;
        }
        return new ArrayList<>();
    }
    public boolean isWifi() { return wifi; }
    public void setWifi(boolean wifi) { this.wifi = wifi; }

    public boolean isGuide() { return guide; }
    public void setGuide(boolean guide) { this.guide = guide; }


    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public int getTotalGuest() { return totalGuest; }
    public void setTotalGuest(int totalGuest) { this.totalGuest = totalGuest; }

    public String getTime() { return timeTour; }

    // Hàm Getter/Setter cho số điện thoại
    public String getTourGuidePhone() { return tourGuidePhone; }
    public void setTourGuidePhone(String tourGuidePhone) { this.tourGuidePhone = tourGuidePhone; }
}