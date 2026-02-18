package app;

import javax.swing.SwingUtilities;

import model.repositories.CreateTables;
import services.SeedService;
import view.ViewHome;

/**
 * Classe de entrada da aplicação
 * Exibe informações básicas de diagnóstico (working directory)
 * Cria/garante as tabelas do banco de dados (DDL) chamando {@link CreateTables.createAllTables()}.
 * Executa o seed inicial (ex.: áreas/bairros, níveis e tamanhos) chamando {@link SeedService.seedDefaults()}
 * Inicializa a interface gráfica Swing, abrindo a {@link ViewHome}.
 * Obs.: A criação de tabelas e o seed são executados antes de abrir a ViewHome. Então, dependendo do volume
 * de dados (por exemplo, seed de áreas via CSV), isso pode causar um pequeno atraso na primeira abertura.
 */
public class Main {
	
	/**
     * Método principal executado pela JVM.
     * Imprime o diretório de execução (working directory).
     * Tenta criar as tabelas e executar o seed dentro de um bloco {@code try/catch}.
     * Inicializa a UI na thread correta do Swing usando {@link SwingUtilities.invokeLater(Runnable)}.
     * Cria e exibe a tela {@link ViewHome}.
     * Qualquer exceção na criação de tabelas/seed é capturada e registrada no console.
     * Mesmo em caso de erro, a UI ainda é inicializada.
     *@param args argumentos de linha de comando (não utilizados)
     */
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