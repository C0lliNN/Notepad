package com.raphaelcollin.notepad.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controller {

    @FXML
    private BorderPane borderPane;
    @FXML
    private MenuItem recortarMenu;
    @FXML
    private MenuItem copiarMenu;
    @FXML
    private MenuItem excluirMenu;
    @FXML
    private MenuItem localizarMenu;
    @FXML
    private MenuItem localizarProximaMenu;
    @FXML
    private CheckMenuItem quebraLinhaMenu;
    @FXML
    private CheckMenuItem barraStatusMenu;
    @FXML
    private TextArea textArea;
    @FXML
        // Esse atributo vai controlar o label da linha da Barra de Status
    private Label linhaLabel;
    @FXML
        // Esse atibuto vai controlar o label da coluna da Barra de Status
    private Label colunaLabel;

        /* Esse atributo vai controlar a fonte atual do programa para que quando o usuário quiser trocar
        * a fonte, a janela da fonte abra com a fonte atual selecionada*/
    private Font fonteAtual = null;

        // Esse atributo auxilia o atributo linhaLabel para controlar a linha do textArea que está selecionada

    private int linha = 1;

        // Esse atributo auxilia o atributo colunaLabel para controlar a coluna do textArea que está selecionada

    private int coluna = 1;

        /* Esse atributo serve para o método handleLocalizar() e handleLocalizarProximo() saibam qual foi
         * o último índice encontrado para a partir desse índice desse índice */

    private int localizarIndice = 0;

        /* Esse atributo serve para controlar a barra de status, que e bottom do Border Pane, nós só exibiremos
         * a barra de status quando o o CheckMenuItem barraStatus estiver selecionado
          * Quando isso acontecer nós vamos color o Node bottom como a parte de baixo da aplicacao
          * borderPane.setBottom(bottom)
          * quando o menu não estiver selecionado nós vamos colocar nulo no bottom
          * borderPane.setBottom(null) */

    private Node bottom;

        // Esse atributo será usado no método handleLocalizarProximo() para saber qual é a string a ser localizada

    private String localizarString = null;

        // Esse atributo representa o arquivo atual que está sendo usado

    private File file = null;

        /* Esse atributo representa o último diretório aberto para que quando formos salvar ou abrir um arquivo
        * o programa abra sempre no último diretório, representado uma melhor experiencia ao usuário*/

    private File parentFile = null;

        // Esse atributo representa o atual estado o arquivo, se ele está salvo ou não.

    private boolean estaSalvo = false;

    public void initialize() {

        /* Nesse ponto, vamos salvar o bottom do Border Pane no atributo bottom pois se o menu
        * Barra Status não estiver selecionado, nós colocaremos null no bottom do Border Pane
        * para que o textArea ocupe o espaco definido, por esse motivo
        * nós precisamos desse atributo*/

        bottom = borderPane.getBottom();

        /* Nesse momento, vamos ler configurações do arquivo config.txt
        * Essas informações são último diretório acessado, se o menu quebra de linha e o menu Barra de status
         * estavam ativados ou não na última vez que o programa foi executado, convencionei 0 para desativado
          * e 1 para ativado.
          * Também vamos ler a última fonte e seu tamanho
          *
          * Na primeira linha do arquivo, temos o último diretório acessado,
          * se o diretorio existir e o programa tiver permissão para acessa-lo, nós instaciaremos um novo arquivo
          * com esse diretório para o atributo parentFile, se o diretório não for válido e continuara como nulo
          * e o programa abriará no diretório padrao
          *
          * Na Segunda e terceira linha, temos as informações dos menus Quebra de linha e Barra de Status
          * Se o conteúdo da linha for 0, o menu será desativado e se for 1 será ativado e processado o método
          * correspondente
          *
          * Na Quarta e última linha, temos a informação do nome da fonte e seu tamnho, separados por vírgula
          * nós vamos ler esses dados e instanciar um novo objeto para o atributon fonteAutal
          * */

        try (BufferedReader reader = new BufferedReader(new FileReader(getClass().getResource("../arquivos/config.txt").getFile()))) {
            String input = reader.readLine();
            if (input != null && !input.trim().isEmpty()) {
                File file = new File(input);
                if(file.exists() && file.canExecute()){
                    parentFile = new File(input);
                }
            }
            input = reader.readLine();
            if(input.equals("1")){
                quebraLinhaMenu.setSelected(true);
            } else {
                quebraLinhaMenu.setSelected(false);
            }
            handleQuebraDeLinha();
            input = reader.readLine();
            if(input.equals("1")){
                barraStatusMenu.setSelected(true);
            } else{
                barraStatusMenu.setSelected(false);
            }
            handleBarraDeStatus();
            input = reader.readLine();
            String[] partes = input.split(",");
            fonteAtual = new Font(partes[0],Integer.parseInt(partes[1]));
            textArea.setFont(fonteAtual);

                /* Nesse listener nós vamos controlar quando a posição do caret (atual posição de digitação) mudar
                * quando isso acontecer, atualizaremos os atributos linha, linhaLabel, coluna e colunaLabel que
                * são usados na Barra de Status */

            textArea.caretPositionProperty().addListener( (observable, oldValue, newValue) ->  {
                if (newValue != null){
                    int position = textArea.getCaretPosition();
                    int contador = 1;
                    int soma = 1;

                    for (int c = 0; c < position; c++){
                        if (textArea.getText().charAt(c) == '\n'){
                            contador++;
                            soma = 1;
                        } else{
                            soma++;
                        }
                    }

                    linha = contador;
                    coluna = soma;

                    linhaLabel.setText("Linha: " + linha);
                    colunaLabel.setText("Coluna: " + coluna);

                }

            });

            /* Quando uma tecla que altere o texto for acionado, o atributo estaSalvo e atualizado,
                indicando que o arquivo não está mais salvo.*/

            textArea.textProperty().addListener( (observable, oldValue, newValue) -> estaSalvo = false);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

     /* Nesse método, vamos controlar a ação do sub-menu Novo, se o arquivo atual estiver salvo ou o conteudo
      * do texto estiver vazio, nós imediatamente vamos zerar o texto e colocar o aquivo atual para nulo.
       * Se isso não acontecer exibiremos a confirmação de saída perguntando se o usuário quer salvar antes de sair
       * de a resposta do úsuario for diferente do botão de cancelar, nós zeraremos o texto, e colocaremos nulo
       * do atributo file que representa o arquivo atual*/

    @FXML
    public void handleNovo() throws Exception {
        if (estaSalvo || textArea.getText().trim().isEmpty()) {
            textArea.setText("");
            file = null;
        } else {
            int result = exibirConfirmacaoSaida();
            if (result >= 0) {
                textArea.setText("");
                file = null;
            }
        }
    }

    /* Nesse método, vamos controlar a ação do sub-menu Abrir.., se o arquivo atual não estiver salvo e não estiver vazio
     * vamos exibir uma confirmação de saída perguntado se o usuário quer salvar o arquivo atual, se ele disser que
      * sim, vamos salvar o arquivo atual e abrir o diálogo de abertura de arquivo. Se o atributo parentFile
      * que representa o último diretório acessado existir e for acessível, o diálogo de abertura do arquivo será aberto
      * nesse diretório senão será aberto no diretório padrão
      * Depois que o arquivo for selecionado pelo usuário, vamos ler o arquivo e exibir seu conteudo no textArea*/

    @FXML
    public void handleAbrir() throws Exception {
        int result = 0;
        if (!estaSalvo && !textArea.getText().trim().isEmpty()) {
            result = exibirConfirmacaoSaida();
        }
        if (result >= 0) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Abrir Arquivo");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);


            if (parentFile != null && parentFile.canExecute() && parentFile.exists()) {
                fileChooser.setInitialDirectory(parentFile);
            }

            File aux = fileChooser.showOpenDialog(borderPane.getScene().getWindow());

            if (aux != null) {
                file = aux;
            }

            if (file != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String input;
                    int c = 0;
                    while ((input = reader.readLine()) != null) {
                        if (!input.trim().isEmpty()) {
                            if (c == 0) {
                                textArea.setText(input);
                            } else {
                                textArea.setText(textArea.getText() + "\n" + input);
                            }
                            c++;
                        }
                    }
                    estaSalvo = true;
                    textArea.positionCaret(textArea.getText().length());
                    parentFile = file.getParentFile();
                }
            }
        }

    }

    /* Nesse método, vamos lidar com a ação do sub-menu Salvar, se o arquivo já estiver sido salvo, nós apenas
     * vamos atualiza-lo pois já temos o seu caminho mas se o arquivo ainda não esitver sido salvo, vamos abrir um
      * diálogo para o usuário selecionar um caminho, se o usuário selecionar um arquivo válido, nós vamos criar
      * um arquivo e salvar ele com o conteudo do textArea*/

    @FXML
    public void handleSalvar() throws IOException {
        if (!estaSalvo) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salvar Arquivo");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            if (parentFile != null && parentFile.canExecute() && parentFile.exists()) {
                fileChooser.setInitialDirectory(parentFile);
            }
            if (file == null) {
                file = fileChooser.showSaveDialog(borderPane.getScene().getWindow());
            }
            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(textArea.getText());
                    estaSalvo = true;
                    parentFile = file.getParentFile();
                }
            }
        }

    }

    /* Nesse método, vamos lidar com o sub-menu Salvar Como.., se o atributo parentFile que representa o último
     * diretório selecionado for um diretório válido, nós vamos abrir o diálogo com esse diretório se não vamos
      * abrir o diálogo no diretório padrão*/

    @FXML
    public void handleSalvarComo() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Arquivo");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        if (parentFile != null && parentFile.canExecute() && parentFile.exists()) {
            fileChooser.setInitialDirectory(parentFile);
        }

        File aux = fileChooser.showSaveDialog(borderPane.getScene().getWindow());
        if (aux != null) {
            file = aux;
        }

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textArea.getText());
                estaSalvo = true;
                parentFile = file.getParentFile();
            }
        }

    }

    /* Nesse método, vamos lidar com o sub-menu Sair, se o arquivo estiver salvo ou o texto estiver vazio,
     * vamos sair imediatamente senão vamos exibir um diálogo de confirmação perguntado se o úsuario quer salvar
     * o arquivo antes de sair, sair sem salvar ou cancelar e voltar para aplicação*/


    @FXML
    public void handleSair() throws Exception {
        if (estaSalvo || textArea.getText().trim().isEmpty()) {
            Platform.exit();
        } else {
            int result = exibirConfirmacaoSaida();
            if (result >= 0) {
                Platform.exit();
            }
        }

    }

        /* Nesse método, vamos lidar quando o menu editar for ativado, se houver algum texto selecionado vamos
        * habilitar os menus copiar, recortar e excluir senão eles vão ficar desativados
        * Se o texto estiver vazio, os menus localizar e localizar proximo ficarão desativados senão
        * serão ativos.*/

    @FXML
    public void handleEditarMenu() {
        if (textArea.getSelectedText().isEmpty()) {
            recortarMenu.setDisable(true);
            copiarMenu.setDisable(true);
            excluirMenu.setDisable(true);
        } else {
            recortarMenu.setDisable(false);
            copiarMenu.setDisable(false);
            excluirMenu.setDisable(false);
        }
        if (textArea.getText().isEmpty()) {
            localizarMenu.setDisable(true);
            localizarProximaMenu.setDisable(true);
        } else {
            localizarMenu.setDisable(false);
            localizarProximaMenu.setDisable(false);
        }
    }

        /* Nesse método, vamos lidar com o sub-menu Desafazer, chamaremos o método do textArea
        * que realiza essa operação*/

    @FXML
    public void handleDesfazer() {
        textArea.undo();
    }

        /* Nesse método, vamos lidar com o sub-menu Recortar, chamaremos o método do textArea
         * que realiza essa operação*/

    @FXML
    public void handleRecortar() {
        textArea.cut();
    }

        /* Nesse método, vamos lidar com o sub-menu Copiar, chamaremos o método do textArea
         * que realiza essa operação*/

    @FXML
    public void handleCopiar() {
        textArea.copy();
    }

        /* Nesse método, vamos lidar com o sub-menu Colar, chamaremos o método do textArea
         * que realiza essa operação*/

    @FXML
    public void handleColar() {
        textArea.paste();
    }

        /* Nesse método, vamos lidar com o sub-menu Excluir, chamaremos o método do textArea
         * que realiza essa operação*/

    @FXML
    public void handleExcluir() {
        if (!textArea.getSelectedText().isEmpty()) {
            textArea.deleteText(textArea.getSelection());
        }
    }

     /* Nesse método, vamos lidar com o sub-menu Localizar, quando esse menu for acionado, exibiremos outra
     * janela com dois botões: Localizar e Cancelar
     *
     * Quando o botão Localizar for acionado, a aplicação vai procurar pela string contida no textField no textArea
     * se a string for encontrada, a aplicação vai destacar essa string no textArea.
     * Se após ser encontrada uma vez, o usuário procurar pela mesma string, a busca começará após a posição da última
     * string, permitindo que a aplicação ache todas strings contidas no textArea mas se a string informada for diferente
     * a aplicação começa a procurar da posição zero
     *
     * Quando o botão Cancelar for acionado, a aplicação vai fechar a nova janela e voltar para a janela principal*/

    @FXML
    public void handleLocalizar() throws Exception {

        textArea.selectRange(0,0);

        GridPane gridPane = FXMLLoader.load(getClass().getResource("../view/janela_localizar.fxml"));
        gridPane.setStyle("-fx-background-color: #F7F7F7");

        TextField textField = (TextField) gridPane.getChildren().get(1);

        HBox hBox = (HBox) gridPane.getChildren().get(2);

        Button localizar = (Button) hBox.getChildren().get(0);
        localizar.setDefaultButton(true);
        localizarIndice = 0;
        localizar.setOnAction(e -> {
            String text = textField.getText();
            if (text.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no processamento");
                alert.initOwner(gridPane.getScene().getWindow());
                alert.setHeaderText("Campo vazio!");
                alert.setContentText("Preencha o campo localizar para que seja realizado o precessamento");
                alert.show();
            } else {
                int index;
                if (localizarString != null && (localizarString.equals(text))) {
                    index = textArea.getText().indexOf(text, localizarIndice);
                } else {
                    index = textArea.getText().indexOf(text);
                }

                if (index >= 0) {
                    textArea.selectRange(index, index + text.length());
                    localizarIndice = index + text.length() + 1;
                    localizarString = text;
                } else if (!localizarString.equals(text)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro no processamento");
                    alert.initOwner(gridPane.getScene().getWindow());
                    alert.setHeaderText("Texto não encontrado!");
                    alert.setContentText("Não foi possível encontrar '" + text + "'");
                    alert.show();
                }
            }


        });

        Button cancelar = (Button) hBox.getChildren().get(1);
        cancelar.setOnAction(event -> {
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.close();
        });

        Stage stage = new Stage();
        stage.setTitle("Localizar");
        stage.setScene(new Scene(gridPane, 400, 120));
        stage.initOwner(borderPane.getScene().getWindow());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../arquivos/icone.png")));
        stage.setResizable(false);
        stage.show();

    }

    /* Nesse método, vamos lidar com o sub-menu Localizar Proximo, se a aplicação já tiver procurada um string antes,
     * ele terá sido salva no atributo localizarString, se esse atributo não for nulo, vamos procurar a próxima string
      * a partir do atributo localizarIndice*/

    @FXML
    public void handleLocalizarProxima() {
        if (localizarString != null) {
            localizarIndice = textArea.getText().indexOf(localizarString, localizarIndice);
            if (localizarIndice >= 0) {
                textArea.selectRange(localizarIndice, localizarIndice + localizarString.length());
                localizarIndice = localizarIndice + localizarString.length() + 1;
            } else {
                localizarIndice = 0;
            }
        }
    }

    /* Nesse método, vamos lidar com o sub-menu Substituir..., quando ele for acionado
     * exibiremos uma nova janela com um Text Field e quatro botões (Localizar Proximo, Substituir, Substituir Todos
      * e cancelar).
      *
      * Quando o botão localizar próximo for acionado, o processamento será o mesmo do método handleLocalizar()
      *
      * Quando o botão substituir for acionado, a aplicação vai procurar no texto a string que está no text field,
      * se ele encontrar vai substituir esse texto. A cada interação com esse botão, o atributo localizarIndice é atualizado
      * para quando for realizada uma nova busca, a aplicação começar a procurar a partir do último texto encontrar, permitindo
      * que se possa procurar por todas strings contidas no textArea
      *
      * Qaundo o botão substituir todos for acionado, a aplicação vai procurar pela string informada no textField no textArea
      * e substituirá todas strings encontradas, diferentemente do botão substituir que só substitui uma de cada vez
      *
      * Quando o botão Cancelar for acionado, a aplicação vai fechar a nova janela e voltar para a janela principal*/

    @FXML
    public void handleSubstituir() throws Exception {

        textArea.selectRange(0,0);

        GridPane gridPane = FXMLLoader.load(getClass().getResource("../view/janela_substituir.fxml"));
        gridPane.setStyle("-fx-background-color: #F7F7F7");

        VBox vBox1 = (VBox) gridPane.getChildren().get(0);

        HBox hBox1 = (HBox) vBox1.getChildren().get(0);

        TextField localizarField = (TextField) hBox1.getChildren().get(1);

        HBox hBox2 = (HBox) vBox1.getChildren().get(1);

        TextField substituirField = (TextField) hBox2.getChildren().get(1);

        CheckBox checkBox = (CheckBox) vBox1.getChildren().get(3);

        VBox vBox2 = (VBox) gridPane.getChildren().get(1);

        Button localizarButton = (Button) vBox2.getChildren().get(0);

        localizarIndice = 0;

        localizarButton.setOnAction(e -> {
            String text = localizarField.getText();
            if (text.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no processamento");
                alert.initOwner(gridPane.getScene().getWindow());
                alert.setHeaderText("Campo vazio!");
                alert.setContentText("Preencha o campo localizar para que seja realizado o precessamento");
                alert.show();
            } else {
                int index;
                if (localizarString != null && (localizarString.equals(text))) {
                    if (checkBox.isSelected()) {
                        index = textArea.getText().indexOf(text, localizarIndice);
                    } else {
                        index = textArea.getText().toLowerCase().indexOf(text.toLowerCase(), localizarIndice);
                    }

                } else {
                    if (checkBox.isSelected()) {
                        index = textArea.getText().indexOf(text);
                    } else {
                        index = textArea.getText().toLowerCase().indexOf(text.toLowerCase());
                    }
                }

                if (index >= 0) {
                    textArea.selectRange(index, index + text.length());
                    localizarIndice = index + text.length() + 1;
                    localizarString = text;
                } else if (!localizarString.equals(text)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro no processamento");
                    alert.initOwner(gridPane.getScene().getWindow());
                    alert.setHeaderText("Texto não encontrado!");
                    alert.setContentText("Não foi possível encontrar '" + text + "'");
                    alert.show();
                }
            }
        });

        Button substituirButton = (Button) vBox2.getChildren().get(1);

        substituirButton.setDefaultButton(true);

        substituirButton.setOnAction(e -> {

            String localizar = localizarField.getText();

            if(!localizar.equals(localizarString)){
                localizarIndice = 0;
            }

            localizarString = localizar;

            String substituir = substituirField.getText();

            if (localizar.isEmpty() || substituir.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no processamento");
                alert.setHeaderText("Campo Vazio!");
                alert.setContentText("Todos os campos devem ser preenchidos para realizar o processamento!");
                alert.initOwner(gridPane.getScene().getWindow());
                alert.show();
            } else {
                int index;
                if(checkBox.isSelected()){
                    index = textArea.getText().indexOf(localizar, localizarIndice);
                } else{
                    index = textArea.getText().toLowerCase().indexOf(localizar.toLowerCase(), localizarIndice);
                }

                if (index >= 0) {
                    textArea.replaceText(index,index + localizar.length(),substituir);
                    textArea.selectRange(index,index + substituir.length());
                    localizarIndice = index + substituir.length() + 1;
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro no processamento");
                    alert.setHeaderText("Texto não encontrado!");
                    alert.setContentText("Não foi possível encontrar '" + localizar + "'");
                    alert.initOwner(gridPane.getScene().getWindow());
                    alert.show();
                    localizarIndice = 0;
                }

            }

        });

        Button substituirTudoButton = (Button) vBox2.getChildren().get(2);

        substituirTudoButton.setOnAction( e -> {
            String localizar = localizarField.getText();

            if(!localizar.equals(localizarString)){
                localizarIndice = 0;
            }

            localizarString = localizar;

            String substituir = substituirField.getText();

            if (localizar.isEmpty() || substituir.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no processamento");
                alert.setHeaderText("Campo Vazio!");
                alert.setContentText("Todos os campos devem ser preenchidos para realizar o processamento!");
                alert.initOwner(gridPane.getScene().getWindow());
                alert.show();
            } else {
                int contador = 0;
                int index;
                if(checkBox.isSelected()){
                    while ((index = textArea.getText().indexOf(localizar, localizarIndice)) >= 0){
                        textArea.replaceText(index,index + localizar.length(),substituir);
                        localizarIndice = index + substituir.length() + 1;
                        contador++;
                    }

                } else{
                    while ((index = textArea.getText().toLowerCase().indexOf(localizar.toLowerCase(), localizarIndice)) >= 0){
                        textArea.replaceText(index,index + localizar.length(),substituir);

                        localizarIndice = index + substituir.length() + 1;
                        contador++;
                    }

                }

                 if (contador == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro no processamento");
                    alert.setHeaderText("Texto não encontrado!");
                    alert.setContentText("Não foi possível encontrar '" + localizar + "'");
                    alert.initOwner(gridPane.getScene().getWindow());
                    alert.show();
                    localizarIndice = 0;
                }
            }

        });


        Button cancelarButton = (Button) vBox2.getChildren().get(3);

        cancelarButton.setOnAction(e -> {
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.close();
        });


        Stage stage = new Stage();
        stage.setScene(new Scene(gridPane, 600, 200));
        stage.setTitle("Substituir");
        stage.setResizable(false);
        stage.initOwner(borderPane.getScene().getWindow());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../arquivos/icone.png")));
        stage.show();

    }

    /* Nesse método, vamos lidar com o sub-menu Ir Para..
     *  o processamento será realizado na classe IrParaController*/

    @FXML
    public void handleIrPara() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/janela_irpara.fxml"));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(fxmlLoader.load());
        dialog.setTitle("Ir para");
        dialog.initOwner(borderPane.getScene().getWindow());

        IrParaController controller = fxmlLoader.getController();

        Optional<ButtonType> resultado = dialog.showAndWait();

        if(resultado.isPresent() && resultado.get().getButtonData().equals(ButtonBar.ButtonData.YES)){
            controller.processarResultado(textArea,borderPane);
        }
    }

    /* Nesse método, vamos lidar com o sub-menu Selecionar Tudo, chamaremos o método selectAll()
    * do textArea que lida com essa operação*/

    @FXML
    public void handleSelecionarTudo() {
        textArea.selectAll();
    }

    /* Nesse método, vamos lidar com o sub-menu Hora/Data, quando esse método for chamado
    * escreveremos no final do texto a data e hora atual no formato EX: 23:18 24/12/2018*/

    @FXML
    public void handleHoraData() {
        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date hora = Calendar.getInstance().getTime();

        String dataFormatada = sdf.format(hora) + " " + hoje.format(formatador);

        textArea.setText(textArea.getText() + " " + dataFormatada);

        textArea.positionCaret(textArea.getText().length());

    }

    /* Nesse método, vamos lidar com o sub-menu Quebra de Linha Automática
    * se esse menu estiver selecionado, o texto do textArea vai quebra quando a linha chegar ao final
    * senão o as linhas serão infinitas e não irão quebar*/

    @FXML
    public void handleQuebraDeLinha() {
        if (quebraLinhaMenu.isSelected()) {
            textArea.setWrapText(true);
        } else {
            textArea.setWrapText(false);
        }
    }

    /* Nesse método, vamos lidar com o sub-menu Quebra de Linha Automática
    * Vamos permitir que o usuário altere a fonte e o seu tamanho, o processamento sera realizado pela classe
    * FonteController*/

    @FXML
    public void handleFonte() throws Exception{
        Dialog<ButtonType> dialog = new Dialog<>();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/janela_fonte.fxml"));
        dialog.setDialogPane(fxmlLoader.load());
        dialog.setTitle("Fonte");
        dialog.initOwner(borderPane.getScene().getWindow());

        FonteController fonteController = fxmlLoader.getController();

        if (fonteAtual != null){
            fonteController.setFonteAtual(fonteAtual);
            fonteController.inicializarCampos();
        }

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get().getText().equals("OK")){
            fonteAtual = fonteController.processarResultado();
            textArea.setFont(fonteAtual);
            textArea.positionCaret(textArea.getText().length());
        }

    }

    /* Nesse método, vamos lidar com o sub-menu Barra de Status
    * se o menu for ativado, vamos colocar o conteudo do atributo bottom como bottom do Border Pane
    * se o menu for desativado, vamos colocar o bottom do Border Pane com nulo para que o Text Area posso ocupar
    * o espaço vazio*/

    @FXML
    public void handleBarraDeStatus(){
        if(barraStatusMenu.isSelected()){
            borderPane.setBottom(bottom);
        } else{
            borderPane.setBottom(null);
        }
    }

    /* Nesse método, vamos lidar com os menus Exibir ajuda e Sobre o Notepad. Quando um dos menus for acionado,
    * o navegador padrão do computador em que está sendo executada a aplicação será aberto ná página do link abaixo*/

    @FXML
    public void handleExibirAjuda() throws Exception{
        Desktop.getDesktop().browse(new URL("https://www.bing.com/search?q=obter+ajuda+com+o+bloco+de+notas+no+windows+10&filters=guid:%224466414-pt-ptb-dia%22%20lang:%22pt-br%22&form=T00032&ocid=HelpPane-BingIA").toURI());
    }

    /* Nesse método, vamos lidar quando uma tecla for acionado, se for um atalho vamos chamar o método correspondente*/

    @FXML
    public void handleKeyPressed(KeyEvent event) throws Exception {
        KeyCombination saveCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination openCombination = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        KeyCombination localizarCombination = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        KeyCombination substituirCombination = new KeyCodeCombination(KeyCode.H,KeyCombination.CONTROL_DOWN);
        KeyCombination irParaCombination = new KeyCodeCombination(KeyCode.G,KeyCombination.CONTROL_DOWN);

        if (saveCombination.match(event)) {
            handleSalvar();
        }

        if (openCombination.match(event)) {
            handleAbrir();
        }

        if (localizarCombination.match(event)) {
            handleLocalizar();
        }

        if (event.getCode().equals(KeyCode.F3)) {
            handleLocalizarProxima();
        }

        if(substituirCombination.match(event)){
            handleSubstituir();
        }

        if(irParaCombination.match(event)){
            handleIrPara();
        }

    }

        /* Esse método será executado quando a aplicação for fechada, quando isso acontecer
        * vamos salvar no arquvo config.txt algumas configurações definidas pelo usuário como
        * último diretório usado, estados dos menus Quebra Automatica de Linha e Barra de Status
        * e a fonte atual
        *
        * Para os menus definiremos 0 para desativado e 1 para ativado
        *
        * */

    public void salvarConfiguracao() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getClass().getResource("../arquivos/config.txt").getFile()))) {
            if(parentFile != null){
                writer.write(parentFile.toString());
            } else{
                writer.write("null");
            }
            writer.newLine();
            if(quebraLinhaMenu.isSelected()){
                writer.write("1");
            } else{
                writer.write("0");
            }
            writer.newLine();
            if(barraStatusMenu.isSelected()){
                writer.write("1");
            } else{
                writer.write("0");
            }
            writer.newLine();
            if(fonteAtual != null){
                writer.write(fonteAtual.getName() + "," + String.format("%.0f",fonteAtual.getSize()));
            } else {
                writer.write("Arial,16");
            }
        }
    }

    /* Esse método será excutado quando o usário quiser fechar a aplicação ou abrir um novo arquivo sem que o atual
    * esteja salvo, será aberto um diálogo com 3 opções(Salvar, Não Salvar, Cancelar)
    * Se o usuário escolher a opção salvar, será chamado o método salvar e retornará 1 e o programa executará a operação
    * solicitada
    * se o usuário escolher a opção não salvar, retornará 0 e o programa executará a operação solicitada
      se o usuário escolher a opção cancelar, retornará -1 o programa não executará a operação solicitada */

    public int exibirConfirmacaoSaida() throws Exception {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(FXMLLoader.load(getClass().getResource("../view/janela_saida.fxml")));
        dialog.setTitle("Bloco de Notas");
        dialog.initOwner(borderPane.getScene().getWindow());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getText().equals("Salvar")) {
            handleSalvar();
            return 1;
        } else if (result.isPresent() && result.get().getText().equals("Não Salvar")) {
            return 0;
        } else if (result.isPresent() && result.get().getText().equals("Cancelar")) {
            return -1;
        }
        return -1;
    }

        // Getters


    public boolean isEstaSalvo() {
        return estaSalvo;
    }

    public TextArea getTextArea() {
        return textArea;
    }


}
