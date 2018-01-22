package nativebin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;


public class Controller {

    @FXML
    private TextField nameField;
    @FXML
    private TextField copyField;
    @FXML
    private Label iconLabel;
    @FXML
    private Label jarLabel;
    @FXML
    private Label saveLabel;
    @FXML
    private Label jreLabel;
    @FXML
    private ImageView loadingIndicator;
    @FXML
    private Label createLabel;
    @FXML
    private CheckBox hideDock;

    private int jre = 0;

    @FXML
    private void selectIcon(ActionEvent actionEvent) {
        FileChooser iconChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ICNS Files (*.icns)", "*.icns");
        iconChooser.getExtensionFilters().add(extFilter);
        File file = iconChooser.showOpenDialog(null);
        iconLabel.setText(file.getAbsolutePath());
    }

    @FXML
    private void selectJar(ActionEvent actionEvent) {
        FileChooser jarChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JAR Files (*.jar)", "*.jar");
        jarChooser.getExtensionFilters().add(extFilter);
        File file = jarChooser.showOpenDialog(null);
        jarLabel.setText(file.getAbsolutePath());
    }

    @FXML
    private void selectSave(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        saveLabel.setText(selectedDirectory.getAbsolutePath());
    }

    @FXML
    private void resetJRE(ActionEvent actionEvent) {
        jreLabel.setText("No JRE set to be Bundled");
    }

    @FXML
    private void bundleJRE(ActionEvent actionEvent) {
        jre = 0;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bundle JRE");
        alert.setHeaderText("How to Choose a JRE");
        alert.setContentText("In order to bundle a JRE, please choose the Java home directory. It should be 'JAVA VERSION/Contents/Home/'.");
        alert.showAndWait();

        DirectoryChooser jreChooser = new DirectoryChooser();
        jreChooser.setInitialDirectory(new File("/Library/Java/JavaVirtualMachines/"));
        File selectedJre = jreChooser.showDialog(null);

        if (selectedJre.getName().equals("Home")) {
            jreLabel.setText(selectedJre.getAbsolutePath());
            jre = 1;
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("JRE Error");
            error.setHeaderText("No JRE Found");
            error.setContentText("Please select a JRE home folder!");
            error.showAndWait();
        }
    }

