package model.repositories;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

	private static final String PROPS_PATH = "src/db.properties";

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "5432";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        DbConfig cfg = loadConfigOrThrow();

        String url = "jdbc:postgresql://" + cfg.host + ":" + cfg.port + "/" + cfg.dbName;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {}

        return DriverManager.getConnection(url, cfg.user, cfg.password);
    }

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

    private static void validateRequired(String db, String user, String pass, String source) throws SQLException {
        if (db == null || user == null || pass == null) {
            throw new SQLException(
                    "Configuração do banco incompleta em " + source + ".\n"
                            + "Obrigatório: DB_NAME, DB_USER, DB_PASSWORD."
            );
        }
    }

    private static String envOrNull(String key) {
        return trimToNull(System.getenv(key));
    }

    private static String trimToNull(String v) {
        if (v == null) return null;
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

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