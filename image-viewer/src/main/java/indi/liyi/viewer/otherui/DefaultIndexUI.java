package indi.liyi.viewer.otherui;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import indi.liyi.viewer.Utils;

/**
 * 默认的索引视图
 */
public class DefaultIndexUI extends IndexUI {
    private boolean overlayStatusBar;

    public DefaultIndexUI(boolean overlayStatusBar) {
        this.overlayStatusBar = overlayStatusBar;
    }

    @Override
    public View createView(Context context) {
        TextView indexView = new TextView(context);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0,
                Utils.dp2px(context, overlayStatusBar ? Utils.getStatusBarHeight(context) + 5 : 5),
                0, 0);
        textParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        indexView.setLayoutParams(textParams);
        indexView.setIncludeFontPadding(false);
        indexView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        indexView.setTextColor(Color.WHITE);
        return indexView;
    }

    @Override
    public void init(int position, int length) {
        ((TextView) getIndexView()).setText((position + 1) + "/" + length);
    }

    @Override
    public void handleItemChanged(int position, int length) {
        ((TextView) getIndexView()).setText((position + 1) + "/" + length);
    }
}
