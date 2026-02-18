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

/**
 * Observação importante: essa lógica para a seed dos bairros/areas foi feita com IA, pois eu não tinha ideia de como seedar e encapsular as áreas se não fosse utilizando a criação local delas no banco
 * Serviço responsável por executar o seed (popular dados iniciais) no banco de dados.
 * Este seed é executado na inicialização da aplicação, antes de abrir a interface, no arquivo {@code app.Main}. 
 * A intenção é garantir que dados essenciais existam para as telas funcionarem corretamente (ex.: combo de bairros/áreas no cadastro).
 *
 * As seed que fazemos são:
 * - Áreas/Bairros (tabela {@code area}): carregados de um arquivo CSV.
 * - Níveis de sabor (tabela {@code flavor_level}): inseridos apenas se a tabela estiver vazia.
 * - Tamanhos (tabela {@code size}): inseridos apenas se a tabela estiver vazia.
 * 
 * Obs.: Como o CSV pode conter muitos bairros, este serviço pode causar um pequeno atraso na primeira abertura da aplicação (a {@code ViewHome} só abre após o seed terminar).
 *
 * Arquivo CSV de áreas: 
 * - Nome do arquivo: {@code areas.csv}
 * - Formato esperado por linha: nome;taxa
 *
 * Onde colocar o arquivo areas.csv?
 * - A leitura tenta primeiro como resource do classpath e, se não encontrar, faz fallback
 * para caminhos relativos (útil quando o projeto não está empacotado como Maven/Gradle):
 * - Classpath: {@code areas.csv}
 * - Arquivo: {@code confeitaria/src/resources/areas.csv}
 * - Arquivo: {@code src/resources/areas.csv}
 */
 

public final class SeedService {
	
	/**
     * Construtor privado para impedir instanciação.
     * Esta classe é um serviço utilitário composto apenas por métodos estáticos.
     */
    private SeedService() {}
    
   /**
    * Executa todos os seeds padrão da aplicação.
    * Carrega áreas/bairros do CSV e insere apenas as áreas inexistentes.
    * Insere níveis de sabor padrão se a tabela estiver vazia.
    * Insere tamanhos padrão se a tabela estiver vazia.
    * @throws SQLException se ocorrer falha ao acessar o banco durante qualquer seed
    */
    public static void seedDefaults() throws SQLException {
    	seedAreasFromCsvIfNeeded();
        seedFlavorLevelsIfEmpty();
        seedSizesIfEmpty();
    }
    
    
    /**
     * Lê o arquivo {@code areas.csv} e insere áreas no banco caso ainda não existam.
     * Estratégia de inserção:
     * - Para cada linha válida do CSV, busca se já existe uma área com o mesmo nome usando {@code RepositoryArea.findByNameArea(name)}.
     * - Se já existir, ignora (não insere duplicado).
     * - Se não existir, insere usando {@code RepositoryArea.createArea(new Area(name, fee))}.
     * - Não utiliza o AreaFactory
     * 
     * Linhas inválidas são ignoradas:
     * - linha vazia
     * - linha iniciada com {@code #}
     * - linha sem o separador {@code ;} com pelo menos 2 partes
     * - taxa não numérica
     * 
     * este método não remove áreas antigas e não atualiza taxas já existentes — apenas garante que todas as áreas do CSV estejam presentes.
     *
     * @throws SQLException em erro de leitura do CSV ou erro de acesso ao banco
     */
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
     * Abre o arquivo {@code areas.csv} para leitura em UTF-8.
     * Classpath resource: {@code areas.csv}
     * Arquivo em {@code confeitaria/src/resources/areas.csv}
     * Arquivo em {@code src/resources/areas.csv}
     * @return reader pronto para leitura linha-a-linha
     * @throws Exception se não encontrar o arquivo em nenhum local suportado
     */
    private static BufferedReader openAreasCsv() throws Exception {
    	//caso o projeto configure resources corretamente
        InputStream is = SeedService.class.getClassLoader().getResourceAsStream("areas.csv");
        if (is != null) {
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        }
        
        //fallback para arquivo no projeto (útil no modo "rodar pela IDE")
        File f1 = new File("confeitaria/src/resources/areas.csv");
        if (f1.exists()) return new BufferedReader(new FileReader(f1, StandardCharsets.UTF_8));

        File f2 = new File("src/resources/areas.csv");
        if (f2.exists()) return new BufferedReader(new FileReader(f2, StandardCharsets.UTF_8));

        throw new Exception("Arquivo areas.csv não encontrado no classpath nem nos caminhos fallback.");
    }
    
    
    /**
     * Insere níveis de sabor padrões se a tabela {@code flavor_level} estiver vazia.
     * Lê a lista atual do banco ({@link RepositoryFlavorLevel#findAllFlavorLevel()}).
     * Se já existir qualquer registro, não faz nada.
     * Se estiver vazio, insere níveis padrão.
     * Não utiliza o FlavorLevelFactory
     * @throws SQLException em caso de erro ao consultar/inserir no banco
     */
    private static void seedFlavorLevelsIfEmpty() throws SQLException {
        RepositoryFlavorLevel repo = new RepositoryFlavorLevel();
        List<FlavorLevel> existing = repo.findAllFlavorLevel();
        if (existing != null && !existing.isEmpty()) return;

        repo.createFlavorLevel(new FlavorLevel(null, "Tradicional", 10.00));
        repo.createFlavorLevel(new FlavorLevel(null, "Especial", 20.00));
    }
    
    /**
     * Insere tamanhos padrões se a tabela {@code size} estiver vazia.
     * Lê a lista atual do banco ({@link RepositorySize#findAllSize()}).
     * Se já existir qualquer registro, não faz nada.
     * Se estiver vazio, insere tamanhos padrão.
     * Não utiliza o SizeFactory
     * @throws SQLException em caso de erro ao consultar/inserir no banco
     */
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