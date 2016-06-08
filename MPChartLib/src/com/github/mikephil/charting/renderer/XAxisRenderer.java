
package com.github.mikephil.charting.renderer;

import android.annotation.TargetApi;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class XAxisRenderer extends AxisRenderer {

    protected XAxis mXAxis;
    private ViewGroup viewGroup;
    private BaseAdapter baseAdapter;
    private List<View> views;


    public XAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, trans);
        this.mXAxis = xAxis;
        mAxisLabelPaint.setColor(Color.BLACK);
        mAxisLabelPaint.setTextAlign(Align.CENTER);
        mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

    }

    public void computeAxis(float xValMaximumLength, List<String> xValues) {

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        StringBuilder widthText = new StringBuilder();

        int xValChars = Math.round(xValMaximumLength);

        for (int i = 0; i < xValChars; i++) {
            widthText.append('h');
        }

        final FSize labelSize = Utils.calcTextSize(mAxisLabelPaint, widthText.toString());

        final float labelWidth = labelSize.width;
        final float labelHeight = Utils.calcTextHeight(mAxisLabelPaint, "Q");

        final FSize labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
                labelWidth,
                labelHeight,
                mXAxis.getLabelRotationAngle());

        StringBuilder space = new StringBuilder();
        int xValSpaceChars = mXAxis.getSpaceBetweenLabels();

        for (int i = 0; i < xValSpaceChars; i++) {
            space.append('h');
        }

        final FSize spaceSize = Utils.calcTextSize(mAxisLabelPaint, space.toString());

        mXAxis.mLabelWidth = Math.round(labelWidth + spaceSize.width);
        mXAxis.mLabelHeight = Math.round(labelHeight);
        mXAxis.mLabelRotatedWidth = Math.round(labelRotatedSize.width + spaceSize.width);
        mXAxis.mLabelRotatedHeight = Math.round(labelRotatedSize.height);

        mXAxis.setValues(xValues);
    }


    public void computeAxis(final BaseAdapter baseAdapter, final ViewGroup viewGroup) {
        this.baseAdapter = baseAdapter;
        this.viewGroup = viewGroup;

        buildVies();
        baseAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                buildVies();
            }
        });
    }

    public void buildVies() {
        int maxX = 0;
        int maxY = 0;
        new Handler(viewGroup.getContext().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (views != null) {
                    for (View view : views) {
                        if (view.getParent() != null) {
                            Log.e("123 remove", view.toString() + "|" + view.getParent().toString());
                            ((ViewGroup) view.getParent()).removeView(view);
                        }


                    }
                }

            }
        });

        views = new ArrayList<>();

        for (int i = 0; i < baseAdapter.getCount(); i++) {
            View view = baseAdapter.getView(i, null, viewGroup);
            views.add(view);
            Log.e("123 realadd", view.toString());
            maxX = Math.max(maxX, view.getLayoutParams().width);
            maxY = Math.max(maxY, view.getLayoutParams().height);
        }

        final FSize labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
                maxX,
                maxY,
                mXAxis.getLabelRotationAngle());

        Log.e("123", maxX + "|" + maxY + "|" + labelRotatedSize.width + "|" + labelRotatedSize.height);
        mXAxis.mLabelWidth = Math.round(10);
        mXAxis.mLabelHeight = Math.round(10);
        mXAxis.mLabelRotatedWidth = Math.round(10);
        mXAxis.mLabelRotatedHeight = Math.round(10);
        mXAxis.setValues(views);
        viewGroup.invalidate();
        viewGroup.requestLayout();
    }


    @Override
    public void renderAxisLabels(Canvas c) {


        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float yoffset = mXAxis.getYOffset();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        if (mXAxis.getPosition() == XAxisPosition.TOP) {
            drawLabels(c, mViewPortHandler.contentTop() - yoffset,
                    new PointF(0.5f, 1.0f));

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {
            drawLabels(c, mViewPortHandler.contentTop() + yoffset + mXAxis.mLabelRotatedHeight,
                    new PointF(0.5f, 1.0f));


        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {
            drawLabels(c, mViewPortHandler.contentBottom() + yoffset,
                    new PointF(0.5f, 0.0f));


        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {
            drawLabels(c, mViewPortHandler.contentBottom() - yoffset - mXAxis.mLabelRotatedHeight,
                    new PointF(0.5f, 0.0f));
        } else { // BOTH SIDED
            drawLabels(c, mViewPortHandler.contentTop() - yoffset,
                    new PointF(0.5f, 1.0f));
            drawLabels(c, mViewPortHandler.contentBottom() + yoffset,
                    new PointF(0.5f, 0.0f));
        }

    }

    public void layout() {
        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float yoffset = mXAxis.getYOffset();
        float pos = mViewPortHandler.contentTop() - yoffset;

        if (mXAxis.getPosition() == XAxisPosition.TOP) {
            pos = mViewPortHandler.contentTop() - yoffset;

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {
            pos = mViewPortHandler.contentTop() + yoffset + mXAxis.mLabelRotatedHeight;


        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {
            pos = mViewPortHandler.contentBottom() + yoffset;


        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {
            pos = mViewPortHandler.contentBottom() - yoffset - mXAxis.mLabelRotatedHeight;
        }

        updateAdapterView(pos);

    }


    @Override
    public void renderAxisLine(Canvas c) {

        if (!mXAxis.isDrawAxisLineEnabled() || !mXAxis.isEnabled())
            return;

        mAxisLinePaint.setColor(mXAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mXAxis.getAxisLineWidth());

        if (mXAxis.getPosition() == XAxisPosition.TOP
                || mXAxis.getPosition() == XAxisPosition.TOP_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop(), mAxisLinePaint);
        }

        if (mXAxis.getPosition() == XAxisPosition.BOTTOM
                || mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    //TODO
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void updateAdapterView(float pos) {
        if (viewGroup == null || baseAdapter == null) {
            return;
        }
        float[] position = new float[]{
                0f, 0f
        };

        for (View view : views) {
            view.setX(-1000);
            view.setY(-1000);
        }


        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            View view = views.get(i);

            mTrans.pointValuesToPixel(position);

            if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                // avoid clipping of the last
                if (i == mXAxis.getValues().size() - 1 && mXAxis.getValues().size() > 1) {
                    float width = view.getLayoutParams().width;

                    if (width > mViewPortHandler.offsetRight() * 2
                            && position[0] + width > mViewPortHandler.getChartWidth())
                        position[0] -= width / 2;

                    // avoid clipping of the first
                } else if (i == 0) {

                    float width = view.getLayoutParams().width;
                    position[0] += width / 2;
                }
            }

            view.setX(position[0] - view.getLayoutParams().width / 2);
            view.setY(pos - view.getLayoutParams().height / 2);

        }

    }

    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void drawLabels(Canvas c, float pos, PointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[]{
                0f, 0f
        };

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                String label = mXAxis.getValues().get(i).toString();

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.getValues().size() - 1 && mXAxis.getValues().size() > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && position[0] + width > mViewPortHandler.getChartWidth())
                            position[0] -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        position[0] += width / 2;
                    }
                }

                drawLabel(c, label, i, position[0], pos, anchor, labelRotationAngleDegrees);

            }


        }
    }

    protected void drawLabel(Canvas c, String label, int xIndex, float x, float y, PointF anchor, float angleDegrees) {
        String formattedLabel = mXAxis.getValueFormatter().getXValue(label, xIndex, mViewPortHandler);
        Utils.drawXAxisValue(c, formattedLabel, x, y, mAxisLabelPaint, anchor, angleDegrees);
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        // pre alloc
        float[] position = new float[]{
                0f, 0f
        };

        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());
        mGridPaint.setPathEffect(mXAxis.getGridDashPathEffect());

        Path gridLinePath = new Path();

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i;
            mTrans.pointValuesToPixel(position);

            if (position[0] >= mViewPortHandler.offsetLeft()
                    && position[0] <= mViewPortHandler.getChartWidth()) {

                gridLinePath.moveTo(position[0], mViewPortHandler.contentBottom());
                gridLinePath.lineTo(position[0], mViewPortHandler.contentTop());

                // draw a path because lines don't support dashing on lower android versions
                c.drawPath(gridLinePath, mGridPaint);
            }

            gridLinePath.reset();
        }
    }

    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = mXAxis.getLimitLines();

        if (limitLines == null || limitLines.size() <= 0)
            return;

        float[] position = new float[2];

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            position[0] = l.getLimit();
            position[1] = 0.f;

            mTrans.pointValuesToPixel(position);

            renderLimitLineLine(c, l, position);
            renderLimitLineLabel(c, l, position, 2.f + l.getYOffset());
        }
    }

    float[] mLimitLineSegmentsBuffer = new float[4];
    private Path mLimitLinePath = new Path();

    public void renderLimitLineLine(Canvas c, LimitLine limitLine, float[] position) {
        mLimitLineSegmentsBuffer[0] = position[0];
        mLimitLineSegmentsBuffer[1] = mViewPortHandler.contentTop();
        mLimitLineSegmentsBuffer[2] = position[0];
        mLimitLineSegmentsBuffer[3] = mViewPortHandler.contentBottom();

        mLimitLinePath.reset();
        mLimitLinePath.moveTo(mLimitLineSegmentsBuffer[0], mLimitLineSegmentsBuffer[1]);
        mLimitLinePath.lineTo(mLimitLineSegmentsBuffer[2], mLimitLineSegmentsBuffer[3]);

        mLimitLinePaint.setStyle(Paint.Style.STROKE);
        mLimitLinePaint.setColor(limitLine.getLineColor());
        mLimitLinePaint.setStrokeWidth(limitLine.getLineWidth());
        mLimitLinePaint.setPathEffect(limitLine.getDashPathEffect());

        c.drawPath(mLimitLinePath, mLimitLinePaint);
    }

    public void renderLimitLineLabel(Canvas c, LimitLine limitLine, float[] position, float yOffset) {
        String label = limitLine.getLabel();

        // if drawing the limit-value label is enabled
        if (label != null && !label.equals("")) {

            mLimitLinePaint.setStyle(limitLine.getTextStyle());
            mLimitLinePaint.setPathEffect(null);
            mLimitLinePaint.setColor(limitLine.getTextColor());
            mLimitLinePaint.setStrokeWidth(0.5f);
            mLimitLinePaint.setTextSize(limitLine.getTextSize());

            float xOffset = limitLine.getLineWidth() + limitLine.getXOffset();

            final LimitLine.LimitLabelPosition labelPosition = limitLine.getLabelPosition();

            if (labelPosition == LimitLine.LimitLabelPosition.RIGHT_TOP) {

                final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                mLimitLinePaint.setTextAlign(Align.LEFT);
                c.drawText(label, position[0] + xOffset, mViewPortHandler.contentTop() + yOffset + labelLineHeight, mLimitLinePaint);
            } else if (labelPosition == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                mLimitLinePaint.setTextAlign(Align.LEFT);
                c.drawText(label, position[0] + xOffset, mViewPortHandler.contentBottom() - yOffset, mLimitLinePaint);
            } else if (labelPosition == LimitLine.LimitLabelPosition.LEFT_TOP) {

                mLimitLinePaint.setTextAlign(Align.RIGHT);
                final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                c.drawText(label, position[0] - xOffset, mViewPortHandler.contentTop() + yOffset + labelLineHeight, mLimitLinePaint);
            } else {

                mLimitLinePaint.setTextAlign(Align.RIGHT);
                c.drawText(label, position[0] - xOffset, mViewPortHandler.contentBottom() - yOffset, mLimitLinePaint);
            }
        }
    }

}
