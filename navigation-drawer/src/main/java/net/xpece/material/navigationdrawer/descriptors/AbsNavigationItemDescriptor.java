package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xpece.material.navigationdrawer.internal.Utils;

/**
 * Created by Eugen on 10. 1. 2015.
 */
public abstract class AbsNavigationItemDescriptor implements CompositeNavigationItemDescriptor {
    /** Item ID. */
    protected int id;

    protected Drawable activatedBackground = null;
    @DrawableRes
    protected int activatedBackgroundId = 0;
    @AttrRes
    protected int activatedBackgroundAttr = 0;
    private boolean customActivatedBackground = false;

    protected AbsNavigationItemDescriptor(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isSticky() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void bindView(View view, boolean selected) {
        if (selected) {
            Utils.setBackground(view, getActivatedBackground(view.getContext()));
        } else {
            view.setBackgroundColor(0);
        }
    }

    @Override
    public final View createView(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
        onViewCreated(view);
        return view;
    }

    /**
     * The specified layout will be inflated as view.
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * Setup views in this method. This concerns mainly themeing.
     * <p></p><strong>IMPORTANT!</strong>
     * The created view must not reflect this objects state.
     * Do not setup listeners in this method.
     *
     * @param view
     */
    public void onViewCreated(View view) {
//    bindView(view, false);
    }

    public AbsNavigationItemDescriptor activatedBackground(Drawable background) {
        this.customActivatedBackground = true;
        this.activatedBackground = background;
        this.activatedBackgroundId = 0;
        this.activatedBackgroundAttr = 0;
        return this;
    }

    public AbsNavigationItemDescriptor activatedBackgroundResource(@DrawableRes int background) {
        this.customActivatedBackground = true;
        this.activatedBackgroundId = background;
        this.activatedBackground = null;
        this.activatedBackgroundAttr = 0;
        return this;
    }

    public AbsNavigationItemDescriptor activatedBackgroundAttribute(@AttrRes int background) {
        this.customActivatedBackground = true;
        this.activatedBackgroundAttr = background;
        this.activatedBackgroundId = 0;
        this.activatedBackground = null;
        return this;
    }

    public AbsNavigationItemDescriptor clearActivatedBackground() {
        this.customActivatedBackground = false;
        this.activatedBackground = null;
        this.activatedBackgroundAttr = 0;
        this.activatedBackgroundId = 0;
        return this;
    }

    public Drawable getActivatedBackground(Context context) {
        if (!customActivatedBackground) {
            return Utils.getActivatedDrawable(context);
        } else if (activatedBackgroundAttr != 0) {
            return Utils.getDrawableAttr(context, activatedBackgroundAttr);
        } else if (activatedBackgroundId != 0) {
            return ContextCompat.getDrawable(context, activatedBackgroundId);
        } else {
            return activatedBackground;
        }
    }

    @Override
    public boolean onClick(View view) {
        return false;
    }

    @Override
    public String toString() {
        return "AbsNavigationItemDescriptor{" +
            "id=" + id +
            '}';
    }

    @Override
    public boolean isRecyclable() {
        return false;
    }
}
