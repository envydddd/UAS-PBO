package Projek;

class KonversiYen extends enkapsulasi {

    public KonversiYen(double jumlah) {
        super(jumlah);
    }

    //kode yang mengoverride ke kode awal
    @Override
    public double konversi() {
        return jumlah * 108;
    }
}