package projekpbo;

import Projek.*;

class konveryen {
    private double jumlah;
    
    public konveryen(double jumlah) {
        this.jumlah = jumlah;
    }
    
    public double konversi() {
        double kurs = Kursdb.getKurs("JPY");
        return jumlah * kurs;
    }
}