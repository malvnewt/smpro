package smpro.app.custom_nodes;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import smpro.app.ResourceUtil;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;
import smpro.app.utils.Translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CustomTableView extends TableView<HashMap<String,Object>> {
    /**
     * Constructs a TableView for the given element.
     *
     * @param cols the colums to add to the table
     */

    public List<HashMap<String, Object>> allData = new ArrayList<>();
    public ListProperty<HashMap<String, Object>> filteredItemsProperty = new SimpleListProperty<>();
    public ListProperty<String> currentSelectedIds = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    public ListProperty<CheckBox> currentItemSelectorP = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    BooleanProperty selectAllProperty = new SimpleBooleanProperty(false);


    public CustomTableView(List<TableColumn<HashMap<String,Object>,String>> cols ,int idcol) {
        itemsProperty().bind(filteredItemsProperty);


        TableColumn<HashMap<String, Object>, String> selectCol = cols.get(idcol);
        selectCol.setCellFactory(new Callback<>() {

            @Override
            public TableCell<HashMap<String, Object>, String> call(TableColumn<HashMap<String, Object>, String> coldata) {
                return new TableCell<>() {

                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            HashMap<String, Object> item = getTableRow().getItem();

//                            String itemid = PgConnector.getFielorBlank(item, "id");
                            String itemid =getItem();


                            setText("");
                            CheckBox cb = new CheckBox();
                            cb.setId(itemid);
                            currentItemSelectorP.add(cb);

                            cb.selectedProperty().addListener((observableValue, aBoolean, selected) -> {
                                if (selected) {
                                    System.out.println("adding selected id "+itemid);
                                    currentSelectedIds.add(itemid);

                                } else {
                                    System.out.println("removing selected id "+itemid);

                                    currentSelectedIds.remove(itemid);
                                }
                            });

                            setGraphic(cb);

                        } else {
                            setText(null);
                            setGraphic(null);

                        }
                    }
                };
            }
        });

//        itemsProperty().addListener((observableValue, hashMaps, t1) -> currentItemSelectorP.clear());

        getColumns().addAll(cols);
        getStyleClass().addAll("dense", "bordered", "striped");

        VBox placeholder = new VBox();
        placeholder.setAlignment(Pos.CENTER);
        Label l = new Label(Translator.getIntl("no_data").toUpperCase());
        l.setStyle("-fx-font-weight: bold;-fx-font-size: 18px;-fx-text-fill: lightgray;-fx-opacity: 0.75");
        l.setGraphic(new ImageView(ResourceUtil.getImageFromResource("images/empty-glass.png", 50, 50, true)));
        placeholder.getChildren().add(l);
        setPlaceholder(placeholder);

    }


    public void loadInitialData(List<HashMap<String, Object>> data) {
        allData.addAll(data);
        filteredItemsProperty.set(FXCollections.observableList(data));
    }

    public void filter(String query) {
        currentItemSelectorP.clear();
        currentSelectedIds.clear();
        selectAllProperty.set(false);
        this.getItems().clear();
        this.setOpacity(0);

        FadeTransition f = new FadeTransition(Duration.millis(200), this);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);

        List<HashMap<String, Object>> res = PgConnector.fetch(query, PgConnector.getConnection());
        filteredItemsProperty.set(FXCollections.observableList(res));


        f.playFromStart();
    }




}
