package Bleach.SoundEngine;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import Bleach.Loader.Discette;

public class Boom {

	private static Sound ambientSoundtrack = null;

	public static Sound getAmbientSoundtrack() {
		return ambientSoundtrack;
	}
	
	public static boolean playSound(String soundKey){
		try {
			playSoundInternal(Discette.getSound(soundKey));
		} catch (LineUnavailableException e1) {
			//e1.printStackTrace();
			return false;
		}
		
		return true;
	}

	private static void playSoundInternal(Sound sound) throws LineUnavailableException {

		Clip clip = createClip(sound);
		clip.start();
	}

	public static void setAmbientSoundtrack(Sound ambientSoundtrack) throws LineUnavailableException {
		Boom.ambientSoundtrack = ambientSoundtrack;
		Clip ambientSoundtrackClip = createClip(ambientSoundtrack);
		ambientSoundtrackClip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	private static Clip createClip(Sound sound) throws LineUnavailableException {
		Clip clip = (Clip) AudioSystem.getLine(sound.getInfo());
		clip.open(sound.getAudioInputStream().getFormat(), sound.getAudioData(), 0, sound.getSize());
		return clip;
	}
}
