package projekpbo;

import Projek.*;

public class enkapsulasinyoba {

    protected double jumlah;

    public enkapsulasinyoba(double jumlah) {
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