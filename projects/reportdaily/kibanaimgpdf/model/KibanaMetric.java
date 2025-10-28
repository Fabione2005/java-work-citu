package com.citi.reports.model;

public class KibanaMetric {
    private String title;
    private Long count;

    public KibanaMetric(String title, Long count) {
        this.title = title;
        this.count = count;
    }

    public String getTitle() { return title; }
    public Long getCount() { return count; }

    @Override
    public String toString() {
        return title + " => " + count;
    }
}
