package win.lamit.phim.presenter;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;
import win.lamit.phim.model.Video;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Video video = (Video) item;
        if (video != null) {
            viewHolder.getTitle().setText(video.title);
            viewHolder.getSubtitle().setText(video.studio);
            viewHolder.getBody().setText(video.description);
        }
    }
}
