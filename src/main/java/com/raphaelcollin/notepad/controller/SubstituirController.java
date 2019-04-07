package com.raphaelcollin.notepad.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SubstituirController {
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField localizarField;
    @FXML
    private TextField substituirField;
    @FXML
    private CheckBox checkBox;
    
    private PrincipalController principalController;

    /* Quando o botão localizar próximo for acionado, o processamento será o mesmo do método handleLocalizar()
     * da clasee LocalizarController */

    @FXML
    public void handleLocalizar() {
        String text = localizarField.getText();
        if (text.isEmpty()) {
            exibirAlert("Campo vazio!","Preencha o campo localizar para que seja realizado o precessamento");


        } else {
            int index;
            if (principalController.getLocalizarString() != null && (principalController.getLocalizarString().equals(text))) {
                if (checkBox.isSelected()) {
                    index = principalController.getTextArea().getText().indexOf(text, principalController.getLocalizarIndice());
                } else {
                    index = principalController.getTextArea().getText().toLowerCase().indexOf(text.toLowerCase(), 
                            principalController.getLocalizarIndice());
                }

            } else {
                if (checkBox.isSelected()) {
                    index = principalController.getTextArea().getText().indexOf(text);
                } else {
                    index = principalController.getTextArea().getText().toLowerCase().indexOf(text.toLowerCase());
                }
            }

            if (index >= 0) {
                principalController.getTextArea().selectRange(index, index + text.length());
                principalController.setLocalizarIndice(index + text.length() + 1);
                principalController.setLocalizarString(text);
            } else if (!principalController.getLocalizarString().equals(text)) {
                exibirAlert("Texto não encontrado!","Não foi possível encontrar '" + text + "'");
            }
        }
    }

    /* Quando o botão substituir for acionado, a aplicação vai procurar no textArea a string que está no localizarField,
     * se ele encontrar vai substituir esse pela string contida no substituir field.
     * A cada interação com esse botão, o atributo localizarIndice é atualizado
     * para quando for realizada uma nova busca, a aplicação começar a procurar a partir do último texto encontrar, permitindo
     * que se possa procurar por todas strings contidas no textArea */

    @FXML
    public void handleSubstituir() {
        String localizar = localizarField.getText();

        if(!localizar.equals(principalController.getLocalizarString())){
            principalController.setLocalizarIndice(0);
        }

        principalController.setLocalizarString(localizar);

        String substituir = substituirField.getText();

        if (localizar.isEmpty() || substituir.isEmpty()) {
            exibirAlert("Campo Vazio!","Todos os campos devem ser preenchidos para realizar o processamento!");
        } else {
            int index;
            if(checkBox.isSelected()){
                index = principalController.getTextArea().getText().indexOf(localizar, principalController.getLocalizarIndice());
            } else{
                index = principalController.getTextArea().getText().toLowerCase().indexOf(localizar.toLowerCase(),
                        principalController.getLocalizarIndice());
            }

            if (index >= 0) {
                principalController.getTextArea().replaceText(index,index + localizar.length(),substituir);
                principalController.getTextArea().selectRange(index,index + substituir.length());
                principalController.setLocalizarIndice(index + substituir.length() + 1);
            } else {
                exibirAlert("Texto não encontrado!","Não foi possível encontrar '" + localizar + "'");
                principalController.setLocalizarIndice(0);
            }

        }
    }

    /* Quando o botão substituir todos for acionado, a aplicação vai procurar pela string informada no textField no textArea
     * e substituirá todas strings encontradas, diferentemente do botão substituir que só substitui uma de cada vez */

    @FXML
    public void handleSubstituirTudo() {
        String localizar = localizarField.getText();

        if(!localizar.equals(principalController.getLocalizarString())){
            principalController.setLocalizarIndice(0);
        }

        principalController.setLocalizarString(localizar);
        String substituir = substituirField.getText();

        if (localizar.isEmpty() || substituir.isEmpty()) {
            exibirAlert("Campo Vazio!","Todos os campos devem ser preenchidos para realizar o processamento!");

        } else {
            int contador = 0;
            int index;
            if(checkBox.isSelected()){
                while ((index = principalController.getTextArea().getText().indexOf(localizar,
                        principalController.getLocalizarIndice())) >= 0){
                    principalController.getTextArea().replaceText(index,index + localizar.length(),substituir);
                    principalController.setLocalizarIndice(index + substituir.length() + 1);
                    contador++;
                }

            } else{
                while ((index = principalController.getTextArea().getText().toLowerCase().indexOf(localizar.toLowerCase(),
                        principalController.getLocalizarIndice())) >= 0){
                    principalController.getTextArea().replaceText(index,index + localizar.length(),substituir);

                    principalController.setLocalizarIndice(index + substituir.length() + 1);
                    contador++;
                }
            }

            if (contador == 0) {

                exibirAlert("Texto não encontrado!","Não foi possível encontrar '" + localizar + "'");
                principalController.setLocalizarIndice(0);
            }
        }
    }

    private void exibirAlert(String headrText, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no processamento");
        alert.initOwner(gridPane.getScene().getWindow());
        alert.setHeaderText(headrText);
        alert.setContentText(content);
        alert.show();
    }

    /* Quando o botão Cancelar for acionado, a aplicação vai fechar a nova janela e voltar para a janela principal*/

    @FXML
    public void handleCancelar() {
        ((Stage) gridPane.getScene().getWindow()).close();
    }

    void setPrincipalController(PrincipalController principalController) {
        this.principalController = principalController;
    }
}
