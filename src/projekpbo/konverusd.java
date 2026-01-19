package projekpbo;

class konverusd {
    private double jumlah;
    
    public konverusd(double jumlah) {
        this.jumlah = jumlah;
    }
    
    public double konversi() {
        double kurs = Kursdb.getKurs("USD");
        return jumlah * kurs;
    }
}