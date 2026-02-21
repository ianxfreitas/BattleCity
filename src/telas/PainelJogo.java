package telas;

import jogo.Configuracoes;

/**
 * DEPRECATED: Use PainelJogoV2 ao invés desta classe.
 * Esta classe foi mantida para compatibilidade mas não é mais utilizada.
 * Agora funciona como um wrapper que chama PainelJogoV2.
 */
public class PainelJogo extends PainelJogoV2 {
	
	public PainelJogo() {
		super(new Configuracoes());
	}
	
	public PainelJogo(Object mapa) {
		super(new Configuracoes());
	}
}
