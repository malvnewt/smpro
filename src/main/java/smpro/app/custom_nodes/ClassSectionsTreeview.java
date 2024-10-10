package smpro.app.custom_nodes;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.LightBase;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignL;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Translator;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassSectionsTreeview extends TreeView<HashMap<String, Object>> {

    public ObjectProperty<HashMap<String, Object>> selectedClassProperty = new SimpleObjectProperty<>();
    public ObjectProperty<TreeItem<HashMap<String, Object>>> selectedItemP = new SimpleObjectProperty<>();

    public TreeItem<HashMap<String, Object>> rootitem;

    private  boolean includeAll = true;

    public ClassSectionsTreeview(boolean... includeAll) {
        this.setOpacity(0);

        FadeTransition f = new FadeTransition(Duration.millis(200), this);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);

        VBox.setVgrow(this, Priority.ALWAYS);
        getStyleClass().addAll("dense", "bordered");

        setRoot(null);

        HashMap<String, Object> rootData = new HashMap<>(Map.of("name", Translator.getIntl("sections").toUpperCase()));
         rootitem = new TreeItem<>(rootData);
        rootitem.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/bluedisk.png", 20, 20, true)));
        setRoot(rootitem);
        rootitem.setExpanded(true);

        List<TreeItem<HashMap<String, Object>>> sectionItems = new ArrayList<>();

        // add all itmm
        TreeItem<HashMap<String, Object>> allItem = new TreeItem<>(new HashMap<>(Map.of("name",Translator.getIntl("all"),"id","allitem")));
        if (includeAll.length > 0) {

            rootitem.getChildren().add(allItem);
            this.includeAll = true;
        }


        List<HashMap<String, Object>> dbSections = PgConnector.fetch("select * from sections order by section_name", PgConnector.getConnection());

        for (HashMap<String, Object> sectionObj : dbSections) {

            //get section classes
            List<HashMap<String, Object>> sectionClasses = PgConnector.fetch(String.format("select * from classes where section='%s' order by level,classname ",
                    PgConnector.getFielorBlank(sectionObj, "section_name")), PgConnector.getConnection());


            TreeItem<HashMap<String, Object>> sectionItem = new TreeItem<>(new HashMap<>(Map.of(
                    "name", PgConnector.getFielorBlank(sectionObj, "section_name"),
                    "itemId", PgConnector.getFielorBlank(sectionObj, "id"),
                    "isEmpty", sectionClasses.isEmpty()

            )));

            sectionItems.add(sectionItem);

            sectionClasses.forEach(cls -> {

                TreeItem<HashMap<String, Object>> classItem = new TreeItem<>(new HashMap<>(Map.of(
                        "name", PgConnector.getFielorBlank(cls, "classname"),
                        "id", PgConnector.getFielorBlank(cls, "id")
                )));

                sectionItem.getChildren().add(classItem);
                sectionItem.setExpanded(true);


            });
        }

        /// set cell factory
        setCellFactory(new Callback<>() {
            @Override
            public TreeCell<HashMap<String, Object>> call(TreeView<HashMap<String, Object>> hashMapTreeView) {
                return new TreeCell<>() {
                    @Override
                    protected void updateItem(HashMap<String, Object> stringObjectHashMap, boolean b) {
                        super.updateItem(stringObjectHashMap, b);

                        if (!b) {
                            setDisable(false);

                            setText(PgConnector.getFielorBlank(stringObjectHashMap, "name").toUpperCase());
                            if (getTreeItem().isLeaf()) {
                                setGraphic(ProjectUtils.createFontIcon(MaterialDesignT.TEXT, 18, Paint.valueOf("gray")));
                                setStyle("-fx-font-size: 11px;-fx-opacity: 0.9");

                            } else {
                                setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/bluedisk.png", 20, 20, true)));
                                setStyle("-fx-font-size: 13px");


                            }
                            setTooltip(new Tooltip(PgConnector.getFielorBlank(stringObjectHashMap, "name").toUpperCase()));

                            if (getTreeItem().getParent() == rootitem) {
                                HashMap<String, Object> data = getItem();
                                boolean isEmpty = Boolean.parseBoolean(String.valueOf(data.get("isEmpty")));
                                if (isEmpty) {
                                    setDisable(true);
                                    setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/bluedisk.png", 20, 20, true)));
                                    setText(String.format("%s [ %s ]", PgConnector.getFielorBlank(stringObjectHashMap, "name").toUpperCase(), Translator.getIntl("empty")));


                                }
                            }


                        } else {
                            setText(null);
                            setGraphic(null);

                        }
                    }
                };
            }
        });

        // add to root
        rootitem.getChildren().addAll(sectionItems);

        getSelectionModel().selectedItemProperty().addListener((observableValue, hashMapTreeItem, item) -> {
            HashMap<String, Object> data = item.getValue();

            if (item.isLeaf()) {
                //filter table
                selectedClassProperty.set(data);
                selectedItemP.set(item);

            }

        });

        f.playFromStart();


    }

    public void reloadTree() {
        this.setOpacity(0);

        FadeTransition f = new FadeTransition(Duration.millis(300), this);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);
        f.setDelay(Duration.millis(100));

        rootitem.getChildren().clear();
        rootitem.setExpanded(true);

        List<TreeItem<HashMap<String, Object>>> sectionItems = new ArrayList<>();

        // add all itmm
        TreeItem<HashMap<String, Object>> allItem = new TreeItem<>(new HashMap<>(Map.of("name",Translator.getIntl("all"),"id","allitem")));
        if (includeAll)
            rootitem.getChildren().add(allItem);


        List<HashMap<String, Object>> dbSections = PgConnector.fetch("select * from sections order by section_name", PgConnector.getConnection());

        for (HashMap<String, Object> sectionObj : dbSections) {

            //get section classes
            List<HashMap<String, Object>> sectionClasses = PgConnector.fetch(String.format("select * from classes where section='%s' order by level,classname ",
                    PgConnector.getFielorBlank(sectionObj, "section_name")), PgConnector.getConnection());


            TreeItem<HashMap<String, Object>> sectionItem = new TreeItem<>(new HashMap<>(Map.of(
                    "name", PgConnector.getFielorBlank(sectionObj, "section_name"),
                    "itemId", PgConnector.getFielorBlank(sectionObj, "id"),
                    "isEmpty", sectionClasses.isEmpty()

            )));

            sectionItems.add(sectionItem);

            sectionClasses.forEach(cls -> {

                TreeItem<HashMap<String, Object>> classItem = new TreeItem<>(new HashMap<>(Map.of(
                        "name", PgConnector.getFielorBlank(cls, "classname"),
                        "id", PgConnector.getFielorBlank(cls, "id")
                )));

                sectionItem.getChildren().add(classItem);
                sectionItem.setExpanded(true);


            });


        }


        // add to root
        rootitem.getChildren().addAll(sectionItems);

        f.playFromStart();


    }




}
