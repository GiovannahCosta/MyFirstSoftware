package services;

import model.entities.Area;
import model.entities.FlavorLevel;
import model.entities.Size;
import model.factories.SizeFactory;
import model.repositories.RepositoryArea;
import model.repositories.RepositoryFlavorLevel;
import model.repositories.RepositorySize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public final class SeedService {

    private SeedService() {}

    public static void seedDefaults() throws SQLException {
    	seedAreasFromCsvIfNeeded();
        seedFlavorLevelsIfEmpty();
        seedSizesIfEmpty();
    }

    private static void seedAreasFromCsvIfNeeded() throws SQLException {
        RepositoryArea repoArea = new RepositoryArea();

        try (BufferedReader br = openAreasCsv()) {
            String line;
            while ((line = br.readLine()) != null) {
                String raw = line.trim();
                if (raw.isEmpty()) continue;
                if (raw.startsWith("#")) continue;

                String[] parts = raw.split(";");
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                if (name.isEmpty()) continue;

                Double fee;
                try {
                    fee = Double.parseDouble(parts[1].trim().replace(",", "."));
                } catch (Exception e) {
                    continue; 
                }

                Area existing = repoArea.findByNameArea(name);
                if (existing != null) continue;

                repoArea.createArea(new Area(name, fee));
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao ler areas.csv: " + e.getMessage(), e);
        }
    }

    /**
     * Tenta abrir o CSV como resource do classpath e,
     * se não achar, tenta caminho relativo (modo simples).
     */
    private static BufferedReader openAreasCsv() throws Exception {
        InputStream is = SeedService.class.getClassLoader().getResourceAsStream("areas.csv");
        if (is != null) {
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        }

        File f1 = new File("confeitaria/src/resources/areas.csv");
        if (f1.exists()) return new BufferedReader(new FileReader(f1, StandardCharsets.UTF_8));

        File f2 = new File("src/resources/areas.csv");
        if (f2.exists()) return new BufferedReader(new FileReader(f2, StandardCharsets.UTF_8));

        throw new Exception("Arquivo areas.csv não encontrado no classpath nem nos caminhos fallback.");
    }

    private static void seedFlavorLevelsIfEmpty() throws SQLException {
        RepositoryFlavorLevel repo = new RepositoryFlavorLevel();
        List<FlavorLevel> existing = repo.findAllFlavorLevel();
        if (existing != null && !existing.isEmpty()) return;

        repo.createFlavorLevel(new FlavorLevel(null, "Tradicional", 10.00));
        repo.createFlavorLevel(new FlavorLevel(null, "Especial", 20.00));
    }

    private static void seedSizesIfEmpty() throws SQLException {
        RepositorySize repo = new RepositorySize();
        List<Size> existing = repo.findAllSize();
        if (existing != null && !existing.isEmpty()) return;
        
        repo.createSize(new Size(null, "Mine", "8 a 10 pessoas", "1.3kg", 130.00));
        repo.createSize(new Size(null, "PP", "20 a 25 pessoas", "2.3kg", 20.00));
        repo.createSize(new Size(null, "P", "35 a 40 pessoas", "3.8kg", 20.00));
        repo.createSize(new Size(null, "M", "50 pessoas", "7kg", 20.00));
        repo.createSize(new Size(null, "G", "90 pessoas", "9kg", 20.00));
    }
}