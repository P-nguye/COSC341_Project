package com.example.cosc341_project;

public class Schedule {
    private String id, title, date, time, garden, repeat, notes;
    private boolean reminder;

    public Schedule() {
        // Default constructor required for Firebase
    }

    public Schedule(String id, String title, String date, String time, String garden,String repeat, String notes, boolean reminder) {
        this.id=id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.garden = garden;
        this.repeat = repeat;
        this.notes = notes;
        this.reminder = reminder;
    }

    public String getDate() {
        return date;
    }

    public String getGarden() {
        return garden;
    }

    public String getNotes() {
        return notes;
    }

    public String getRepeat() {
        return repeat;
    }


    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGarden(String garden) {
        this.garden = garden;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }
}
