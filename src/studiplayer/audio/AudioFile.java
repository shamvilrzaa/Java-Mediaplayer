package studiplayer.audio;
import java.io.File;

public abstract class AudioFile {
	protected String pathname;
	protected String filename;
	protected String author;
	protected String title;
	
	private boolean isWindows() {
		 return System.getProperty("os.name").toLowerCase()
		 .indexOf("win") >= 0;
		}
	
	public AudioFile() {
		this.pathname = "";
		this.filename = "";
		this.author = "";
		this.title = "";
	}
	
	public AudioFile(String path) throws NotPlayableException{
		this();	
		parsePathname(path);
		isFileReadable();
		parseFilename(this.filename);
	}
	
	private void isFileReadable() throws NotPlayableException{
        if (this.pathname == null || this.pathname.trim().isEmpty()) {
            throw new NotPlayableException(pathname, "Empty file path");
        }
        
        File file = new File(this.pathname);
        
        if (!file.exists()) {
            throw new NotPlayableException(pathname, "File doesn't exist");
        }
        
        if (!file.isFile()) {
            throw new NotPlayableException(pathname, "Not a file");
        }
        
        if (!file.canRead()) {
            throw new NotPlayableException(pathname, "File is not readable");
        }
    }

	
	public void parsePathname(String path) {
	    path = path.trim();
	    if (path.isEmpty()) {
	        this.pathname = "";
	        this.filename = "";
	        return;
	    }
	    if (path.length() >= 2 && path.charAt(1) == ':' && !isWindows()) {
	        path = "/" + path.charAt(0) + "/" + path.substring(2);
	    }
	    if (isWindows()) {
	        path = path.replace('/', '\\');
	        while (path.contains("\\\\")) {
	            path = path.replace("\\\\", "\\");
	        }
	    } else {
	        path = path.replace('\\', '/');
	        while (path.contains("//")) {
	            path = path.replace("//", "/");
	        }
	    }
	    this.pathname = path;
	    int index1 = path.lastIndexOf('/');
	    int index2 = path.lastIndexOf('\\');
	    int idx;
	    if (index1 > index2) {
	        idx = index1;
	    } else {
	        idx = index2;
	    }
	    if (idx >= 0) {
	        this.filename = path.substring(idx + 1).trim();
	    } else {
	        this.filename = path.trim();
	    }
	}

	public void parseFilename(String file) {
		this.author = "";
        this.title = "";
        if (file == null || file.trim().isEmpty()) {
	        return;
	    }
	    if (file.equals("-")) {
	        this.title = "-";
	        return;
	    }
	    if (file.startsWith(".")) {
	        return;
	    }
	    String name = file;
		int dotPos = file.lastIndexOf('.');
		if (dotPos > 0) {
		name = file.substring(0, dotPos);
		}
		if (name.isEmpty()) {
	        return;
	    }
		int sepPos = name.indexOf(" - ");
		if (sepPos >= 0) {
			this.author = name.substring(0, sepPos).trim();
			this.title = name.substring(sepPos + 3).trim();
		} else {
			this.author = "";
			this.title = name.trim();
		}
	}

	public String getPathname() {
		return this.pathname;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String toString() {
	    if (this.author.isEmpty() && this.title.isEmpty()) {
	        return "";
	    } else if (this.author.isEmpty()) {
	        return this.title;
	    } else {
	        return this.author + " - " + this.title;
	    }
	}
	
	public abstract void play() throws NotPlayableException;
	public abstract void togglePause();
	public abstract void stop();
	
	public abstract String formatDuration();
	public abstract String formatPosition();
		
	}



	

