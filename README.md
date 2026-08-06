# Java MediaPlayer

A full-featured audio player built in Java with a JavaFX GUI, developed as part of coursework at TH Ingolstadt. The project focuses on clean OOP design, extensible architecture, and real-time multithreaded playback.

## Features

- Play, pause, stop, and skip audio tracks
- Playlist management with M3U file loading and saving
- Sort tracks by title, author, album, or duration
- Search and filter functionality with live table updates
- Supports WAV files and tagged audio files (reads metadata tags)
- Real-time playback timer running on a separate thread
- Cross-platform path handling (Windows and Unix)

## Architecture and Design Patterns

The project is structured around a clear inheritance hierarchy and several classic OOP design patterns:

- **Abstract class `AudioFile`** as the base for all audio types, with abstract methods for play, pause, stop, duration, and position formatting
- **`SampledFile` and `WavFile`** for raw WAV audio; **`TaggedFile`** extends `SampledFile` and reads ID3-style metadata tags (title, author, album, duration)
- **Factory pattern** via `AudioFileFactory` to instantiate the correct audio file type from a path
- **Iterator pattern** via `ControllablePlayListIterator` for traversing and controlling playback position in the playlist
- **Strategy/Comparator pattern** for sorting: `TitleComparator`, `AuthorComparator`, `AlbumComparator`, `DurationComparator` all implement `Comparator<AudioFile>` and are selected via the `SortCriterion` enum
- **Multithreading**: `PlayerThread` handles audio playback without blocking the UI; `TimerThread` updates the elapsed time label independently
- **`SongTable`** extends JavaFX `TableView` and wraps the playlist in an observable list for reactive UI updates

## Tech Stack

Java, JavaFX, OOP, Multithreading, Iterator Pattern, Factory Pattern, Comparator Pattern, M3U playlist format

## Project Structure

```
studiplayer/
├── audio/
│   ├── AudioFile.java          # Abstract base class
│   ├── SampledFile.java        # WAV playback logic
│   ├── WavFile.java            # WAV file specifics
│   ├── TaggedFile.java         # Tagged audio with metadata
│   ├── AudioFileFactory.java   # Factory for file instantiation
│   ├── PlayList.java           # Playlist with sort, filter, M3U I/O
│   ├── ControllablePlayListIterator.java
│   ├── NotPlayableException.java
│   ├── SortCriterion.java
│   ├── AlbumComparator.java
│   ├── AuthorComparator.java
│   ├── TitleComparator.java
│   └── DurationComparator.java
└── ui/
    ├── Player.java             # JavaFX Application entry point
    ├── SongTable.java          # TableView wrapper for playlist
    └── Song.java               # JavaFX-compatible song model
```

## How to Run

Requires Java 11+ and JavaFX SDK.

```bash
git clone https://github.com/shamvilrzaa/Java-MediaPlayer.git
cd Java-MediaPlayer
# Compile and run with your IDE (Eclipse or IntelliJ) or via command line with JavaFX modules
```
