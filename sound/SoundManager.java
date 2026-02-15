package sound;

import java.net.URL;
import java.util.logging.Logger;
import javax.sound.sampled.*;

public class SoundManager {
    private static final Logger LOGGER = Logger.getLogger(SoundManager.class.getName());
    private static boolean isSoundEnabled = true;

    public static void setSoundEnabled(boolean enabled) {
        isSoundEnabled = enabled;
    }

    public static void play(String fileName) {
        if (!isSoundEnabled) return;
        try {
            URL soundURL = SoundManager.class.getResource("/sound/" + fileName);
            if (soundURL == null) {
                LOGGER.warning("Sound file not found: " + fileName);
                return;
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            LOGGER.severe("Error playing sound " + fileName + ": " + e.getMessage());
        }
    }
}