# Ponto Online API & Web App

Um sistema completo de **Controle de Ponto Eletrônico** moderno, desenvolvido para gerenciar horários, jornadas de trabalho, justificativas de ausências e solicitações de férias de funcionários.

A aplicação conta com um backend robusto em **Kotlin + Spring Boot** e um frontend responsivo em **HTML5, CSS3 e JavaScript Vanilla**, servido diretamente pela aplicação.

## 🚀 Funcionalidades

O sistema possui dois níveis de acesso distintos (Admin e Funcionário), cada um com suas respectivas permissões:

### 👤 Painel do Funcionário
- **Registro de Ponto:** Bater ponto em tempo real com **validação de Geolocalização** (garantindo que o funcionário está no local de trabalho permitido).
- **Histórico:** Visualização de todos os registros de ponto passados, com saldo de horas.
- **Justificativas:** Solicitação de justificativas para faltas ou atrasos, com envio de anexos/motivos.
- **Férias:** Interface para solicitação de períodos de férias.
- **Dashboard:** Resumo das horas trabalhadas no dia e no mês.

### 👔 Painel do Administrador (Gestor/RH)
- **Dashboard Gerencial:** Visão geral da assiduidade, funcionários ativos e métricas do dia.
- **Gestão de Funcionários:** Cadastro, edição, ativação e desativação de colaboradores.
- **Gestão de Locais Permitidos:** Cadastro de cercas virtuais (geolocalização) determinando onde o ponto pode ser registrado.
- **Aprovação de Demandas:** Análise e aprovação de Justificativas de ausência e Solicitações de Férias.
- **Gestão de Escalas e Turnos:** Configuração de horários de trabalho e atribuição para os funcionários.
- **Configurações da Empresa:** Informações da empresa e parâmetros gerais.
- **Registro Manual:** Possibilidade de lançar pontos retroativos ou correções manuais para os funcionários.
- **Relatórios:** Exportação do Espelho de Ponto em formato PDF.

---

## 🛠 Tecnologias Utilizadas

### Backend
- **Kotlin 1.9** com **Spring Boot 3.5**
- **Spring Security & JWT** (JSON Web Tokens) para autenticação e autorização
- **Spring Data JPA** & **Hibernate** para mapeamento objeto-relacional (ORM)
- **PostgreSQL** como banco de dados relacional principal
- **Swagger / OpenAPI 3** para documentação automática de APIs
- **OpenPDF & Thymeleaf** para geração de relatórios PDF

### Frontend
- **HTML5, CSS3 e JavaScript (Vanilla)**
- Design responsivo, estilização customizada e uso da **Geolocation API** nativa dos navegadores.

---

## 🐳 Executando com Docker (Recomendado)

O projeto está totalmente configurado para rodar em containers Docker, tornando o ambiente de desenvolvimento e produção muito mais simples e padronizado.

### Pré-requisitos
- [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/) instalados.

### Passos para rodar
Na raiz do projeto, simplesmente execute:

```bash
docker-compose up -d --build
```

Isso fará com que o Docker:
1. Suba um container com o **PostgreSQL** (banco de dados) configurado automaticamente.
2. Compile a aplicação Kotlin passando pelo estágio de *build* do Gradle.
3. Suba a aplicação Spring Boot na porta `8080`.

Após isso, acesse:
- **Aplicação Web:** [http://localhost:8080](http://localhost:8080)
- **Documentação da API (Swagger):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔧 Executando Localmente (Sem Docker para a API)

Caso queira rodar o banco no Docker e a aplicação na sua IDE (IntelliJ, Eclipse, VS Code):

1. **Suba apenas o Banco de Dados:**
   ```bash
   docker-compose up -d db
   ```

2. **Execute a aplicação via Gradle wrapper:**
   ```bash
   # No Windows
   .\gradlew.bat bootRun

   # No Linux/Mac
   ./gradlew bootRun
   ```

---

## 📚 Estrutura da API (Principais Rotas)

A documentação detalhada interativa de todas as rotas fica disponível no Swagger (`/swagger-ui.html`). Abaixo, um resumo da estrutura:

### Autenticação & Usuário Público
- `POST /login` - Autenticação com credenciais (retorna JWT e Refresh Token)
- `POST /api/auth/refresh` - Renova acessos expirados usando Refresh Token
- `GET /api/me` - Retorna os dados do usuário atual autenticado

### Funcionários (Admin)
- `GET /api/admin/funcionarios` - Lista de colaboradores
- `POST /api/admin/funcionarios` - Cadastra novo colaborador
- `PUT /api/admin/funcionarios/{id}/escala` - Atribui escala de trabalho

### Registro de Ponto (Mobile / Web)
- `POST /registros-ponto` - Registra batida de ponto (exige lat/lng da geolocalização)
- `GET /registros-ponto/funcionario/{id}/hoje` - Lista registros atuais
- `GET /api/geolocalizacao/validar` - Valida se a lat/lng atual está numa zona permitida

### Justificativas & Férias
- `GET /api/justificativas/pendentes` - Justificativas agurdando gestor
- `POST /api/justificativas/{id}/processar` - Aprova/reprova faltas
- `POST /api/ferias` - Solicita período de férias

### Relatórios
- `GET /api/relatorios/cartao-ponto/{funcionarioId}` - Baixa Espelho de Ponto em PDF protegido

---

## 🔐 Variáveis de Ambiente e Configuração

Na raiz de recursos (`src/main/resources/application.properties`), as credenciais padrão já vêm configuradas para subir no ambiente de desenvolvimento:
- **Database URL:** `jdbc:postgresql://localhost:5432/ponto_online_db`
- **Database User:** `admin`
- **Database Password:** `admin`

*Nota: Em produção, estas variáveis devem ser injetadas de forma segura via orquestrador de containers ou env vars.*

---

## 👨‍💻 Tratamento de Erros e Exceções

O sistema conta com um manipulador global de exceções (`TratadorDeExcecoes.kt`), que intercepta falhas de negócio (ex: "Funcionário fora do local permitido", "Saldo de horas negativo", "Token Expirado") e devolve respostas padronizadas em JSON com os Status Codes HTTP corretos (400, 401, 403, 404, 500).

---

## 📝 Licença
Desenvolvido como projeto de portfólio e sistema corporativo de gestão de tempo e presença.

## 👤 Autor

**JOÃO LUCAS BERNARDES**

* **LinkedIn**:https://www.linkedin.com/in/joaolucasbernardes/