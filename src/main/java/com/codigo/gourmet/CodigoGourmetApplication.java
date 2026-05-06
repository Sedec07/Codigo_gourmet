package com.codigo.gourmet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.codigo.gourmet.model.*;

@SpringBootApplication
public class CodigoGourmetApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodigoGourmetApplication.class, args);

        // --- CÓDIGO DE PRUEBA PARA MOSTRAR AVANCE ---
        System.out.println("\n--- PRUEBA DE SISTEMA DE PEDIDOS ---");
        
        Producto p1 = new Producto("Hamburguesa Doble", 18000, 10);
        Producto p2 = new Producto("Malteada Vainilla", 7500, 15);

        Pedido nuevoPedido = new Pedido(101);
        nuevoPedido.agregarItem(new ItemPedido(p1, 2));
        nuevoPedido.agregarItem(new ItemPedido(p2, 1));

        nuevoPedido.mostrarResumen();
        System.out.println("-----------------------------------\n");
	}
}