import os
import shutil

# Diretórios
src_telas = r"c:\Users\usuario\Desktop\BattleCity\src\telas"
bin_telas = r"c:\Users\usuario\Desktop\BattleCity\bin\telas"

# Arquivos .java não utilizados
java_files_to_remove = [
    "TelaCreditos.java",
    "TelasJogoCorrigida.java",
    "TelasJogoNova.java",
    "TelasJogoSimples.java",
    "TelasJogo_backup.java",
    "TelasJogo_new.java",
    "TelasJogo_temp.java"
]

# Arquivos .class obsoletos
class_files_to_remove = [
    "TelasJogoCorrigida.class",
    "TelasJogoNova.class",
    "TelasJogoSimples_Alt.class",
    "TelasJogo_Backup.class",
    "TelasJogo_New.class",
    "TelasJogo_Temp.class"
]

print("=" * 60)
print("REMOCAO DE ARQUIVOS DE TELAS NAO UTILIZADAS")
print("=" * 60)
print()

# Remover arquivos .java
print("Removendo arquivos .java nao utilizados de src/telas/...")
print("-" * 60)
for file in java_files_to_remove:
    filepath = os.path.join(src_telas, file)
    if os.path.exists(filepath):
        try:
            os.remove(filepath)
            print(f"✓ Removido: {file}")
        except Exception as e:
            print(f"✗ Erro ao remover {file}: {e}")
    else:
        print(f"- {file} nao encontrado (ignorado)")

print()

# Remover arquivos .class
print("Removendo arquivos .class obsoletos de bin/telas/...")
print("-" * 60)
for file in class_files_to_remove:
    filepath = os.path.join(bin_telas, file)
    if os.path.exists(filepath):
        try:
            os.remove(filepath)
            print(f"✓ Removido: {file}")
        except Exception as e:
            print(f"✗ Erro ao remover {file}: {e}")
    else:
        print(f"- {file} nao encontrado (ignorado)")

print()
print("=" * 60)
print("LIMPEZA CONCLUIDA!")
print("=" * 60)
print()
print("Arquivos mantidos em src/telas/:")
print("  • TelasJogo.java (classe principal)")
print("  • PainelJogoV2.java (painel de jogo)")
print("  • PainelJogo.java (wrapper para compatibilidade)")
print("  • TelaGameOver.java (tela de game over)")
print("  • TelaPause.java (tela de pausa)")
print()
print("Arquivos restantes em src/telas/:")
remaining = os.listdir(src_telas)
for file in sorted(remaining):
    if file.endswith('.java'):
        print(f"  • {file}")
print()
