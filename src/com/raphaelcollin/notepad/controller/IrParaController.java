package com.raphaelcollin.notepad.controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class IrParaController {
    @FXML
    private TextField textField;


    /* Esse método é processado quando o usuário clica no botão Ir Para do diálogo. Quando isso acontece,
    * Nós vamos checar o textField. Se ele for vazio, não for um número ou o número informado for maior que o numero
    * total de linhas, vamos exibir um erro.
    *
    * Se esses possibilidades não acontecerem, nós vamos selecionar alguns caracteres no textArea do controller
     * principal da linha escolhida*/

    @FXML
    void processarResultado(TextArea textArea, BorderPane borderPane){

        if(textField.getText().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no processamento");
            alert.initOwner(borderPane.getScene().getWindow());
            alert.setHeaderText("Campo Vazio!");
            alert.setContentText("O campo 'Número da linha' deve ser preenchido!");
            alert.show();
        } else{
            try {
                int numeroDaLinhaEscolhida = Integer.parseInt(textField.getText());
                String[] partes = textArea.getText().split("\n");
                int numeroTotalDeLinhas = textArea.getText().split("\n").length;
                if (numeroDaLinhaEscolhida > numeroTotalDeLinhas){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro no processamento");
                    alert.initOwner(borderPane.getScene().getWindow());
                    alert.setHeaderText("Campo Inválido!");
                    alert.setContentText("O número da linha não pode ser maior que o total de linhas");
                    alert.show();
                } else{
                    int numeroCaracteres = 0;
                    for (int c = 0; c < numeroDaLinhaEscolhida-1; c++){
                        numeroCaracteres += partes[c].length();
                    }
                    textArea.positionCaret(numeroCaracteres + numeroDaLinhaEscolhida - 1);
                    textArea.selectRange(numeroCaracteres + numeroDaLinhaEscolhida - 1,numeroCaracteres + numeroDaLinhaEscolhida - 1 + 5);
                }

            } catch (NumberFormatException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no processamento");
                alert.initOwner(borderPane.getScene().getWindow());
                alert.setHeaderText("Campo inválido!");
                alert.setContentText("Preencha o campo apenas com números");
                alert.show();
            }
        }

    }
}
