
package Controller;

import DAO.UsuarioDAO;
import Model.Usuario;
import View.ViewLogIn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Melqui Erazo
 */
public class CtrlLogin implements ActionListener {
    private Usuario mod;
    private UsuarioDAO modDAO;
    private ViewLogIn frm;

    public CtrlLogin(Usuario mod, UsuarioDAO modDAO, ViewLogIn frm) {
        this.mod = mod;
        this.modDAO = modDAO;
        this.frm = frm;
        // Escuchamos el botón de entrar de la vista
        this.frm.btnEntrar.addActionListener(this);
    }

    public void iniciar() {
        frm.setTitle("Login - Restaurante");
        frm.setLocationRelativeTo(null); // Centrar pantalla
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Aquí va la lógica de validar usuario...
    }
}
