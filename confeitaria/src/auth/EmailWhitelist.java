package auth;

import java.util.Set;
/**
 * Classe utilitária de autorização baseada em whitelist de e-mails.
 * É a forma de abstração encontrada para simplificar a lógica de "Grupos de Acesso" modelada originalmente.
 * Restringe funcionalidades administrativas (ex.: "Cadastro de Produtos (Admin)")
 * Permite que apenas e-mails específicos (pré-aprovados) acessem telas/ações de admin
 * 
 * Como funciona:
 * - Existe um conjunto fixo de e-mails permitidos ({@link ALLOWED_EMAILS}).
 * - O método {@link isAllowed(String)} normaliza o email (trim + lowercase) e verifica se está no conjunto.
 * 
 */
public final class EmailWhitelist {
	
	/**
     * Conjunto de e-mails autorizados a acessar funções administrativas.
     * {@link Set.of(Object...)} cria um conjunto imutável.
     *  O método {@link #isAllowed(String)} converte o email informado para lowercase, então o ideal é manter estes e-mails em minúsculo.
     */
	private static final Set<String> ALLOWED_EMAILS = Set.of(
            "julia.santosspinheiro@gmail.com",
            "giovannahgsantos@gmail.com",
            "admin@gmail.com"
    );
	
	/**
     * Construtor privado para impedir instanciação.
     * Esta classe é utilitária e deve ser usada apenas via métodos estáticos.
     */
    private EmailWhitelist() {}
    
    /**
     * Verifica se um e-mail está autorizado (whitelist).
     * Se {@code email} for null, retorna {@code false}.
     * Normaliza o e-mail com {@code trim()} e {@code toLowerCase()}.
     * Verifica se o e-mail normalizado existe em {@link ALLOWED_EMAILS}.
     * @param email e-mail a validar
     * @return true se o e-mail estiver na whitelist; false caso contrário
     */
    public static boolean isAllowed(String email) {
        if (email == null) return false;
        return ALLOWED_EMAILS.contains(email.trim().toLowerCase());
    }

}
