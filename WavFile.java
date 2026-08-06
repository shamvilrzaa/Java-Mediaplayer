package studiplayer.audio;
import studiplayer.basic.WavParamReader;

public class WavFile extends SampledFile {
	
	public WavFile() {
		super();
	}
	
	public WavFile(String path) throws NotPlayableException {
		super(path);
		readAndSetDurationFromFile();
	}
	
	public void readAndSetDurationFromFile() throws NotPlayableException {
	    try {
	        WavParamReader.readParams(getPathname());
	        float frameRate = WavParamReader.getFrameRate();
	        long numOfFrames = WavParamReader.getNumberOfFrames();
	        setDuration(computeDuration(numOfFrames, frameRate));
	    } catch (Exception e) {
	        throw new NotPlayableException(getPathname(), "Could not read WAV parameters", e);
	    }
	}

	
	public static long computeDuration(long numOfFrames, float frameRate) {		
		return (long)((numOfFrames / frameRate) * 1000000L);
	}
	
	public String toString() {
		String baseString = super.toString();
        String durationString = formatDuration();
        
        return baseString + " - " + durationString;
	}
	
}
