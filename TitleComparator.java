package studiplayer.audio;

import java.util.Comparator;

public class TitleComparator implements Comparator<AudioFile> {
    public int compare(AudioFile o1, AudioFile o2) {
        if (o1 == null || o2 == null) {
            throw new IllegalArgumentException("Cannot compare null AudioFiles");
        }
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
