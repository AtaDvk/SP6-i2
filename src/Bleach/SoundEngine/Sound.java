package Bleach.SoundEngine;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;

public class Sound {
	private byte[] audio;
	private AudioInputStream audioInputStream;
	private DataLine.Info info;
	private int size;

	public Sound(AudioInputStream audioInputStream, int size, Info info, byte[] audioData) {
		this.audioInputStream = audioInputStream;
		this.size = size;
		this.info = info;
		this.audio = audioData;
	}

	public byte[] getAudioData() {
		return audio;
	}

	public AudioInputStream getAudioInputStream() {
		return audioInputStream;
	}

	public DataLine.Info getInfo() {
		return info;
	}

	public int getSize() {
		return size;
	}
}