#!/bin/bash
# Script para remover arquivos de telas nao utilizadas
# Execute com: bash remover_telas.sh

echo "========================================"
echo "REMOCAO DE ARQUIVOS DE TELAS OBSOLETAS"
echo "========================================"
echo ""

src_dir="c:/Users/usuario/Desktop/BattleCity/src/telas"
bin_dir="c:/Users/usuario/Desktop/BattleCity/bin/telas"

# Arrays de arquivos a remover
java_files=(
    "TelaCreditos.java"
    "TelasJogoCorrigida.java"
    "TelasJogoNova.java"
    "TelasJogoSimples.java"
    "TelasJogo_backup.java"
    "TelasJogo_new.java"
    "TelasJogo_temp.java"
)

class_files=(
    "TelasJogoCorrigida.class"
    "TelasJogoNova.class"
    "TelasJogoSimples_Alt.class"
    "TelasJogo_Backup.class"
    "TelasJogo_New.class"
    "TelasJogo_Temp.class"
)

echo "Removendo arquivos .java nao utilizados..."
for file in "${java_files[@]}"; do
    filepath="$src_dir/$file"
    if [ -f "$filepath" ]; then
        rm -f "$filepath"
        echo "✓ Removido: $file"
    fi
done

echo ""
echo "Removendo arquivos .class obsoletos..."
for file in "${class_files[@]}"; do
    filepath="$bin_dir/$file"
    if [ -f "$filepath" ]; then
        rm -f "$filepath"
        echo "✓ Removido: $file"
    fi
done

echo ""
echo "========================================"
echo "LIMPEZA CONCLUIDA!"
echo "========================================"
echo ""
echo "Arquivos mantidos em src/telas:"
ls -1 "$src_dir"/*.java 2>/dev/null | xargs -n1 basename
