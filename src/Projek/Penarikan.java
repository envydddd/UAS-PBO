package Projek;

class Penarikan extends enkapsulasi {

    private double saldo = 1000000;

    public Penarikan(double jumlah) {
        super(jumlah);
    }

    public boolean tarik() {

        if (jumlah < 50000) {
            System.out.println("Minimal penarikan Rp50.000");
            System.out.println("Saldo: Rp" + saldo + "\n");
            return false;
        }

        if (jumlah > saldo) {
            System.out.println("Saldo tidak cukup! Saldo: Rp" + saldo + "\n");
            return false;
        }

        saldo -= jumlah;
        System.out.println("Penarikan berhasil: Rp" + jumlah);
        System.out.println("Sisa saldo: Rp" + saldo + "\n");
        return true;
    }

    public double getsaldo() {
        return saldo;
    }
}