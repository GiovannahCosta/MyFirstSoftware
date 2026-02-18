package model.repositories;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe utilitária responsável por fornecer conexões JDBC com o PostgreSQL.
 * Responsável por carregar a configuração do banco (env vars ou arquivo {@code db.properties}),
 * montar a URL JDBC do PostgreSQL e garantir que o driver do PostgreSQL seja carregado (quando necessário).
 * 
 * Ordem de leitura de configuração:
 * - Variáveis de ambiente: DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
 * - Arquivo properties: {@link #PROPS_PATH}
 * 
 * Se não encontrar configuração em nenhuma fonte, lança {@link SQLException} com instruções claras.
 */

public final class DBConnection {
	
	/**
	 * Caminho do arquivo de propriedades (modo arquivo).
	 * Este caminho é relativo ao working directory (diretório de execução).
	 */
	private static final String PROPS_PATH = "src/db.properties";
	
	 /**
     * Host padrão, usado quando não informado explicitamente.
     */
    private static final String DEFAULT_HOST = "localhost";
    

    /**
     * Porta padrão, usada quando não informada explicitamente.
     */
    private static final String DEFAULT_PORT = "5432";
    
    /**
     * Construtor privado para impedir instanciação.
     * Esta classe é um utilitário com métodos estáticos.
     */
    private DBConnection() {}
    
    /**
     * Obtém uma {@link Connection} JDBC com PostgreSQL.
     * Funcionamento:
     * - Carrega configuração via {@link #loadConfigOrThrow()}.
     * - Monta a URL JDBC: {@code jdbc:postgresql://host:port/dbName}.
     * - Tenta carregar o driver {@code org.postgresql.Driver}.
     * - Abre e retorna a conexão via {@link DriverManager#getConnection(String, String, String)}.
     * @return conexão JDBC aberta
     * @throws SQLException se o banco não estiver configurado ou se falhar ao conectar
     */
    public static Connection getConnection() throws SQLException {
        DbConfig cfg = loadConfigOrThrow();

        String url = "jdbc:postgresql://" + cfg.host + ":" + cfg.port + "/" + cfg.dbName;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {}

        return DriverManager.getConnection(url, cfg.user, cfg.password);
    }
    
    
    /**
     * Carrega a configuração do banco a partir de env vars ou arquivo.
     * Fluxo:
     * - Tenta carregar de variáveis de ambiente ({@link #loadFromEnv()}).
     * - Se não houver, tenta carregar do arquivo ({@link #loadFromPropertiesFile()}).
     * - Se nenhuma existir, lança {@link SQLException} com instruções de configuração.
     * @return configuração carregada
     * @throws SQLException se não houver configuração ou se estiver incompleta
     */
    private static DbConfig loadConfigOrThrow() throws SQLException {
        DbConfig envCfg = loadFromEnv();
        if (envCfg != null) return envCfg;

        DbConfig fileCfg = loadFromPropertiesFile();
        if (fileCfg != null) return fileCfg;

        throw new SQLException(
                "Banco não configurado.\n"
                        + "- Crie o arquivo: " + PROPS_PATH + "\n"
                        + "  com as chaves DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD\n"
                        + "  (DB_HOST/DB_PORT podem ser opcionais se quiser localhost/5432)\n"
                        + "- OU defina as variáveis de ambiente: DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD"
        );
    }
    
    
    
    /**
     * Carrega configuração a partir de variáveis de ambiente.
     * Se nenhuma variável estiver definida, retorna {@code null} (indicando "não configurado por env").
     * Se alguma variável estiver definida, exige obrigatoriamente: DB_NAME, DB_USER, DB_PASSWORD.
     * DB_HOST e DB_PORT são opcionais (assumem {@link #DEFAULT_HOST} e {@link #DEFAULT_PORT})
     * @return configuração ou null
     * @throws SQLException se a configuração estiver parcialmente definida e incompleta
     */
    private static DbConfig loadFromEnv() throws SQLException {
        String host = envOrNull("DB_HOST");
        String port = envOrNull("DB_PORT");
        String db   = envOrNull("DB_NAME");
        String user = envOrNull("DB_USER");
        String pass = envOrNull("DB_PASSWORD");

        if (db == null && user == null && pass == null && host == null && port == null) {
            return null; 
        }

        host = host != null ? host : DEFAULT_HOST;
        port = port != null ? port : DEFAULT_PORT;

        validateRequired(db, user, pass, "variáveis de ambiente");
        return new DbConfig(host, port, db, user, pass);
    }
    
    
    /**
     * Carrega configuração a partir de arquivo properties (modo local).
     * Funcionamento:
     * - Tenta abrir {@link #PROPS_PATH}.
     * - Lê chaves DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD.
     * - DB_HOST e DB_PORT podem ser omitidos (assume defaults).
     * - DB_NAME, DB_USER e DB_PASSWORD são obrigatórios.
     * @return configuração ou null se o arquivo não existir
     * @throws SQLException se ocorrer erro ao ler/parsear o arquivo
     */
    private static DbConfig loadFromPropertiesFile() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(PROPS_PATH)) {
            props.load(in);

            String host = trimToNull(props.getProperty("DB_HOST"));
            String port = trimToNull(props.getProperty("DB_PORT"));
            String db   = trimToNull(props.getProperty("DB_NAME"));
            String user = trimToNull(props.getProperty("DB_USER"));
            String pass = trimToNull(props.getProperty("DB_PASSWORD"));

            host = host != null ? host : DEFAULT_HOST;
            port = port != null ? port : DEFAULT_PORT;

            validateRequired(db, user, pass, "arquivo " + PROPS_PATH);
            return new DbConfig(host, port, db, user, pass);

        } catch (java.io.FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new SQLException("Erro ao ler configurações do banco em " + PROPS_PATH + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida se campos obrigatórios foram informados.
     * @param db nome do banco
     * @param user usuário
     * @param pass senha
     * @param source descrição da origem (env/file) para mensagem de erro
     * @throws SQLException se algum campo obrigatório estiver ausente
     */
    private static void validateRequired(String db, String user, String pass, String source) throws SQLException {
        if (db == null || user == null || pass == null) {
            throw new SQLException(
                    "Configuração do banco incompleta em " + source + ".\n"
                            + "Obrigatório: DB_NAME, DB_USER, DB_PASSWORD."
            );
        }
    }
    
    /**
     * Obtém variável de ambiente e aplica {@code trim()}.
     * @param key nome da variável
     * @return valor normalizado ou null se ausente/vazio
     */
    private static String envOrNull(String key) {
        return trimToNull(System.getenv(key));
    }
    
    /**
     * Normaliza string: null → null, vazio → null, caso contrário retorna {@code trim()}.
     * @param v valor bruto
     * @return valor normalizado
     */
    private static String trimToNull(String v) {
        if (v == null) return null;
        v = v.trim();
        return v.isEmpty() ? null : v;
    }
    
    /**
     * Estrutura interna que representa a configuração carregada do banco.
     * É usada apenas como transporte de dados entre carregamento e montagem da URL.
     */
    private static final class DbConfig {
        final String host;
        final String port;
        final String dbName;
        final String user;
        final String password;

        DbConfig(String host, String port, String dbName, String user, String password) {
            this.host = host;
            this.port = port;
            this.dbName = dbName;
            this.user = user;
            this.password = password;
        }
    }
}