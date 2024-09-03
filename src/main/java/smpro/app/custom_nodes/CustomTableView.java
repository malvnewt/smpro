package smpro.app.custom_nodes;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import smpro.app.utils.PgConnector;
import smpro.app.utils.ProjectUtils;

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
    public ListProperty<HashMap<String, Object>> currentSelectedItemsProperty = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));


    public CustomTableView(List<TableColumn<HashMap<String,Object>,String>> cols ,int idcol) {
        itemsProperty().bind(filteredItemsProperty);


        TableColumn<HashMap<String, Object>, String> selectCol = cols.get(idcol);
        selectCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<HashMap<String, Object>, String> call(TableColumn<HashMap<String, Object>, String> hashMapStringTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (!b) {
                            HashMap<String, Object> item = getTableRow().getItem();

                            setText("");
                            CheckBox cb = new CheckBox();
                            cb.selectedProperty().addListener((observableValue, aBoolean, selected) -> {
                                if (selected) {
                                    //add to selections
                                    currentSelectedItemsProperty.add(item);
                                } else {
                                    //remove from selections
                                    currentSelectedItemsProperty.remove(item);
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

        getColumns().addAll(cols);
        getStyleClass().addAll("dense", "bordered", "striped");

    }


    public void loadInitialData(List<HashMap<String, Object>> data) {
        allData.addAll(data);
        filteredItemsProperty.set(FXCollections.observableList(data));
    }

    public void filter(String query) {
        currentSelectedItemsProperty.set(FXCollections.observableList(new ArrayList<>()));
        List<HashMap<String, Object>> res = PgConnector.fetch(query, PgConnector.getConnection());
        filteredItemsProperty.set(FXCollections.observableList(res));
    }


}
