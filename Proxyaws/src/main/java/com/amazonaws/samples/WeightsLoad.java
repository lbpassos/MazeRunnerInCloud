package com.amazonaws.samples;

import com.sun.net.httpserver.HttpExchange;

class WeightsLoad{
    private long weight;
    private HttpExchange request;
    private String x0;
    private String y0;
    private String x1;
    private String y1;
    private String velocity;
    private String strategy;
    private String maze;
    
    public WeightsLoad(long w, HttpExchange h, String[] p) {
    	this.weight = w;
    	this.request = h;
    	this.x0 = p[0];
    	this.y0 = p[1];
    	this.x1 = p[2];
    	this.y1 = p[3];
    	this.velocity = p[4];
    	this.strategy = p[5];
    	this.maze = p[6];    	
    }
    
    public long getWeight() {
    	return weight;
    }
    public HttpExchange getCaller() {
    	return request;
    }
    public String getX0() {
    	return x0;
    }
    public String getY0() {
    	return y0;
    }
    public String getX1() {
    	return x1;
    }
    public String getY1() {
    	return y1;
    }
    public String getVelocity() {
    	return velocity;
    }
    public String getStrategy() {
    	return strategy;
    }
    public String getMaze() {
    	return maze;
    }
}
