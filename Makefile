.PHONY: test test-ci coverage

test:
	mvn verify

test-ci:
	mvn verify "-Dspring.profiles.active=ci"

coverage:
	mvn clean test jacoco:report
	@echo "Report generated at: target/site/jacoco/index.html"