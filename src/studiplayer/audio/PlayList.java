package studiplayer.audio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PlayList implements Iterable<AudioFile> {
    private List<AudioFile> list = new LinkedList<>();
    private String search = "";
    private SortCriterion sortCriterion = SortCriterion.DEFAULT;
    private int current = 0;

    public PlayList() {}

    public PlayList(String m3uPathname) {
        try {
            loadFromM3U(m3uPathname);
        } catch (NotPlayableException e) {
            throw new RuntimeException("Unable to load M3U file: " + m3uPathname, e);
        }
    }


    public void add(AudioFile file) {
        list.add(file);
    }

    public void remove(AudioFile file) {
        list.remove(file);
        if (current >= list.size()) current = Math.max(0, list.size() - 1);
    }

    public int size() {
        return list.size();
    }

    public AudioFile currentAudioFile() {
        if (list.isEmpty()) return null;
        
        List<AudioFile> filteredSortedList = new ArrayList<>();
        String lcSearch = search.toLowerCase();

        for (AudioFile file : list) {
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
            if (matches) filteredSortedList.add(file);
        }

        if (sortCriterion != SortCriterion.DEFAULT) {
            switch (sortCriterion) {
                case AUTHOR:
                    filteredSortedList.sort(new AuthorComparator());
                    break;
                case TITLE:
                    filteredSortedList.sort(new TitleComparator());
                    break;
                case ALBUM:
                    filteredSortedList.sort(new AlbumComparator());
                    break;
                case DURATION:
                    filteredSortedList.sort(new DurationComparator());
                    break;
                default:
                    break;
            }
        }
        
        if (filteredSortedList.isEmpty()) return null;
        
        if (current >= filteredSortedList.size()) {
            current = 0;
        }
        
        return filteredSortedList.get(current);
    }

    public void nextSong() {
        if (!list.isEmpty()) {
            List<AudioFile> filteredSortedList = new ArrayList<>();
            String lcSearch = search.toLowerCase();

            for (AudioFile file : list) {
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
                if (matches) filteredSortedList.add(file);
            }

            if (sortCriterion != SortCriterion.DEFAULT) {
                switch (sortCriterion) {
                    case AUTHOR:
                        filteredSortedList.sort(new AuthorComparator());
                        break;
                    case TITLE:
                        filteredSortedList.sort(new TitleComparator());
                        break;
                    case ALBUM:
                        filteredSortedList.sort(new AlbumComparator());
                        break;
                    case DURATION:
                        filteredSortedList.sort(new DurationComparator());
                        break;
                    default:
                        break;
                }
            }
            
            if (!filteredSortedList.isEmpty()) {
                current = (current + 1) % filteredSortedList.size();
            }
        }
    }



    public void loadFromM3U(String path) throws NotPlayableException {
        list.clear();
        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    try {
                        add(AudioFileFactory.createAudioFile(line));
                    } catch (NotPlayableException e) {
                        System.err.println("Skipping unplayable file: " + line);
                    }
                }
            }
        } catch (IOException e) {
            throw new NotPlayableException(path, "Error reading playlist file", e);
        }
    }

    public void saveAsM3U(String pathname) {
        try (FileWriter writer = new FileWriter(pathname)) {
            String sep = System.getProperty("line.separator");
            for (AudioFile file : list) {
                writer.write(file.getPathname() + sep);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving playlist", e);
        }
    }

    public List<AudioFile> getList() {
        return list;
    }

    public SortCriterion getSortCriterion() {
        return sortCriterion;
    }

    public void setSortCriterion(SortCriterion sortCriterion) {
        this.sortCriterion = sortCriterion;
        current = 0;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
        current = 0;
    }

    @Override
    public Iterator<AudioFile> iterator() {
        return new ControllablePlayListIterator(list, search, sortCriterion);
    }

    public void jumpToAudioFile(AudioFile file) {
        List<AudioFile> filteredSortedList = new ArrayList<>();
        String lcSearch = search.toLowerCase();

        for (AudioFile audioFile : list) {
            boolean matches = search.isEmpty();
            if (!matches && audioFile.getAuthor() != null &&
                audioFile.getAuthor().toLowerCase().contains(lcSearch)) matches = true;
            if (!matches && audioFile.getTitle() != null &&
                audioFile.getTitle().toLowerCase().contains(lcSearch)) matches = true;
            if (!matches && audioFile instanceof TaggedFile) {
                TaggedFile tf = (TaggedFile)audioFile;
                if (tf.getAlbum() != null && tf.getAlbum().toLowerCase().contains(lcSearch)) {
                    matches = true;
                }
            }
            if (matches) filteredSortedList.add(audioFile);
        }
        
        if (sortCriterion != SortCriterion.DEFAULT) {
            switch (sortCriterion) {
                case AUTHOR:
                    filteredSortedList.sort(new AuthorComparator());
                    break;
                case TITLE:
                    filteredSortedList.sort(new TitleComparator());
                    break;
                case ALBUM:
                    filteredSortedList.sort(new AlbumComparator());
                    break;
                case DURATION:
                    filteredSortedList.sort(new DurationComparator());
                    break;
                default:
                    break;
            }
        }
        
        int index = filteredSortedList.indexOf(file);
        if (index >= 0) {
            current = index;
        }
    }
    
    public void filter() {
    }

    public void sort() {
    }

}