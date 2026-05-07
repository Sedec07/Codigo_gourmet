package Model;

/**
 * @author Melqui Erazo
 */
public class PedidoDetalle {
    private int det_id;
    private int ped_id;
    private String det_plato_nombre;
    private int det_cantidad;
    private double det_precio;
    private String det_observacion;

    public PedidoDetalle() {}

    // Getters y Setters...
    public String getDet_plato_nombre() { 
        return det_plato_nombre; 
    }
    public void setDet_plato_nombre(String det_plato_nombre) { 
        this.det_plato_nombre = det_plato_nombre; 
    }

    public double getDet_precio() {
        return det_precio; 
    }
    public void setDet_precio(double det_precio) {
        this.det_precio = det_precio; 
    }
    
    // Agrega el resto de Getters y Setters según necesites
}