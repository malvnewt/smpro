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
import org.kordamp.ikonli.materialdesign2.*;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Translator;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SubjectsClassesTreeview extends TreeView<HashMap<String, Object>> {

    public ObjectProperty<HashMap<String, Object>> selectedSubjectProperty = new SimpleObjectProperty<>();
    public ObjectProperty<TreeItem<HashMap<String, Object>>> selectedItemP = new SimpleObjectProperty<>();

    public TreeItem<HashMap<String, Object>> rootitem;

    private  boolean includeAll = false;

    public SubjectsClassesTreeview() {
        this.setOpacity(0);

        FadeTransition f = new FadeTransition(Duration.millis(200), this);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);

        VBox.setVgrow(this, Priority.ALWAYS);
        getStyleClass().addAll("dense", "bordered");

        setRoot(null);

        HashMap<String, Object> rootData = new HashMap<>(Map.of("name", Translator.getIntl("classes").toUpperCase(),"id","rootid"));
        rootitem = new TreeItem<>(rootData);
        rootitem.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/bluedisk.png", 20, 20, true)));
        setRoot(rootitem);
        rootitem.setExpanded(true);


        List<TreeItem<HashMap<String, Object>>> categoryItems = new ArrayList<>();
        List<HashMap<String, Object>> classes = PgConnector.fetch("select * from classes order by level,classname ", PgConnector.getConnection());


        classes.forEach(cls -> {

            TreeItem<HashMap<String, Object>> classInsubItem = new TreeItem<>(new HashMap<>(Map.of(
                    "name", String.format("%s (%s)",PgConnector.getFielorBlank(cls, "classname"),PgConnector.getFielorBlank(cls, "class_abbreviation")),
                    "id", PgConnector.getNumberOrNull(cls, "id")
            )));

            categoryItems.add(classInsubItem);


        });

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
                                setGraphic(ProjectUtils.createFontIcon(MaterialDesignB.BOOK, 18, Paint.valueOf("gray")));
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
        rootitem.getChildren().addAll(categoryItems);

        getSelectionModel().selectedItemProperty().addListener((observableValue, hashMapTreeItem, item) -> {
            HashMap<String, Object> data = item.getValue();

            if (item.isLeaf()) {
                //filter table
                selectedSubjectProperty.set(data);
                selectedItemP.set(item);

            }

        });

        f.playFromStart();


    }


    public void reloadTree() {

        rootitem.getChildren().clear();

        List<TreeItem<HashMap<String, Object>>> categoryItems = new ArrayList<>();
        List<HashMap<String, Object>> classes = PgConnector.fetch("select * from classes order by level,classname ", PgConnector.getConnection());

        classes.forEach(cls -> {

            TreeItem<HashMap<String, Object>> classInsubItem = new TreeItem<>(new HashMap<>(Map.of(
                    "name", String.format("%s (%s)",PgConnector.getFielorBlank(cls, "classname"),PgConnector.getFielorBlank(cls, "class_abbreviation")),
                    "id", PgConnector.getNumberOrNull(cls, "id")
            )));

            categoryItems.add(classInsubItem);


        });

        rootitem.getChildren().addAll(categoryItems);

        this.getSelectionModel().select(selectedItemP.get());





    }




}
