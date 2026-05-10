IMAGE_NAME := srinil-stay-core

.DEFAULT_GOAL := help

.PHONY: run up up-build up-infra down down-infra down-clean test build clean docker-build format format-check help

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

format: ## Apply Spotless code formatting
	./mvnw spotless:apply

format-check: ## Check code formatting with Spotless
	./mvnw spotless:check

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
