package win.lamit.phim.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<Video> playlist;
    private int currentPosition;
    public Playlist() {
        playlist = new ArrayList<>();
        currentPosition = 0;
    }
    public void clear() {
        playlist.clear();
    }
    public void add(Video video) {
        playlist.add(video);
    }
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
    public int size() {
        return playlist.size();
    }
    public Video next() {
        if ((currentPosition + 1) < size()) {
            currentPosition++;
            return playlist.get(currentPosition);
        }
        return null;
    }
    public Video previous() {
        if (currentPosition - 1 >= 0) {
            currentPosition--;
            return playlist.get(currentPosition);
        }
        return null;
    }
}