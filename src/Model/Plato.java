package Model;

/**
 * @author Melqui Erazo
 */
public class Plato {
    private int pla_id;
    private String pla_descripcion;
    private double pla_precio;
    private int est_id; // Estado del plato (Disponible/Agotado)

    public Plato() {
    }

    public Plato(int pla_id, String pla_descripcion, double pla_precio, int est_id) {
        this.pla_id = pla_id;
        this.pla_descripcion = pla_descripcion;
        this.pla_precio = pla_precio;
        this.est_id = est_id;
    }

    // Getters y Setters
    public int getPla_id() { return pla_id; }
    public void setPla_id(int pla_id) { this.pla_id = pla_id; }

    public String getPla_descripcion() { return pla_descripcion; }
    public void setPla_descripcion(String pla_descripcion) { this.pla_descripcion = pla_descripcion; }

    public double getPla_precio() { return pla_precio; }
    public void setPla_precio(double pla_precio) { this.pla_precio = pla_precio; }

    public int getEst_id() { return est_id; }
    public void setEst_id(int est_id) { this.est_id = est_id; }

    @Override
    public String toString() {
        return pla_descripcion;
    }
}