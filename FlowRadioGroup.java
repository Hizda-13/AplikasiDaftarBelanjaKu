package com.ryanjuniarto.daftarbelanjaku;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

public class FlowRadioGroup extends RadioGroup {

    public FlowRadioGroup(Context context) {
        super(context);
    }

    public FlowRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // ========================
    // FIX: Gunakan RadioGroup.LayoutParams (punya margin!)
    // ========================
    @Override
    public RadioGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new RadioGroup.LayoutParams(getContext(), attrs);
    }

    @Override
    protected RadioGroup.LayoutParams generateDefaultLayoutParams() {
        return new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    protected RadioGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new RadioGroup.LayoutParams(p);
    }

    // ========================
    // Flow Layout Measure
    // ========================
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width = 0;
        int height = 0;
        int lineWidth = 0;
        int lineHeight = 0;

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (lineWidth + childWidth > widthSize) {
                width = Math.max(width, lineWidth);
                height += lineHeight;

                lineWidth = childWidth;
                lineHeight = childHeight;

            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }

        width = Math.max(width, lineWidth);
        height += lineHeight;

        setMeasuredDimension(width, height);
    }

    // ========================
    // Flow Layout Positioning
    // ========================
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int curLeft = 0;
        int curTop = 0;
        int lineHeight = 0;

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();

            int cW = child.getMeasuredWidth();
            int cH = child.getMeasuredHeight();

            // Pindah ke next row
            if (curLeft + cW + params.leftMargin + params.rightMargin > getWidth()) {
                curLeft = 0;
                curTop += lineHeight;
                lineHeight = 0;
            }

            int left = curLeft + params.leftMargin;
            int top = curTop + params.topMargin;

            child.layout(left, top, left + cW, top + cH);

            curLeft += cW + params.leftMargin + params.rightMargin;
            lineHeight = Math.max(lineHeight, cH + params.topMargin + params.bottomMargin);
        }
    }
}
