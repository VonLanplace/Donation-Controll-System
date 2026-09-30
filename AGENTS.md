# AGENTS.md

Java 17. Gradle 9.5.1 wrapper. Spring Boot 4.0.6. Donation-control system. No CI, no README, no lint/format task.

## Verify

```bash
./gradlew build          # compile + test; the only real gate
./gradlew test           # same test, faster
```

`check`+`test` only verification task. No lint/format → MapStruct/Hibernate warning during `compileJava` only static signal. Read them.

## The one test is the JPA integration test

`src/test/java/com/vonlanplace/doacao/DoacaoApplicationTests.java` hold only `contextLoads()`. Sole net for entity mapping, derived query, record DTO shape — all fail at context startup. Run after touching entity, `@OneToMany`/`@ManyToOne`, derived-query name, DTO record. Caught real break: `mappedBy` naming column not attribute; `findDataNascimento` missing `By`.

## Running the app

`main()` NOT start web server. Call `Application.launch(JavaFXApplication.class)`; `JavaFXApplication.init()` boot Spring via `SpringApplicationBuilder` → start Tomcat `:8080`.

```bash
./gradlew bootRun                                  # needs a DISPLAY; this box has :1
curl http://localhost:8080/api/produto
```

API only via `bootRun`; `bootRun` need display. `Unsupported JavaFX configuration: classes were loaded from 'unnamed module'` benign.

DB in-mem H2 `jdbc:h2:mem:db_doacao`, wiped each boot. Console user `sa`, no password — but `/h2-console` return 404 despite `spring.h2.console.enabled=true`. Do not trust.

## Jackson 3, not Jackson 2

Boot 4 pull `tools.jackson.core:jackson-databind`. Import `tools.jackson.databind.ObjectMapper`; `com.fasterxml.jackson.databind` fail to compile. `TestRestTemplate` not on classpath — test service direct, autowire `tools.jackson.databind.ObjectMapper`.

## MapStruct

Impl in `build/generated/sources/annotationProcessor/java/main/` — read `*MapperImpl.java` to confirm mapping, no guess. `lombok-mapstruct-binding` wired, Lombok getter visible to processor.

Mapper ignore every target service set by hand: `id`, `produtos`, `cesta`, `marcaProduto`, `tipoProduto`. DTO type ≠ entity type → need `@Mapping(target = ..., ignore = true)` or `source = "x.nome"` bridge. Else compile error or silent unmapped warning.

## DTO conventions

Records not class. Suffix uppercase `DTO`; `ProdutoCreateDTO` never `ProdutoCreateDto`. Lookup key asymmetric by design, this trip people:

- `MarcaProduto`/`TipoProduto` by unique `nome` — `findByNome`, `update(String nomeAtual, DTO)`, `delete(String nome)`. No `id` on any DTO.
- `Produto` by `UUID id`, but marca/tipo by `nome` — `String marcaProduto`, `String tipoProduto` on create/update DTO.
- Nested response DTO id-free on purpose → `GET /api/produto` return `"marcaProduto":{"nome":"Nike"}`.

`ProdutoResponseDTO` take no edit for nested shape; edit nested DTO instead.

## No migrations

`spring.jpa.hibernate.ddl-auto` unset → `create-drop` on embedded H2. Schema derive from entity, no Flyway/Liquibase, drift silent. Verify DDL, never assume. Test worker inherit env not CLI `-D`, so use env:

```bash
SPRING_APPLICATION_JSON='{"spring":{"jpa":{"properties":{"jakarta":{"persistence":{"schema-generation":{"scripts":{"action":"create","create-target":"/tmp/schema.sql"}}}}}}}}' ./gradlew test --rerun-tasks
```

## Lombok `@Data` on bidirectional entities is a live landmine

`@Data` FORBIDDEN on entity. Bidirectional pair (`MarcaProduto.produtos` ↔ `Produto.marcaProduto`, `Doacao.produtos` ↔ `DoacaoProduto.doacao`, `Residencia.recebedores` ↔ `Recebedor.residencia`) plus `@Data` generate `toString`/`equals`/`hashCode` both end; log or compare managed entity recurse till stack overflow.

Instead `@Getter` `@Setter` + `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` with `@EqualsAndHashCode.Include` on `id` + `@ToString(onlyExplicitlyIncluded = true)`. Composite id: hand-write `equals`/`hashCode` over each side `getId()`, never compare entity.

Name constraint, never inline `unique = true`: `@UniqueConstraint(name = "uk_<table>_<col>")`, `foreignKey = @ForeignKey(name = "fk_<table>_<col>")`. Else Hibernate emit random `FKbi7iy19k7t1...` that change every run. Cascade only on aggregate root `Doacao.produtos`, never from child.

Service return DTO. Never hand entity to log, assertion, or `Page.toString()`.

## Known-broken, do not assume it works

- `dev/TestController` (`/test`) + `templates/test.html` = staged scratch. `dev` package hold nothing else.

`MarcaProdutoService.delete`/`TipoProdutoService.delete` delete guard is **correct**: `if (produtoRepository.existsByMarcaProduto(...)) throw` → unlinked pass, linked 409. Covered both way by `MarcaProdutoServiceTest` and `TipoProdutoServiceTest`. An older note here claimed it was inverted — it was wrong, delete it again.

