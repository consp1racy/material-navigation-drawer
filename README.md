Material Navigation Drawer
==========================

Navigation Drawer according to Material Design spec.

How it works
------------

 - Icon color defaults to `android:textColorSecondary` when not selected. See `NavigationItemDescriptor`.
 - Text color and selected icon color defaults to `android:textColorPrimary`. See `NavigationItemDescriptor`.
 - Activated background is set to 12% of `android:colorForeground`. This currently limits the use to white drawer on white theme or black on black.
 - List background is set to `android:colorForegroundInverse`. (*)
 - Divider color is 12% of `android:colorForeground`. (*)

(*) You can modify this by accessing `NavigationDrawerFragment.getListView()`.

Please note that this is not a drawer per se, the fragment can be used as a pinned side bar (e.g. on 10" tablets). For usage example see the `sample `directory.

Minimum API is 11, because activated selector is introduced just then.

Library is not on jcenter yet, to get it add the following to your `build.gradle`:
```groovy
repositories {
    maven {
        url "http://dl.bintray.com/consp1racy/maven"
    }
}

dependencies {
    compile 'net.xpece.material:navigation-drawer:0.1.1'
}
```
