package Projek;

class KonversiDollar extends enkapsulasi {
    private double jumlah;

    public KonversiDollar(double jumlah) {
        super(jumlah);
    }

    public double konversi() {
        double kurs = KursDAO.getKurs("USD");
        return jumlah * kurs;
    }
}