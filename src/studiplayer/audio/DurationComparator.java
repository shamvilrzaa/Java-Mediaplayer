package studiplayer.audio;

import java.util.Comparator;

public class DurationComparator implements Comparator<AudioFile> {
    public int compare(AudioFile a1, AudioFile a2) {
        if (a1 == null || a2 == null) {
            throw new IllegalArgumentException("Cannot compare null AudioFiles");
        }

        Long duration1 = (a1 instanceof SampledFile) ? ((SampledFile) a1).getDuration() : null;
        Long duration2 = (a2 instanceof SampledFile) ? ((SampledFile) a2).getDuration() : null;

        if (duration1 == null && duration2 == null) return 0;
        if (duration1 == null) return -1;
        if (duration2 == null) return 1;

        return duration1.compareTo(duration2);
    }
}
