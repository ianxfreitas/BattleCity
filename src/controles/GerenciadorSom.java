package controles;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class GerenciadorSom {

    public static void tocarSom(String caminhoArquivo) {
        try {
            // Puxa o arquivo da pasta /res/
            URL url = GerenciadorSom.class.getResource(caminhoArquivo);

            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start(); // Dá o play!
            } else {
                System.err.println("⚠️ Som não encontrado: " + caminhoArquivo);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("⚠️ Erro ao tentar tocar o som: " + caminhoArquivo);
            e.printStackTrace();
        }
    }
}