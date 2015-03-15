package com.wallysphere.app;

public class SettingsData {
	Boolean onoff;
	String frequency;
	String network;

	/**
	 * @return the onoff
	 */
	public Boolean getOnoff() {
		return onoff;
	}

	/**
	 * @param onoff the onoff to set
	 */
	public void setOnoff(Boolean onoff) {
		this.onoff = onoff;
	}

	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the network
	 */
	public String getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(String network) {
		this.network = network;
	}

	public SettingsData(Boolean onoff, String frequency, String network) {
		this.onoff = onoff;
		this.frequency = frequency;
		this.network = network;
	}
}