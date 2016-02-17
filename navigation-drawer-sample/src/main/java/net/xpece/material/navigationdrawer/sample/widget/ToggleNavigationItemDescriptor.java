package net.xpece.material.navigationdrawer.sample.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import net.xpece.material.navigationdrawer.descriptors.AbsNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.internal.ViewHolder;
import net.xpece.material.navigationdrawer.sample.R;

/**
 * Created by Eugen on 10. 1. 2015.
 */
public class ToggleNavigationItemDescriptor extends AbsNavigationItemDescriptor {

    private boolean mChecked = false;

    /** Item label. */
    protected String textChecked = null;
    @StringRes
    protected int textIdChecked = 0;

    /** Item label. */
    protected String text = null;
    @StringRes
    protected int textId = 0;

    // This should not be here! The descriptor should not hold any means of presentation!
    private Toast mToast = null;

    public ToggleNavigationItemDescriptor(int id) {
        super(id);
    }

    public ToggleNavigationItemDescriptor checked(boolean checked) {
        this.mChecked = checked;
        return this;
    }

    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public int getLayoutId() {
        return R.layout.mnd_custom_item_toggle;
    }

    @Override
    public void bindView(final View view, final boolean selected) {
        super.bindView(view, false);

        setup(view);
    }

    public ToggleNavigationItemDescriptor text(String text) {
        this.text = text;
        this.textId = 0;
        return this;
    }

    public ToggleNavigationItemDescriptor text(@StringRes int text) {
        this.textId = text;
        this.text = null;
        return this;
    }

    public String getText(Context context) {
        if (textId != 0) {
            return context.getString(textId);
        } else {
            return text;
        }
    }

    public ToggleNavigationItemDescriptor textChecked(String textChecked) {
        this.textChecked = textChecked;
        this.textIdChecked = 0;
        return this;
    }

    public ToggleNavigationItemDescriptor textChecked(@StringRes int textChecked) {
        this.textIdChecked = textChecked;
        this.textChecked = null;
        return this;
    }

    public String getTextChecked(Context context) {
        if (textIdChecked != 0) {
            return context.getString(textIdChecked);
        } else {
            return textChecked;
        }
    }

    @Override
    public boolean onClick(View view) {
        // on list item click - update text and toggle
        updateToggle(view, !mChecked);
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
        toggle.setChecked(mChecked);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mChecked = isChecked;
                updateText(view);

                onChange(view.getContext(), mChecked);
            }
        });

        updateText(view);
    }

    private void updateText(View view) {
        Context context = view.getContext();
        TextView text = ViewHolder.get(view, R.id.text);
        if (mChecked) {
            text.setTextColor(getColor(context, R.attr.colorAccent, Color.BLACK));
        } else {
            text.setTextColor(getColor(context, android.R.attr.textColorPrimaryNoDisable, Color.BLACK));
        }
        text.setText(mChecked ? getTextChecked(context) : getText(context));
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

    @TargetApi(12)
    private void doWork(Context context) {
        final Toast t = Toast.makeText(context, "Toggled to " + mChecked, Toast.LENGTH_SHORT);

        if (Build.VERSION.SDK_INT >= 12) {
            // This thing will allow us to cancel previous toast issued by this descriptor's item
            // while avoiding memory leaks.
            // Hooking up to view attach events is only available since API 12.

            if (mToast != null) {
                mToast.cancel();
                mToast = null;
            }

            t.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    mToast = t;
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (mToast != null && v == mToast.getView()) {
                        // Remove reference to self.
                        mToast = null;
                    }
                }
            });
        }

        t.show();
    }
}
