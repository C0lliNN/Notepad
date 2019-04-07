package com.raphaelcollin.notepad.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LocalizarController {
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField textField;
    @FXML
    private CheckBox checkBox;

    private PrincipalController principalController;

    /* Quando o botão Localizar for acionado, a aplicação vai procurar pela string contida no textField no textArea
     * se a string for encontrada, a aplicação vai destacar essa string no textArea. Se o check box estiver ativo, iremos
     * diferenciar maiusculas de minusculas.
     * Se após ser encontrada uma vez, o usuário procurar pela mesma string, a busca começará após a posição da última
     * string, permitindo que a aplicação ache todas strings contidas no textArea mas se a string informada for diferente
     * a aplicação começa a procurar da posição zero */

    @FXML
    public void handleLocalizar() {
        String text = textField.getText();
        if (text.trim().isEmpty()) {

            exibirAlert("Preencha o campo localizar para que seja realizado o precessamento");

        } else {
            int index;
            if (principalController.getLocalizarString() != null && (principalController.getLocalizarString().equalsIgnoreCase(text))) {
                if (checkBox.isSelected()){
                    index = principalController.getTextArea().getText().indexOf(text, principalController.getLocalizarIndice());
                } else {
                    index = principalController.getTextArea().getText().toLowerCase().
                            indexOf(text.toLowerCase(), principalController.getLocalizarIndice());
                }
            } else {
                if (checkBox.isSelected()){
                    index = principalController.getTextArea().getText().indexOf(text);
                } else {
                    index = principalController.getTextArea().getText().toLowerCase().indexOf(text.toLowerCase());
                }
            }

            if (index >= 0) {
                principalController.getTextArea().selectRange(index, index + text.length());
                principalController.setLocalizarIndice(index + text.length() + 1);
                principalController.setLocalizarString(text);
            } else if (principalController.getLocalizarString() != null && !principalController.getLocalizarString().equals(text)) {
                exibirAlert("Não foi possível encontrar '" + text + "'");
            }
        }
    }

    /* Quando o botão Cancelar for acionado, a aplicação vai fechar a nova janela e voltar para a janela principal  */

    @FXML
    public void handleCancelar() {
        ((Stage) gridPane.getScene().getWindow()).close();
    }

    /* Setter para o controle principal */

    void setPrincipalController(PrincipalController principalController) {
        this.principalController = principalController;
    }

    private void exibirAlert(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no processamento");
        alert.initOwner(gridPane.getScene().getWindow());
        alert.setHeaderText("Texto não encontrado!");
        alert.setContentText(content);
        alert.show();
    }
}
