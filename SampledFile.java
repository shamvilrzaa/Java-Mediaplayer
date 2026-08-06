package studiplayer.audio;
import studiplayer.basic.BasicPlayer;

public abstract class SampledFile extends AudioFile {
	protected long duration;
	protected boolean paused;
	
	public SampledFile() {
		super();
        this.duration = 0;
        this.paused = false;
	}
	
	public SampledFile(String path) throws NotPlayableException {
		super(path);
        this.duration = 0;
        this.paused = false;
	}
	
    public void play() throws NotPlayableException{
		BasicPlayer.stop();
		try {
		    BasicPlayer.play(getPathname());
		} catch (Exception e) {
		    throw new NotPlayableException(getPathname(), "Cannot play file", e);
		}
	    paused = false;
    }

    @Override
    public void togglePause() {
        BasicPlayer.togglePause();
        paused = !paused;
    }

    @Override
    public void stop() {
        BasicPlayer.stop();
        paused = false;
    }

    public String formatDuration() {
    	return timeFormatter(duration);
    }

	
    public String formatPosition() {
    	return timeFormatter(BasicPlayer.getPosition());
    }
 
    public static String timeFormatter(long microseconds) {
        if (microseconds < 0) {
            throw new RuntimeException("Negative time value not allowed");
        }

        long totalSeconds = microseconds / 1_000_000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        if (minutes > 99 || (minutes == 99 && seconds > 59)) {
            throw new RuntimeException("Time value exceeds maximum format limit of 99:59");
        }

        return String.format("%02d:%02d", minutes, seconds);
    }


	
	public long getDuration() {
		return duration;
	}	
	
	public void setDuration(long duration) {
        this.duration = duration;
    }
	
	
}

