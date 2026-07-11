.PHONY: test serve docker-up docker-down

JAVA_HOME_PATH := $(shell if [ -f .java_home_path ]; then cat .java_home_path; elif [ -d .tools/jdk-21.0.7+6/Contents/Home ]; then echo $(CURDIR)/.tools/jdk-21.0.7+6/Contents/Home; else echo ""; fi)

ifeq ($(JAVA_HOME_PATH),)
  JAVA_ENV :=
else
  JAVA_ENV := JAVA_HOME=$(JAVA_HOME_PATH) PATH=$(JAVA_HOME_PATH)/bin:$$PATH
endif

test:
	$(JAVA_ENV) ./gradlew test --no-daemon

serve:
	mkdir -p data
	$(JAVA_ENV) ./gradlew bootRun --no-daemon

docker-up:
	docker compose up --build

docker-down:
	docker compose down
