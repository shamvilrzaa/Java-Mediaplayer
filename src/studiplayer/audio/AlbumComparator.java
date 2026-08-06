package studiplayer.audio;

import java.util.Comparator;

public class AlbumComparator implements Comparator<AudioFile> {
    public int compare(AudioFile a1, AudioFile a2) {
        if (a1 == null || a2 == null) {
            throw new IllegalArgumentException("Cannot compare null AudioFiles");
        }

        String album1 = (a1 instanceof TaggedFile) ? ((TaggedFile) a1).getAlbum() : null;
        String album2 = (a2 instanceof TaggedFile) ? ((TaggedFile) a2).getAlbum() : null;

        if (album1 == null && album2 == null) return 0;
        if (album1 == null) return -1;
        if (album2 == null) return 1;

        return album1.compareTo(album2);
    }
}
