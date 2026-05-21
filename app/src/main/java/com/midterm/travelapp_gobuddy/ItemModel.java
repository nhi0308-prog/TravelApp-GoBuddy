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
    private ArrayList<String> pics;
    private boolean wifi;
    private boolean guide;

    private String guideName;
    private int totalGuest;
    private String tourGuidePhone;

    // để đọc dữ liệu kiểu Id, Name, ImagePath từ Firebase
    private int Id;
    private String Name;
    private String ImagePath;

    // ===== CHỖ BỔ SUNG: Biến hứng dữ liệu danh mục từ Firebase =====
    private String category;

    public ItemModel() {
    }

    // ===== CHỖ BỔ SUNG: Getter và Setter cho category =====
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    // ========================================================

    public String getTimeTour() {
        return timeTour;
    }

    public void setTimeTour(String timeTour) {
        this.timeTour = timeTour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // lấy tên hiển thị, ưu tiên title, nếu không có thì lấy Name
    public String getDisplayTitle() {
        if (title != null && !title.isEmpty()) {
            return title;
        }
        if (Name != null && !Name.isEmpty()) {
            return Name;
        }
        return "No title";
    }
    public ArrayList<String> getPics() {
        return pics;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // lấy địa chỉ hiển thị
    public String getDisplayAddress() {
        if (address != null && !address.isEmpty()) {
            return address;
        }
        return "No address";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDateTour() {
        return dateTour;
    }

    public void setDateTour(String dateTour) {
        this.dateTour = dateTour;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ArrayList<String> getPicList() {
        return pic;
    }

    public void setPic(ArrayList<String> pic) {
        this.pic = pic;
    }

    public ArrayList<String> getPic() {
        if (pic != null) {
            return pic;
        }
        return new ArrayList<>();
    }

    //  lấy link ảnh đầu tiên, ưu tiên pic[0], nếu không có thì lấy ImagePath
    public String getDisplayImage() {
        if (pic != null && !pic.isEmpty()) {
            return pic.get(0);
        }
        if (ImagePath != null && !ImagePath.isEmpty()) {
            return ImagePath;
        }
        return "";
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isGuide() {
        return guide;
    }

    public void setGuide(boolean guide) {
        this.guide = guide;
    }

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public int getTotalGuest() {
        return totalGuest;
    }

    public void setTotalGuest(int totalGuest) {
        this.totalGuest = totalGuest;
    }

    public String getTime() {
        return timeTour;
    }

    public String getTourGuidePhone() {
        return tourGuidePhone;
    }

    public void setTourGuidePhone(String tourGuidePhone) {
        this.tourGuidePhone = tourGuidePhone;
    }

    // getter/setter cho Id, Name, ImagePath

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }
}