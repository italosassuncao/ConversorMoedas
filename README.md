# 💰 Conversor de Moedas: Cotação em Tempo Real (Crypto, Moedas e Ações)

Este projeto é um aplicativo Android nativo desenvolvido em **Kotlin** e **Jetpack Compose**, oferecendo cotações de mercado em tempo real, histórico de preços, busca otimizada, sistema de favoritos e alertas personalizados.

---

## 🛠️ Tecnologias e Ferramentas

O aplicativo foi construído utilizando as seguintes tecnologias modernas do ecossistema Android:

- Linguagem de Programação: Kotlin
- Interface de Usuário: Jetpack Compose (Modern UI Toolkit)
- Arquitetura: Clean Architecture (MVVM)
- Persistência: Room (Banco de Dados Local)
- Gerenciamento de Estado: Jetpack ViewModel + Kotlin Flow
- Injeção de Dependência: Koin
- Networking: Retrofit & OkHttp
- Serialização: Kotlinx Serialization
- APIs Utilizadas:
- - CoinGecko: Cotações e Histórico de Criptomoedas.
- - AlphaVantage: Cotações de Ações e Moedas Tradicionais (Forex).

---

## 🏗️ Estrutura do Projeto

A estrutura segue o padrão MVVM acoplado ao Clean Architecture, com a camada de Domínio desacoplada das implementações de Dados.

### 📁 Camadas Principais

- **data**
  - Lida com fontes de dados: APIs e banco local  
- **models**
  - Data classes para requisições e respostas  
- **repository**
  - Implementações concretas das interfaces de domínio  
- **di**
  - Módulos Koin (rede, repositórios, viewmodels)  
- **util**
  - Classes auxiliares (ex: `Resource` para estado de rede)  
- **domain**
  - Interface `QuotationRepository` + regras de negócio  
- **presentation**
  - Telas e ViewModels do Compose  
  - **explore:** listagem e busca  
  - **detail:** gráfico e informações históricas  
- **ui.theme**
  - Cores, tipografia e estilos do Compose

---

## 🚀 Funcionalidades Atuais (v1.0)

| Funcionalidade             | Status      | Detalhes |
|---------------------------|-------------|----------|
| **Integração Multi-API**  | ✅ Concluído | Mescla cotações de CoinGecko (Crypto) e AlphaVantage (Ações/Forex). |
| **Persistência de Favoritos**  | ✅ Concluído | Room implementado para salvar favoritos localmente. |
| **Tela de Favoritos**              | ✅ Concluído | Listagem em tempo real e remoção de itens salvos (via Flow). |
| **Toggle Favoritos**             | ✅ Concluído | Adicionar/remover favoritos na Tela de Detalhes. |
| **Listagem e Busca**      | ✅ Concluído | Exibição unificada de todos os ativos com busca otimizada (debounce). |
| **Gráfico de Histórico**| ✅ Concluído | Visualização de dados históricos (Crypto) usando Canvas. |

---

## 💡 Próximos Passos (Roadmap)
Foco na conclusão das funcionalidades de alerta e melhoria do histórico:
- **Sistema de Alertas:** Implementar a lógica de alertas customizáveis, utilizando WorkManager para agendamento em segundo plano. 
- **Tela de Alertas:** Criar a UI para configuração e histórico de alertas.  
- **Histórico AlphaVantage:** Implementar a busca de histórico de preços para Ações e Moedas.


MIT License

Copyright (c) 2025 Italo

---
