package studiplayer.audio;

public class AudioFileFactory {
    public static AudioFile createAudioFile(String path) throws NotPlayableException{
        String lowerPath = path.toLowerCase();

        if (lowerPath.endsWith(".wav")) {
            return new WavFile(path);
        } else if (lowerPath.endsWith(".mp3") || lowerPath.endsWith(".ogg")) {
            return new TaggedFile(path);
        } else {
        	throw new NotPlayableException(path, "Unknown suffix for AudioFile \"" + path + "\"");

        }
    }
}