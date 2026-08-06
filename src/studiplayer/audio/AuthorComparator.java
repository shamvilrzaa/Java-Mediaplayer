package studiplayer.audio;

import java.util.Comparator;

public class AuthorComparator implements Comparator<AudioFile> {
    public int compare(AudioFile a1, AudioFile a2) {
        if (a1 == null || a2 == null) {
            throw new IllegalArgumentException("Cannot compare null AudioFiles");
        }
        return a1.getAuthor().compareTo(a2.getAuthor());
    }
}
