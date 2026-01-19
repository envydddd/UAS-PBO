package Projek;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class controller implements Initializable {

    @FXML private TextField dollarField;
    @FXML private Label dollarResult;
    @FXML private ListView<String> dollarHistory;
    @FXML private TextField kursDollarField;

    @FXML private TextField yenField;
    @FXML private Label yenResult;
    @FXML private ListView<String> yenHistory;
    @FXML private TextField kursYenField;

    @FXML private TextField penarikanField;
    @FXML private Label penarikanStatus;
    @FXML private ListView<String> penarikanHistory;
    @FXML private Label saldoLabel;

    @FXML private TextArea memoryInfo;
    @FXML private Label runtimeInfo;

    private final ArrayList<Double> dollarHistoryList = new ArrayList<>();
    private final ArrayList<Double> yenHistoryList = new ArrayList<>();
    private final ArrayList<Double> penarikanHistoryList = new ArrayList<>();

    private final Penarikan penarikanSystem = new Penarikan(1_000_000);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateSaldoLabel();
        dollarResult.setText("0");
        yenResult.setText("0");
        penarikanStatus.setText("-");
        runtimeInfo.setText("-");
    }

    @FXML
    private void convertDollar(ActionEvent event) {
        try {
            double jumlah = Double.parseDouble(dollarField.getText());
            KonversiDollar konversi = new KonversiDollar(jumlah);
            double hasil = konversi.konversi();
            
            saveDollarHistory(jumlah, hasil);

            dollarResult.setText(String.format("Rp %,.2f", hasil));
            dollarHistoryList.add(jumlah);
            updateDollarHistory();
            dollarField.clear();

        } catch (NumberFormatException e) {
            showError("Input dollar tidak valid");
        }
    }
    
    private void saveDollarHistory(double jumlah, double hasil) {
        String sql =
            "INSERT INTO dollar_history (jumlah_dollar, hasil_rupiah) VALUES (?, ?)";

        try (Connection c = database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, jumlah);
            ps.setDouble(2, hasil);
            ps.executeUpdate();

        } catch (Exception e) {
            showError("Gagal menyimpan history dollar");
        }
    }
    
    @FXML
    private void updateKursDollar(ActionEvent e) {
        try {
            double nilai = Double.parseDouble(kursDollarField.getText());
            KursDAO.updateKurs("USD", nilai);
            kursDollarField.clear();
        } catch (NumberFormatException ex) {
            showError("Nilai kurs tidak valid");
        }
    }

    @FXML
    private void convertYen(ActionEvent event) {
        try {
            double jumlah = Double.parseDouble(yenField.getText());
            KonversiYen konversi = new KonversiYen(jumlah);
            double hasil = konversi.konversi();
            
            saveYenHistory(jumlah, hasil);

            yenResult.setText(String.format("Rp %,.2f", hasil));
            yenHistoryList.add(jumlah);
            updateYenHistory();
            yenField.clear();

        } catch (NumberFormatException e) {
            showError("Input yen tidak valid");
        }
    }
    
    private void saveYenHistory(double jumlah, double hasil) {
        String sql =
            "INSERT INTO yen_history (jumlah_yen, hasil_rupiah) VALUES (?, ?)";

        try (Connection c = database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, jumlah);
            ps.setDouble(2, hasil);
            ps.executeUpdate();

        } catch (Exception e) {
            showError("Gagal menyimpan history yen");
        }
    }
    
    @FXML
    private void updateKursYen(ActionEvent e) {
        try {
            double nilai = Double.parseDouble(kursYenField.getText());
            KursDAO.updateKurs("JPY", nilai);
            kursYenField.clear();
        } catch (NumberFormatException ex) {
            showError("Nilai kurs tidak valid");
        }
    }

    @FXML
    private void tarikUang(ActionEvent event) {
        try {
            double jumlah = Double.parseDouble(penarikanField.getText());
            penarikanSystem.setJumlah(jumlah);

            if (penarikanSystem.tarik()) {
                penarikanStatus.setText("Penarikan berhasil");
                savePenarikanHistory(jumlah, "BERHASIL");
                updateSaldoLabel();
            } else {
                penarikanStatus.setText("Saldo tidak cukup");
                savePenarikanHistory(jumlah, "GAGAL");
            }
            
            penarikanHistoryList.add(jumlah);
            updatePenarikanHistory();
            updateSaldoLabel();
            penarikanField.clear();

        } catch (NumberFormatException e) {
            showError("Jumlah penarikan tidak valid");
        }
    }
    
    private void savePenarikanHistory(double jumlah, String status) {
    String sql =
        "INSERT INTO penarikan_history (jumlah, status) VALUES (?, ?)";

    try (Connection c = database.connect();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setDouble(1, jumlah);
        ps.setString(2, status);
        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
        showError("Gagal menyimpan history penarikan");
    }
}


    @FXML
    private void checkRuntime(ActionEvent event) {
        Runtime rt = Runtime.getRuntime();

        long start = System.nanoTime();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        long used = total - free;
        rt.gc();
        long end = System.nanoTime();

        memoryInfo.setText(
            "Total Memory : " + toMB(total) + " MB\n" +
            "Free Memory  : " + toMB(free) + " MB\n" +
            "Used Memory  : " + toMB(used) + " MB"
        );

        runtimeInfo.setText(
            String.format("%.3f ms", (end - start) / 1_000_000.0)
        );
    }

    private void updateDollarHistory() {
        dollarHistory.getItems().clear();
        for (int i = 0; i < dollarHistoryList.size(); i++) {
            dollarHistory.getItems().add(
                (i + 1) + ". $" + dollarHistoryList.get(i)
            );
        }
    }

    private void updateYenHistory() {
        yenHistory.getItems().clear();
        for (int i = 0; i < yenHistoryList.size(); i++) {
            yenHistory.getItems().add(
                (i + 1) + ". ¥" + yenHistoryList.get(i)
            );
        }
    }

    private void updatePenarikanHistory() {
        penarikanHistory.getItems().clear();
        for (int i = 0; i < penarikanHistoryList.size(); i++) {
            penarikanHistory.getItems().add(
                (i + 1) + ". Rp " + penarikanHistoryList.get(i)
            );
        }
    }

    private void updateSaldoLabel() {
        saldoLabel.setText(String.format("Rp %,.2f", penarikanSystem.getsaldo()));
    }

    private double toMB(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
