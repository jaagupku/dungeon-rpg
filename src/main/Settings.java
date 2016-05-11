package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Settings {

	private boolean fullscreen;
	private String aspectRatio;
	private int winWidth, winHeight;
	private double masterVolume, musicVolume, soundVolume;
	private double scale;
	private static final Settings DEFAULT = new Settings(false, "16:9", 1280, 720, 1.0, 1.0, 1.0);

	public Settings(boolean fullscreen, String aspectRatio, int winWidth, int winHeight, double masterVolume,
			double musicVolume, double soundVolume) {
		super();
		this.fullscreen = fullscreen;
		this.aspectRatio = aspectRatio;
		setWindowSize(winWidth, winHeight);
		this.masterVolume = masterVolume;
		this.musicVolume = musicVolume;
		this.soundVolume = soundVolume;
	}

	public static ObservableList<String> getAspectRatioLabels() {
		return FXCollections.observableArrayList("4:3", "16:9", "16:10");
	}

	public static ObservableList<String> getResolutionLabels(String ratio) {
		switch (ratio) {
		case "4:3":
			return FXCollections.observableArrayList("640x480", "800x600", "1024x768", "1152x864", "1280x960",
					"1400x1050", "1600x1200");
		case "16:9":
			return FXCollections.observableArrayList("852x480", "1280x720", "1365x768", "1600x900", "1920x1080");
		case "16:10":
			return FXCollections.observableArrayList("1440x900", "1680x1050", "1920x1200");
		default:
			throw new IllegalArgumentException("Illegal aspect ratio " + ratio);
		}

	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public String getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public void setWindowSize(int width, int height) {
		winWidth = width;
		winHeight = height;
		scale = Math.sqrt(Math.pow(winWidth, 2) + Math.pow(winHeight, 2)) / 930;
	}

	public int getWinWidth() {
		return winWidth;
	}

	public int getWinHeight() {
		return winHeight;
	}

	public double getMasterVolume() {
		return masterVolume;
	}

	public void setMasterVolume(double masterVolume) {
		this.masterVolume = masterVolume;
	}

	public double getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(double musicVolume) {
		this.musicVolume = musicVolume;
	}

	public double getSoundVolume() {
		return soundVolume;
	}

	public void setSoundVolume(double soundVolume) {
		this.soundVolume = soundVolume;
	}

	public static Settings loadSettings() {
		// TODO
		return DEFAULT;
	}

	public static void saveSettings(Settings s) {

	}

	public double getScale() {
		return scale;
	}
}
