package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.xpece.material.navigationdrawer.R;
import net.xpece.material.navigationdrawer.internal.Utils;
import net.xpece.material.navigationdrawer.internal.ViewHolder;

/**
 * Created by Eugen on 10. 1. 2015.
 */
public class BaseNavigationItemDescriptor extends AbsNavigationItemDescriptor
    implements GraphicNavigationItemDescriptor {
    /** Whether item stays active after selecting. */
    protected boolean sticky = false;

    /** Icon resource ID. Gets colored appropriately. */
    @DrawableRes
    protected int iconId = 0;

    /** Item label. */
    protected String text = null;
    @StringRes
    protected int textId = 0;

    /** Color of item icon and text when selected. */
    protected int activeColor = 0xff000000;
    @ColorRes
    protected int activeColorId = R.color.mnd_text_primary_light;
    @AttrRes
    int activeColorAttr = android.R.attr.textColorPrimary;

    /** Color of item icon when not selected. Should be 54% black or 100% white. */
    protected int passiveColor = 0;
    @ColorRes
    protected int passiveColorId = 0;
    @AttrRes
    int passiveColorAttr = 0;
    /**
     * Whether the passive colors have been modified.
     * If not use primary text color for icon in case of dark theme.
     */
    private boolean customPassiveColor = false;

    private Drawable icon = null;
    /** Whether the icon should be tinted. */
    private boolean tintIcon = true;

    private boolean enabled = true;

    public BaseNavigationItemDescriptor(int id) {super(id);}

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public BaseNavigationItemDescriptor enable() {
        this.enabled = true;
        return this;
    }

    public BaseNavigationItemDescriptor disable() {
        this.enabled = false;
        return this;
    }

    public BaseNavigationItemDescriptor iconResource(@DrawableRes int icon) {
        this.iconId = icon;
        this.icon = null;
        return this;
    }

    @Override
    public Drawable getIcon(Context context) {
        if (iconId != 0) {
            Drawable d = ContextCompat.getDrawable(context, iconId);
            if (d != null) {
                d.mutate();
            }
            return d;
        } else {
            return icon;
        }
    }

    public BaseNavigationItemDescriptor iconColorAlwaysPassiveOff() {
        this.tintIcon = true;
        return this;
    }

    public BaseNavigationItemDescriptor iconColorAlwaysPassiveOn() {
        this.tintIcon = false;
        return this;
    }

    @Override
    public boolean isIconColorAlwaysPassiveOff() {
        return this.tintIcon;
    }

    public BaseNavigationItemDescriptor text(String text) {
        this.text = text;
        this.textId = 0;
        return this;
    }

    public BaseNavigationItemDescriptor text(@StringRes int text) {
        this.textId = text;
        this.text = null;
        return this;
    }

    @Override
    public String getText(Context context) {
        if (textId != 0) {
            return context.getString(textId);
        } else {
            return text;
        }
    }

    public BaseNavigationItemDescriptor activeColor(int color) {
        this.activeColor = color;
        this.activeColorId = 0;
        this.activeColorAttr = 0;
        return this;
    }

    public BaseNavigationItemDescriptor activeColorResource(@ColorRes int color) {
        this.activeColorId = color;
        this.activeColor = 0;
        this.activeColorAttr = 0;
        return this;
    }

    public BaseNavigationItemDescriptor activeColorAttribute(@AttrRes int color) {
        this.activeColorAttr = color;
        this.activeColorId = 0;
        this.activeColor = 0;
        return this;
    }

    @Override
    public int getActiveColor(Context context) {
        if (activeColorAttr != 0) {
            return Utils.getColor(context, activeColorAttr, 0xff000000);
        } else if (activeColorId != 0) {
            return context.getResources().getColor(activeColorId);
        } else {
            return activeColor;
        }
    }

    public BaseNavigationItemDescriptor passiveColor(int color) {
        this.customPassiveColor = true;
        this.passiveColor = color;
        this.passiveColorId = 0;
        this.passiveColorAttr = 0;
        return this;
    }

    public BaseNavigationItemDescriptor passiveColorResource(@ColorRes int color) {
        this.customPassiveColor = true;
        this.passiveColorId = color;
        this.passiveColorAttr = 0;
        this.passiveColor = 0;
        return this;
    }

    public BaseNavigationItemDescriptor passiveColorAttribute(@AttrRes int color) {
        this.customPassiveColor = true;
        this.passiveColorAttr = color;
        this.passiveColorId = 0;
        this.passiveColor = 0;
        return this;
    }

    @Override
    public int getPassiveColor(Context context) {
        if (!customPassiveColor) {
            // icons in dark themes should be 100% white
            final int secondary = Utils.getColor(context, android.R.attr.textColorSecondary, 0xde000000);
            if ((secondary & 0xffffff) == 0xffffff) {
                return Utils.getColor(context, android.R.attr.textColorPrimary, 0xffffffff);
            } else {
                return secondary;
            }
        } else if (passiveColorAttr != 0) {
            return Utils.getColor(context, passiveColorAttr, 0xde000000);
        } else if (passiveColorId != 0) {
            return context.getResources().getColor(passiveColorId);
        } else {
            return passiveColor;
        }
    }

    public BaseNavigationItemDescriptor sticky() {
        sticky = true;
        return this;
    }

    public BaseNavigationItemDescriptor notSticky() {
        sticky = false;
        return this;
    }

    @Override
    public boolean isSticky() {
        return sticky;
    }

    @Override
    public int getLayoutId() {
        return R.layout.mnd_item_base;
    }

    @Override
    public void bindView(View view, boolean selected) {
        super.bindView(view, selected);

        Context context = view.getContext();

        ImageView icon = ViewHolder.get(view, R.id.icon);
        TextView text = ViewHolder.get(view, R.id.text);

        Drawable iconDrawable = getIcon(context);
        boolean tintIcon = isIconColorAlwaysPassiveOff();
        int passiveColor = getPassiveColor(context);
        int activeColor = getActiveColor(context);
        String textString = getText(context);
        int textColor = Utils.getColor(context, android.R.attr.textColorPrimary, 0xde000000);

        text.setText(textString);
        if (selected) {
            text.setTextAppearance(context, R.style.TextAppearance_MaterialNavigationDrawer_Item_Selected);
            text.setTextColor(activeColor);
        } else {
            text.setTextAppearance(context, R.style.TextAppearance_MaterialNavigationDrawer_Item);
            text.setTextColor(textColor);
        }
//    text.setTextColor(Utils.createActivatedColor(textColor, activeColor));

        if (iconDrawable != null) {
            if (tintIcon && selected) {
                icon.setImageDrawable(Utils.tintDrawable(iconDrawable, activeColor));
            } else {
                icon.setImageDrawable(Utils.tintDrawable(iconDrawable, passiveColor));
            }
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
            icon.setImageDrawable(null);
        }
    }

    @Override
    public String toString() {
        return "BaseNavigationItemDescriptor{" +
            "id=" + id +
            "sticky=" + sticky +
            '}';
    }

    @Override
    public BaseNavigationItemDescriptor activatedBackground(Drawable drawable) {
        return (BaseNavigationItemDescriptor) super.activatedBackground(drawable);
    }

    @Override
    public BaseNavigationItemDescriptor activatedBackgroundResource(@ColorRes int drawable) {
        return (BaseNavigationItemDescriptor) super.activatedBackgroundResource(drawable);
    }

    @Override
    public BaseNavigationItemDescriptor activatedBackgroundAttribute(@AttrRes int drawable) {
        return (BaseNavigationItemDescriptor) super.activatedBackgroundAttribute(drawable);
    }

    @Override
    public BaseNavigationItemDescriptor clearActivatedBackground() {
        return (BaseNavigationItemDescriptor) super.clearActivatedBackground();
    }

    @Override
    public boolean isRecyclable() {
        return true;
    }
}
