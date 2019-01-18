package com.raphaelcollin.notepad.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
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

        nomeFonteListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue != null && nomeFonteListView.getSelectionModel().getSelectedItem() != null){
                nomeFonteField.setText(newValue.getText());
                exemploField.setFont(new Font(newValue.getText(),
                        Integer.parseInt(tamanhoListView.getSelectionModel().getSelectedItem())));
            }
        });

        nomeFonteListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

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
                 ObservableList<Label> nameList = FXCollections.observableArrayList();
                 String [] fontes = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

                 for (String font : fontes){
                     Label label = new Label(font);
                     label.setFont(new Font(font,16));
                     nameList.add(label);
                 }

                 return nameList;
             }
         };

         nomeFonteListView.itemsProperty().bind(task.valueProperty());

         task.setOnSucceeded(e -> inicializarCampos());

         new Thread(task).start();



         ObservableList<String> tamanhoList = FXCollections.observableArrayList();

         tamanhoList.addAll("8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72");


         tamanhoListView.setItems(tamanhoList);
     }

    /* Quando esse Controller for instanciado, o atributo vai receber a fonte atual da aplicação. Quando isso acontecer,
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

                    // Bug relacionado A Threads

                try {
                    nomeFonteListView.getSelectionModel().select(indexNome);
                } catch (Exception e){
                    System.out.println("Erro: " + e.getMessage());
                }

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

                    // Bug relacionado a Thread

                try {
                    tamanhoListView.getSelectionModel().select(indexSize);
                } catch (Exception e){
                    System.out.println("erro:" + e.getMessage());
                }

                tamanhoListView.scrollTo(indexSize);
            }
            tamanhoFonteField.setText(String.format("%.0f", fonteAtual.getSize()));
        } else{
            nomeFonteListView.getSelectionModel().select(2);
            nomeFonteField.setText("Arial");
            tamanhoListView.getSelectionModel().select(6);
            tamanhoFonteField.setText("16");
        }
    }


        /*
        * Esse método será executado quando o botão Ok do diálogo for acionado. Quando isso acontecer,
        * vamos retornar a fonte selecionada pelo usuário para o Controller principal, para que ele possa alterar a fonte
        * do textArea
        * */

    Font processarResultado(){
        String fontName = nomeFonteField.getText();
        int fontSize = Integer.parseInt(tamanhoFonteField.getText());
        return new Font(fontName,fontSize);
    }

        // Setter

    void setFonteAtual(Font fonteAtual) {
        this.fonteAtual = fonteAtual;
        inicializarListas();
    }
}
