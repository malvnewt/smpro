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
import java.util.Objects;


public class CustomTableView extends TableView<HashMap<String,Object>> {
    /**
     * Constructs a TableView for the given element.
     *
     * @param cols the colums to add to the table
     */

    public List<HashMap<String, Object>> allData = new ArrayList<>();
    public ListProperty<HashMap<String, Object>> filteredItemsProperty = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    public ListProperty<String> currentSelectedIds = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    public ListProperty<CheckBox> currentItemSelectorP = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    BooleanProperty selectAllProperty = new SimpleBooleanProperty(false);


    public CustomTableView(List<TableColumn<HashMap<String,Object>,String>> cols ,int... idcol) {
        setItems(FXCollections.observableList(new ArrayList<>()));

        itemsProperty().bind(filteredItemsProperty);
        filteredItemsProperty.addAll(new ArrayList<>());

        if (idcol.length > 0) {

        TableColumn<HashMap<String, Object>, String> selectCol = cols.get(idcol[0]);
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

        }


//        itemsProperty().addListener((observableValue, hashMaps, t1) -> currentItemSelectorP.clear());

        getColumns().addAll(cols);
        getStyleClass().addAll("dense", "bordered", "striped");
        setPlaceholder(ProjectUtils.getTablePlaceholder(35));

    }


    public void loadInitialData(List<HashMap<String, Object>> data) {
        allData.addAll(data);
        filteredItemsProperty.set(FXCollections.observableList(data));
    }

    public void filter(String query,List<HashMap<String,Object>>... data) {
        currentItemSelectorP.clear();
        currentSelectedIds.clear();
        selectAllProperty.set(false);
        this.filteredItemsProperty.clear();
        this.setOpacity(0);

        FadeTransition f = new FadeTransition(Duration.millis(200), this);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);

        if (data.length == 0) {
            List<HashMap<String, Object>> res = PgConnector.fetch(query, PgConnector.getConnection());
            filteredItemsProperty.set(FXCollections.observableList(res));


        } else {
            filteredItemsProperty.set(FXCollections.observableList(data[0]));


        }



        f.playFromStart();
    }

    public void selectItem(String itemId) {

        boolean isselected=false;
        try {
            HashMap<String, Object> objToSelect = getItems().stream().filter(item -> Objects.equals(itemId,PgConnector.
                    getFielorBlank(item, "id"))).findFirst().orElse(null);
            if (!Objects.equals(null,objToSelect)) getSelectionModel().select(objToSelect);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }






}
