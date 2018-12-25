package com.raphaelcollin.notepad.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

        /* Por haver uma grande quantidade de fontes, optei pela opção de lê-las a partir do arquivo fontes.txt
        * evitando assim, uma granda quantidade de código nesse método
        * a cada leitura de fonte, vamos adicionar a respectiva fonte a uma lista e no final vamos colocar essa lista
        * no nomeFonteListView
        *
        * Já para a lista de tamanhos, como é um lista relativamente pequena, resolvi fazer de maneira manual*/

    public void initialize(){
        ObservableList<Label> nameList = FXCollections.observableArrayList();
        try (BufferedReader reader = new BufferedReader(new FileReader("fontes.txt"))){
            String input;
            while ((input = reader.readLine()) != null){
                Label label = new Label(input);
                label.setFont(new Font(input,16));
                nameList.add(label);
            }
            nomeFonteListView.getItems().setAll(nameList);
            nomeFonteListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        } catch (IOException e){
            e.printStackTrace();
        }

        ObservableList<String> tamanhoList = FXCollections.observableArrayList();

        tamanhoList.addAll("8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72");

        tamanhoListView.getItems().setAll(tamanhoList);
        tamanhoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        inicializarCampos();

            /* Os dois próximos listener, vão lidar quando a seleção mudar, ou seja, quando for selecionado outro item
            * em uma das listas. Quando isso acontecer, vamos alerar o respectivo textField acima com o texto do item
            * selecionado e também alerar o campo de exemplo, alterando o tamanho ou o tipo da fonte */

        tamanhoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                tamanhoFonteField.setText(newValue);
                exemploField.setFont(new Font(nomeFonteListView.getSelectionModel().getSelectedItem().getText(),
                        Integer.parseInt(newValue)));
            }
        });
        nomeFonteListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue != null){
                nomeFonteField.setText(newValue.getText());
                exemploField.setFont(new Font(newValue.getText(),
                        Integer.parseInt(tamanhoListView.getSelectionModel().getSelectedItem())));
            }
        });


    }

     /* Quando esse Controller for instanciado, o atributo vai receber a fonte atual da aplicação. Quando isso acontecer,
      * precisamos que as ListView selecionem o respectivo nome da fonte atual e o seu tamanho para gerar uma boa
       * experiência ao usuário. Esse é o propósito dessa função*/

    void inicializarCampos(){
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

        // Setters

    void setFonteAtual(Font fonteAtual) {
        this.fonteAtual = fonteAtual;
    }
}
