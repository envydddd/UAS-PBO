package projekpbo;

import java.sql.*;

public class Kursdb {
    public static double getKurs(String mataUang) {
        String sql = "SELECT nilai FROM kurs WHERE mata_uang = ?";
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mataUang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("nilai");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void updateKurs(String mataUang, double nilaiBaru) {
        String sql = "UPDATE kurs SET nilai = ? WHERE mata_uang = ?";
        try (Connection c = databasenyoba.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, nilaiBaru);
            ps.setString(2, mataUang);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}