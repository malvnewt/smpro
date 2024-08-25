/*
 * Just copy and paste the code.
 */
package smpro.app.tableadapter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author Hasan Selman Kara
 */
public class EditableTableFx {

    private TableView<Person> table = new TableView<>();
    private final ObservableList<Typ> typData
            = FXCollections.observableArrayList(
                    new Typ("Hund"),
                    new Typ("Fuchs"),
                    new Typ("Esel"));
    private final ObservableList<Person> data
            = FXCollections.observableArrayList(
                    new Person("Jacob", typData.get(0), new Date()),
                    new Person("Urs", typData.get(1), new Date()),
                    new Person("Hans", typData.get(2), new Date()),
                    new Person("Ueli", typData.get(2), new Date()));

    final HBox hb = new HBox();


    class EditingCell extends TableCell<Person, String> {

        private TextField textField;

        private EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(item);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
//                        setGraphic(null);
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnAction((e) -> commitEdit(textField.getText()));
            textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue) {
                    System.out.println("Commiting " + textField.getText());
                    commitEdit(textField.getText());
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

    class DateEditingCell extends TableCell<Person, Date> {

        private DatePicker datePicker;

        private DateEditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createDatePicker();
                setText(null);
                setGraphic(datePicker);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getDate().toString());
            setGraphic(null);
        }

        @Override
        public void updateItem(Date item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (datePicker != null) {
                        datePicker.setValue(getDate());
                    }
                    setText(null);
                    setGraphic(datePicker);
                } else {
                    setText(getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    setGraphic(null);
                }
            }
        }

        private void createDatePicker() {
            datePicker = new DatePicker(getDate());
            datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            datePicker.setOnAction((e) -> {
                System.out.println("Committed: " + datePicker.getValue().toString());
                commitEdit(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            });
//            datePicker.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                if (!newValue) {
//                    commitEdit(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
//                }
//            });
        }

        private LocalDate getDate() {
            return getItem() == null ? LocalDate.now() : getItem().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    class ComboBoxEditingCell extends TableCell<Person, Typ> {

        private ComboBox<Typ> comboBox;

        private ComboBoxEditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createComboBox();
                setText(null);
                setGraphic(comboBox);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getTyp().getTyp());
            setGraphic(null);
        }

        @Override
        public void updateItem(Typ item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (comboBox != null) {
                        comboBox.setValue(getTyp());
                    }
                    setText(getTyp().getTyp());
                    setGraphic(comboBox);
                } else {
                    setText(getTyp().getTyp());
                    setGraphic(null);
                }
            }
        }

        private void createComboBox() {
            comboBox = new ComboBox<>(typData);
            comboBoxConverter(comboBox);
            comboBox.valueProperty().set(getTyp());
            comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            comboBox.setOnAction((e) -> {
                System.out.println("Committed: " + comboBox.getSelectionModel().getSelectedItem());
                commitEdit(comboBox.getSelectionModel().getSelectedItem());
            });
//            comboBox.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                if (!newValue) {
//                    commitEdit(comboBox.getSelectionModel().getSelectedItem());
//                }
//            });
        }

        private void comboBoxConverter(ComboBox<Typ> comboBox) {
            // Define rendering of the list of values in ComboBox drop down. 
            comboBox.setCellFactory((c) -> {
                return new ListCell<Typ>() {
                    @Override
                    protected void updateItem(Typ item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getTyp());
                        }
                    }
                };
            });
        }

        private Typ getTyp() {
            return getItem() == null ? new Typ("") : getItem();
        }
    }

    public static class Typ {

        private final SimpleStringProperty typ;

        public Typ(String s) {
            this.typ = new SimpleStringProperty(s);
        }

        public String getTyp() {
            return this.typ.get();
        }

        public StringProperty typProperty() {
            return this.typ;
        }

        public void setTyp(String typ) {
            this.typ.set(typ);
        }

        @Override
        public String toString() {
            return typ.get();
        }

    }
    
    public static class Project {
        private final SimpleStringProperty name;
        private final SimpleListProperty<Person> persons;

        public Project(String name, List<Person> persons) {
            this.name = new SimpleStringProperty(name);
            
            this.persons = new SimpleListProperty<>();
            this.persons.setAll(persons);
        }
        
        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return this.name;
        }

        public void setName(String name) {
            this.name.set(name);
        }
        
        public List<Person> getPersons() {
            return this.persons.get();
        }
        
        public SimpleListProperty<Person> personsProperty() {
            return this.persons;
        }
        
        public void setPersons(List<Person> persons) {
            this.persons.setAll(persons);
        }
        
    }

    public static class Person {

        private final SimpleStringProperty firstName;
        private final SimpleObjectProperty<Typ> typ;
        private final SimpleObjectProperty<Date> birthday;

        public Person(String firstName, Typ typ, Date bithday) {
            this.firstName = new SimpleStringProperty(firstName);
            this.typ = new SimpleObjectProperty(typ);
            this.birthday = new SimpleObjectProperty(bithday);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public StringProperty firstNameProperty() {
            return this.firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName.set(firstName);
        }

        public Typ getTypObj() {
            return typ.get();
        }

        public ObjectProperty<Typ> typObjProperty() {
            return this.typ;
        }

        public void setTypObj(Typ typ) {
            this.typ.set(typ);
        }

        public Date getBirthday() {
            return birthday.get();
        }

        public ObjectProperty<Date> birthdayProperty() {
            return this.birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday.set(birthday);
        }

    }

}