package com.raphaelcollin.notepad.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.util.Callback;
import java.awt.*;


public class FonteController {

    @FXML
    private TextField nomeFonteField;
    @FXML
    private TextField tamanhoFonteField;
    @FXML
    private TextField exemploField;
    @FXML
    private ListView<Label> nomeFonteListView;
    @FXML
    private ListView<String> tamanhoListView;

    private Font fonteAtual = null;

    public void initialize(){

            /* Os dois próximos listener, vão lidar quando a seleção mudar, ou seja, quando for selecionado outro item
            * em uma das listas. Quando isso acontecer, vamos alerar o respectivo textField acima com o texto do item
            * selecionado e também alerar o campo de exemplo, alterando o tamanho ou o tipo da fonte */

        tamanhoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && tamanhoListView.getSelectionModel().getSelectedItem() != null){
                tamanhoFonteField.setText(newValue);
                exemploField.setFont(new Font(nomeFonteListView.getSelectionModel().getSelectedItem().getText(),
                        Integer.parseInt(newValue)));
            }
        });

        tamanhoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        nomeFonteListView.setOnMouseClicked( event ->  {
            if(nomeFonteListView.getSelectionModel().getSelectedItem() != null){
                Label label = nomeFonteListView.getSelectionModel().getSelectedItem();
                nomeFonteField.setText(label.getText());
            }
        });

            // Configurando ToolTip

        nomeFonteListView.setCellFactory(new Callback<ListView<Label>, ListCell<Label>>() {
            @Override
            public ListCell<Label> call(ListView<Label> param) {
                final Tooltip tooltip = new Tooltip();
                return  new ListCell<Label>(){
                    @Override
                    public void updateItem(Label item, boolean empty){
                        super.updateItem(item,empty);
                        if(item != null){
                            setText(item.getText());
                            tooltip.setText(item.getText());
                            setTooltip(tooltip);
                            setFont(new Font(item.getText(),16));
                        }
                    }
                };

            }
        });

        nomeFonteListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        /* Quando o usuário digitar algo no Campo Nome da Fonte, vamos pesquisar as fontes que se enquadram nos caracteres
         * digitados e mostrar para o usuário, se nenhuma fonte for encontrada, vamos mostrar as fontes desde o inicio */

        nomeFonteField.setOnKeyPressed(event -> {

                boolean encontrou = false;

                for (Label label : nomeFonteListView.itemsProperty().get()){
                    if (label.getText().toLowerCase().indexOf(nomeFonteField.getText().toLowerCase()) == 0){
                        nomeFonteListView.scrollTo(label);
                        nomeFonteListView.getSelectionModel().select(label);
                        encontrou = true;
                        break;
                    }
                }

                if (!encontrou){
                    nomeFonteListView.scrollTo(0);
                    nomeFonteListView.getSelectionModel().select(0);
                }

        });


    }

        /* Inicializando listas
         *
          * A Lista de nomes das fontes será criada a partir das fontes disponíveis no sistema operacional em que está
          * sendo executada a aplicação. Utilizaremos outro Thread para realizar esse carregamento
          *
          * A Lista de tamanhos das fontes será definida manualmente*/

     private void inicializarListas(){
         Task<ObservableList<Label>> task = new Task<ObservableList<Label>>() {
             @Override
             protected ObservableList<Label> call(){
                 ObservableList<Label> fontes = FXCollections.observableArrayList();
                 String [] fontes2 = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

                 for (String font : fontes2){
                     Label label = new Label(font);
                     label.setFont(new Font(font,16));
                     fontes.add(label);
                 }
                 return fontes;
             }
         };

         nomeFonteListView.itemsProperty().bind(task.valueProperty());

            /* Quando o carregamento das fontes terminar, vamos inicializar os campos
             * para que quando o usuário digite o nome de uma fonte no textField, essa fonte seja localizada */

         task.setOnSucceeded(e -> inicializarCampos());

         new Thread(task).start();



         ObservableList<String> tamanhoList = FXCollections.observableArrayList();

         tamanhoList.addAll("8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72");


         tamanhoListView.setItems(tamanhoList);
     }

    /* Quando esse PrincipalController for instanciado, o atributo vai receber a fonte atual da aplicação. Quando isso acontecer,
     * precisamos que as ListView selecionem o respectivo nome da fonte atual e o seu tamanho para gerar uma boa
     * experiência ao usuário. Esse é o propósito dessa função*/

    private void inicializarCampos(){


        if (fonteAtual != null){
            int indexNome = -1;
            for(int c = 0; c < nomeFonteListView.getItems().size(); c++){
                if(nomeFonteListView.getItems().get(c).getText().equals(fonteAtual.getName())){
                    indexNome = c;
                }
            }
            if(indexNome >= 0){

                nomeFonteListView.getSelectionModel().select(indexNome);
                nomeFonteListView.scrollTo(indexNome);

            }
            nomeFonteField.setText(fonteAtual.getName());

            int indexSize = -1;

            for(int c = 0; c < tamanhoListView.getItems().size(); c++){
                if(tamanhoListView.getItems().get(c).equals(String.format("%.0f",fonteAtual.getSize()))){
                    indexSize = c;
                }
            }

            if(indexSize >= 0){

                tamanhoListView.getSelectionModel().select(indexSize);
                tamanhoListView.scrollTo(indexSize);
            }
            tamanhoFonteField.setText(String.format("%.0f", fonteAtual.getSize()));
        } else{
            nomeFonteListView.getSelectionModel().select(2);
            nomeFonteField.setText("Arial");
            tamanhoListView.getSelectionModel().select(6);
            tamanhoFonteField.setText("16");
        }

        /* O próximo listener, vai lidar quando a seleção mudar, ou seja, quando for selecionado outro item
         * em uma das listas. Quando isso acontecer, vamos alerar o respectivo textField acima com o texto do item
         * selecionado e também alerar o campo de exemplo, alterando o tamanho ou o tipo da fonte */

        nomeFonteListView.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) -> {
            Label label = nomeFonteListView.getSelectionModel().getSelectedItem();
            exemploField.setFont(new Font(label.getText(),
                    Integer.parseInt(tamanhoListView.getSelectionModel().getSelectedItem())));
        });

    }

        /*
        * Esse método será executado quando o botão Ok do diálogo for acionado. Quando isso acontecer,
        * vamos retornar a fonte selecionada pelo usuário para o PrincipalController principal, para que ele possa alterar a fonte
        * do textArea
        * */

    Font processarResultado(){
        String fontName = nomeFonteListView.getSelectionModel().getSelectedItem().getText();
        int fontSize = Integer.parseInt(tamanhoListView.getSelectionModel().getSelectedItem());
        return new Font(fontName,fontSize);
    }

        // Setter

    void setFonteAtual(Font fonteAtual) {
        this.fonteAtual = fonteAtual;
        inicializarListas();
    }
}
