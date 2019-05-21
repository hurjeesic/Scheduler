package com.example.scheduler;

class SearchListItem {
    private String date;
    private String title;
    private String tags;
    private String index;

    public SearchListItem(String date, String index, String title, String tags) {
        this.date = date;
        this.index = index;
        this.title = title;
        this.tags = tags;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIndex() {
        return index;
    }
}