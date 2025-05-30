# 📚 Library Manager

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17+-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

**Sistema de Gerenciamento de Biblioteca Desktop**

*Uma aplicação robusta para controle completo de bibliotecas*

[📖 Documentação](#-funcionalidades) • [🚀 Instalação](#️-como-executar) • [🤝 Contribuição](#-contribuição) • [📜 Licença](#-licença)

</div>

---

## 🎯 Sobre o Projeto

O **Library Manager** é uma aplicação desktop moderna desenvolvida para gerenciamento completo de bibliotecas. Construído com **Java 21**, **JavaFX** e **PostgreSQL**, o sistema oferece uma interface intuitiva e funcionalidades robustas para controle de acervo, usuários e empréstimos.

### ✨ Diferenciais

- 🏗️ **Arquitetura Sólida**: Implementa padrões MVC, DAO e princípios SOLID
- 🎨 **Interface Moderna**: JavaFX com design responsivo e intuitivo
- 🔒 **Segurança**: Validações robustas e tratamento de erros
- 🚀 **Performance**: Otimizado para grandes volumes de dados
- 📊 **Relatórios**: Sistema de relatórios integrado

## 🔥 Funcionalidades

### 📖 Gestão de Livros
- ✅ Cadastro completo com ISBN, autor, categoria e editora
- ✅ Edição e exclusão com confirmação de segurança
- ✅ Busca avançada por múltiplos critérios
- ✅ Controle de disponibilidade em tempo real

### 👥 Gestão de Usuários
- ✅ Cadastro com dados pessoais e contato
- ✅ Histórico completo de empréstimos
- ✅ Sistema de notificações automáticas
- ✅ Controle de status (ativo/inativo)

### 📋 Controle de Empréstimos
- ✅ Registro de empréstimos com data de devolução
- ✅ Controle de multas por atraso
- ✅ Renovação automática (quando permitido)
- ✅ Relatórios de empréstimos em atraso

### 🔍 Sistema de Busca
- ✅ Busca instantânea com feedback visual
- ✅ Filtros por categoria, autor e disponibilidade
- ✅ Visualização em cards organizados
- ✅ Exportação de resultados

## 🛠️ Tecnologias

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Java** | 21 (LTS) | Linguagem principal |
| **JavaFX** | 17+ | Interface gráfica |
| **PostgreSQL** | 13+ | Banco de dados |
| **Gradle** | 8.0+ | Build e dependências |
| **JUnit** | 5+ | Testes unitários |

## 📁 Arquitetura do Projeto

```
library-manager/
├── 📁 src/main/java/
│   └── 📁 com/managerlibrary/
│       ├── 📁 controllers/     # Controladores JavaFX
│       ├── 📁 services/        # Regras de negócio
│       ├── 📁 daos/           # Camada de acesso a dados
│       ├── 📁 entities/       # Entidades do domínio
│       ├── 📁 infra/          # Infraestrutura (DB, Config)
│       ├── 📁 utils/          # Utilitários e helpers
│       └── 📄 Main.java       # Ponto de entrada
├── 📁 src/main/resources/
│   ├── 📁 fxml/              # Arquivos de interface
│   ├── 📁 css/               # Estilos CSS
│   └── 📁 images/            # Recursos visuais
├── 📁 src/test/java/         # Testes unitários
├── 📄 build.gradle.kts       # Configuração Gradle
└── 📄 database/              # Scripts SQL
    ├── 📄 schema.sql         # Estrutura do banco
    └── 📄 data.sql           # Dados iniciais
```

## 🏛️ Padrões e Princípios

### 🔧 Padrões de Projeto
- **MVC**: Separação clara entre apresentação, lógica e dados
- **DAO**: Abstração da camada de persistência
- **Factory**: Criação de objetos complexos
- **Observer**: Notificações entre componentes

### 📐 Princípios SOLID
- **S**ingle Responsibility: Uma responsabilidade por classe
- **O**pen/Closed: Aberto para extensão, fechado para modificação
- **L**iskov Substitution: Substituição sem quebrar funcionalidade
- **I**nterface Segregation: Interfaces específicas e coesas
- **D**ependency Inversion: Dependa de abstrações, não de implementações

## ⚡ Como Executar

### 📋 Pré-requisitos
- Java 21 ou superior
- PostgreSQL 13 ou superior
- Git

### 🚀 Instalação

1. **Clone o repositório**
```bash
git clone https://github.com/eziocdl/library-manager.git
cd library-manager
```

2. **Configure o banco de dados**
```bash
# Crie o banco de dados
createdb manager_library

# Execute os scripts SQL (na pasta database/)
psql -d manager_library -f database/schema.sql
psql -d manager_library -f database/data.sql
```

3. **Configure as credenciais**
```java
// Edite src/main/java/com/managerlibrary/infra/DatabaseConnection.java
private static final String URL = "jdbc:postgresql://localhost:5432/manager_library";
private static final String USER = "seu_usuario";
private static final String PASSWORD = "sua_senha";
```

4. **Execute a aplicação**
```bash
# Linux/Mac
./gradlew run

# Windows
gradlew.bat run
```

### 🧪 Executar Testes
```bash
# Todos os testes
./gradlew test

# Testes com relatório
./gradlew test --info
```

### 📦 Gerar Executável
```bash
# Gerar JAR
./gradlew jar

# Gerar distribuição completa
./gradlew distZip
```

## 📸 Screenshots

<div align="center">

### Tela Principal

<img width="1920" alt="Screenshot 2025-05-30 at 15 01 34" src="https://github.com/user-attachments/assets/5ebf83ce-193d-41a4-9670-5838d509f0ff" />


### Gestão de Livros

<img width="1920" alt="Screenshot 2025-05-30 at 15 01 24" src="https://github.com/user-attachments/assets/13cd29d7-d1cb-410b-915a-8ad35cd240a3" />


### Controle de Empréstimos

<img width="1920" alt="Screenshot 2025-05-30 at 15 02 26" src="https://github.com/user-attachments/assets/cb4f00b4-77ce-4729-97bc-16271f81bda5" />

</div>

## 📊 Banco de Dados

### Modelo ER
```sql
-- Principais tabelas
books (id, title, author, isbn, category, available)
users (id, name, email, phone, address, active)
loans (id, book_id, user_id, loan_date, return_date, returned)
```

### Scripts Disponíveis
- `schema.sql`: Estrutura completa do banco
- `data.sql`: Dados de exemplo para teste
- `migrations/`: Scripts de migração de versão

## 🤝 Contribuição

Contribuições são sempre bem-vindas! Siga estes passos:

1. **Fork** o projeto
2. **Crie** uma branch para sua feature
   ```bash
   git checkout -b feature/nova-funcionalidade
   ```
3. **Commit** suas mudanças
   ```bash
   git commit -m "feat: adiciona nova funcionalidade"
   ```
4. **Push** para a branch
   ```bash
   git push origin feature/nova-funcionalidade
   ```
5. **Abra** um Pull Request

### 📝 Padrões de Commit
- `feat:` Nova funcionalidade
- `fix:` Correção de bug
- `docs:` Documentação
- `style:` Formatação
- `refactor:` Refatoração
- `test:` Testes

## 📈 Roadmap

- [ ] 🌐 Interface web (Spring Boot)
- [ ] 📱 Aplicativo mobile
- [ ] 🔄 Sincronização em nuvem
- [ ] 📊 Dashboard analítico
- [ ] 🔔 Notificações push
- [ ] 📖 API REST completa

## 🐛 Reportar Issues

Encontrou um bug ou tem uma sugestão? 

1. Verifique se já não existe uma issue similar
2. Crie uma nova issue com:
   - Descrição clara do problema
   - Passos para reproduzir
   - Comportamento esperado vs atual
   - Screenshots (se aplicável)

## 📜 Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<div align="center">

### 👨‍💻 Desenvolvido por

**Ezio Cintra de Lima**

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/eziocdl)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/eziocdl)

---

⭐ **Se este projeto te ajudou, deixe uma estrela!** ⭐

</div>
