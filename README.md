<div align="center">

```
███████╗██╗████████╗███╗   ███╗ █████╗ ██████╗ ██╗  ██╗
██╔════╝██║╚══██╔══╝████╗ ████║██╔══██╗██╔══██╗██║ ██╔╝
█████╗  ██║   ██║   ██╔████╔██║███████║██████╔╝█████╔╝
██╔══╝  ██║   ██║   ██║╚██╔╝██║██╔══██║██╔══██╗██╔═██╗
██║     ██║   ██║   ██║ ╚═╝ ██║██║  ██║██║  ██║██║  ██╗
╚═╝     ╚═╝   ╚═╝   ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝
```

### Registre. Evolua. Supere.

*API REST para acompanhamento de treinos de musculacao*

<br/>

[![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/features/actions)
[![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=black)](https://render.com/)
[![Neon](https://img.shields.io/badge/Neon-00E599?style=for-the-badge&logo=neon&logoColor=black)](https://neon.tech/)

<br/>

![Status](https://img.shields.io/badge/status-em_desenvolvimento-yellow?style=flat-square)
![Version](https://img.shields.io/badge/versão-1.0.0-blue?style=flat-square)
![License](https://img.shields.io/badge/licença-MIT-green?style=flat-square)

</div>

<br/>

---

## O que e o FitMark?

O FitMark e uma API REST que permite ao atleta **estruturar, executar e acompanhar** sua progressao na academia com precisao cirurgica.

Esqueca planilhas. Esqueca apps genéricos. O FitMark foi pensado para quem leva o treino a sério.

<br/>

<div align="center">

| | Funcionalidade |
|:---:|---|
| 🗂️ | **Splits** — organize seus dias em blocos (PPL, Upper/Lower, ABC...) |
| 🏋️ | **Workouts** — defina os treinos dentro de cada split, com ordem e notas |
| 💪 | **Exercicios** — carga, series, reps, posicao — tudo versionado |
| ⏱️ | **Sessoes ao vivo** — inicie um treino e registre cada serie em tempo real |
| 📊 | **Historico** — veja a evolucao de carga e volume por exercicio |
| 🔒 | **Auth JWT** — cada usuario ve apenas os seus proprios dados |

</div>

<br/>

---

## Endpoints

> **Base URL local:** `http://localhost:8080`
> **Base URL producao:** `https://<seu-servico>.onrender.com`
> Rotas marcadas com 🔒 exigem `Authorization: Bearer <token>`

<br/>

### 🔑 Autenticacao — `/auth`

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/auth/register` | Cadastro de novo usuario |
| `POST` | `/auth/login` | Login — retorna accessToken e refreshToken |
| `POST` | `/auth/refresh` | Renova o par de tokens (rotacao de refreshToken) |
| `POST` | `/auth/logout` | 🔒 Revoga o refreshToken e encerra a sessao |
| `GET` | `/auth/me` | 🔒 Perfil do usuario autenticado |
| `POST` | `/auth/forgot-password` | Solicita codigo de redefinicao de senha por e-mail |
| `POST` | `/auth/reset-password` | Redefine a senha com o codigo recebido |

<br/>

### 👤 Usuarios — `/users`

| Metodo | Rota | Descricao |
|---|---|---|
| `GET` | `/users/workouts` | 🔒 Todos os treinos do usuario |
| `GET` | `/users/sessions` | 🔒 Historico de sessoes concluidas |
| `GET` | `/users/sessions/active` | 🔒 Sessao de treino ativa (204 se nenhuma) |
| `GET` | `/users/sessions/{sessionId}` | 🔒 Detalhes de uma sessao com sets agrupados |
| `PATCH` | `/users/sessions/{sessionId}/abandon` | 🔒 Abandona a sessao ativa |
| `PATCH` | `/users/profile-photo` | 🔒 Atualiza URL da foto de perfil |

<br/>

### 🗂️ Splits — `/splits`

| Metodo | Rota | Descricao |
|---|---|---|
| `GET` | `/splits` | 🔒 Lista todos os splits do usuario |
| `POST` | `/splits` | 🔒 Cria um novo split |
| `GET` | `/splits/{splitId}` | 🔒 Detalhes de um split |
| `PUT` | `/splits/{splitId}` | 🔒 Atualiza o titulo do split |
| `DELETE` | `/splits/{splitId}` | 🔒 Remove o split e seus treinos |

<br/>

### 🏋️ Workouts — `/splits/{splitId}/workouts`

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/splits/{splitId}/workouts` | 🔒 Cria workout no split |
| `GET` | `/splits/{splitId}/workouts/{workoutId}` | 🔒 Detalhes do workout |
| `PUT` | `/splits/{splitId}/workouts/{workoutId}` | 🔒 Atualiza titulo e notas do workout |
| `DELETE` | `/splits/{splitId}/workouts/{workoutId}` | 🔒 Remove o workout |

<br/>

### 💪 Exercicios — `/splits/{splitId}/workouts/{workoutId}/exercises`

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `.../exercises` | 🔒 Adiciona exercicio ao treino |
| `GET` | `.../exercises/{exerciseId}` | 🔒 Detalhes do exercicio (carga atual, reps) |
| `PUT` | `.../exercises/{exerciseId}` | 🔒 Atualiza nome e numero de series |
| `PATCH` | `.../exercises/reorder` | 🔒 Reordena posicoes dos exercicios |
| `DELETE` | `.../exercises/{exerciseId}` | 🔒 Remove exercicio e reordena os demais |
| `GET` | `.../exercises/exerciselog/{exerciseId}` | 🔒 Historico de series por exercicio |

<br/>

### ⏱️ Sessoes de Treino — `/splits/{splitId}/workouts/{workoutId}`

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `.../workoutsession-start` | 🔒 Inicia sessao de treino |
| `POST` | `.../sessions/{sessionId}/sets` | 🔒 Registra serie na sessao |
| `PATCH` | `.../sessions/{sessionId}/sets/{setId}` | 🔒 Edita serie registrada |
| `PATCH` | `.../sessions/{sessionId}/finish` | 🔒 Finaliza sessao e salva resumo |

<br/>

---

## Tipos de Serie (`SetType`)

<div align="center">

| Tipo | Descricao |
|:---:|---|
| `WORK` | Serie de trabalho principal |
| `WARMUP` | Aquecimento |
| `DROP` | Drop set — reduz carga sem descanso |
| `FAILURE` | Ate a falha muscular |
| `BACKOFF` | Serie de desaquecimento |
| `AMRAP` | As Many Reps As Possible |
| `REST_PAUSE` | Pausa curta e continua |
| `SUPERSET` | Superset com outro exercicio |

</div>

<br/>

---

## Stack Tecnica

<div align="center">

| Camada | Tecnologia | Papel |
|---|---|---|
| Linguagem | Java 17 | Base do projeto |
| Framework | Spring Boot 4 | Web, IoC, autoconfiguracao |
| Seguranca | Spring Security + Auth0 JWT | Autenticacao e autorizacao |
| Persistencia | Spring Data JPA + Hibernate | ORM e repositorios |
| Banco (local) | PostgreSQL 16 via Docker | Banco para desenvolvimento |
| Banco (prod) | Neon (PostgreSQL serverless) | Banco em producao na nuvem |
| Migracoes | Flyway | Versionamento do schema |
| Boilerplate | Lombok | Getters, construtores, builders |
| Build | Maven | Dependencias e empacotamento |
| Containerizacao | Docker + Docker Compose | Empacotamento e ambiente local |
| Registry | GitHub Container Registry (GHCR) | Armazena a imagem Docker |
| CI/CD | GitHub Actions | Build e push automatico da imagem |
| Deploy | Render | Hospedagem da API em producao |

</div>

<br/>

---

## CI/CD e Deploy

```
push para branch Docker
        │
        ▼
GitHub Actions (docker-build.yml)
  └── Build da imagem Docker (multi-stage)
  └── Push para ghcr.io/sorenseng/api-fitmark:latest
        │
        ▼
Render detecta nova imagem no GHCR
  └── Pull e redeploy automatico
        │
        ▼
API rodando com banco Neon (PostgreSQL serverless)
```

**Variaveis de ambiente no Render:**

| Variavel | Descricao |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexao do Neon (`jdbc:postgresql://...`) |
| `SPRING_DATASOURCE_USERNAME` | Usuario do banco Neon |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco Neon |
| `JWT_SECRET` | Segredo para assinar os tokens JWT |
| `BREVO_API_KEY` | Chave de API do Brevo para envio de e-mails |
| `MAIL_FROM` | Endereco de e-mail remetente |
| `MAIL_FROM_NAME` | Nome exibido no campo "De" (padrao: FitMark) |

<br/>

---

## Arquitetura

```
src/main/java/com/Sorensen/FitMark/
│
├── config/security/
│   ├── JWT/              ← Filtro de autenticacao e geracao de token
│   └── service/          ← UserDetailsService para o Spring Security
│
├── controller/           ← Camada HTTP: recebe e responde requisicoes
├── service/              ← Regras de negocio
├── repository/           ← Interfaces Spring Data JPA
├── entity/               ← Entidades JPA (mapeamento das tabelas)
├── dto/                  ← Request/Response bodies tipados
│   ├── auth/
│   ├── split/
│   ├── workout/
│   ├── exercise/
│   ├── ExerciseLog/
│   ├── user/
│   └── error/
└── Util/                 ← Utilitarios compartilhados
```

<br/>

### Hierarquia de dados

```
Usuario
 └── Split  (ex: PPL, ABC)
      └── Workout  (ex: Push, Pull, Leg)
           └── Exercise  (ex: Supino, Agachamento)
                └── SetLog  (serie registrada na sessao)

WorkoutSession  ←  iniciada por Workout
 └── SetLog[]   ←  series registradas em tempo real
```

<br/>

---

## Como rodar

### Opcao 1 — Docker Compose (recomendado)

Pre-requisito: [Docker](https://docs.docker.com/get-docker/) instalado.

**1. Clone o repositorio**

```bash
git clone https://github.com/SorensenG/Api-FitMark.git
cd Api-FitMark
```

**2. Crie o arquivo `.env` na raiz**

```env
DB_NAME=Api-FitMark
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=seu_secret_jwt_aqui
```

**3. Suba os containers**

```bash
docker compose up --build
```

> Isso sobe a API na porta `8080` e um PostgreSQL 16 local. As migracoes Flyway rodam automaticamente.

**4. Teste se esta no ar**

```bash
curl http://localhost:8080/teste
```

<br/>

### Opcao 2 — Sem Docker (Maven direto)

Pre-requisitos: Java 17+, PostgreSQL em `localhost:5432`, Maven 3.8+

**1. Crie o banco**

```sql
CREATE DATABASE "Api-FitMark";
```

**2. Configure `src/main/resources/application.properties`**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Api-FitMark
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
jwt.secret=seu_secret_jwt_aqui
```

**3. Suba a aplicacao**

```bash
./mvnw spring-boot:run
```

<br/>

---

## Autenticacao

Todas as rotas protegidas exigem o token no header:

```http
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5c...
```

Fluxo:

```
POST /auth/register        →  cria conta
POST /auth/login           →  retorna { accessToken, refreshToken }
GET  /auth/me              →  retorna perfil do usuario autenticado
POST /auth/refresh         →  troca refreshToken por novo par de tokens
POST /auth/logout          →  revoga o refreshToken

POST /auth/forgot-password →  envia codigo de 6 digitos por e-mail
POST /auth/reset-password  →  valida codigo e redefine a senha
```

<br/>

---

<div align="center">

**FitMark** — feito com foco e progressao sobrecarga

Desenvolvido por [Sorensen](https://github.com/SorensenG)

</div>
