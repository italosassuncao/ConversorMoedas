# 💰 QuotationApp: Cotação em Tempo Real (Crypto, Moedas e Ações)

Este projeto é um aplicativo Android nativo desenvolvido em **Kotlin** e **Jetpack Compose**, oferecendo cotações de mercado em tempo real, histórico de preços, busca otimizada, sistema de favoritos e alertas personalizados.

---

## 🛠️ Tecnologias e Ferramentas

O aplicativo utiliza tecnologias modernas do ecossistema Android:

- **Linguagem:** Kotlin  
- **UI:** Jetpack Compose  
- **Arquitetura:** Clean Architecture (módulos: *data*, *domain*, *presentation*)  
- **Gerenciamento de Estado:** ViewModel + Kotlin Flow + Compose State  
- **Injeção de Dependência:** Koin  
- **Rede:** Retrofit & OkHttp  
- **Serialização:** Kotlinx Serialization  
- **Banco de Dados (Futuro):** Room  

### APIs Utilizadas
- **CoinGecko:** Cotações e histórico de criptomoedas  
- **AlphaVantage (futuro):** Ações e moedas tradicionais  

---

## 🏗️ Estrutura do Projeto

A arquitetura segue o padrão **MVVM** dentro do contexto de **Clean Architecture**, garantindo separação de responsabilidades e alta testabilidade.

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
| **Listagem de Cotações**  | ✅ Concluído | Exibe criptomoedas com preço e variação (24h). |
| **Pesquisa**              | ✅ Concluído | Campo com *debounce* para otimizar requisições. |
| **Navegação**             | ✅ Concluído | Bottom Bar (Explorar, Favoritos, Alertas). |
| **Tela de Detalhes**      | ✅ Concluído | Mostra infos e gráfico de 7 dias via Canvas. |
| **Injeção de Dependência**| ✅ Concluído | Koin configurado para toda a arquitetura. |
| **Tratamento de Erros**   | ✅ Concluído | Sealed class `Resource` para loading/sucesso/erro. |

---

## 💡 Próximos Passos (Roadmap)

- **Integração AlphaVantage:** Suporte para ações e moedas tradicionais.  
- **Persistência (Room):** Armazenar favoritos e alertas localmente.  
- **Tela de Favoritos:** UI e lógica para gerenciar favoritos.  
- **Sistema de Alertas:** Alertas customizados usando WorkManager e/ou Push Notifications.


MIT License

Copyright (c) 2025 Italo

---
