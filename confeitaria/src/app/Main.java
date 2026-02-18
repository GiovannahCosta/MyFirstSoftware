// SistemaPrincipal.java
package app;

import javax.swing.SwingUtilities;

import model.repositories.CreateTables;
import services.SeedService;
import view.ViewHome;

public class Main {
    public static void main(String[] args) {
    	
    	System.out.println("Working dir: " + System.getProperty("user.dir"));
    	try {
    		CreateTables.createAllTables();
    		SeedService.seedDefaults();
    	} catch(Exception e) {
    		System.err.println("Erro ao criar tabelas: " + e.getMessage());
    	}
    	
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			ViewHome tela = new ViewHome();
    			tela.setVisible(true);
    		}
    	});
    }
   
}