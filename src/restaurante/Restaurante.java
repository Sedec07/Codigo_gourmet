package restaurante;

import DAO.UsuarioDAO;
import Model.Usuario;
import Controller.CtrlLogin;
import View.ViewLogIn;

/**
 * @author Melqui Erazo
 */
public class Restaurante {

    public static void main(String[] args) {
        // 1. Instanciamos la Vista del Login
        ViewLogIn frmLog = new ViewLogIn();
        
        // 2. Instanciamos el Modelo DAO (que tiene la conexión a la BD)
        UsuarioDAO modLog = new UsuarioDAO();
        
        // 3. Instanciamos el Modelo de datos vacío para el usuario
        Usuario modUsu = new Usuario();
        
        // 4. El controlador une todo y maneja los eventos
        CtrlLogin ctrl = new CtrlLogin(modUsu, modLog, frmLog);
        
        // 5. Inicializamos y mostramos la pantalla
        ctrl.iniciar();
        frmLog.setVisible(true);
    }
}