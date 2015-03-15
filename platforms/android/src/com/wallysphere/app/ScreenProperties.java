package com.wallysphere.app;

public class ScreenProperties {
	int screenWidth;
	int screenHeight;
	double screenDensity;

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public double getScreenDensity() {
		return screenDensity;
	}

	public void setScreenDensity(double screenDensity) {
		this.screenDensity = screenDensity;
	}

	public ScreenProperties(int screenWidth, int screenHeight, double screenDensity) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.screenDensity = screenDensity;
	}
}