    @FXML
    private void create(ActionEvent actionEvent) throws IOException, InterruptedException {

        loadingIndicator.setVisible(true);

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        new File(saveLabel.getText() + "/" + nameField.getText() + ".app").mkdir();
                        new File(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/").mkdir();
                        new File(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/MacOS/").mkdir();
                        new File(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/Resources/").mkdir();

                        try {
                            Files.copy( Paths.get(jarLabel.getText()),
                                    Paths.get( saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/MacOS/application.jar" ),
                                    StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.out.print("JAR ERROR");
                        }

                        try {
                            Files.copy( Paths.get(iconLabel.getText()),
                                    Paths.get( saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/Resources/application.icns" ),
                                    StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.out.print("JAR ERROR");
                        }

                        try{
                            PrintWriter writer = new PrintWriter(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/Info.plist", "UTF-8");
                            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                            writer.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
                            writer.println("<plist version=\"1.0\">");
                            writer.println("<dict>");
                            writer.println("\t<key>CFBundleDevelopmentRegion</key>");
                            writer.println("\t<string>English</string>");
                            writer.println("\t<key>CFBundleExecutable</key>");
                            writer.println("\t<string>launcher</string>");
                            writer.println("\t<key>CFBundleIconFile</key>");
                            writer.println("\t<string>application.icns</string>");
                            writer.println("\t<key>CFBundleName</key>");
                            writer.println("\t<string>" + nameField.getText() + "</string>");
                            writer.println("\t<key>CFBundleInfoDictionaryVersion</key>");
                            writer.println("\t<string>6.0</string>");
                            writer.println("\t<key>CFBundlePackageType</key>");
                            writer.println("\t<string>APPL</string>");
                            writer.println("\t<key>CFBundleShortVersionString</key>");
                            writer.println("\t<string>2.2</string>");
                            writer.println("\t<key>CFBundleSignature</key>");
                            writer.println("\t<string>xmmd</string>");
                            writer.println("\t<key>CFBundleVersion</key>");
                            writer.println("\t<string>2.2</string>");
                            writer.println("\t<key>NSAppleScriptEnabled</key>");
                            writer.println("\t<string>NO</string>");
                            writer.println("\t<key>CFBundleIdentifier</key>");
                            writer.println("\t<string>" + copyField.getText() + "</string>");
                            writer.println("</dict>");
                            writer.println("</plist>");
                            writer.close();
                        } catch (IOException e) {
                            // do something
                        }


                        if (jre == 0) {
                            try {
                                PrintWriter writer = new PrintWriter(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/MacOS/launcher", "UTF-8");
                                writer.println("#!/bin/sh\n" +
                                        "# Constants\n" +
                                        "JAVA_MAJOR=1\n" +
                                        "JAVA_MINOR=1\n" +
                                        "APP_JAR=\"application.jar\"\n" +
                                        "APP_NAME=\"" + nameField.getText() + "\"\n" +
                                        "VM_ARGS=\"\"\n" +
                                        "\n" +
                                        "# Set the working directory\n" +
                                        "DIR=$(cd \"$(dirname \"$0\")\"; pwd)\n" +
                                        "\n" +
                                        "# Error message for NO JAVA dialog\n" +
                                        "ERROR_TITLE=\"Cannot launch $APP_NAME\"\n" +
                                        "ERROR_MSG=\"$APP_NAME requires Java version $JAVA_MAJOR.$JAVA_MINOR or later to run.\"\n" +
                                        "DOWNLOAD_URL=\"http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html\"\n" +
                                        "\n" +
                                        "# Is Java installed?\n" +
                                        "if type -p java; then\n" +
                                        "    _java=java\n" +
                                        "elif [[ -n \"$JAVA_HOME\" ]] && [[ -x \"$JAVA_HOME/bin/java\" ]]; then\n" +
                                        "    _java=\"$JAVA_HOME/bin/java\"\n" +
                                        "else\n" +
                                        "    osascript \\\n" +
                                        "\t-e \"set question to display dialog \\\"$ERROR_MSG\\\" with title \\\"$ERROR_TITLE\\\" buttons {\\\"Cancel\\\", \\\"Download\\\"} default button 2\" \\\n" +
                                        "\t-e \"if button returned of question is equal to \\\"Download\\\" then open location \\\"$DOWNLOAD_URL\\\"\"\n" +
                                        "\techo \"$ERROR_TITLE\"\n" +
                                        "\techo \"$ERROR_MSG\"\n" +
                                        "\texit 1\n" +
                                        "fi\n" +
                                        "\n" +
                                        "# Java version check\n" +
                                        "if [[ \"$_java\" ]]; then\n" +
                                        "    version=$(\"$_java\" -version 2>&1 | awk -F '\"' '/version/ {print $2}')\n" +
                                        "    if [[ \"$version\" < \"$JAVA_MAJOR.$JAVA_MINOR\" ]]; then\n" +
                                        "        osascript \\\n" +
                                        "    \t-e \"set question to display dialog \\\"$ERROR_MSG\\\" with title \\\"$ERROR_TITLE\\\" buttons {\\\"Cancel\\\", \\\"Download\\\"} default button 2\" \\\n" +
                                        "    \t-e \"if button returned of question is equal to \\\"Download\\\" then open location \\\"$DOWNLOAD_URL\\\"\"\n" +
                                        "    \techo \"$ERROR_TITLE\"\n" +
                                        "    \techo \"$ERROR_MSG\"\n" +
                                        "    \texit 1\n" +
                                        "    fi\n" +
                                        "fi\n" +
                                        "\n" +
                                        "# Run the application\n" +
                                        "exec $_java $VM_ARGS -Dapple.laf.useScreenMenuBar=true -Dcom.apple.macos.use-file-dialog-packages=true -Xdock:name=\"$APP_NAME\" -Xdock:icon=\"$DIR/../Resources/application.icns\" -cp \".;$DIR;\" -jar \"$DIR/$APP_JAR\"\n");
                                writer.close();
                            } catch (IOException e) {
                                // do something
                            }
                        } else if (jre == 1) {
                            try {
                                PrintWriter writer = new PrintWriter(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/MacOS/launcher", "UTF-8");
                                writer.println("#!/bin/sh\n" +
                                        "\n" +
                                        "# Constants\n" +
                                        "JAVA_MAJOR=1\n" +
                                        "JAVA_MINOR=1\n" +
                                        "APP_JAR=\"application.jar\"\n" +
                                        "APP_NAME=\"" + nameField.getText() + "\"\n" +
                                        "VM_ARGS=\"\"\n" +
                                        "\n" +
                                        "# Set the working directory\n" +
                                        "DIR=$(cd \"$(dirname \"$0\")\"; pwd)\n" +
                                        "\n" +
                                        "# Run the application\n" +
                                        "exec $DIR/../Java/bin/java $VM_ARGS -Dapple.laf.useScreenMenuBar=true -Dcom.apple.macos.use-file-dialog-packages=true -Xdock:name=\"$APP_NAME\" -Xdock:icon=\"$DIR/../Resources/application.icns\" -cp \".;$DIR;\" -jar \"$DIR/$APP_JAR\"\n" +
                                        "\n");
                                writer.close();
                            } catch (IOException e) {
                                // do something
                            }

                            File srcDir = new File(jreLabel.getText());
                            File destDir = new File(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/Java/");
                            try {
                                FileUtils.copyDirectory(srcDir, destDir);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            File java = new File(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/Java/bin/java");
                            java.setExecutable(true);

                            jre = 0;

                        } else {
                            System.out.print("NativeBin JRE Error!");
                        }

                        File launcher = new File(saveLabel.getText() + "/" + nameField.getText() + ".app/Contents/MacOS/launcher");
                        launcher.setExecutable(true);

                        loadingIndicator.setVisible(false);
                        createLabel.setVisible(true);

                    }
                },
                100
        );

    }

    @FXML
    private void quit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    private void about(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("about.fxml"));
        stage.setTitle("About NativeBin");
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(new Scene(root, 500, 100));
        stage.show();
    }

}
