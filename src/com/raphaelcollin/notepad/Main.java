package com.raphaelcollin.notepad;

import com.raphaelcollin.notepad.controller.Controller;
import java.awt.Dimension;
import java.awt.Toolkit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;
    
    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource
                ("/com/raphaelcollin/notepad/view/janela_principal.fxml"));

        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        primaryStage.setTitle("Bloco de Notas");

            // Configurando largura e altura para 70% da resolução atual do computador que está sendo executado

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.width * 0.7;
        double height = screenSize.height * 0.7;
        primaryStage.setScene(new Scene(root, width, height));

            // CSS

        root.getStylesheets().add(getClass().getResource("/com/raphaelcollin/notepad/estilo.css").toExternalForm());

            // Colocando Icone

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/raphaelcollin/notepad/arquivos/icone.PNG")));

            /* Configurando quando o usuário tenta sair do programa
             * Se o conteudo estiver vazio ou estiver salvo, vamos sair imediatamente mas,
              * se o conteudo não estiver salvo, exibiremos um alerta para o usuário perguntado se ele deseja
              * salvar antes de sair*/

        primaryStage.setOnCloseRequest(e -> {

            if(controller.getTextArea().getText().trim().isEmpty() || controller.isEstaSalvo()){
                Platform.exit();
            } else{
                try {
                    int result = controller.exibirConfirmacaoSaida();
                    if(result >= 0){
                        Platform.exit();
                    } else{
                        e.consume();
                    }
                } catch (Exception e2){
                    e2.printStackTrace();
                }
            }

        });

        primaryStage.show();

    }

    
    @Override
    public void stop() {
        controller.salvarConfiguracao();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
