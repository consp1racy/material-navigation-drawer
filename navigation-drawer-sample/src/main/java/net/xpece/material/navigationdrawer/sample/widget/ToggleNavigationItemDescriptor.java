package net.xpece.material.navigationdrawer.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import net.xpece.material.navigationdrawer.descriptors.AbsNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.internal.ViewHolder;
import net.xpece.material.navigationdrawer.sample.R;

import hugo.weaving.DebugLog;

/**
 * Created by Eugen on 10. 1. 2015.
 */
public class ToggleNavigationItemDescriptor extends AbsNavigationItemDescriptor {

  private boolean checked = false;

  public ToggleNavigationItemDescriptor(long id) {
    super(id);
  }

  public ToggleNavigationItemDescriptor checked(boolean checked) {
    this.checked = checked;
    return this;
  }

  public boolean isChecked() {
    return checked;
  }

  @Override
  public int getLayoutId() {
    return R.layout.mnd_custom_item_toggle;
  }

  @Override
  public void loadInto(final View view, final boolean selected) {
    super.loadInto(view, false);

    setup(view);
  }

  @Override
  @DebugLog
  public boolean onClick(View view) {
    // on list item click - update text and toggle
    updateToggle(view, !checked);
//    updateText(view); // automatically
    return true;
  }

  private void updateToggle(View view, boolean checked) {
    SwitchCompat toggle = ViewHolder.get(view, R.id.toggle);
    if (toggle.isChecked() != checked) {
      toggle.toggle();
    }
  }

  private void setup(final View view) {
    SwitchCompat toggle = ViewHolder.get(view, R.id.toggle);
    toggle.setOnCheckedChangeListener(null);
    toggle.setChecked(checked);
    toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checked = !checked;
        updateText(view);

        onChange(view.getContext(), checked);
      }
    });

    updateText(view);
  }

  private void updateText(View view) {
    Context context = view.getContext();
    TextView text = ViewHolder.get(view, R.id.text);
    if (checked) {
      text.setTextColor(getColor(context, R.attr.colorAccent, Color.BLACK));
    } else {
      text.setTextColor(getColor(context, android.R.attr.textColorPrimaryNoDisable, Color.BLACK));
    }
    text.setText(checked ? "Bring it!" : "Hell no!");
  }

  public static int getColor(Context context, @AttrRes int attr, int fallback) {
    TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
    try {
      return ta.getColor(0, fallback);
    } finally {
      ta.recycle();
    }
  }

  public void onChange(Context context, boolean checked) {
    doWork(context);
  }

  private void doWork(Context context) {
    Toast.makeText(context, "Toggled to " + checked, Toast.LENGTH_SHORT).show();
  }
}
