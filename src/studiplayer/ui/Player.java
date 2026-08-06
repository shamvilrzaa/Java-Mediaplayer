package studiplayer.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import studiplayer.audio.*;

import java.io.File;
import java.net.URL;

public class Player extends Application {

    public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
    private static final String PLAYLIST_DIRECTORY = "playlists";
    private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
    private static final String NO_CURRENT_SONG = " - ";

    private PlayList playList;
    private boolean useCertPlayList = false;

    private Button playButton, pauseButton, stopButton, nextButton;
    private Label playListLabel, playTimeLabel, currentSongLabel;
    private ChoiceBox<SortCriterion> sortChoiceBox;
    private TextField searchTextField;
    private Button filterButton;
    private SongTable songTable;

    private PlayerThread playerThread;
    private TimerThread timerThread;

    public Player() {}

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("APA Player");

        String path = useCertPlayList ? DEFAULT_PLAYLIST : getPlaylistPathViaFileChooser(stage);
        loadPlayList(path);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        GridPane filterPane = new GridPane();
        filterPane.setHgap(10);
        filterPane.setVgap(5);
        filterPane.setPadding(new Insets(5));

        searchTextField = new TextField();
        sortChoiceBox = new ChoiceBox<>();
        sortChoiceBox.getItems().addAll(SortCriterion.values());
        filterButton = new Button("Display");

        filterPane.add(new Label("Search:"), 0, 0);
        filterPane.add(searchTextField, 1, 0);
        filterPane.add(new Label("Sort by:"), 0, 1);
        filterPane.add(sortChoiceBox, 1, 1);
        filterPane.add(filterButton, 2, 1);

        TitledPane titledFilterPane = new TitledPane("Filter", filterPane);
        titledFilterPane.setCollapsible(false);
        root.setTop(titledFilterPane);

