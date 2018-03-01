package com.apollo.statusbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by lei.xiao on 2018/1/8.
 */

public class StatusBarUtils {

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static String sMiuiVersionName;

    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    public static void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * 设置页面最外层布局 FitsSystemWindows 属性
     * @param activity
     * @param value
     */
    public static void setFitsSystemWindows(Activity activity, boolean value) {
        ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(android.R.id.content);
        View parentView = contentFrameLayout.getChildAt(0);
        if (parentView != null) {
            parentView.setFitsSystemWindows(value);
            if (parentView instanceof DrawerLayout) {
                DrawerLayout drawer = (DrawerLayout) parentView;
                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
                drawer.setClipToPadding(false);
            }
        }
    }

    /**
     * 当statusBar设置成透明时为其设置背景颜色
     * @param activity
     * @param color statusBar的颜色
     * @param statusBarHeight statusBarHeight
     * @param isDrawerLayout 根布局是否为DrawerLayout
     * @param mContentResourcesIdInDrawerLayout DrawerLayout里面布局ID
     */
    public static void initStatusBar(Activity activity,int color,int statusBarHeight,boolean isDrawerLayout,int mContentResourcesIdInDrawerLayout){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //由于4.4以上6.0之前设置状态栏里面的字体、ICON等颜色只有少数机型有效，所以状态栏颜色不能是白色
            if(color==Color.WHITE && isAtLeastKitKat() && isPreM()){
                if(!isMIUI() && !isFlyme()){
                    color=activity.getResources().getColor(R.color.colorPrimaryDark);
                }
            }
            if (isDrawerLayout) {
                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                    setFitsSystemWindows(activity,true);
                }
                //要在内容布局增加状态栏，否则会盖在侧滑菜单上
                ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
                //DrawerLayout 则需要在第一个子视图即内容试图中添加padding
                View parentView = rootView.getChildAt(0);
                if(parentView.findViewWithTag("statusBar")!=null){
                    parentView.findViewWithTag("statusBar").setVisibility(statusBarHeight==0?View.GONE:View.VISIBLE);
                    return;
                }
                LinearLayout linearLayout = new LinearLayout(activity);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                View statusBarView = new View(activity);
                statusBarView.setTag("statusBar");
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        statusBarHeight);
                statusBarView.setBackgroundColor(color);
                //添加占位状态栏到线性布局中
                linearLayout.addView(statusBarView, lp);
                //侧滑菜单
                DrawerLayout drawer = (DrawerLayout) parentView;
                //内容视图
                View content = activity.findViewById(mContentResourcesIdInDrawerLayout);
                //将内容视图从 DrawerLayout 中移除
                drawer.removeView(content);
                //添加内容视图
                linearLayout.addView(content, content.getLayoutParams());
                //将带有占位状态栏的新的内容视图设置给 DrawerLayout
                drawer.addView(linearLayout, 0);
            } else {
                //设置 paddingTop
                ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.setPadding(0, statusBarHeight, 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //直接设置状态栏颜色
                    activity.getWindow().setStatusBarColor(color);
                } else {
                    //根布局添加占位状态栏
                    ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                    if (decorView.findViewWithTag("statusBar") != null) {
                        if (statusBarHeight == 0 ) {
                            decorView.removeView(decorView.findViewWithTag("statusBar"));
                        }
                        return;
                    }
                    View statusBarView = new View(activity);
                    statusBarView.setTag("statusBar");
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            statusBarHeight);
                    statusBarView.setBackgroundColor(color);
                    decorView.addView(statusBarView, lp);
                }
            }
        }
    }

    /**
     * 利用反射获取状态栏高度
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean setDarkModeCompat(Activity activity,boolean isDark){
        if(isMIUI()){
            setMiuiStatusBarDarkMode(activity,isDark);
            return true;
        }else if(isFlyme()){
            setFlymeStatusBarDarkMode(activity.getWindow(),isDark);
            return true;
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            setDarkModeAfterM(activity,isDark);
            return true;
        }else {
            return false;
        }
    }

    public static boolean isMIUI() {
        Properties prop= new Properties();
        boolean isMIUI;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            prop.load(stream);
            String name = prop.getProperty(KEY_MIUI_VERSION_NAME, null);
            if(name!=null){
                sMiuiVersionName = name.toLowerCase();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(stream!=null){
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        isMIUI= prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        return isMIUI;
    }

    public static boolean isPreMIUI9(){
        if(TextUtils.isEmpty(sMiuiVersionName)){
            return false;
        }
        int version =  Integer.valueOf(sMiuiVersionName.substring(1));
        return version < 9;
    }

    public static boolean isFlyme() {
        Properties prop= new Properties();
        boolean isFlyme;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            prop.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(stream!=null){
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        isFlyme = prop.getProperty("ro.build.user", "").equalsIgnoreCase("flyme");
        return isFlyme;
    }

    public static boolean isPreM(){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    public static boolean isAtLeastKitKat(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        if(isPreM()||isPreMIUI9()){
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            setDarkModeAfterM(activity,darkmode);
        }
        return false;
    }

    static void setFlymeStatusBarDarkMode(Window window, boolean dark) {
        if (Build.VERSION.SDK_INT < 23) {
            WindowManager.LayoutParams winParams = window.getAttributes();
            String flagName = "MEIZU_FLAG_DARK_STATUS_BAR_ICON";
            try {
                Field f = winParams.getClass().getDeclaredField(flagName);
                f.setAccessible(true);
                int bits = f.getInt(winParams);
                Field f2 = winParams.getClass().getDeclaredField("meizuFlags");
                f2.setAccessible(true);
                int meizuFlags = f2.getInt(winParams);
                int oldFlags = meizuFlags;
                if (dark) {
                    meizuFlags |= bits;
                } else {
                    meizuFlags &= ~bits;
                }
                if (oldFlags != meizuFlags) {
                    f2.setInt(winParams, meizuFlags);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            View decorView = window.getDecorView();
            int SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0;
            try {
                Field field = View.class.getField("SYSTEM_UI_FLAG_LIGHT_STATUS_BAR");
                SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = field.getInt(null);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (decorView != null) {
                int oldVis = decorView.getSystemUiVisibility();
                int newVis = oldVis;
                if (dark) {
                    newVis |= SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    newVis &= ~SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                if (newVis != oldVis) {
                    decorView.setSystemUiVisibility(newVis);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void setDarkModeAfterM(Activity activity,boolean isDark){
        if(isDark){
            activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }else {
            activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}
