package Projek;

public class enkapsulasi {

    protected double jumlah;

    public enkapsulasi(double jumlah) {
        this.jumlah = jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public double getJumlah() {
        return jumlah;
    }
    
    //polymorpism 
    public double konversi(){
        return 0;
    }
}