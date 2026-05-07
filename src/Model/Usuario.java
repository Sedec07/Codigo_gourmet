package Model;

/**
 * @author Melqui Erazo
 */
public class Usuario {
    private int usu_id;
    private String usu_nombre;
    private String usu_login;
    private String usu_pass;
    private int perf_id; // 1: Admin, 2: Mesero, 3: Cocinero
    private String usu_estado;

    public Usuario() {
    }

    public Usuario(int usu_id, String usu_nombre, String usu_login, String usu_pass, int perf_id, String usu_estado) {
        this.usu_id = usu_id;
        this.usu_nombre = usu_nombre;
        this.usu_login = usu_login;
        this.usu_pass = usu_pass;
        this.perf_id = perf_id;
        this.usu_estado = usu_estado;
    }

    // Getters y Setters
    public int getUsu_id() {
        return usu_id; 
    }
    public void setUsu_id(int usu_id) 
        { this.usu_id = usu_id; }

    public String getUsu_nombre(){
        return usu_nombre; 
    }
    public void setUsu_nombre(String usu_nombre) { 
        this.usu_nombre = usu_nombre; 
    }

    public String getUsu_login() { 
        return usu_login; 
    }
    public void setUsu_login(String usu_login) {
        this.usu_login = usu_login; 
    }

    public String getUsu_pass() {
        return usu_pass; 
    }
    public void setUsu_pass(String usu_pass) {
        this.usu_pass = usu_pass; 
    }

    public int getPerf_id() { 
        return perf_id; 
    }
    public void setPerf_id(int perf_id) {
        this.perf_id = perf_id; 
    }

    public String getUsu_estado() {
        return usu_estado; 
    }
    public void setUsu_estado(String usu_estado) {
        this.usu_estado = usu_estado; 
    }
}