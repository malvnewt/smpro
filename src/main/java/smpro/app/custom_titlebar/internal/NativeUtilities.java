package smpro.app.custom_titlebar.internal;

import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import smpro.app.custom_titlebar.internal.libraries.DwmApi;
import smpro.app.custom_titlebar.internal.libraries.User32Ex;
import smpro.app.custom_titlebar.internal.structs.DWMWINDOWATTRIBUTE;


import java.util.UUID;

import static com.sun.jna.platform.win32.WinUser.SM_CXPADDEDBORDER;
import static com.sun.jna.platform.win32.WinUser.SM_CYSIZEFRAME;

public class NativeUtilities {

    /**
     * *should* return the HWND for the Specified Stage
     * might not, because JavaFX ist stupid and has no way
     * to do this
     * @param stage the Stage
     * @return hopefully the HWND for the correct stage
     */
    public static WinDef.HWND getHwnd(Stage stage) {
        String randomId = UUID.randomUUID().toString();
        String title = stage.getTitle();
        stage.setTitle(randomId);
        WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, randomId);
        stage.setTitle(title);
        return hWnd;
    }


    /**
     * Enables/disables the Immersive Dark Mode for a specified stage
     * officially only supported (documented) since Win 11 Build 22000
     * @param stage the stage to enable the Dark mode for
     * @param enabled if immersive dark mod should be enabled
     * @return if Immersive Dark Mode could be enabled successfully
     */
    public static boolean setImmersiveDarkMode(Stage stage, boolean enabled) {
        WinDef.HWND hWnd = getHwnd(stage);
        WinNT.HRESULT res = DwmApi.INSTANCE.DwmSetWindowAttribute(hWnd, DWMWINDOWATTRIBUTE.DWMWA_USE_IMMERSIVE_DARK_MODE, new IntByReference(enabled ? 1 : 0), 4);
        return res.longValue() >= 0;
    }

    /**
     * Sets the Caption Color of the specified Stage to the specified Color
     * this does only work since Win 11 Build 22000
     * @param stage the Stage to change the Caption Color
     * @param color the Color to use
     * @return if the change was successful
     */
    public static boolean setCaptionColor(Stage stage, Color color) {
        WinDef.HWND hWnd = getHwnd(stage);
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        // win api accepts the colors in reverse order
        int rgb = red + (green << 8) + (blue << 16);
        WinNT.HRESULT res = DwmApi.INSTANCE.DwmSetWindowAttribute(hWnd, DWMWINDOWATTRIBUTE.DWMWA_CAPTION_COLOR, new IntByReference(rgb), 4);
        return res.longValue() >= 0;
    }

    /**
     * sets the caption to the specified color if supported
     * if not supported uses immersive dark mode if color is mostly dark
     * @param stage the stage to modify
     * @param color the color to set the caption
     * @return if the stage was modified
     */
    public static boolean customizeCation(Stage stage, Color color) {
        boolean success = setCaptionColor(stage, color);
        if(!success) {
            int red = (int) (color.getRed() * 255);
            int green = (int) (color.getGreen() * 255);
            int blue = (int) (color.getBlue() * 255);
            int colorSum = red + green + blue;

            boolean dark = colorSum < 255 * 3 / 2;
            success = setImmersiveDarkMode(stage, dark);
        }
        return success;
    }

    public static boolean isMaximized(WinDef.HWND hWnd) {
        BaseTSD.LONG_PTR windowStyle = User32Ex.INSTANCE.GetWindowLongPtr(hWnd, WinUser.GWL_STYLE);
        return (windowStyle.longValue() & WinUser.WS_MAXIMIZE) == WinUser.WS_MAXIMIZE;
    }

    public static int getResizeHandleHeight(WinDef.HWND hWnd) {
        int dpi = User32Ex.INSTANCE.GetDpiForWindow(hWnd);
        return User32Ex.INSTANCE.GetSystemMetricsForDpi(SM_CXPADDEDBORDER, dpi) +
                User32Ex.INSTANCE.GetSystemMetricsForDpi(SM_CYSIZEFRAME, dpi);
    }
}
