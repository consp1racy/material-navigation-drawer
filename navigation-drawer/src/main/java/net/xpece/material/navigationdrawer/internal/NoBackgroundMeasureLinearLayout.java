package net.xpece.material.navigationdrawer.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * LinearLayout which does not take its background dimensions into account
 * when measuring its dimensions.
 * <p/>
 * Created by Eugen on 22. 2. 2015.
 */
public class NoBackgroundMeasureLinearLayout extends LinearLayout {

  private static final int[] ATTRS = new int[]{android.R.attr.minWidth, android.R.attr.minHeight};

  private int mMinWidth;
  private int mMinHeight;

  public NoBackgroundMeasureLinearLayout(Context context) {
    super(context);
    init(context, null, 0, 0);
  }

  public NoBackgroundMeasureLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0, 0);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public NoBackgroundMeasureLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public NoBackgroundMeasureLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    TypedArray a = context.obtainStyledAttributes(attrs, ATTRS, defStyleAttr, defStyleRes);
    mMinWidth = a.getInt(0, 0);
    mMinHeight = a.getInt(1, 0);
    a.recycle();
  }

  @Override
  protected int getSuggestedMinimumHeight() {
    return mMinHeight;
  }

  @Override
  protected int getSuggestedMinimumWidth() {
    return mMinWidth;
  }

  @Override
  public void setMinimumHeight(int minHeight) {
    super.setMinimumHeight(minHeight);
    mMinHeight = minHeight;
  }

  @Override
  public void setMinimumWidth(int minWidth) {
    super.setMinimumWidth(minWidth);
    mMinWidth = minWidth;
  }
}
