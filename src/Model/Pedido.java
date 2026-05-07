package Model;

/**
 * @author Melqui Erazo
 */
public class Pedido {
    private int ped_id;
    private String ped_fecha;
    private int mes_id;      // Relacionado con el mes_id de la tabla Mesa
    private int usu_id;      // ID del mesero (usuario) que toma el pedido
    private int est_id;      // ID del estado (1: Pendiente, 2: Preparando, etc.)

    public Pedido() {
    }

    public Pedido(int ped_id, String ped_fecha, int mes_id, int usu_id, int est_id) {
        this.ped_id = ped_id;
        this.ped_fecha = ped_fecha;
        this.mes_id = mes_id;
        this.usu_id = usu_id;
        this.est_id = est_id;
    }

    // Getters y Setters
    public int getPed_id() {
        return ped_id;
    }

    public void setPed_id(int ped_id) {
        this.ped_id = ped_id;
    }

    public String getPed_fecha() {
        return ped_fecha;
    }

    public void setPed_fecha(String ped_fecha) {
        this.ped_fecha = ped_fecha;
    }

    public int getMes_id() {
        return mes_id;
    }

    public void setMes_id(int mes_id) {
        this.mes_id = mes_id;
    }

    public int getUsu_id() {
        return usu_id;
    }

    public void setUsu_id(int usu_id) {
        this.usu_id = usu_id;
    }

    public int getEst_id() {
        return est_id;
    }

    public void setEst_id(int est_id) {
        this.est_id = est_id;
    }

    @Override
    public String toString() {
        return "Pedido #" + ped_id + " - Mesa ID: " + mes_id;
    }
}