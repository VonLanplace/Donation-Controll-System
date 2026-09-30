# AGENTS.md

Java 17 / Gradle 9.5.1 wrapper / Spring Boot 4.0.6 donation-control system. No CI, no README, no lint or format tasks.

## Verify

```bash
./gradlew build          # compile + test; the only real gate
./gradlew test           # same test, faster
```

`check` and `test` are the only verification tasks. There is no lint/format step, so MapStruct/Hibernate warnings printed during `compileJava` are the only static signal — read them, they are not noise.

## The one test is the JPA integration test

`src/test/java/com/vonlanplace/doacao/DoacaoApplicationTests.java` holds only `contextLoads()`. It is the sole regression net for entity mappings, derived queries, and record DTO shapes, because all of those fail at context startup. Always run it after touching entities, `@OneToMany`/`@ManyToOne`, Spring Data derived-query method names, or DTO records. It has caught real breakage: a `mappedBy` that named a column instead of an attribute, and a `findDataNascimento` missing its `By`.

## Running the app

`main()` does **not** start the web server. It calls `Application.launch(JavaFXApplication.class)`, and `JavaFXApplication.init()` boots the Spring context via `SpringApplicationBuilder` — which is what starts Tomcat on `:8080`.

```bash
./gradlew bootRun                                  # needs a DISPLAY; this box has :1
curl http://localhost:8080/api/produto
```

So the REST API is only reachable through `bootRun`, and `bootRun` needs a display. The `Unsupported JavaFX configuration: classes were loaded from 'unnamed module'` warning on startup is benign.

Database is in-memory H2 (`jdbc:h2:mem:db_doacao`), wiped on every boot; H2 console at `/h2-console` (user `sa`, empty password).

## Jackson 3, not Jackson 2

Spring Boot 4 pulls `tools.jackson.core:jackson-databind`. Import `tools.jackson.databind.ObjectMapper`. Any `com.fasterxml.jackson.databind` import will not compile. `TestRestTemplate` is not on the classpath as shipped — exercise services directly and serialize with an autowired `tools.jackson.databind.ObjectMapper`.

## MapStruct

Generated impls land in `build/generated/sources/annotationProcessor/java/main/` — read `*MapperImpl.java` there to confirm real mapping behavior instead of guessing. `lombok-mapstruct-binding` is wired, so Lombok getters are visible to the processor.

Mappers ignore every target that the service sets by hand (`id`, `produtos`, `cesta`, `marcaProduto`, `tipoProduto`). When a DTO field's type does not line up with the entity's, the mapper needs an explicit `@Mapping(target = ..., ignore = true)` or a `source = "x.nome"` bridge; otherwise you get a compile error or a silent unmapped-property warning.

## DTO conventions

Records, not classes. Suffix is uppercase `DTO` (`ProdutoCreateDTO`, never `ProdutoCreateDto`).

Lookup keys are asymmetric by design and this trips people up:

- `MarcaProduto` / `TipoProduto` are addressed by their unique `nome` — `findByNome`, `update(String nomeAtual, DTO)`, `delete(String nome)`. They have no `id` on any DTO.
- `Produto` is addressed by `UUID id`, but references marca/tipo by `nome` (`String marcaProduto`, `String tipoProduto` on the create/update DTOs).
- Nested response DTOs are `id`-free on purpose, so `GET /api/produto` returns `"marcaProduto":{"nome":"Nike"}`.

`ProdutoResponseDTO` takes no edits for nested shapes; drop or add the field on the nested DTO instead.

## No migrations

`spring.jpa.hibernate.ddl-auto` is unset, so it defaults to `create-drop` on embedded H2. The schema is derived straight from the entities, and there is no Flyway/Liquibase to catch drift. Entity changes silently reshape the DB.

## Lombok `@Data` on bidirectional entities is a live landmine

`MarcaProduto.produtos` ↔ `Produto.marcaProduto` and `TipoProduto.produtos` ↔ `Produto.tipoProduto` are bidirectional, and `@Data` generates `toString`/`equals`/`hashCode` on both ends. Logging or comparing a managed entity recurses until stack overflow. The service layer returns DTOs specifically to avoid this — stay on DTOs and do not hand entities to logs, assertions, or `Page.toString()`.

## Known-broken, do not assume it works

- `ProdutoController` (Thymeleaf, `/produto`) returns the view `produto/form`, which does not exist under `src/main/resources/templates/`.
- `templates/produto/listar.html` renders `${produto.nome}` (not a field on `ProdutoResponseDTO`) and links `@{/css/listar.css}` while the file is at `static/css/produto/listar.css`.

`ProdutoAPIController` (`/api/produto`) is the only REST controller that works end to end. `MarcaProdutoService` and `TipoProdutoService` have no controllers at all.

`TestController` (`/test`) and `templates/test.html` are uncommitted scratch work staged in the index — not part of the real feature set.

## Layout

Feature packages under `com.vonlanplace.doacao`: `produto` (+ `marca`, `tipo`), `recebedor` (+ `residencia`), `doacao`, `cesta`, `entrevistador`, `exception`, `javafx`. The layering within a feature is Entity → `*Mapper` → `*DTO` record → `*Service` → `*Repository` → Controller; the service resolves relationships from names/ids and returns response DTOs, never entities.

## Git

Active branch is `spring`. Nothing is auto-verified on push, so run `./gradlew build` yourself before reporting work as done.
