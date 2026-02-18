package services;

import model.entities.Area;
import model.entities.FlavorLevel;
import model.entities.Size;
import model.repositories.RepositoryArea;
import model.repositories.RepositoryFlavorLevel;
import model.repositories.RepositorySize;

import java.sql.SQLException;
import java.util.List;

public final class SeedService {

    private SeedService() {}

    public static void seedDefaults() throws SQLException {
        seedAreasIfEmpty();
        seedFlavorLevelsIfEmpty();
        seedSizesIfEmpty();
    }

    private static void seedAreasIfEmpty() throws SQLException {
        RepositoryArea repoArea = new RepositoryArea();
        List<Area> existing = repoArea.findAllArea();
        if (existing != null && !existing.isEmpty()) return;

        repoArea.createArea(new Area("Centro", 5.00));
        repoArea.createArea(new Area("Zona Norte", 7.50));
        repoArea.createArea(new Area("Zona Sul", 8.00));
    }

    private static void seedFlavorLevelsIfEmpty() throws SQLException {
        RepositoryFlavorLevel repo = new RepositoryFlavorLevel();
        List<FlavorLevel> existing = repo.findAllFlavorLevel();
        if (existing != null && !existing.isEmpty()) return;

        // Ajuste nomes e preços como quiser
        repo.createFlavorLevel(new FlavorLevel(null, "Suave", 0.00));
        repo.createFlavorLevel(new FlavorLevel(null, "Médio", 2.50));
        repo.createFlavorLevel(new FlavorLevel(null, "Forte", 4.00));
    }

    private static void seedSizesIfEmpty() throws SQLException {
        RepositorySize repo = new RepositorySize();
        List<Size> existing = repo.findAllSize();
        if (existing != null && !existing.isEmpty()) return;

        // name, yield, weight, price — conforme seu entity Size
        repo.createFlavorLevel(new Size(null, "P", "4 fatias", "450g", 0.00));
        repo.createFlavorLevel(new Size(null, "M", "8 fatias", "900g", 8.00));
        repo.createFlavorLevel(new Size(null, "G", "12 fatias", "1.3kg", 15.00));
    }
}