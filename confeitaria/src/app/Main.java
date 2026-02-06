// SistemaPrincipal.java
package app;

import model.repositories.CreateTables;

public class Main {
    public static void main(String[] args) {
    	try {
    		CreateTables.createAllTables();
    	} catch(Exception e) {
    		System.err.println("Erro ao criar tabelas: " + e.getMessage());
    	}
    }
   
}