package projekpbo;

public class Penarikannyoba {
    private double saldo;
    private double jumlah;
    
    public Penarikannyoba(double saldoAwal) {
        this.saldo = saldoAwal;
    }
    
    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }
    
    public boolean tarik() {
        if (jumlah <= saldo) {
            saldo -= jumlah;
            return true;
        }
        return false;
    }
    
    public double getsaldo() {
        return saldo;
    }

}
