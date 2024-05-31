APP_BUILD=`date +%FT%T%z`
APP_VERSION=`(sed -n 's,.*<version>\(.*\)</version>.*,\1,p' pom.xml | head -1)`
APP_NAME=`(sed -n 's,.*<name>\(.*\)</name>.*,\1,p' pom.xml | head -1)`
APP_FULL_NAME="${APP_NAME}-${APP_VERSION}.jar"

DOCKER_IMAGE=players-lab

# Example: AWS related variables, eu-west-3 is Paris region
AWS_REGION=eu-north-1
AWS_ACCOUNT_NUMBER=111111111111
# Check file valid: cat -e -t -v Makefile

env: ## - List of Env variables
	export $(cat .env | xargs)
	@echo
	@echo
	@echo "IMPORTANT: Configure valid environment properties: LOG_SOURCE, CONFIG_SOURCE and PLAYERS_CONFIG_SOURCE"
	@echo "Application architecture: $(CONFIG_ARCHITECTURE)"
	@echo "Application configuration file: $(CONFIG_SOURCE)"
	@echo "Application logger configuration file: $(LOG_SOURCE)"
	@echo "Players configuration file: $(PLAYERS_CONFIG_SOURCE)"
	@echo "Application full name: $(APP_FULL_NAME)"
	@echo "Application name: $(APP_NAME)"
	@echo "Application version: $(APP_VERSION)"

help: ## - Show help message
	@printf "\033[32m\xE2\x9c\x93 usage: make [target]\n\n\033[0m"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

build:env ## - Compile and Build application
	@echo "building............"
	@mvn clean package

docker-build:build ## - Build Docker image for  application
	@echo "docker............"
	@aws ecr get-login-password --region $(AWS_REGION) | docker login --username AWS --password-stdin $(AWS_ACCOUNT_NUMBER).dkr.ecr.$(AWS_REGION).amazonaws.com
	@docker build --build-arg "PLAYERS_CONFIG_SOURCE=$(PLAYERS_CONFIG_SOURCE)" --build-arg "LOG_SOURCE=$(LOG_SOURCE)" --build-arg "CONFIG_SOURCE=$(CONFIG_SOURCE)" --build-arg "APP_VERSION=$(APP_VERSION)" --build-arg "APP_BUILD=$(APP_BUILD)" --build-arg "APP_FULL_NAME=$(APP_FULL_NAME)" --build-arg "AWS_REGION=$(AWS_REGION)" --build-arg "AWS_ACCOUNT_NUMBER=$(AWS_ACCOUNT_NUMBER)"  -t $(DOCKER_IMAGE):$(APP_VERSION) . --progress=plain --no-cache --platform linux/amd64

docker-scan: ## - Scan for known vulnerabilities the  docker image
	@printf "\033[32m\xE2\x9c\x93 Scan for known vulnerabilities  docker image\n\033[0m"
	@docker scan -f Dockerfile $(DOCKER_IMAGE):$(APP_VERSION)

docker-ls: ## - List of docker images
	@printf "\033[32m\xE2\x9c\x93 Look at the size dude !\n\033[0m"
	@docker image ls $(DOCKER_IMAGE)

docker-run:	## - Run in Docker the  docker image
	@printf "\033[32m\xE2\x9c\x93 Run the smallest and secured  docker image\n\033[0m"
	@docker run $(DOCKER_IMAGE):$(APP_VERSION)

docker-info: ## - Docker Info about  docker image
	@printf "\033[32m\xE2\x9c\x93 Docker Info about  docker image, version $(APP_VERSION), build $(APP_BUILD)\n\033[0m"
	@docker inspect $(DOCKER_IMAGE):$(APP_VERSION)

docker-delete-image: ## - Delete  docker image
	@printf "\033[32m\xE2\x9c\x93 Delete  docker image\n\033[0m"
	@docker rmi $(DOCKER_IMAGE):$(APP_VERSION)

docker-delete-images: ## - Delete all docker unused images
	@printf "\033[32m\xE2\x9c\x93 Delete all docker unused images\n\033[0m"
	@docker image prune -a

docker-delete-containers: ## - Delete all docker stopped containers
	@printf "\033[32m\xE2\x9c\x93 Delete all docker stopped containers\n\033[0m"
	@docker container prune

push-to-aws:	## - Push docker image to AWS Elastic Container Registry
	@printf "\033[32m\xE2\x9c\x93 Push docker image '$(DOCKER_IMAGE):$(APP_VERSION)' to AWS Elastic Container Registry: $(AWS_ACCOUNT_NUMBER).dkr.ecr.$(AWS_REGION).amazonaws.com\n\033[0m"
	@aws ecr get-login-password --region $(AWS_REGION) | docker login --username AWS --password-stdin $(AWS_ACCOUNT_NUMBER).dkr.ecr.$(AWS_REGION).amazonaws.com
	@docker tag $(DOCKER_IMAGE):$(APP_VERSION) $(AWS_ACCOUNT_NUMBER).dkr.ecr.$(AWS_REGION).amazonaws.com/$(DOCKER_IMAGE):$(APP_VERSION)
	@docker push $(AWS_ACCOUNT_NUMBER).dkr.ecr.$(AWS_REGION).amazonaws.com/$(DOCKER_IMAGE):$(APP_VERSION)

scan:	## - Scan for known vulnerabilities the  docker image
	@printf "\033[32m\xE2\x9c\x93 Scan for known vulnerabilities  docker image\n\033[0m"
	@docker scan -f Dockerfile $(DOCKER_IMAGE):$(APP_VERSION)

run:build ## - Compile, Build and locally run application
	@echo "running............"
	@sh ./scripts/local-run.sh
