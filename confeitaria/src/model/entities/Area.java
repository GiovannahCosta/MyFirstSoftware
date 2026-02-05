package model.entities;

import java.util.List;

/**
 * Área/região de entrega (bairro ou zona). Usada no endereço para cálculo de taxa (fee).
 */
public class Area {

    /** Áreas de exemplo para seed inicial – bairros de Anápolis-GO. */
    private static final List<Area> DEFAULT_AREAS = List.of(
            new Area("Centro", 0.0),
            new Area("Jardim Bandeirante", 5.0),
            new Area("Vila Norte", 5.0),
            new Area("Residencial Ayrton Senna", 6.0),
            new Area("Vale dos Pássaros", 6.0),
            new Area("Granjas Santo Antônio", 7.0),
            new Area("Residencial do Cerrado", 6.0),
            new Area("Residencial Valência", 6.0),
            new Area("Vila Nossa Senhora da Abadia", 5.0)
    );

    private Integer id;
    private String name;
    private double fee;

    public Area() {}

    public Area(String name, double fee) {
        setName(name);
        setFee(fee);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    /**
     * Retorna a lista de áreas de exemplo para criação no banco (seed).
     * Usado no bootstrap (Main) para popular a tabela area via RepositoryArea.
     */
    public static List<Area> getDefaultAreas() {
        return DEFAULT_AREAS;
    }

    @Override
    public String toString() {
        return "Area [id=" + id + ", name=" + name + ", fee=" + fee + "]";
    }
}
