package Model;

/**
 * @author Melqui Erazo
 */
public class Mesa {
    private int mes_id;
    private String mes_numero;
    private int mes_capacidad;
    private String mes_estado;

    public Mesa() {}

    public Mesa(int mes_id, String mes_numero, int mes_capacidad, String mes_estado) {
        this.mes_id = mes_id;
        this.mes_numero = mes_numero;
        this.mes_capacidad = mes_capacidad;
        this.mes_estado = mes_estado;
    }

    // Getters y Setters
    public int getMes_id() { return mes_id; }
    public void setMes_id(int mes_id) { this.mes_id = mes_id; }

    public String getMes_numero() { return mes_numero; }
    public void setMes_numero(String mes_numero) { this.mes_numero = mes_numero; }

    public String getMes_estado() { return mes_estado; }
    public void setMes_estado(String mes_estado) { this.mes_estado = mes_estado; }

    @Override
    public String toString() {
        return mes_numero + " (" + mes_estado + ")";
    }
}