REST controllers name `*APIController`; Thymeleaf controller name `*ViewController` (suffix say response is HTML, not JSON). BCrypt wired, unused: `config/PasswordConfig` expose `PasswordEncoder`, `Entrevistador.senhaHash` → `senha_hash`, no `EntrevistadorService` so no `encode(...)` call site.

## Layout

Feature package under `com.vonlanplace.doacao`: `produto` (+`marca`, `tipo`), `recebedor` (+`residencia`), `doacao`, `cesta`, `entrevistador`, `config`, `exception`, `javafx`, `dev`, `web`. Layering: Entity → `*Mapper` → `*DTO` record → `*Service` → `*Repository` → Controller. Service resolve relation from name/id, return response DTO, never entity.

Feature-first, not layer-first. `marca`/`tipo` nest under `produto`, `residencia` under `recebedor` on purpose: lookup data owned by the parent feature. Every file's `package` match its directory — no exception. `javafx/controller` = FXML controllers named by `fx:controller` in the `.fxml`, `javafx/springIntegration` = JavaFX-to-Spring bridge, neither is web. `web` = pages that belong to no feature (home, 404) and the view-side exception advice.

## Web (Thymeleaf, server-rendered)

`@Controller` call `*Service` direct, never HTTP. `*APIController` stay the JSON contract. No CORS config, no JS bundle, no `thymeleaf-layout-dialect`.

Compose with `th:replace` + `templates/fragments/*` only: `head(css)` (the `css` arg is a filename in `static/css/`, or `null`), `header`, `trail`, `pagination(url, page, sort)`. `default.html` was deleted as orphan — do not reintroduce a page that copies the shell by hand.

`th:each` goes over `${page.content}`, **never** over the `Page`, and never `#lists.isEmpty(page)`: `Page` is not a `List` and the SpEL type conversion throws before the template renders. `page.sort` is a `Sort` object — interpolating it yields `"codigoBarras: ASC"`, invalid as a `?sort=` value, so the controller passes `ordenacao` as a string.

`ViewExceptionHandler` is `@ControllerAdvice(assignableTypes = …)` **and** `@Order(HIGHEST_PRECEDENCE)`, both load-bearing:

- Unscoped, a bare `@ControllerAdvice` also matches `@RestController` (meta-annotated `@Controller`) and would make `/api/*` answer the 404 page HTML instead of `problem+json`.
- `@RestControllerAdvice` on `GlobalExceptionHandler` is **not** REST-scoped: it is meta-annotated `@ControllerAdvice`, so it also reaches view controllers. Both advices handle `EntityNotFoundException`, and without the order the REST one won — `/produto/detalhe/{id-inexistente}` returned 404 with a `problem+json` body. Restricting `assignableTypes` is not enough on its own; the order is what keeps view HTML and API JSON in their own lanes. Add every new `*ViewController` to that list.

`spring.data.web.pageable.max-page-size=100` is set in `application.properties`, global, so it caps `?size=` on view and API alike. Before it: `?size=100000` and `?size=1e9` returned HTTP 200 and Spring tried to materialize the whole result in memory. The API REST returns raw `Page`, which Spring warns is not a stable JSON contract — that is a contract decision, not a bug to fix here.

### CSS layering (two real bugs, both found by sampling rendered pixels)

`head(css)` loads the base itself: `colors`, `default`, `header`, `trail`, **`components`**, **`card`**, **`table`**. The `css` arg is *extra* page CSS on top — today only `forms.css` uses it. Do not move those three back behind the arg:

- `components.css` owns `.pagination`, which the `pagination` fragment already emits on any paginated page. Behind the arg, `/produto` paginated unstyled. Its `.flash-*` rules were deleted with the `flash` fragment: no template included it and no controller set `mensagem`/`tipo`, so it was dead CSS. Both come back with the first write form and its `RedirectAttributes`.
- `card.css` was not loaded by any page, so `.card` rendered with no background, no border and no teal `.card-header` band. `table.css` was requested by `listar.html` but the `.card` wrapping it was invisible.

`static/css/produto/listar.css` and `templates/default.html` were deleted. Adding a page-specific file that restyles bare `body`/`table`/`th`/`td` is how the layout got broken before — every rule stays class-scoped.

No lint/format task, and CSS has no test. Verification is a headless screenshot plus a pixel sample, because reading the stylesheet proves nothing about the cascade. `DISPLAY=:1` here; Firefox needs a throwaway `--profile` and can leave a `.parentlock` that silently kills every later screenshot. `pkill -f bootRun` matches the invoking shell and kills the session — match on the java PID instead.

### Palette

ColorHunt `00797924b1b1ffe2afe37434` → `007979 #24b1b1 #ffe2af #e37434`. Two of the four fail text contrast on white (`#24b1b1` 2.63:1, `#e37434` 3.06:1, minimum 4.5:1), so `colors.css` keeps the raw hexes as `--palette-*` for decorative use and derives text/background roles: `--secondary-color #177e7e` (4.86:1), `--accent-color #c2551c` (4.55:1), `--primary-color #007979` (white text 5.24:1), `--highlight-color #ffe2af` (table head), `--highlight-suave #fdf3e0` (row hover, kept distinct from the head). Everything else consumes the 8 role tokens — swapping palettes means rewriting `colors.css` only. The old `#2c3e50/#3498db/#e74c3c` set is gone.

## Git

Branch `spring`. Nothing auto-verify on push → run `./gradlew build` before report work done.
