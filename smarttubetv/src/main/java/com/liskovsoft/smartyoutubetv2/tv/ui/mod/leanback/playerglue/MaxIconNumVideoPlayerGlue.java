package com.liskovsoft.smartyoutubetv2.tv.ui.mod.leanback.playerglue;

import android.content.Context;
import androidx.leanback.media.PlaybackBaseControlGlue;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.media.PlayerAdapter;
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;
import androidx.leanback.widget.PlaybackRowPresenter;
import androidx.leanback.widget.RowPresenter;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.smartyoutubetv2.tv.ui.mod.leanback.playerglue.PlaybackTransportRowPresenter.TopEdgeFocusListener;

public abstract class MaxIconNumVideoPlayerGlue<T extends PlayerAdapter>
        extends PlaybackTransportControlGlue<T> implements TopEdgeFocusListener {
    /**
     * Constructor for the glue.
     *
     * @param context
     * @param impl    Implementation to underlying media player.
     */
    public MaxIconNumVideoPlayerGlue(Context context, T impl) {
        super(context, impl);
    }

    @Override
    protected PlaybackRowPresenter onCreateRowPresenter() {
        final AbstractDetailsDescriptionPresenter detailsPresenter =
                new AbstractDetailsDescriptionPresenter() {
                    @Override
                    protected void onBindDescription(ViewHolder
                                                             viewHolder, Object obj) {
                        // Mod: fix clipped title on videos with embedded icons
                        Helpers.setField(viewHolder, "mTitleMargin", 0);

                        PlaybackBaseControlGlue<?> glue = (PlaybackBaseControlGlue<?>) obj;
                        viewHolder.getTitle().setText(glue.getTitle());
                        viewHolder.getSubtitle().setText(glue.getSubtitle());
                    }
                };

        PlaybackTransportRowPresenter rowPresenter = new PlaybackTransportRowPresenter() {
            @Override
            protected void onBindRowViewHolder(RowPresenter.ViewHolder vh, Object item) {
                super.onBindRowViewHolder(vh, item);
                vh.setOnKeyListener(MaxIconNumVideoPlayerGlue.this);

                ViewHolder viewHolder = (ViewHolder) vh;

                addQualityInfoListener(viewHolder.mQualityListener);
                viewHolder.mTopEdgeFocusListener = MaxIconNumVideoPlayerGlue.this;
            }
            @Override
            protected void onUnbindRowViewHolder(RowPresenter.ViewHolder vh) {
                super.onUnbindRowViewHolder(vh);
                vh.setOnKeyListener(null);
            }
        };
        rowPresenter.setDescriptionPresenter(detailsPresenter);
        return rowPresenter;
    }

    public interface QualityInfoListener {
        void onQualityInfoChanged(String content);
    }

    protected abstract void addQualityInfoListener(QualityInfoListener listener);

    public abstract void onTopEdgeFocused();
}
