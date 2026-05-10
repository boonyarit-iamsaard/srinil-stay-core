IMAGE_NAME := srinil-stay-core
SQLFLUFF_DIALECT := postgres
SQLFLUFF_PATHS := src/main/resources/db/migration

.DEFAULT_GOAL := help

.PHONY: run up up-build up-infra down down-infra down-clean test build clean docker-build format format-check setup-git-hooks sql-format sql-format-check help

run: ## Run the app locally with Spring Boot
	SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run

up: ## Start the full stack (application and infrastructure)
	docker compose up -d --wait

up-build: ## Build and start the full stack
	docker compose up -d --wait --build

up-infra: ## Start infrastructure only (postgres)
	docker compose -f compose.yaml up -d --wait

down: ## Stop the full stack
	docker compose down

down-infra: ## Stop infrastructure only
	docker compose -f compose.yaml down

down-clean: ## Stop the full stack and remove volumes
	docker compose down -v

format: sql-format ## Apply Spotless and SQLFluff code formatting
	./mvnw spotless:apply

format-check: sql-format-check ## Check code formatting with Spotless and SQLFluff
	./mvnw spotless:check

setup-git-hooks: ## Configure local Git hooks path
	./mvnw git-build-hook:configure

sql-format: ## Apply SQLFluff formatting to PostgreSQL migrations
	@if command -v sqlfluff >/dev/null 2>&1; then \
		sqlfluff fix --dialect $(SQLFLUFF_DIALECT) $(SQLFLUFF_PATHS); \
	else \
		echo "sqlfluff not found; skipping SQL formatting"; \
	fi

sql-format-check: ## Check PostgreSQL migrations with SQLFluff
	@if command -v sqlfluff >/dev/null 2>&1; then \
		sqlfluff lint --dialect $(SQLFLUFF_DIALECT) $(SQLFLUFF_PATHS); \
	else \
		echo "sqlfluff not found; skipping SQL lint"; \
	fi

test: ## Run tests
	./mvnw test

build: ## Package the application (skip tests)
	./mvnw package -DskipTests

clean: ## Clean build artifacts
	./mvnw clean

docker-build: ## Build Docker image tagged as local-production
	docker build --tag $(IMAGE_NAME):local-production .

help: ## Show available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  %-15s %s\n", $$1, $$2}'
