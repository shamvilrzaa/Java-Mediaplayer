package studiplayer.audio;
import java.util.Map;

import studiplayer.basic.TagReader;

public class TaggedFile extends SampledFile{
	private String album;
	
	public TaggedFile() {
		super();
		}
	
	public TaggedFile(String path) throws NotPlayableException {
		super(path);
		readAndStoreTags();
	}
	
	public void readAndStoreTags() throws NotPlayableException{
		try {
		Map<String, Object> tagMap = TagReader.readTags(this.getPathname());
		Object titleValue = tagMap.get("title");
        if (titleValue != null) {
            this.title = titleValue.toString().trim();
        }
        Object authorValue = tagMap.get("author");
        if (authorValue == null) {
            authorValue = tagMap.get("artist");
        }
        if (authorValue != null) {
            this.author = authorValue.toString().trim();
        }
        Object albumValue = tagMap.get("album");
        if (albumValue != null) {
            this.album = albumValue.toString().trim();
        }
        Object durationValue = tagMap.get("duration");
        if (durationValue != null) {
                this.duration = Long.parseLong(durationValue.toString());
            }
		} catch (Exception e) {
	        throw new NotPlayableException(this.getPathname(), "Could not read tags", e);
	    }
    }
	
	public String getAlbum() {
		return this.album != null ? this.album : "";
	}
	
	public String toString() {
		String baseString = super.toString();
        String durationString = formatDuration();
        
        if (this.album != null && !this.album.isEmpty()) {
            return baseString + " - " + this.album + " - " + durationString;
        } else {
            return baseString + " - " + durationString;
        }
	}
	
		
}
