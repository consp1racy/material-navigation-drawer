package com.sqisland.android.sliding_pane_layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;

/**
 * @author https://github.com/chiuki/sliding-pane-layout
 * @author http://blog.sqisland.com/2015/01/partial-slidingpanelayout.html
 */
public class CrossFadeSlidingPaneLayout extends SlidingPaneLayout {
  private View partialView = null;
  private View fullView = null;

  // helper flag pre honeycomb - visibility and click response handling
  private boolean mWasOpened = false;

  public CrossFadeSlidingPaneLayout(Context context) {
    super(context);
  }

  public CrossFadeSlidingPaneLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CrossFadeSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    if (getChildCount() < 1) {
      return;
    }

    View panel = getChildAt(0);
    if (!(panel instanceof ViewGroup)) {
      return;
    }

    ViewGroup viewGroup = (ViewGroup) panel;
    if (viewGroup.getChildCount() != 2) {
      return;
    }
    fullView = viewGroup.getChildAt(0);
    partialView = viewGroup.getChildAt(1);

    super.setPanelSlideListener(crossFadeListener);
  }

  @Override
  public void setPanelSlideListener(final PanelSlideListener listener) {
    if (listener == null) {
      super.setPanelSlideListener(crossFadeListener);
      return;
    }

    super.setPanelSlideListener(new PanelSlideListener() {
      @Override
      public void onPanelSlide(View panel, float slideOffset) {
        crossFadeListener.onPanelSlide(panel, slideOffset);
        listener.onPanelSlide(panel, slideOffset);
      }

      @Override
      public void onPanelOpened(View panel) {
        crossFadeListener.onPanelOpened(panel);
        listener.onPanelOpened(panel);
      }

      @Override
      public void onPanelClosed(View panel) {
        crossFadeListener.onPanelClosed(panel);
        listener.onPanelClosed(panel);
      }
    });
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    // this has to be done on each layout (changed means views were added or removed)
    if (partialView != null) {
      updateVisibility(isOpen());
    }
  }

  private SimplePanelSlideListener crossFadeListener = new SimplePanelSlideListener() {
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
      super.onPanelSlide(panel, slideOffset);
      if (partialView == null || fullView == null) {
        return;
      }

      updateVisibility(slideOffset);

      setAlpha(partialView, 1 - slideOffset); // only top view should fade
//      setAlpha(fullView, slideOffset); // full view should be opaque anyway
      // this could save some overdraw
    }
  };

  private void updateVisibility(float slideOffset) {
    if (Build.VERSION.SDK_INT < 11) {
      updateVisibilityBase(slideOffset);
    } else {
      updateVisibilityHoneycomb(slideOffset == 1);
    }
  }

  private void updateVisibility(boolean visible) {
    if (Build.VERSION.SDK_INT < 11) {
      updateVisibilityBase(visible);
    } else {
      updateVisibilityHoneycomb(visible);
    }
  }

  private void updateVisibilityBase(float slideOffset) {
    if (slideOffset == 1 && !mWasOpened) {
      updateVisibilityBase(true);
      mWasOpened = true;
    } else if (slideOffset < 1 && mWasOpened) {
      updateVisibilityBase(false);
      mWasOpened = false;
    }
  }

  /**
   * Pre-Honeycomb the views have to be physically repositioned otherwise they still listen
   * on original places for touch.
   * Curiously this happened here even when visibility was set to gone.
   *
   * @param visible
   */
  private void updateVisibilityBase(boolean visible) {
    if (visible) {
      partialView.layout(-partialView.getWidth(), 0, 0, partialView.getHeight());
    } else {
      partialView.layout(0, 0, partialView.getWidth(), partialView.getHeight());
    }
  }

  private void updateVisibilityHoneycomb(boolean visible) {
    partialView.setVisibility(visible ? View.GONE : VISIBLE);
  }

  @TargetApi(11)
  private static void setAlpha(View view, float alpha) {
      ViewHelper.setAlpha(view, alpha);
  }
}
