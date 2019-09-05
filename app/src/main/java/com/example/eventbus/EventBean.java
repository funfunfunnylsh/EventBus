package com.example.eventbus;

class EventBean {
    private String one;
    private String two;

    public EventBean(String one, String two) {
        this.one = one;
        this.two = two;
    }

    public String getOne() {
        return one;
    }

    public String getTwo() {
        return two;
    }
}
