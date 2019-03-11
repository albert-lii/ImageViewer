package indi.liyi.viewer.otherui;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import java.text.DecimalFormat;

import indi.liyi.viewer.Utils;
import indi.liyi.viewer.progrv.ProgressWheel;

import static android.view.View.VISIBLE;

/**
 * 默认的加载进度视图
 */
public class DefaultProgressUI extends ProgressUI {
    private DecimalFormat mProgressFormat;

    public DefaultProgressUI() {
        mProgressFormat = new DecimalFormat("#%");
    }

    @Override
    public View createView(Context context) {
        // 添加进度条
        ProgressWheel progressView = new ProgressWheel(context);
        final int size = Utils.dp2px(context, 60);
        FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(size, size);
        barLp.gravity = Gravity.CENTER;
        progressView.setLayoutParams(barLp);
        final int barWidth = Utils.dp2px(context, 3);
        progressView.setBarColor(Color.parseColor("#CCFFFFFF"));
        progressView.setBarWidth(barWidth);
        progressView.setBarLength(Utils.dp2px(context, 50));
        progressView.setRimColor(Color.parseColor("#22FFFFFF"));
        progressView.setRimWidth(barWidth);
        progressView.setContourColor(Color.parseColor("#10000000"));
        progressView.setSpinSpeed(3.5f);
        progressView.setText("");
        progressView.setTextColor(Color.parseColor("#CCFFFFFF"));
        progressView.setTextSize(Utils.dp2px(context, 14));
        return progressView;
    }

    @Override
    public void handleProgress(float progress) {
        if (getProgressView().getVisibility() == VISIBLE) {
            ((ProgressWheel) getProgressView()).setText(mProgressFormat.format(progress));
            ((ProgressWheel) getProgressView()).setProgress((int) (progress * 360));
            if (progress == 1f) {
                stop();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        ((ProgressWheel) getProgressView()).startSpinning();
    }

    @Override
    public void stop() {
        super.stop();
        ((ProgressWheel) getProgressView()).stopSpinning();
    }
}
