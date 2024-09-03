package smpro.app.custom_titlebar;

import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import smpro.app.custom_titlebar.internal.StageManager;

public class CustomCaption {

    private static final StageManager stageManager = new StageManager();

    /**
     * Applies the custom caption with specified properties
     * @param stage the Stage to apply the config
     * @param config the configuration to apply
     */
    public static void useForStage(@NotNull Stage stage, @NotNull CaptionConfiguration config) {
        stageManager.registerStage(stage, config);
    }

    public static void useForStage(@NotNull Stage stage,boolean isdialog, @NotNull CaptionConfiguration config) {
        stageManager.registerStage(stage, config,isdialog);
    }


    /**
     * same as {@link CustomCaption#useForStage(Stage, CaptionConfiguration)}
     * but uses the default config ({@link CaptionConfiguration#DEFAULT_CONFIG})
     * @param stage the stage to apply the custom caption
     */
    public static void useForStage(@NotNull Stage stage) {
        useForStage(stage, CaptionConfiguration.DEFAULT_CONFIG);
    }


    /**
     * removes all customizations that were previously added
     * @param stage the stage to remove the customizations
     */
    public static void removeCustomization(@NotNull Stage stage) {
        stageManager.releaseStage(stage);
    }
}
