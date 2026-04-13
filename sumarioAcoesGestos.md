# Sumário Resumido das Ações por Gestos

## Controlo de Reprodução de Música
Ações principais do player.

- Identificação da faixa musical → ✅ 
- Inclinar direita → Próxima música ✅ 
- Inclinar esquerda → Música anterior ✅ 
- Tapar sensor de proximidade por 3 segundos → Pausar música - c/ janela de pausa entre acções ✅ 
- Tapar sensor de proximidade por 3 segundos → Retomar reprodução - c/ janela de pausa entre acções ✅ 
- Sacudir rapidamente → Ativar modo Shuffle ✅ 

(Virar o telemóvel ao contrário → Parar música)

# Controlo de Volume
Ações contínuas e intuitivas.

- Inclinar telemóvel para a frente  → Aumentar volume ✅ 
- Inclinar telemóvel para a tras → Diminuir volume ✅ 

(Manter telemóvel inclinado para cima → Aumentar volume gradualmente)
(Manter telemóvel inclinado para baixo → Diminuir volume gradualmente)

# Modos do Sistema
Estados da aplicação.

⚠️ Não recomendado pelas melhores prática de UI - Existe um botão de Sensors ON/OFF toggle ⚠️
- Sacudir 1 vez → Ativar modo gestos - c/ janela de pausa entre acções ✅ 
- Sacudir 1 vez novamente → Desativar modo gestos - c/ janela de pausa entre acções ✅ 

(Tapar sensor por 1 segundo → Bloquear gestos)
(Tapar sensor por 1 segundo novamente → Desbloquear gestos)

# Feedback ao Utilizador (Automático)
Ações do sistema após eventos - confirmação dos comandos

- Vibração curta → Gesto reconhecido
Shake → vibração (forte e curta)
Proximity → vibração leve 
Tilt → SEM vibração ✅
Volume → SEM vibração ✅

(Vibração longa → Erro)
(Som curto → Ação executada)

- Mostrar volume atual ✅
- Mostrar música atual ✅

# Sensores Utilizados (Resumo)

- Acelerómetro - Detecta:
inclinar
sacudir
movimento

- Rotation Vector - Detecta:
rotação
orientação

- Proximity - Detecta:
mão perto
confirmação


---
# TODO in the future

- Mensagem no ecrã → Estado atual
- Mostrar nome da playlist ativa

## Gestão de Playlist (Músicas)
Operações sobre músicas dentro de uma playlist.

- Sacudir 2 vezes → Adicionar música à playlist ativa
- Sacudir 3 vezes → Remover música da playlist ativa
- Inclinar e manter por 2 segundos → Repetir música atual
- Rodar 180° → Adicionar música aos favoritos

## Gestão de Playlists (Estrutura)
Operações sobre playlists.

- Rodar telemóvel 360° → Criar nova playlist
- Tapar sensor de proximidade por 2 segundos → Confirmar criação
- Tapar sensor por 5 segundos → Apagar playlist atual
- Manter telemóvel imóvel por 3 segundos → Guardar playlist

## Navegação entre Playlists
Ações de navegação.

- Rodar ligeiramente para a direita → Próxima playlist
- Rodar ligeiramente para a esquerda → Playlist anterior
- Tapar sensor de proximidade por 2 segundos → Selecionar playlist
- Sacudir 3 vezes → Entrar em modo navegação de playlists
- Não fazer gestos por 5 segundos → Sair do modo navegação