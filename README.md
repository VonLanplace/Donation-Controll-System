# Donation Control System 🏢🥦

O **Donation Control System** é uma aplicação desktop para gerenciamento e triagem de doações de alimentos e montagem de cestas básicas. Desenvolvido em **Java 21** com interface **JavaFX** e gerenciado via **Gradle (Kotlin DSL)**.

O projeto segue o padrão arquitetural **Ports & Adapters (Hexagonal)** para garantir o isolamento das regras de negócio.

---

## 📺 Demonstração do Sistema

<p align="center">
  <kbd>
    <video src="TrabalhoPOO.mp4" 
           controls 
           width="750" 
           height="auto" 
           style="max-width: 100%; border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.15); display: block;">
    </video>
  </kbd>
</p>

*Caso o player não carregue, assista abrindo o arquivo por aqui: [TrabalhoPOO.mp4](./TrabalhoPOO.mp4)*

---

## 🛠️ Tecnologias Utilizadas

- **Java 21:** Uso de Pattern Matching e Records.
- **JavaFX:** Interface gráfica e controle de eventos reativos.
- **Gradle:** Build script via `build.gradle.kts` (Kotlin DSL).
- **MariaDB 11.x:** Persistência de dados com Driver JDBC Nativo.
- **Docker Compose:** Orquestração do banco de dados local.

---

## 🏗️ Estrutura do Projeto (Hexagonal)

- **`adapter`**: Portas de entrada/saída e DTOs de transferência.
- **`controllers`**: Pontes entre os eventos da View e os Services.
- **`exceptions`**: Exceções de domínio (ex: `CpfInvalidoException`).
- **`model`**: Entidades ricas de negócio baseadas em `IEntity`.
- **`persistence`**: Camada de dados (DAOs e conectores SQL).
- **`service`**: Centralização dos casos de uso e regras puras.
- **`views`**: Telas JavaFX segmentadas por contexto (Admin/Doador).

---

## 📂 Árvore de Diretórios

```text
.
├── app
│   ├── build.gradle.kts     # Dependências e Plugins
│   └── src
│       └── main/java/edu/fatec/poo/
│           ├── adapter/
│           ├── configs/
│           ├── controllers/
│           ├── exceptions/
│           ├── model/
│           ├── persistence/
│           ├── service/
│           └── views/
├── mariaDB
│   └── compose.yaml         # Container do MariaDB
└── TrabalhoPOO.mp4          # Vídeo demonstrativo

```

---

## 🚀 Como Executar o Projeto

### 1. Clonar o Repositório

```bash
git clone [https://github.com/VonLanplace/Donation-Controll-System.git](https://github.com/VonLanplace/Donation-Controll-System.git)
cd Donation-Controll-System

```

### 2. Subir o Banco de Dados (MariaDB)

```bash
cd mariaDB
docker compose up -d
cd ..

```

### 3. Executar a Aplicação

**No Linux/macOS:**

```bash
./gradlew clean app:run

```

**No Windows (Prompt/PowerShell):**

```cmd
gradlew.bat clean app:run

```

---

## 📝 Detalhes de Implementação

* **Validações:** Formatação e checagem de dados em tempo real na interface com `WindowStandardFormatting`.
* **Tabelas Automáticas:** A classe `mariadbCreateTable` gera a estrutura do banco dinamicamente no primeiro boot.
* **Níveis de Acesso:** Controle hierárquico mapeado via enum `Acesso`, gerenciando as permissões de telas de forma segura.

