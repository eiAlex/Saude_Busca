package com.github.com.eiAlex.SaudeBusca.domain;

import com.github.com.eiAlex.SaudeBusca.R;



public class UBS {
    private String name;
    private int category;
    private String description;
    private double latitude;
    private double longitude;
    private String phone;
    private String ambience;
    private String elderly;
    private String equipments;
    private String medicines;


    //gets and sets


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getAmbience() {
        return ambience;
    }

    public void setAmbience(String ambience) {
        this.ambience = ambience;
    }

    public String getElderly() {
        return elderly;
    }

    public void setElderly(String elderly) {
        this.elderly = elderly;
    }

    public String getEquipments() {
        return equipments;
    }

    public void setEquipments(String equipments) {
        this.equipments = equipments;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public static int getCategorySource(int c) {
        switch (c) {
            case 1:
                return (R.drawable.ubs);
            case 2:
                return (R.drawable.hospital);

            default:
                return (R.drawable.ubs);
        }
    }
}