        songTable = new SongTable(playList);
        root.setCenter(songTable);

        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(5));

        GridPane infoPane = new GridPane();
        infoPane.setHgap(10);
        infoPane.setVgap(5);

        playListLabel = new Label("Playlist:");
        playListLabel.setText("Playlist: " + path);
        currentSongLabel = new Label(NO_CURRENT_SONG);
        playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);

        infoPane.add(playListLabel, 0, 0);
        infoPane.add(new Label("Now Playing:"), 0, 1);
        infoPane.add(currentSongLabel, 1, 1);
        infoPane.add(new Label("Time:"), 0, 2);
        infoPane.add(playTimeLabel, 1, 2);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        playButton = createButton("play.jpg");
        pauseButton = createButton("pause.jpg");
        stopButton = createButton("stop.jpg");
        nextButton = createButton("next.jpg");

        buttonBox.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);

        bottomBox.getChildren().addAll(infoPane, buttonBox);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();

        playButton.setOnAction(e -> {
            terminateThreads(false);
            AudioFile af = playList.currentAudioFile();
            if (af != null) {
                printAudioFileAction("Playing", af);
                updateSongInfo(af);
                setButtonStates(false, true, true, true);
                startThreads(false);
            }
        });

        pauseButton.setOnAction(e -> {
            AudioFile af = playList.currentAudioFile();
            if (af != null) {
                af.togglePause();
                printAudioFileAction("Pausing", af);
            }
        });

        stopButton.setOnAction(e -> {
            terminateThreads(false);
            AudioFile af = playList.currentAudioFile();
            if (af != null) {
                af.stop();
                printAudioFileAction("Stopping", af);
            }
            updateSongInfo(null);
            setButtonStates(true, false, false, true);
        });

        nextButton.setOnAction(e -> {
            terminateThreads(false);
            AudioFile af = playList.currentAudioFile();
            if (af != null) {
                af.stop();
                printAudioFileAction("Stopping", af);
            }
            playList.nextSong();
            AudioFile next = playList.currentAudioFile();
            if (next != null) {
                printAudioFileAction("Playing", next);
                updateSongInfo(next);
                setButtonStates(false, true, true, true);
                startThreads(false);
            }
        });

        songTable.setRowSelectionHandler(e -> {
            Song selected = songTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                AudioFile af = selected.getAudioFile();
                playList.jumpToAudioFile(af);
                System.out.println("Jumped to and selected: " + af.toString());
                updateSongInfo(af);
                setButtonStates(false, true, true, true);
            }
        });

        filterButton.setOnAction(e -> {
            if (playList != null) {
                String search = searchTextField.getText();
                SortCriterion sort = sortChoiceBox.getValue();

                playList.setSearch(search == null ? "" : search);
                playList.setSortCriterion(sort != null ? sort : SortCriterion.DEFAULT);
                playList.sort();
                playList.filter();
                songTable.refreshSongs();
            }
        });
        setButtonStates(true, false, false, true);
    }

    private void printAudioFileAction(String action, AudioFile af) {
        String album = (af instanceof TaggedFile) ? ((TaggedFile) af).getAlbum() : "";
        String duration = (af instanceof SampledFile) ? ((SampledFile) af).formatDuration() : "";

        System.out.println(action + " " + af.getAuthor() + " - " + af.getTitle() + " - " + album + " - " + duration);
        System.out.println("Filename is " + af.getFilename());
    }

    private void setButtonStates(boolean play, boolean pause, boolean stop, boolean next) {
        playButton.setDisable(!play);
        pauseButton.setDisable(!pause);
        stopButton.setDisable(!stop);
        nextButton.setDisable(!next);
    }

    private void updateSongInfo(AudioFile af) {
        Platform.runLater(() -> {
            if (af == null) {
                currentSongLabel.setText(NO_CURRENT_SONG);
                playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
            } else {
                currentSongLabel.setText(af.toString());
                playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
            }
        });
    }

    public void setUseCertPlayList(boolean value) {
        this.useCertPlayList = value;
    }

    public void loadPlayList(String pathname) {
        if (pathname == null || pathname.isEmpty()) {
            pathname = DEFAULT_PLAYLIST;
        }

        try {
            if (playList == null) {
                playList = new PlayList(pathname);
            } else {
                playList.loadFromM3U(pathname);
            }

            if (playListLabel != null) {
                playListLabel.setText("Playlist: " + pathname);
            }

            if (songTable != null) {
                songTable.refreshSongs();
            }

            System.out.println("Loaded " + playList.size() + " songs:");
            for (AudioFile af : playList.getList()) {
                System.out.println(" - " + af.getAuthor() + " - " + af.getTitle());
            }

        } catch (NotPlayableException e) {
            System.err.println("Error loading playlist: " + e.getMessage());
        }
    }

    private void setPlayList(String pathname) {
        loadPlayList(pathname);
    }

    private String getPlaylistPathViaFileChooser(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Playlist");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("M3U files", "*.m3u"));
        chooser.setInitialDirectory(new File(PLAYLIST_DIRECTORY));
        File selected = chooser.showOpenDialog(stage);
        return selected == null ? DEFAULT_PLAYLIST : selected.getAbsolutePath();
    }

    public Button createButton(String iconfile) {
        try {
            URL url = getClass().getResource("/icons/" + iconfile);
            ImageView imageView = new ImageView(new Image(url.toString()));
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            Button button = new Button("", imageView);
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setStyle("-fx-background-color: #fff;");
            return button;
        } catch (Exception e) {
            System.out.println("Image icons/" + iconfile + " not found!");
            System.exit(-1);
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void startThreads(boolean onlyTimer) {
        if (!onlyTimer) {
            playerThread = new PlayerThread();
            playerThread.start();
        }
        timerThread = new TimerThread();
        timerThread.start();
    }

    private void terminateThreads(boolean onlyTimer) {
        if (timerThread != null) {
            timerThread.terminate();
            timerThread = null;
        }
        if (!onlyTimer && playerThread != null) {
            playerThread.terminate();
            playerThread = null;
        }
    }

    private class PlayerThread extends Thread {
        private boolean stopped = false;

        public void terminate() {
            stopped = true;
        }

        @Override
        public void run() {
            while (!stopped) {
                AudioFile af = playList.currentAudioFile();
                if (af == null) return;

                songTable.selectSong(af);

                try {
                    af.play();
                } catch (Exception e) {
                    System.err.println("Playback error: " + e.getMessage());
                }

                if (!stopped) {
                    playList.nextSong();
                } else {
                    return;
                }
            }
        }
    }

    private class TimerThread extends Thread {
        private boolean stopped = false;

        public void terminate() {
            stopped = true;
        }

        @Override
        public void run() {
            while (!stopped) {
                AudioFile af = playList.currentAudioFile();
                if (af != null) {
                    Platform.runLater(() -> playTimeLabel.setText(af.formatPosition()));
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
