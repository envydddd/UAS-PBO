package projekpbo;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class controllernyoba implements Initializable {

    @FXML private TextField dollarField;
    @FXML private Label dollarResult;
    @FXML private ListView<String> dollarHistory;
    @FXML private TextField kursDollarField;
    @FXML private TextField hapusDollarField;

    @FXML private TextField yenField;
    @FXML private Label yenResult;
    @FXML private ListView<String> yenHistory;
    @FXML private TextField kursYenField;
    @FXML private TextField hapusYenField;

    @FXML private TextField penarikanField;
    @FXML private Label penarikanStatus;
    @FXML private ListView<String> penarikanHistory;
    @FXML private Label saldoLabel;
    @FXML private TextField hapusPenarikanField;

    @FXML private TextArea memoryInfo;
    @FXML private Label runtimeInfo;

    private final Penarikannyoba penarikanSystem = new Penarikannyoba(1_000_000);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateSaldoLabel();
        dollarResult.setText("0");
        yenResult.setText("0");
        penarikanStatus.setText("-");
        runtimeInfo.setText("-");
        
        // Load data kurs saat inisialisasi
        loadCurrentKurs();
        // Load history dari database
        loadDollarHistory();
        loadYenHistory();
        loadPenarikanHistory();
    }
    
    private void loadCurrentKurs() {
        double kursUSD = Kursdb.getKurs("USD");
        double kursJPY = Kursdb.getKurs("JPY");
        kursDollarField.setText(String.valueOf(kursUSD));
        kursYenField.setText(String.valueOf(kursJPY));
    }

    @FXML
    private void convertDollar(ActionEvent event) {
        try {
            double jumlah = Double.parseDouble(dollarField.getText());
            konverusd konversi = new konverusd(jumlah);
            double hasil = konversi.konversi();
            
            saveDollarHistory(jumlah, hasil);

            dollarResult.setText(String.format("Rp %,.2f", hasil));
            dollarField.clear();
            loadDollarHistory();

        } catch (NumberFormatException e) {
            showError("Input dollar tidak valid");
        }
    }
    
    private void saveDollarHistory(double jumlah, double hasil) {
        String sql = "INSERT INTO dollar_history (jumlah_dollar, hasil_rupiah) VALUES (?, ?)";
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, jumlah);
            ps.setDouble(2, hasil);
            ps.executeUpdate();
        } catch (Exception e) {
            showError("Gagal menyimpan history dollar");
        }
    }
    
    private void loadDollarHistory() {
        dollarHistory.getItems().clear();
        String sql = "SELECT * FROM dollar_history ORDER BY waktu DESC";
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            int counter = 1;
            while (rs.next()) {
                double jumlah = rs.getDouble("jumlah_dollar");
                double hasil = rs.getDouble("hasil_rupiah");
                String item = counter + ". $" + jumlah + " → Rp " + String.format("%,.2f", hasil);
                dollarHistory.getItems().add(item);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void updateKursDollar(ActionEvent e) {
        try {
            double nilai = Double.parseDouble(kursDollarField.getText());
            Kursdb.updateKurs("USD", nilai);
            showInfo("Kurs USD berhasil diupdate menjadi: " + nilai);
        } catch (NumberFormatException ex) {
            showError("Nilai kurs tidak valid");
        }
    }

    @FXML
    private void convertYen(ActionEvent event) {
        try {
            double jumlah = Double.parseDouble(yenField.getText());
            konveryen konversi = new konveryen(jumlah);
            double hasil = konversi.konversi();
            
            saveYenHistory(jumlah, hasil);

            yenResult.setText(String.format("Rp %,.2f", hasil));
            yenField.clear();
            loadYenHistory();

        } catch (NumberFormatException e) {
            showError("Input yen tidak valid");
        }
    }
    
    private void saveYenHistory(double jumlah, double hasil) {
        String sql = "INSERT INTO yen_history (jumlah_yen, hasil_rupiah) VALUES (?, ?)";
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, jumlah);
            ps.setDouble(2, hasil);
            ps.executeUpdate();
        } catch (Exception e) {
            showError("Gagal menyimpan history yen");
        }
    }
    
    private void loadYenHistory() {
        yenHistory.getItems().clear();
        String sql = "SELECT * FROM yen_history ORDER BY waktu DESC";
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            int counter = 1;
            while (rs.next()) {
                double jumlah = rs.getDouble("jumlah_yen");
                double hasil = rs.getDouble("hasil_rupiah");
                String item = counter + ". ¥" + jumlah + " → Rp " + String.format("%,.2f", hasil);
                yenHistory.getItems().add(item);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void updateKursYen(ActionEvent e) {
        try {
            double nilai = Double.parseDouble(kursYenField.getText());
            Kursdb.updateKurs("JPY", nilai);
            showInfo("Kurs JPY berhasil diupdate menjadi: " + nilai);
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
            
            penarikanField.clear();
            loadPenarikanHistory();

        } catch (NumberFormatException e) {
            showError("Jumlah penarikan tidak valid");
        }
    }
    
    private void savePenarikanHistory(double jumlah, String status) {
        String sql = "INSERT INTO penarikan_history (jumlah, status) VALUES (?, ?)";
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, jumlah);
            ps.setString(2, status);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal menyimpan history penarikan");
        }
    }
    
    private void loadPenarikanHistory() {
        penarikanHistory.getItems().clear();
        String sql = "SELECT * FROM penarikan_history ORDER BY waktu DESC";
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            int counter = 1;
            while (rs.next()) {
                double jumlah = rs.getDouble("jumlah");
                String status = rs.getString("status");
                String item = counter + ". Rp " + String.format("%,.2f", jumlah) + 
                             " - " + status;
                penarikanHistory.getItems().add(item);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // FITUR HAPUS HISTORY DOLLAR
    @FXML
    private void hapusHistoryDollar(ActionEvent event) {
        try {
            int nomor = Integer.parseInt(hapusDollarField.getText());
            hapusHistoryByNumber("dollar_history", nomor);
            hapusDollarField.clear();
            loadDollarHistory();
            showInfo("History dollar ke-" + nomor + " berhasil dihapus");
        } catch (NumberFormatException e) {
            showError("Nomor tidak valid");
        }
    }
    
    @FXML
    private void hapusSemuaDollar(ActionEvent event) {
        hapusSemuaHistory("dollar_history");
        loadDollarHistory();
        showInfo("Semua history dollar berhasil dihapus");
    }
    
    // FITUR HAPUS HISTORY YEN
    @FXML
    private void hapusHistoryYen(ActionEvent event) {
        try {
            int nomor = Integer.parseInt(hapusYenField.getText());
            hapusHistoryByNumber("yen_history", nomor);
            hapusYenField.clear();
            loadYenHistory();
            showInfo("History yen ke-" + nomor + " berhasil dihapus");
        } catch (NumberFormatException e) {
            showError("Nomor tidak valid");
        }
    }
    
    @FXML
    private void hapusSemuaYen(ActionEvent event) {
        hapusSemuaHistory("yen_history");
        loadYenHistory();
        showInfo("Semua history yen berhasil dihapus");
    }
    
    // FITUR HAPUS HISTORY PENARIKAN
    @FXML
    private void hapusHistoryPenarikan(ActionEvent event) {
        try {
            int nomor = Integer.parseInt(hapusPenarikanField.getText());
            hapusHistoryByNumber("penarikan_history", nomor);
            hapusPenarikanField.clear();
            loadPenarikanHistory();
            showInfo("History penarikan ke-" + nomor + " berhasil dihapus");
        } catch (NumberFormatException e) {
            showError("Nomor tidak valid");
        }
    }
    
    @FXML
    private void hapusSemuaPenarikan(ActionEvent event) {
        hapusSemuaHistory("penarikan_history");
        loadPenarikanHistory();
        showInfo("Semua history penarikan berhasil dihapus");
    }
    
    private void hapusHistoryByNumber(String tableName, int nomor) {
        // Mendapatkan ID berdasarkan nomor urutan
        String getIdSql = "SELECT id FROM " + tableName + " ORDER BY waktu DESC OFFSET ? LIMIT 1";
        String deleteSql = "DELETE FROM " + tableName + " WHERE id = ?";
        
        try (Connection c = databasenyoba.connect();
             PreparedStatement psGet = c.prepareStatement(getIdSql);
             PreparedStatement psDelete = c.prepareStatement(deleteSql)) {
            
            psGet.setInt(1, nomor - 1);
            ResultSet rs = psGet.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                psDelete.setInt(1, id);
                psDelete.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal menghapus history");
        }
    }
    
    private void hapusSemuaHistory(String tableName) {
        String sql = "DELETE FROM " + tableName;
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal menghapus semua history");
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

        runtimeInfo.setText(String.format("%.3f ms", (end - start) / 1_000_000.0));
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
    
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}