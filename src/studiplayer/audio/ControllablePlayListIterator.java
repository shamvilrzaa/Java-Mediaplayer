package studiplayer.audio;

import java.util.*;

public class ControllablePlayListIterator implements Iterator<AudioFile> {
    private List<AudioFile> files;
    private int currentIndex;

    public ControllablePlayListIterator(List<AudioFile> files) {
        this(files, "", SortCriterion.DEFAULT);
    }

    public ControllablePlayListIterator(List<AudioFile> files, String search, SortCriterion sort) {
        this.files = new ArrayList<>();
        String lcSearch = search.toLowerCase();

        for (AudioFile file : files) {
            boolean matches = search.isEmpty();
            if (!matches && file.getAuthor() != null &&
                file.getAuthor().toLowerCase().contains(lcSearch)) matches = true;
            if (!matches && file.getTitle() != null &&
                file.getTitle().toLowerCase().contains(lcSearch)) matches = true;
            if (!matches && file instanceof TaggedFile) {
                TaggedFile tf = (TaggedFile)file;
                if (tf.getAlbum() != null && tf.getAlbum().toLowerCase().contains(lcSearch)) {
                    matches = true;
                }
            }
            if (matches) this.files.add(file);
        }

        if (sort != SortCriterion.DEFAULT) {
            switch (sort) {
                case AUTHOR:
                    this.files.sort(new AuthorComparator());
                    break;
                case TITLE:
                    this.files.sort(new TitleComparator());
                    break;
                case ALBUM:
                    this.files.sort(new AlbumComparator());
                    break;
                case DURATION:
                    this.files.sort(new DurationComparator());
                    break;
                default:
                    break;
            }
        }

        this.currentIndex = 0;
    }

    public boolean hasNext() {
        return currentIndex < files.size();
    }

    public AudioFile next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return files.get(currentIndex++);
    }

    public AudioFile jumpToAudioFile(AudioFile file) {
        int index = files.indexOf(file);
        if (index >= 0) {
            currentIndex = index + 1;
            return file;
        }
        return null;
    }
}